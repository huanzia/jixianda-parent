package com.jixianda.mapper;

import com.jixianda.entity.WarehouseSku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WarehouseSkuMapper {

    void insert(WarehouseSku warehouseSku);

    void update(WarehouseSku warehouseSku);

    WarehouseSku getById(Long id);

    WarehouseSku selectByWarehouseAndDishId(@Param("warehouseId") Long warehouseId,
                                            @Param("dishId") Long dishId);

    int deductStock(@Param("warehouseId") Long warehouseId,
                    @Param("dishId") Long dishId,
                    @Param("count") Integer count);

    int restoreStock(@Param("warehouseId") Long warehouseId,
                     @Param("dishId") Long dishId,
                     @Param("count") Integer count);
    
    int releaseLockStock(@Param("warehouseId") Long warehouseId,
                         @Param("dishId") Long dishId,
                         @Param("count") Integer count);

    int updateStatusById(@Param("id") Long id, @Param("status") Integer status);

    int updateStatusByWarehouseAndDish(@Param("warehouseId") Long warehouseId,
                                       @Param("dishId") Long dishId,
                                       @Param("status") Integer status);
}
