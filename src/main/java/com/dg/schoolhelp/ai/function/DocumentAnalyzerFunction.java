package com.dg.schoolhelp.ai.function;

import com.dg.schoolhelp.utils.WeatherUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;
import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;


/**
 * 通过@Description描述函数的用途，这样ai在多个函数中可以根据描述进行选择。
 */
@Description("某城市的天气怎么样")
@Service
public class DocumentAnalyzerFunction implements Function<DocumentAnalyzerFunction.Request, DocumentAnalyzerFunction.Response> {
    @Autowired
    private WeatherUtils weatherUtils;
    /**
     * 通过@JsonProperty声明属性名称和是否必填
     * 通过@JsonPropertyDescription描述属性的用途，这样ai可以提取出符合参数描述的内容。
     */
    @Data
    public static class Request {
        @JsonProperty(required = true, value = "city")
        @JsonPropertyDescription(value = "城市名称")
        String city;
    }

    public record Response(String result) {
    }

    @SneakyThrows
    @Override
    public Response apply(Request request) {

        Map<String,String> map = weatherUtils.cityRequest(request.city);
        String result = "";
        System.out.println(map);
        if (map!=null) result = "天气情况为"+map.get("weather")+"温度为"+map.get("temperature")+"°C";
        else result = "当前没有查到准确的信息，需要准确的城市名称";
        return new Response(result);
    }


}
