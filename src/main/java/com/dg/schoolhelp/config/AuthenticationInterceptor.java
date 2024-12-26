package com.dg.schoolhelp.config;

import com.dg.schoolhelp.utils.RedisUtils;
import com.dg.schoolhelp.utils.TokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Writer:DG
 * @Data:2024/6/7 10:13
 */

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisUtils redisUtils;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean tip = false;
        //首先从Header中取出Token
        String accessToken = request.getHeader("access_token");
        String refreshToken  = request.getHeader("refresh_token");
        //如果有accessToken，则验证token是否有效
        if (accessToken!= null) {tip = isValidToken(accessToken);
        //如果有accessToken无效，如果有refreshToken，则验证refreshToken是否有效
        if (!tip && refreshToken!= null) if (isValidToken(accessToken,refreshToken)) {
            String newAccessToken = new TokenUtils().accessTokenMap();
            redisUtils.updateHashField(refreshToken, "access_token", newAccessToken);
            response.setHeader("access_token", newAccessToken);
        }
        }

        if (tip) return true;
        return returnFalse(response);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 请求处理完成后的处理
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完成后的清理工作
    }

    private boolean isValidToken(String token) {
        // 验证token是否有效
        return redisUtils.hasKey(token);
    }

    private boolean isValidToken(String accessToken, String refreshToken) {
        // 验证token是否有效
        if (redisUtils.hasKey(refreshToken)) {
            return accessToken.equals(redisUtils.HashGet(refreshToken,"access_token"));
        }
        return false;
    }

    private boolean returnFalse(HttpServletResponse response) throws IOException {

        // 设置响应状态码为401未授权
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 设置响应内容类型为application/json
        response.setContentType("application/json;charset=UTF-8");

        // 构建错误信息
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("status", "401");
        errorData.put("error", "未获取授权");

        // 将错误信息转换为JSON并写入响应
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(errorData);

        // 写入响应
        response.getWriter().write(jsonResponse);
        return false;
    }

}
