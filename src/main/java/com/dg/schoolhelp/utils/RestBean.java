package com.dg.schoolhelp.utils;

import lombok.Data;

/**
 * @Writer:DG
 * @Data:2024/6/26 14:15
 * @explain:
 */

@Data
public class RestBean<T> {
    private int status;
    private boolean message;
    private T data;

    public RestBean(int status, boolean message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> RestBean<T> success(){ return new RestBean<>(200,true,null);}

    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(200,true, data);
    }

    public static <T> RestBean<T> failure(int status){ return new RestBean<>(status,false, null); }

    public static <T> RestBean<T> failure(int status, T data){
        return new RestBean<>(status,false, data);
    }
}

