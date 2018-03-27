package com.bs.youmin.entity;

import java.util.Date;

/**
 * @Author:zhuwj
 * @Description: 相册
 * @Date:Created in 11:13 2018/3/23
 */
public class YAlbum {

    private String aId;
    private String aName;
    private String aCover;
    private String aDescribe;
    private Integer aPrivacy;
    private Date aCreateDate;
    private Integer aState;
    private String uId;


    //附加属性
    private Integer likecount;

    public Integer getLikecount() {
        return likecount;
    }

    public void setLikecount(Integer likecount) {
        this.likecount = likecount;
    }

    public String getaId() {
        return aId;
    }

    public void setaId(String aId) {
        this.aId = aId;
    }

    public String getaName() {
        return aName;
    }

    public void setaName(String aName) {
        this.aName = aName;
    }

    public String getaCover() {
        return aCover;
    }

    public void setaCover(String aCover) {
        this.aCover = aCover;
    }

    public String getaDescribe() {
        return aDescribe;
    }

    public void setaDescribe(String aDescribe) {
        this.aDescribe = aDescribe;
    }

    public Integer getaPrivacy() {
        return aPrivacy;
    }

    public void setaPrivacy(Integer aPrivacy) {
        this.aPrivacy = aPrivacy;
    }

    public Date getaCreateDate() {
        return aCreateDate;
    }

    public void setaCreateDate(Date aCreateDate) {
        this.aCreateDate = aCreateDate;
    }

    public Integer getaState() {
        return aState;
    }

    public void setaState(Integer aState) {
        this.aState = aState;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    @Override
    public String toString() {
        return "YAlbum{" +
                "aId='" + aId + '\'' +
                ", aName='" + aName + '\'' +
                ", aCover='" + aCover + '\'' +
                ", aDescribe='" + aDescribe + '\'' +
                ", aPrivacy=" + aPrivacy +
                ", aCreateDate=" + aCreateDate +
                ", aState=" + aState +
                ", uId='" + uId + '\'' +
                '}';
    }
}
