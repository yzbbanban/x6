package com.example.x6.entity;

public class SendMessage<T> {
    /**
     * 状态
     */
    private String status;

    /**
     * 数据
     */
    private T data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SendMessage{" +
                "status='" + status + '\'' +
                ", data=" + data +
                '}';
    }
}
