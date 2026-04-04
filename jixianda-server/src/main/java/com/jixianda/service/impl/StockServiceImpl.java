package com.jixianda.service.impl;

import com.jixianda.dto.StockDTO;
import com.jixianda.entity.WarehouseSku;
import com.jixianda.exception.BaseException;
import com.jixianda.mapper.StockMapper;
import com.jixianda.mapper.WarehouseSkuMapper;
import com.jixianda.result.PageResult;
import com.jixianda.service.StockService;
import com.jixianda.vo.StockVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private WarehouseSkuMapper warehouseSkuMapper;

    @Override
    public PageResult pageQuery(Long warehouseId, String name, Integer page, Integer pageSize) {
        int currentPage = (page == null || page <= 0) ? 1 : page;
        int currentPageSize = (pageSize == null || pageSize <= 0) ? 10 : pageSize;
        int offset = (currentPage - 1) * currentPageSize;

        long total = stockMapper.count(warehouseId, name);
        List<StockVO> records = stockMapper.pageQuery(warehouseId, name, offset, currentPageSize);
        return new PageResult(total, records);
    }

    @Override
    public void updateStock(StockDTO stockDTO) {
        WarehouseSku warehouseSku = warehouseSkuMapper.selectByWarehouseAndDishId(
                stockDTO.getWarehouseId(), stockDTO.getDishId());
        int stock = stockDTO.getStock() == null ? 0 : stockDTO.getStock();

        if (warehouseSku != null) {
            WarehouseSku update = new WarehouseSku();
            update.setId(warehouseSku.getId());
            update.setStock(stock);
            warehouseSkuMapper.update(update);
            return;
        }

        WarehouseSku insert = new WarehouseSku();
        insert.setWarehouseId(stockDTO.getWarehouseId());
        insert.setDishId(stockDTO.getDishId());
        insert.setStock(stock);
        insert.setLockStock(0);
        insert.setStatus(1);
        warehouseSkuMapper.insert(insert);
    }

    @Override
    public void updateStatus(StockDTO stockDTO) {
        if (stockDTO.getStatus() == null) {
            throw new BaseException("status cannot be null");
        }

        int affected;
        if (stockDTO.getId() != null) {
            affected = warehouseSkuMapper.updateStatusById(stockDTO.getId(), stockDTO.getStatus());
        } else if (stockDTO.getWarehouseId() != null && stockDTO.getDishId() != null) {
            affected = warehouseSkuMapper.updateStatusByWarehouseAndDish(
                    stockDTO.getWarehouseId(), stockDTO.getDishId(), stockDTO.getStatus());
        } else {
            throw new BaseException("missing identifier for status update");
        }

        if (affected <= 0) {
            throw new BaseException("warehouse_sku record not found");
        }
    }
}