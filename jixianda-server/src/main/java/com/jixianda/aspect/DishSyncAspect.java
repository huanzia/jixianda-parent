package com.jixianda.aspect;

import com.jixianda.doc.DishDoc;
import com.jixianda.dto.DishDTO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Aspect
@Component
public class DishSyncAspect {

    private static final String DISH_DETAIL_SQL =
            "select d.id as dish_id, d.name as dish_name, d.price as dish_price, d.image as dish_image, " +
            "c.name as category_name, ws.warehouse_id as warehouse_id, ws.stock as stock " +
            "from dish d " +
            "left join category c on d.category_id = c.id " +
            "left join warehouse_sku ws on ws.dish_id = d.id " +
            "where d.id = ?";

    private final ElasticsearchOperations elasticsearchOperations;
    private final JdbcTemplate jdbcTemplate;
    private final Executor businessTaskExecutor;

    public DishSyncAspect(ElasticsearchOperations elasticsearchOperations,
                          JdbcTemplate jdbcTemplate,
                          @Qualifier("businessTaskExecutor") Executor businessTaskExecutor) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.jdbcTemplate = jdbcTemplate;
        this.businessTaskExecutor = businessTaskExecutor;
    }

    @AfterReturning(
            value = "(execution(* com.jixianda.service.impl.DishServiceImpl.saveWithFlaver(..)) || " +
                    "execution(* com.jixianda.service.impl.DishServiceImpl.saveWithFlavor(..))) && args(dishDTO)",
            argNames = "dishDTO")
    public void afterSaveWithFlaver(DishDTO dishDTO) {
        executeAfterCommit(() -> CompletableFuture.runAsync(() -> {
            Long dishId = resolveCreatedDishId(dishDTO);
            if (dishId != null) {
                syncDishToEs(dishId);
            }
        }, businessTaskExecutor));
    }

    @AfterReturning(
            value = "execution(* com.jixianda.service.impl.DishServiceImpl.updateWithFlavor(..)) && args(dishDTO)",
            argNames = "dishDTO")
    public void afterUpdateWithFlavor(DishDTO dishDTO) {
        if (dishDTO == null || dishDTO.getId() == null) {
            return;
        }
        executeAfterCommit(() -> CompletableFuture.runAsync(
                () -> syncDishToEs(dishDTO.getId()),
                businessTaskExecutor
        ));
    }

    @AfterReturning(
            value = "execution(* com.jixianda.service.impl.DishServiceImpl.startOrStop(..)) && args(status,id)",
            argNames = "status,id")
    public void afterStartOrStop(Integer status, Long id) {
        if (id == null) {
            return;
        }
        executeAfterCommit(() -> CompletableFuture.runAsync(
                () -> syncDishToEs(id),
                businessTaskExecutor
        ));
    }

    @AfterReturning(
            value = "execution(* com.jixianda.service.impl.DishServiceImpl.deleteBatch(..)) && args(ids)",
            argNames = "ids")
    public void afterDeleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        executeAfterCommit(() -> CompletableFuture.runAsync(() -> {
            for (Long id : ids) {
                if (id != null) {
                    elasticsearchOperations.delete(String.valueOf(id), DishDoc.class);
                }
            }
        }, businessTaskExecutor));
    }

    private void executeAfterCommit(Runnable task) {
        if (TransactionSynchronizationManager.isSynchronizationActive()
                && TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    task.run();
                }
            });
            return;
        }
        task.run();
    }

    private void syncDishToEs(Long dishId) {
        try {
            DishDoc dishDoc = loadDishDoc(dishId);
            if (dishDoc == null) {
                elasticsearchOperations.delete(String.valueOf(dishId), DishDoc.class);
                return;
            }
            elasticsearchOperations.save(dishDoc);
        } catch (Exception e) {
            log.error("Dish ES incremental sync failed, dishId={}, msg={}", dishId, e.getMessage(), e);
        }
    }

    private DishDoc loadDishDoc(Long dishId) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(DISH_DETAIL_SQL, dishId);
        if (rows == null || rows.isEmpty()) {
            return null;
        }
        Map<Long, DishDoc> docMap = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            Long id = toLong(row.get("dish_id"));
            if (id == null) {
                continue;
            }
            DishDoc dishDoc = docMap.computeIfAbsent(id, key ->
                    DishDoc.builder()
                            .id(key)
                            .name(toStringValue(row.get("dish_name")))
                            .price(toDouble(row.get("dish_price")))
                            .image(toStringValue(row.get("dish_image")))
                            .categoryName(toStringValue(row.get("category_name")))
                            .warehouseStock(new ArrayList<>())
                            .build()
            );
            Long warehouseId = toLong(row.get("warehouse_id"));
            Integer stock = toInteger(row.get("stock"));
            if (warehouseId != null) {
                dishDoc.getWarehouseStock().add(
                        DishDoc.WarehouseStock.builder()
                                .warehouseId(warehouseId)
                                .stock(stock == null ? 0 : stock)
                                .build()
                );
            }
        }
        return docMap.get(dishId);
    }

    private Long resolveCreatedDishId(DishDTO dishDTO) {
        if (dishDTO == null) {
            return null;
        }
        if (dishDTO.getId() != null) {
            return dishDTO.getId();
        }
        String sql = "select id from dish where name = ? order by create_time desc limit 1";
        List<Long> ids = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("id"), dishDTO.getName());
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        return ids.get(0);
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }

    private Double toDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).doubleValue();
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.parseDouble(String.valueOf(value));
    }

    private String toStringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
