package com.jixianda.server.task;

import com.jixianda.constant.StatusConstant;
import com.jixianda.entity.Dish;
import com.jixianda.mapper.DishMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Slf4j
public class StockPreheatRunner implements CommandLineRunner {

    private static final String DISH_STOCK_KEY_PREFIX = "stock:dish:";

    private final DishMapper dishMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final JdbcTemplate jdbcTemplate;

    public StockPreheatRunner(DishMapper dishMapper, StringRedisTemplate stringRedisTemplate, JdbcTemplate jdbcTemplate) {
        this.dishMapper = dishMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        Dish query = new Dish();
        query.setStatus(StatusConstant.ENABLE);
        List<Dish> dishList = dishMapper.list(query);

        int count = 0;
        for (Dish dish : dishList) {
            if (dish == null || dish.getId() == null) {
                continue;
            }
            Integer stock = jdbcTemplate.queryForObject(
                    "select stock from dish where id = ?",
                    Integer.class,
                    dish.getId()
            );
            if (stock == null) {
                stock = 0;
            }
            String key = DISH_STOCK_KEY_PREFIX + dish.getId();
            stringRedisTemplate.opsForValue().set(key, String.valueOf(stock));
            count++;
        }

        log.info("极鲜达系统启动：已成功将 {} 个生鲜商品的库存预热至 Redis！", count);
    }
}
