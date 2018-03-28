package com.bs.youmin.entity;

import java.util.Date;

/**
 * @Author:zhuwj
 * @Description:
 * @Date:Created in 11:45 2018/3/23
 */
public class YUser {

    private String uId;
    private String uName;
    private String uAccount;
    private String uPwd;
    private Integer uState;
    private Date uCreateDate;
    private String uHeadImg;
    private String uSign;

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuAccount() {
        return uAccount;
    }

    public void setuAccount(String uAccount) {
        this.uAccount = uAccount;
    }

    public String getuPwd() {
        return uPwd;
    }

    public void setuPwd(String uPwd) {
        this.uPwd = uPwd;
    }

    public Integer getuState() {
        return uState;
    }

    public void setuState(Integer uState) {
        this.uState = uState;
    }

    public Date getuCreateDate() {
        return uCreateDate;
    }

    public void setuCreateDate(Date uCreateDate) {
        this.uCreateDate = uCreateDate;
    }

    public String getuHeadImg() {
        return uHeadImg;
    }

    public void setuHeadImg(String uHeadImg) {
        this.uHeadImg = uHeadImg;
    }

    public String getuSign() {
        return uSign;
    }

    public void setuSign(String uSign) {
        this.uSign = uSign;
    }

    @Override
    public String toString() {
        return "YUser{" +
                "uId='" + uId + '\'' +
                ", uName='" + uName + '\'' +
                ", uAccount='" + uAccount + '\'' +
                ", uPwd='" + uPwd + '\'' +
                ", uState=" + uState +
                ", uCreateDate=" + uCreateDate +
                ", uHeadImg='" + uHeadImg + '\'' +
                ", uSign='" + uSign + '\'' +
                '}';
    }
}
