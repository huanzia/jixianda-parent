package com.jixianda.service;/*
 *@program:sky-take_out
 *@author: huanzi
 *@Time: 2026/2/2  17:16
 *
 */

import com.jixianda.dto.ShoppingCartDTO;
import com.jixianda.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查询购物车
     * @param userId
     * @return
     */
    List<ShoppingCart> list(Long userId);

    /**
     * 删除购物车中一个商品
     * @param shoppingCartDTO
     */
    void subshoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 清空购物车
     * @param userId
     */
    void cleanShoppingCart(Long userId);

}
