package com.example.x6.entity;

import org.litepal.crud.DataSupport;

public class BucketBill extends DataSupport{
    private Integer id;
    private String idName;
    private String explain;
    private String name;
    private Long createTime;
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BucketBill{" +
                "id=" + id +
                ", idName='" + idName + '\'' +
                ", explain='" + explain + '\'' +
                ", name='" + name + '\'' +
                ", createTime=" + createTime +
                ", status=" + status +
                '}';
    }
}
