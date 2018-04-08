package com.bs.youmin.model;

/**
 * Token的Model类，可以增加字段提高安全性，例如时间戳、url签名
 * @author zwj
 * @date 2018/3/28
 */
public class TokenModel {

    //用户id
    private String userId;

    private String uId;

    //随机生成的uuid
    private String token;

    private String headerImg;

    private String sign;

    public TokenModel(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getHeaderImg() {
        return headerImg;
    }

    public void setHeaderImg(String headerImg) {
        this.headerImg = headerImg;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
