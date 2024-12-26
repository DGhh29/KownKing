package com.dg.schoolhelp.utils;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class TokenUtils {

    public Map<String,String> tokenMap(){
        Map<String,String> map=new HashMap<>();
        UUID access_token = UUID.randomUUID();
        UUID refresh_token = UUID.randomUUID();
        map.put("access_token",access_token.toString());
        map.put("refresh_token",refresh_token.toString());
        return map;
    }

    public Map<String,String> tokenMap(String access_token,String refresh_token){
        Map<String,String> map=new HashMap<>();
        map.put("access_token",access_token);
        map.put("refresh_token",refresh_token);
        return map;
    }


    public String accessTokenMap(){return UUID.randomUUID().toString();}

}
