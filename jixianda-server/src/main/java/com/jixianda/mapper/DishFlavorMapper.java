package com.jixianda.mapper;

import com.jixianda.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 插入口味表
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据dish_id删除关联的口味
     * @param dish_id
     */
    @Delete("delete from dish_flavor where dish_id = #{dish_id}")
    void deleteBydishId(Long dish_id);

    /**
     * 根据dish_ids集合批量删除关联的口味
     * @param dish_ids
     */
    void deleteBydishIds(List<Long> dish_ids);

    /**
     * 根据菜品id(dish_id)查询口味数据
     * @param dishId
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getByDishId(Long dishId);
}
