package com.jixianda.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class StockVO implements Serializable {

    private Long warehouseSkuId;

    private Long dishId;

    private String dishName;

    private String image;

    private String categoryName;

    private Integer currentStock;

    private Integer status;
}
