package com.jixianda.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * dish
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dish implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // dish name
    private String name;

    // dish category id
    private Long categoryId;

    // dish price
    private BigDecimal price;

    // image
    private String image;

    // description
    private String description;

    // 0 disabled, 1 enabled
    private Integer status;

    // @Deprecated only for virtual total-stock display; use WarehouseSku for trading.
    private Integer stock;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;
}
