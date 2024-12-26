package com.dg.schoolhelp.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    public String getkey(String momo){
        return stringRedisTemplate.opsForValue().get(momo);
    }



    public Boolean delete(String momo){
        return stringRedisTemplate.delete(momo);
    }

    public void set(String momo, String momoVlaue, long timeout){ stringRedisTemplate.opsForValue().set(momo, momoVlaue, timeout, TimeUnit.SECONDS); }

    public void set(String momo, String momoVlaue){
        stringRedisTemplate.opsForValue().set(momo, momoVlaue);
    }

    public String HashGet(String momo, String momoKey) {
        // 从 Hash 中获取指定字段的值
        return (String) stringRedisTemplate.opsForHash().get(momo, momoKey);
    }

    public void HshSet(String momo, Map<String, String> momomap, long timeout){
        // 批量存储 HashMap 到 Redis
        stringRedisTemplate.opsForHash().putAll(momo, momomap);
        // 设置 Hash 的过期时间
        stringRedisTemplate.expire(momo, timeout, TimeUnit.SECONDS);
    }

    public void updateHashField(String momo, String momoKey, String momoValue) {
        stringRedisTemplate.opsForHash().put(momo, momoKey, momoValue);
    }

    public boolean hasKey(String momo){return Boolean.TRUE.equals(stringRedisTemplate.hasKey(momo));}
}
