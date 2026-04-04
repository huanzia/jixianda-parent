package com.jixianda.mapper;

import com.jixianda.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author huanzi
 * @date 2026/1/8 17:08
 */
@Mapper
public interface SetmealDishMapper {
    /**
     * 根据id查询菜品关联套餐信息
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(@Param("dishIds") List<Long> dishIds);

    /**
     * 批量插入套餐的菜品
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);


    /**
     * 批量删除套餐关联的菜品
     * @param setmeal_ids
     */
    void deleteBySetmealIds(List<Long> setmeal_ids);


    /**
     * 通过id查询套餐关联的菜品
     * @param id
     * @return
     */
    @Select("SELECT  * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getSetmealDishesById(Long id);



    /**
     * 根据套餐id删除套餐和菜品的关联关系
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);


    /**
     * 根据套餐id查询套餐和菜品的关联关系
     * @param setmealId
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long setmealId);
}
