package com.jixianda.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class StockDTO implements Serializable {

    private Long id;

    private Long warehouseId;

    private Long dishId;

    private Integer stock;

    private Integer status;
}
