package com.moyu.redarmy.core.message;

public class MessageInfo {
    //源客户端id
    private String sourceClientId;
    //目标客户端id
    private String targetClientId;
    //目标类型
    private int targetClientType;
    //消息类型
    private int msgType;
    //消息内容
    private String msgContent;

    public int getTargetClientType() {
        return targetClientType;
    }

    public void setTargetClientType(int targetClientType) {
        this.targetClientType = targetClientType;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getSourceClientId() {
        return sourceClientId;
    }

    public void setSourceClientId(String sourceClientId) {
        this.sourceClientId = sourceClientId;
    }

    public String getTargetClientId() {
        return targetClientId;
    }

    public void setTargetClientId(String targetClientId) {
        this.targetClientId = targetClientId;
    }


    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    @Override
    public String toString() {
        return "MessageInfo{" +
                "sourceClientId='" + sourceClientId + '\'' +
                ", targetClientId='" + targetClientId + '\'' +
                ", msgType=" + msgType +
                ", msgContent='" + msgContent + '\'' +
                '}';
    }
}
