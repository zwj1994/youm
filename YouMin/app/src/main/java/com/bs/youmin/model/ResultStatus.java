package com.bs.youmin.model;

/**
 * 自定义请求状态码
 * @author ScienJus
 * @date 2015/7/15.
 */
public enum ResultStatus {
    SUCCESS(100, "成功"),
    FAIL(500, "失败"),
    NO_KEY_ERROR(-1000, "密钥不存在"),
    USERNAME_OR_PASSWORD_ERROR(-1001, "用户名或密码错误"),
    USER_NOT_FOUND(-1002, "用户不存在"),
    USER_NOT_LOGIN(-1003, "用户未登录"),
    USER_NOT_CAN_LOGIN(-1004, "账号禁用"),
    SYS_ERROR(-1005, "系统异常");

    /**
     * 返回码
     */
    private int code;

    /**
     * 返回结果描述
     */
    private String message;

    ResultStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
