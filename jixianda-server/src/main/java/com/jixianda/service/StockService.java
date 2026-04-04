package com.jixianda.service;

import com.jixianda.dto.StockDTO;
import com.jixianda.result.PageResult;

public interface StockService {

    PageResult pageQuery(Long warehouseId, String name, Integer page, Integer pageSize);

    void updateStock(StockDTO stockDTO);

    void updateStatus(StockDTO stockDTO);
}
