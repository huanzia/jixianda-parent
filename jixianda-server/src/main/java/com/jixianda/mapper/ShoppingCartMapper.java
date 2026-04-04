package com.jixianda.mapper;

import com.jixianda.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    List<ShoppingCart> list(ShoppingCart shoppingCart);

    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart cart);

    @Insert("insert into shopping_cart (name, image, dish_id, setmeal_id, dish_flavor, number, amount, create_time, user_id, warehouse_id) " +
            "values (#{name}, #{image}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime}, #{userId}, #{warehouseId})")
    void insert(ShoppingCart shoppingCart);

    @Select("select * from shopping_cart where user_id = #{userId}")
    List<ShoppingCart> getListByUserId(Long userId);

    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(Long id);

    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteAllByUserId(Long userId);

    void insertBatch(List<ShoppingCart> shoppingCartList);
}