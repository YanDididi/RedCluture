package com.moyu.redarmy.model;

import org.apache.ibatis.type.Alias;

@Alias("RoomExperiencer")
public class RoomExperiencer {
    int id;
    int deviceId;
    int roomId;

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

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
}
