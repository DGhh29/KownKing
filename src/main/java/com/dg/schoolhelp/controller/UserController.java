package com.dg.schoolhelp.controller;

import com.alibaba.fastjson2.JSONObject;
import com.dg.schoolhelp.entity.User;
import com.dg.schoolhelp.service.IUserService;
import com.dg.schoolhelp.utils.RedisUtils;
import com.dg.schoolhelp.utils.RestBean;
import com.dg.schoolhelp.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 *  用户接口
 *
 * @author DG
 * @since 2024-09-23
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private IUserService userService;

    //小程序开发者id
    @Value("${wxapp.AppID}")
    private String AppID;

    //小程序的密钥（重置密钥需要在yaml中修改）
    @Value("${wxapp.AppSecret}")
    private String AppSecret;

    //向微信服务器获取openid模块
    private String haveOpenid(String code){
        String userOpenid = null;
        //1、向微信服务器 使用登录凭证 code 获取 session_key 和 openid
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + AppID +
                "&secret=" + AppSecret +
                "&js_code=" + code +
                "&grant_type=authorization_code";

        // 创建 RestTemplate 对象
        RestTemplate restTemplate = new RestTemplate();
        //获取数据
        ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
        JSONObject jsonObject = JSONObject.parseObject(res.getBody());
        if (jsonObject != null) {
            userOpenid = jsonObject.getString("openid");
        }
        return userOpenid;
    }

    //注册模块
    @PostMapping("/register")
    public RestBean register(@RequestBody Map<String, Object> request) {
        try {
            Map<String, Object> userMap = (Map<String, Object>) request.get("user");
            User user = new User();
            user.setUserName((String) userMap.get("userName"));
            user.setUserAvatar((String) userMap.get("userAvatar"));

            String code = (String) request.get("code");

            String userOpenid = haveOpenid(code);
            user.setUserOpenid(userOpenid);

            userService.SignUp(user);
            return RestBean.success("注册成功");

        } catch (Exception e) {
            return RestBean.failure(500);
        }
    }




    @GetMapping("/login")
    public RestBean login(String code,@RequestHeader(value = "access_token", required = false) String accessToken,@RequestHeader(value = "refresh_token", required = false)String refreshToken) {

        try {
            Map<String, String> tokenMap;
            String userOpenid = haveOpenid(code);
            User user = userService.Login(userOpenid);
            if (user == null) return RestBean.failure(401, "用户不存在");
            if (accessToken != null || refreshToken != null)  {
                redisUtils.delete(accessToken);
                redisUtils.delete(refreshToken);
            }
            tokenMap = tokenUtils.tokenMap();
            redisUtils.set(tokenMap.get("access_token"), String.valueOf(user.getId()), 60 * 60);
            Map<String, String> refresh_token_map = new HashMap<>();
            refresh_token_map.put("access_token", tokenMap.get("access_token"));
            refresh_token_map.put("user",String.valueOf(user.getId()));
            redisUtils.HshSet(tokenMap.get("refresh_token"), refresh_token_map, 60 * 60 * 24);
            tokenMap.put("userId", String.valueOf(user.getId()));
            tokenMap.put("userName", user.getUserName());
            tokenMap.put("userAvatar", user.getUserAvatar());

            return RestBean.success(tokenMap);
        } catch (Exception e) {
            return RestBean.failure(500);
        }
    }
}
