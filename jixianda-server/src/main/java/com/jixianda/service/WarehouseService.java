package com.jixianda.service;

import com.jixianda.dto.WarehousePageQueryDTO;
import com.jixianda.entity.Warehouse;
import com.jixianda.result.PageResult;

import java.util.List;

public interface WarehouseService {

    void save(Warehouse warehouse);

    void update(Warehouse warehouse);

    void deleteBatch(List<Long> ids);

    PageResult pageQuery(WarehousePageQueryDTO warehousePageQueryDTO);

    Warehouse getById(Long id);

    void startOrStop(Integer status, Long id);

    List<Warehouse> list(Integer status);
}
