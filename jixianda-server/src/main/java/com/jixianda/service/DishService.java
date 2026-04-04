package com.jixianda.service;

import com.jixianda.dto.DishDTO;
import com.jixianda.dto.DishPageQueryDTO;
import com.jixianda.entity.Dish;
import com.jixianda.result.PageResult;
import com.jixianda.vo.DishVO;

import java.util.List;

/**
 * @author huanzi
 * @date 2025/12/20 16:24
 */
public interface DishService {


    /**
     * 新增加菜品和口味表
     * @param dishDTO
     */
    public void saveWithFlaver(DishDTO dishDTO);


    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult PageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 菜品批量删除
     * @param ids
     */
    void deleteBatch(List<Long> ids);


    /**
     * 根据id查询菜品和对应的口味数据
     * @param id
     * @return
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 修改菜品
     * @param dishDTO
     */
    void updateWithFlavor(DishDTO dishDTO);

    /**
     * 菜品启售/停售
     */
    void startOrStop(Integer status, Long id);


//    /**
//     * 根据分类名称查询菜品信息
//     * @param categoryName
//     * @return
//     */
//    List<Dish> list(String categoryName);
    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    List<Dish> list(Long categoryId);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);

    List<DishVO> listWithFlavorByWarehouse(Long categoryId, Long warehouseId);
}
