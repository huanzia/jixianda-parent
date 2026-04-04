package com.jixianda.server.task;

import com.jixianda.doc.DishDoc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DishSyncRunner implements CommandLineRunner {

    private static final String FULL_SYNC_SQL =
            "select d.id as dish_id, d.name as dish_name, d.price as dish_price, d.image as dish_image, " +
            "c.name as category_name, ws.warehouse_id as warehouse_id, ws.stock as stock " +
            "from dish d " +
            "left join category c on d.category_id = c.id " +
            "left join warehouse_sku ws on ws.dish_id = d.id";

    private final JdbcTemplate jdbcTemplate;
    private final ElasticsearchOperations elasticsearchOperations;

    @Value("${jixianda.es.sync-on-startup:true}")
    private boolean syncOnStartup;

    @Override
    public void run(String... args) {
        if (!syncOnStartup) {
            log.info("Dish ES full sync is disabled.");
            return;
        }
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(FULL_SYNC_SQL);
            Map<Long, DishDoc> docMap = new LinkedHashMap<>();

            for (Map<String, Object> row : rows) {
                Long dishId = toLong(row.get("dish_id"));
                if (dishId == null) {
                    continue;
                }
                DishDoc dishDoc = docMap.computeIfAbsent(dishId, id ->
                        DishDoc.builder()
                                .id(id)
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

            List<DishDoc> docs = new ArrayList<>(docMap.values());
            IndexOperations indexOperations = elasticsearchOperations.indexOps(DishDoc.class);
            if (indexOperations.exists()) {
                indexOperations.delete();
            }
            indexOperations.create();
            indexOperations.putMapping(indexOperations.createMapping(DishDoc.class));

            if (!docs.isEmpty()) {
                elasticsearchOperations.save(docs);
            }
            log.info("Dish ES full sync finished, total docs: {}", docs.size());
        } catch (Exception e) {
            log.error("Elasticsearch同步失败，暂降级为数据库查询: {}", e.getMessage(), e);
        }
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
