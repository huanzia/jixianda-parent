package com.jixianda.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseSku implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long warehouseId;

    private Long dishId;

    private Integer stock;

    private Integer lockStock;

    /** 0: 停售, 1: 起售 */
    private Integer status;
}