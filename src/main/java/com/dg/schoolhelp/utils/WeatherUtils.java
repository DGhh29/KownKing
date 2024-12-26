package com.dg.schoolhelp.utils;

import com.alibaba.fastjson2.JSONObject;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class WeatherUtils {

    public Map<String,String> cityRequest(String city) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        Request request = new Request.Builder()
                .url("https://api.seniverse.com/v3/weather/now.json?key=S1TgCN-pVDwbpefPb&location=" + city + "&language=zh-Hans&unit=c")
                .get() // 使用 GET 方法，GET 请求不需要请求体
                .build();

        Response response = client.newCall(request).execute();

        // 处理返回的响应
        if (response.isSuccessful()) {
            String responseData = null;
            if (response.body() != null) {
                responseData = response.body().string();
            }

            // 确保返回的响应数据非空
            if (responseData != null) {
                JSONObject jsonObject = JSONObject.parseObject(responseData);

                // 提取并打印 'now' 部分
                if (jsonObject != null) {
                    JSONObject now = jsonObject.getJSONArray("results")
                            .getJSONObject(0)
                            .getJSONObject("now");

                    // 获取天气描述和温度
                    String weatherText = now.getString("text");
                    String temperature = now.getString("temperature");

                    Map<String,String> map = new HashMap<>();
                    map.put("weather",weatherText);
                    map.put("temperature",temperature);
                    return map;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}
