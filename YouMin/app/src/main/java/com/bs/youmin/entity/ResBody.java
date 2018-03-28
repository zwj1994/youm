package com.bs.youmin.entity;

/**
 * Created by Mrzhu on 2018/3/28.
 */

public class ResBody<T> {

    private String code;

    private T result;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
