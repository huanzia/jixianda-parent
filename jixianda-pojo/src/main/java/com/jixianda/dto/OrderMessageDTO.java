package com.jixianda.dto;

import com.jixianda.entity.ShoppingCart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String orderNumber;
    private Boolean seckill;
    private Long dishId;
    private Integer number;
    private Long addressBookId;
    private Long warehouseId;
    private Integer payMethod;
    private BigDecimal totalAmount;
    private String remark;
    private LocalDateTime estimatedDeliveryTime;
    private Integer deliveryStatus;
    private Integer tablewareNumber;
    private Integer tablewareStatus;
    private Integer packAmount;
    private LocalDateTime orderTime;
    private List<ShoppingCart> shoppingCartList;
}