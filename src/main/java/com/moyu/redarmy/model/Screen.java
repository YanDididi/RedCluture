package com.moyu.redarmy.model;

public class Screen {
    int id;
    int deviceId;
    int createTime;
    int expiryTime;
    String coverImg;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public int getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(int expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    @Override
    public String toString() {
        return "Screen{" +
                "id=" + id +
                ", deviceId=" + deviceId +
                ", createTime=" + createTime +
                ", expiryTime=" + expiryTime +
                ", coverImg='" + coverImg + '\'' +
                '}';
    }
}
