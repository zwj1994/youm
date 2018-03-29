package com.bs.youmin.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/3/17 0017.
 */

public class User implements Serializable {

    public User() {
    }

    public User(String username, String password, String publicKey, String key, String identifier) {
        this.username = username;
        this.password = password;
        this.publicKey = publicKey;
        this.key = key;
        this.identifier = identifier;
    }

    private String username;

    private String password;

    private String publicKey;

    private String key;

    private String identifier;

    private String token;

    private String headerImg;

    private String sign;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", key='" + key + '\'' +
                ", identifier='" + identifier + '\'' +
                ", token='" + token + '\'' +
                ", headerImg='" + headerImg + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
