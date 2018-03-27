package com.bs.youmin.entity;


public class MainGridModel {

    private String time;
    private String url;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "MainGridModel{" +
                "time='" + time + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
