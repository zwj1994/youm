package com.bs.youmin.entity;

/**
 * @Author:zhuwj
 * @Description: Banner轮播图
 * @Date:Created in 14:05 2018/3/30
 */
public class YBanner {

    private String bId;
    private String bName;
    private String bDesc;
    private String bIcon;
    private String bUrl;
    private String bState;
    private String bLevel;
    private String bCreateDate;

    public String getbId() {
        return bId;
    }

    public void setbId(String bId) {
        this.bId = bId;
    }

    public String getbName() {
        return bName;
    }

    public void setbName(String bName) {
        this.bName = bName;
    }

    public String getbDesc() {
        return bDesc;
    }

    public void setbDesc(String bDesc) {
        this.bDesc = bDesc;
    }

    public String getbIcon() {
        return bIcon;
    }

    public void setbIcon(String bIcon) {
        this.bIcon = bIcon;
    }

    public String getbUrl() {
        return bUrl;
    }

    public void setbUrl(String bUrl) {
        this.bUrl = bUrl;
    }

    public String getbState() {
        return bState;
    }

    public void setbState(String bState) {
        this.bState = bState;
    }

    public String getbLevel() {
        return bLevel;
    }

    public void setbLevel(String bLevel) {
        this.bLevel = bLevel;
    }

    public String getbCreateDate() {
        return bCreateDate;
    }

    public void setbCreateDate(String bCreateDate) {
        this.bCreateDate = bCreateDate;
    }

    @Override
    public String toString() {
        return "YBanner{" +
                "bId='" + bId + '\'' +
                ", bName='" + bName + '\'' +
                ", bDesc='" + bDesc + '\'' +
                ", bIcon='" + bIcon + '\'' +
                ", bUrl='" + bUrl + '\'' +
                ", bState='" + bState + '\'' +
                ", bLevel='" + bLevel + '\'' +
                ", bCreateDate='" + bCreateDate + '\'' +
                '}';
    }
}
