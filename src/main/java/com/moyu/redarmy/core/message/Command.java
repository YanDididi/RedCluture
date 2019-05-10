package com.moyu.redarmy.core.message;

public class Command {
    public static final int CLIENT_EXPERIENCER=1;
    public static final int CLIENT_LEADER=2;
    public static final int CLIENT_CONTROLLER=3;


    public static final int MSG_SYNC_DATA=1;
    public static final int MSG_CONTROL=2;

    public static final int COMMAND_LOCK=1;

    public enum Lock {
        LOCK(1),
        UNLOCK(0);
        public int status;
        Lock(int status) {
            this.status = status;
        }
    }
}
