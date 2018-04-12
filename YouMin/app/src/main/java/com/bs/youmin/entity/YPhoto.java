package com.bs.youmin.entity;

import java.util.Date;

/**
 * @Author:zhuwj
 * @Description: 相片
 * @Date:Created in 11:23 2018/3/23
 */
public class YPhoto {

    private String pId;
    private String pBig;
    private String pSmall;
    private Integer pDown;
    private String aId;
    private Integer pState;
    private Date pCreateDate;

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpBig() {
        return Ip.LOAD_IMG_URL_ORIGINAL + pBig;
    }

    public void setpBig(String pBig) {
        this.pBig = pBig;
    }

    public String getpSmall() {
        return Ip.LOAD_IMG_URl_COMPRESS + pSmall;
    }

    public void setpSmall(String pSmall) {
        this.pSmall = pSmall;
    }

    public Integer getpDown() {
        return pDown;
    }

    public void setpDown(Integer pDown) {
        this.pDown = pDown;
    }

    public String getaId() {
        return aId;
    }

    public void setaId(String aId) {
        this.aId = aId;
    }

    public Integer getpState() {
        return pState;
    }

    public void setpState(Integer pState) {
        this.pState = pState;
    }

    public Date getpCreateDate() {
        return pCreateDate;
    }

    public void setpCreateDate(Date pCreateDate) {
        this.pCreateDate = pCreateDate;
    }

    @Override
    public String toString() {
        return "YPhoto{" +
                "pId='" + pId + '\'' +
                ", pBig='" + pBig + '\'' +
                ", pSmall='" + pSmall + '\'' +
                ", pDown=" + pDown +
                ", aId='" + aId + '\'' +
                ", pState=" + pState +
                ", pCreateDate=" + pCreateDate +
                '}';
    }
}
