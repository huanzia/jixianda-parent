package com.jixianda.mapper;

import com.github.pagehelper.Page;
import com.jixianda.dto.GoodsSalesDTO;
import com.jixianda.dto.OrdersPageQueryDTO;
import com.jixianda.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);



    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    int updateStatusByNumberAndStatus(@Param("number") String number,
                                      @Param("fromStatus") Integer fromStatus,
                                      @Param("toStatus") Integer toStatus,
                                      @Param("payStatus") Integer payStatus,
                                      @Param("checkoutTime") LocalDateTime checkoutTime);
    /**
     * 分页条件查询并按下单时间排序
     * @param ordersPageQueryDTO
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单
     * @param id
     */
    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);

    /**
     * 根据状态统计订单数量
     * @param status
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    /**
     * 获取超时15分钟的订单
     * @param status
     * @param orderTime
     * @return
     */
    @Select("SELECT * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeOut(Integer status, LocalDateTime orderTime);



    /**
     * 查询一天的订单总额
     * @param map
     * @return
     */
    Double sumByMap(Map map);

    /**
     * 查询指定时间区间内的订单数量
     * @param map
     */
    Integer countByMap(Map map);

    /**
     * 查询销量排名top10
     * @param begin
     * @param end
     * @return
     */
    List<GoodsSalesDTO> getSalesTop(LocalDateTime begin, LocalDateTime end);
}
