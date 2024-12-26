package com.dg.schoolhelp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Writer:DG
 * @Data:2024/6/7 10:07
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // 使用@Autowired注解自动注入一个名为authenticationInterceptor的拦截器实例
    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        // 将authenticationInterceptor添加到拦截器链中
//        registry.addInterceptor(authenticationInterceptor)
//                // 设置拦截所有的URL请求
//                .addPathPatterns("/**")
//                // 排除掉以 /api/ 开头的URL请求
//                .excludePathPatterns("/api/user/**")
//                .excludePathPatterns("/api/img/**");
//    }


    //cors跨域
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("*") // 设置允许的来源
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("*")
//                .allowCredentials(false);
//    }
}