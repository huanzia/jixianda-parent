package com.jixianda.server.test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SeckillPressureTest {

    private static final String TARGET_URL = "http://localhost:58088/user/order/seckill";
    private static final String AUTH_TOKEN = "YOUR_TOKEN_HERE";
    private static final String REQUEST_BODY = "{\"dishId\":70,\"number\":1,\"addressBookId\":2}";
    private static final int THREAD_COUNT = 1;

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch readyLatch = new CountDownLatch(THREAD_COUNT);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int idx = i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    readyLatch.countDown();
                    try {
                        startLatch.await();
                        String response = doPost(TARGET_URL, REQUEST_BODY, AUTH_TOKEN);
                        System.out.println("thread-" + idx + " response=" + response);
                    } catch (Exception e) {
                        System.out.println("thread-" + idx + " error=" + e.getMessage());
                    } finally {
                        endLatch.countDown();
                    }
                }
            });
        }

        readyLatch.await();
        long start = System.currentTimeMillis();
        startLatch.countDown();
        endLatch.await();
        long end = System.currentTimeMillis();

        System.out.println("totalRequests=" + THREAD_COUNT + ", totalCostMs=" + (end - start));
        executorService.shutdown();
    }

    private static String doPost(String url, String jsonBody, String token) throws Exception {
        HttpURLConnection conn = null;
        try {
            URL target = new URL(url);
            conn = (HttpURLConnection) target.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("authentication", token);

            byte[] body = jsonBody.getBytes(StandardCharsets.UTF_8);
            OutputStream os = null;
            try {
                os = conn.getOutputStream();
                os.write(body);
                os.flush();
            } finally {
                if (os != null) {
                    os.close();
                }
            }

            int code = conn.getResponseCode();
            InputStream is = null;
            try {
                is = code >= 400 ? conn.getErrorStream() : conn.getInputStream();
                String resp = readAll(is);
                return "status=" + code + " body=" + resp;
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static String readAll(InputStream is) throws Exception {
        if (is == null) {
            return "";
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        return new String(bos.toByteArray(), StandardCharsets.UTF_8);
    }
}