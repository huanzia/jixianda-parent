package com.jixianda.service;

import com.jixianda.dto.UserLoginDTO;
import com.jixianda.entity.User;

import java.time.LocalDateTime;

public interface UserService {

    /**
     * 执行用户微信登录。
     * 负责把前端传来的登录凭证转换成系统内用户，并返回可供网关鉴权使用的登录结果。
     */
    User wxlogin(UserLoginDTO userLoginDTO);

    /**
     * 根据用户 id 查询用户信息。
     * 负责提供基础用户资料，供登录后回显或内部链路查询使用。
     */
    User getById(Long id);

    /**
     * 根据手机号查询用户。
     * 负责支持开发登录或按手机号定位测试用户的场景。
     */
    User getByPhone(String phone);

    /**
     * 获取一个可用用户。
     * 负责给开发环境登录兜底，避免联调时必须手工指定账号。
     */
    User getAnyUser();

    /**
     * 按时间范围统计用户数量。
     * 负责给后台统计页或数据看板提供基础用户增长数据。
     */
    Integer countByTime(LocalDateTime begin, LocalDateTime end);
}
