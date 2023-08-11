package com.snrt.helloworld.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
    public static void main(String[] args) {

        try {
            URL url = new URL("http://api.caonm.net/api/qqyy/163?key=你的接口密钥，登录控制台后在密钥管理页面申请");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方式
            connection.setRequestMethod("GET");
            connection.connect();

            // 获取响应码
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    // 读取到的内容给line变量
                    System.out.println(line);
                }
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
