package com.jixianda.utils;

import com.jixianda.constant.JwtClaimsConstant;
import com.jixianda.properties.JwtProperties;

import java.util.HashMap;
import java.util.Map;

public class TokenGeneratorTest {

    public static void main(String[] args) {
        JwtProperties jwtProperties = new JwtProperties();

        jwtProperties.setAdminSecretKey("change_me_admin_jwt_secret");
        jwtProperties.setAdminTtl(7200000L);
        jwtProperties.setAdminTokenName("token");

        jwtProperties.setUserSecretKey("change_me_user_jwt_secret");
        jwtProperties.setUserTtl(7200000L);
        jwtProperties.setUserTokenName("authentication");

        Map<String, Object> adminClaims = new HashMap<String, Object>();
        // 管理端载荷：empId 表示后台登录员工主键，用于权限与身份识别
        adminClaims.put(JwtClaimsConstant.EMP_ID, 1L);
        String adminToken = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                adminClaims
        );
        System.out.println("[管理端测试 Token] (Header: " + jwtProperties.getAdminTokenName() + ") -> " + adminToken);

        Map<String, Object> userClaims = new HashMap<String, Object>();
        // C 端载荷：userId 表示前台用户主键，用于用户侧接口鉴权
        userClaims.put(JwtClaimsConstant.USER_ID, 1L);
        String userToken = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                userClaims
        );
        System.out.println("[C 端测试 Token] (Header: " + jwtProperties.getUserTokenName() + ") -> " + userToken);
    }
}
