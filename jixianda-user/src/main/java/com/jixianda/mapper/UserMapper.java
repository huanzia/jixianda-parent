package com.jixianda.mapper;

import com.jixianda.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper
/**
 * 用户数据访问层。
 * 这个 mapper 服务于用户登录、用户基础资料查询和统计业务，
 * 并与 XML / 注解 SQL 配合把用户表数据提供给 service 层使用。
 */
public interface UserMapper {

    /**
     * 按 openid 查询用户。
     * 这是微信登录链路里判断“老用户还是新用户”的关键查询。
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 插入用户基础记录。
     * 由 service 在微信登录成功后调用，用于创建新的系统用户。
     */
    void insert(User user);

    /**
     * 按 id 查询用户基础信息。
     * 主要供登录后回显和内部业务链路使用。
     */
    @Select("select * from user where id = #{userId}")
    User getById(Long userId);

    /**
     * 按手机号查询用户。
     * 主要用于开发登录和测试用户定位场景。
     */
    @Select("select * from user where phone = #{phone} limit 1")
    User getByPhone(String phone);

    /**
     * 取一个最早创建的用户作为兜底样本。
     * 这是开发登录的辅助能力，方便本地联调时快速生成 token。
     */
    @Select("select * from user order by id asc limit 1")
    User getAnyUser();

    /**
     * 按时间范围统计用户数量。
     * 这个查询给统计页面使用，反映某个时间段内的用户增长情况。
     */
    @Select("<script>" +
            "select count(id) from user " +
            "<where>" +
            "  <if test='begin != null'> and create_time <![CDATA[ >= ]]> #{begin} </if>" +
            "  <if test='end != null'> and create_time <![CDATA[ <= ]]> #{end} </if>" +
            "</where>" +
            "</script>")
    Integer countByTime(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);

    /**
     * 按 Map 条件统计用户数量。
     * 这是更通用的统计入口，供 XML 里更复杂的条件拼接使用。
     */
    Integer countByMap(Map map);
}
