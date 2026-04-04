package com.jixianda.service.impl;

import com.jixianda.dto.WarehousePageQueryDTO;
import com.jixianda.entity.Warehouse;
import com.jixianda.mapper.WarehouseMapper;
import com.jixianda.result.PageResult;
import com.jixianda.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private WarehouseMapper warehouseMapper;

    @Override
    public void save(Warehouse warehouse) {
        if (warehouse.getStatus() == null) {
            warehouse.setStatus(1);
        }
        warehouseMapper.insert(warehouse);
    }

    @Override
    public void update(Warehouse warehouse) {
        warehouseMapper.update(warehouse);
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        warehouseMapper.deleteBatch(ids);
    }

    @Override
    public PageResult pageQuery(WarehousePageQueryDTO warehousePageQueryDTO) {
        // 货仓分页查询：为多前置仓运营后台提供列表能力
        int page = warehousePageQueryDTO.getPage() <= 0 ? 1 : warehousePageQueryDTO.getPage();
        int pageSize = warehousePageQueryDTO.getPageSize() <= 0 ? 10 : warehousePageQueryDTO.getPageSize();
        int offset = (page - 1) * pageSize;

        long total = warehouseMapper.count(warehousePageQueryDTO.getName());
        List<Warehouse> records = warehouseMapper.pageQuery(warehousePageQueryDTO.getName(), offset, pageSize);
        return new PageResult(total, records);
    }

    @Override
    public Warehouse getById(Long id) {
        return warehouseMapper.getById(id);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        warehouseMapper.updateStatus(status, id);
    }

    @Override
    public List<Warehouse> list(Integer status) {
        // 多仓业务下按状态筛选货仓（1 启用），供前端/调度侧选择可用仓
        return warehouseMapper.list(status);
    }
}
