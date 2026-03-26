package com.jixianda.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jixianda.constant.MessageConstant;
import com.jixianda.constant.StatusConstant;
import com.jixianda.dto.DishDTO;
import com.jixianda.dto.DishPageQueryDTO;
import com.jixianda.entity.Dish;
import com.jixianda.entity.DishFlavor;
import com.jixianda.exception.DeletionNotAllowedException;
import com.jixianda.mapper.DishFlavorMapper;
import com.jixianda.mapper.DishMapper;
import com.jixianda.mapper.SetmealDishMapper;
import com.jixianda.result.PageResult;
import com.jixianda.service.DishService;
import com.jixianda.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜品业务实现类。
 * 这个类属于 service 实现层，负责把菜品基础信息、口味信息和仓维度可见性组合起来，
 * 供 server 端首页、分类页和管理端维护接口使用。
 */
@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品和口味。
     * 这里不是只插入 dish 表，因为前台展示的菜品往往还要带上多规格口味，
     * 所以要把基础菜品和口味明细一起保存，保证后续下单时能完整还原商品信息。
     */
    @Override
    @Transactional
    public void saveWithFlaver(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        if (dish.getStatus() == null) {
            dish.setStatus(StatusConstant.ENABLE);
        }
        dishMapper.insert(dish);

        List<DishFlavor> flavors = dishDTO.getFlavors();
        Long dishId = dish.getId();
        if (flavors == null || flavors.isEmpty()) {
            log.info("[业务安全检查] 菜品 {} 未配置口味，已跳过口味表操作。", dishId);
            return;
        }

        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }
        dishFlavorMapper.insertBatch(flavors);
    }

    /**
     * 菜品分页查询。
     * 这个方法服务于管理端分页列表，分页是为了避免一次性加载过多菜品影响后台操作体验。
     */
    @Override
    public PageResult PageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除菜品。
     * 删除前先检查上架状态和套餐关联关系，是因为已上架菜品和被套餐引用的菜品都不能直接删除，
     * 这样可以避免前台展示和套餐配置出现悬空数据。
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish != null && StatusConstant.ENABLE.equals(dish.getStatus())) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && !setmealIds.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteBydishIds(ids);
    }

    /**
     * 根据 id 查询菜品及口味。
     * 编辑回显，前端需要一次拿到基础菜品和口味列表，才能完整展示编辑表单。
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.getById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(loadDishFlavors(id));
        return dishVO;
    }

    /**
     * 修改菜品和口味。
     * 先删后改是为了让旧口味配置失效，避免更新后残留旧口味影响下单展示。
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        dishFlavorMapper.deleteBydishId(dishDTO.getId());

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors == null || flavors.isEmpty()) {
            log.info("[业务安全检查] 菜品 {} 未配置口味，已跳过口味表操作。", dishDTO.getId());
            return;
        }

        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishDTO.getId());
        }
        dishFlavorMapper.insertBatch(flavors);
    }

    /**
     * 启售或停售菜品。
     * 通过状态控制菜品是否能在前台出现，避免未准备好的商品被用户下单。
     */
    @Override
    @Transactional
    public void startOrStop(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.update(dish);
    }

    /**
     * 根据分类 id 查询菜品。
     * 这个接口更多服务于基础菜品筛选，默认只返回启售中的菜品，确保前台看到的是可下单商品。
     */
    @Override
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }

    /**
     * 条件查询菜品及口味。
     * 这里不仅查 dish，还会把口味一起带出来，是因为前台首页和分类页展示的不是单独菜品，而是完整商品卡片。
     */
    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);
        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish entity : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(entity, dishVO);
            dishVO.setFlavors(loadDishFlavors(entity.getId()));
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }


    /**
     * 因为首页通常不是一次把所有商品全查出来，而是有“分类切换”的
     * @param categoryId
     * @param warehouseId
     * @return
     */
    @Override
    public List<DishVO> listWithFlavorByWarehouse(Long categoryId, Long warehouseId) {
        // 首页商品不是“所有菜品都展示”，而是要先按仓过滤，再展示当前仓可售的商品集合。
        List<Dish> dishList = dishMapper.listByCategoryAndWarehouse(categoryId, warehouseId);
        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish entity : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(entity, dishVO);
            dishVO.setFlavors(loadDishFlavors(entity.getId()));
            dishVOList.add(dishVO);
        }
        return dishVOList;
    }

    private List<DishFlavor> loadDishFlavors(Long dishId) {
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(dishId);
        return flavors == null ? new ArrayList<>() : flavors;
    }
}
