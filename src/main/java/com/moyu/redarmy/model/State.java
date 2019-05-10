package com.moyu.redarmy.model;

import org.apache.ibatis.type.Alias;

@Alias("State")
public class State {
    public static final int STATE_ONLINE=1;
    public static final int STATE_OFFLINE=0;

    int online=0;
    int lock=1;

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public int getLock() {
        return lock;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }
}
