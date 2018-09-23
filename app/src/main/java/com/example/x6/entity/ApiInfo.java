package com.example.x6.entity;

public class ApiInfo {

    //ip
    private String ip;
    //接口路径
    private String url;

    public ApiInfo() {
    }

    public ApiInfo(String ip, String url) {
        this.ip = ip;
        this.url = url;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ApiInfo{" +
                "ip='" + ip + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
