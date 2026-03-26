package com.jixianda.gateway.test;

import com.jixianda.constant.JwtClaimsConstant;
import com.jixianda.utils.JwtUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class GatewayAuthTest {

    private static final String URL = "http://localhost:81/user/dish/list?categoryId=1";
    private static final String JWT_SECRET = "itheima";
    private static final long JWT_TTL = 7200000L;

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        int statusA = request(restTemplate, null);
        assertStatus("测试 A (无 Token)", statusA, 401);

        int statusB = request(restTemplate, "bad-token-content");
        assertStatus("测试 B (伪造 Token)", statusB, 401);

        String legalToken = generateLegalToken();
        int statusC = request(restTemplate, legalToken);
        assertStatus("测试 C (合法 Token)", statusC, 200);
    }

    private static int request(RestTemplate restTemplate, String token) {
        HttpHeaders headers = new HttpHeaders();
        if (token != null) {
            headers.add("authentication", token);
        }
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, entity, String.class);
            return response.getStatusCodeValue();
        } catch (Exception e) {
            String message = e.getMessage();
            if (message != null && message.contains("401")) {
                return 401;
            }
            throw e;
        }
    }

    private static String generateLegalToken() {
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put(JwtClaimsConstant.USER_ID, 1L);
        return JwtUtil.createJWT(JWT_SECRET, JWT_TTL, claims);
    }

    private static void assertStatus(String testName, int actual, int expected) {
        if (actual != expected) {
            throw new RuntimeException(testName + " 失败，期望状态码: " + expected + "，实际: " + actual);
        }
        System.out.println(testName + " 通过，状态码: " + actual);
    }
}