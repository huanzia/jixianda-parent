package com.jixianda.controller.user;

import com.github.benmanes.caffeine.cache.Cache;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.jixianda.constant.StatusConstant;
import com.jixianda.doc.DishDoc;
import com.jixianda.result.Result;
import com.jixianda.service.DishService;
import com.jixianda.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

@RestController("userDishController")
@RequestMapping("/user")
@Slf4j
@Api(tags = "C端商品接口")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    @Qualifier("dishCache")
    private Cache<String, Object> dishCache;
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @GetMapping("/dish/list")
    @ApiOperation(value = "按分类查询商品列表", notes = "需要token(authentication)。warehouseId必传，用于按仓库展示商品")
    @SuppressWarnings("unchecked")
    public Result<List<DishVO>> list(@RequestParam(required = false) Long categoryId,
                                     @RequestParam Long warehouseId) {
        if (warehouseId == null) {
            return Result.error("warehouseId is required");
        }
        String key = "dish_" + categoryId + "_" + warehouseId;

        List<DishVO> list = (List<DishVO>) dishCache.getIfPresent(key);
        if (list != null && !list.isEmpty()) {
            log.info("[L2 Cache] hit Caffeine, key={}", key);
            return Result.success(list);
        }

        list = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if (list != null && !list.isEmpty()) {
            dishCache.put(key, list);
            log.info("[L2 Cache] hit Redis, key={}", key);
            return Result.success(list);
        }

        list = dishService.listWithFlavorByWarehouse(categoryId, warehouseId);

        redisTemplate.opsForValue().set(key, list);
        dishCache.put(key, list);
        log.info("[L2 Cache] hit MySQL then write Redis+Caffeine, key={}", key);
        return Result.success(list);
    }

    @GetMapping("/sku/search")
    @ApiOperation(value = "按仓库搜索在售商品", notes = "需要token(authentication)。warehouseId必传，name选填")
    @SentinelResource(value = "skuSearch", blockHandler = "skuSearchBlockHandler")
    public Result<List<DishVO>> searchByWarehouse(@RequestParam(required = false) String name,
                                                   @RequestParam Long warehouseId) {
        if (warehouseId == null) {
            return Result.error("warehouseId is required");
        }
        try {
            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                    .withPageable(PageRequest.of(0, 50))
                    .withQuery(
                            boolQuery()
                                    .must((name == null || name.trim().isEmpty()) ? matchAllQuery() : matchQuery("name", name))
                                    .filter(nestedQuery(
                                            "warehouseStock",
                                            boolQuery()
                                                    .must(termQuery("warehouseStock.warehouseId", warehouseId))
                                                    .must(rangeQuery("warehouseStock.stock").gt(0)),
                                            ScoreMode.None
                                    ))
                    );

            SearchHits<DishDoc> hits = elasticsearchOperations.search(queryBuilder.build(), DishDoc.class);
            List<Long> dishIds = hits.stream()
                    .map(SearchHit::getContent)
                    .map(DishDoc::getId)
                    .collect(Collectors.toList());

            if (dishIds.isEmpty()) {
                return Result.success(new ArrayList<>());
            }

            List<DishVO> result = new ArrayList<>();
            for (Long dishId : dishIds) {
                DishVO dishVO = dishService.getByIdWithFlavor(dishId);
                if (dishVO != null && StatusConstant.ENABLE.equals(dishVO.getStatus())) {
                    result.add(dishVO);
                }
            }
            return Result.success(result);
        } catch (Exception e) {
            log.error("sku search failed, warehouseId={}, name={}, msg={}", warehouseId, name, e.getMessage(), e);
            return Result.error("search service is temporarily unavailable");
        }
    }

    public Result<List<DishVO>> skuSearchBlockHandler(String name, Long warehouseId, BlockException ex) {
        return Result.error("系统繁忙，请稍后再试");
    }
}
