package com.example.x6.entity;

public class SendOperaTime {


    /**
     * id : 1
     * operaTime : 1500000000
     * explain : xxxxx
     */

    private int id;
    private long operaTime;
    private String explain;

    public SendOperaTime() {
    }

    public SendOperaTime(int id, long operaTime, String explain) {
        this.id = id;
        this.operaTime = operaTime;
        this.explain = explain;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getOperaTime() {
        return operaTime;
    }

    public void setOperaTime(long operaTime) {
        this.operaTime = operaTime;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    @Override
    public String toString() {
        return "SendOperaTime{" +
                "id=" + id +
                ", operaTime=" + operaTime +
                ", explain='" + explain + '\'' +
                '}';
    }
}
