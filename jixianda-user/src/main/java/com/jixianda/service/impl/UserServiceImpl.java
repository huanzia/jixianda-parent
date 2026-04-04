package com.jixianda.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jixianda.constant.MessageConstant;
import com.jixianda.dto.UserLoginDTO;
import com.jixianda.entity.User;
import com.jixianda.exception.LoginFailedException;
import com.jixianda.mapper.UserMapper;
import com.jixianda.properties.WeChatProperties;
import com.jixianda.service.UserService;
import com.jixianda.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@Slf4j
/**
 * 用户业务实现类。
 * 这个类属于 service 实现层，负责把微信登录、用户基础资料查询和用户统计串成完整业务流程，
 * 并为 controller 提供可直接返回给前端的数据。
 */
public class UserServiceImpl implements UserService {

    // 微信登录接口地址，user 服务通过它把 code 换成 openid。
    private static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    // 微信配置保存 appid 和 secret，用于拼接微信登录请求参数。
    private WeChatProperties weChatProperties;
    @Autowired
    // 用户数据持久化交给 mapper，service 负责登录流程和业务判断。
    private UserMapper userMapper;

    @Override
    /**
     * 执行微信登录并返回系统内用户。
     * 这个方法承担从微信身份到本地用户的转换，是用户登录链路的核心入口。
     */
    public User wxlogin(UserLoginDTO userLoginDTO) {
        String openid = getOpenid(userLoginDTO.getCode());
        if (openid == null) {
            throw new LoginFailedException(MessageConstant.USER_NOT_LOGIN);
        }

        // 先按 openid 找老用户，能找到就复用，找不到再创建新用户，保持登录幂等。
        User user = userMapper.getByOpenid(openid);
        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        return user;
    }

    @Override
    /**
     * 根据 id 查询用户基础资料。
     * 主要服务于登录后回显、内部服务查询和开发调试等场景。
     */
    public User getById(Long id) {
        return userMapper.getById(id);
    }

    @Override
    /**
     * 根据手机号查询用户。
     * 主要用于开发登录、测试账号定位和后台辅助查询。
     */
    public User getByPhone(String phone) {
        return userMapper.getByPhone(phone);
    }

    @Override
    /**
     * 取一个可用用户。
     * 这个兜底逻辑专门给开发登录使用，保证没有指定账号时也能快速进入系统。
     */
    public User getAnyUser() {
        return userMapper.getAnyUser();
    }

    @Override
    /**
     * 按时间范围统计用户数量。
     * 这个能力通常给管理端统计页使用，用于观察用户增长趋势。
     */
    public Integer countByTime(LocalDateTime begin, LocalDateTime end) {
        return userMapper.countByTime(begin, end);
    }

    /**
     * 调微信接口换取 openid。
     * 这个私有方法把 code、appid 和 secret 组装成请求参数，封装微信登录细节，避免业务方法里堆满协议处理代码。
     */
    private String getOpenid(String code) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("appid", weChatProperties.getAppid());
        params.put("secret", weChatProperties.getSecret());
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");

        String json = HttpClientUtil.doGet(WX_LOGIN_URL, params);
        JSONObject jsonObject = JSON.parseObject(json);
        return jsonObject.getString("openid");
    }
}
