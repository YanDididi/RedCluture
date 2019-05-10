package com.moyu.redarmy.core.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.moyu.redarmy.model.BaseInfo;
import com.moyu.redarmy.model.Device;
import com.moyu.redarmy.model.State;
import com.moyu.redarmy.util.MYUtil;
import com.moyu.redarmy.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Component
public class MessageEventHandler {
    private final Logger logger = LoggerFactory.getLogger(MessageEventHandler.class);

    private final SocketIOServer server;
    @Autowired
    MessageHelper messageHelper;
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    public MessageEventHandler(SocketIOServer server) {
        this.server = server;
    }

    @OnConnect
    public void onConnect(SocketIOClient client) {
        System.out.println(client.getHandshakeData().toString());
        String clientId = client.getHandshakeData().getSingleUrlParam("deviceId");
        int clientType = MYUtil.ParseInt(client.getHandshakeData().getSingleUrlParam("clientType"));
        System.out.println("====sessionId:" + client.getSessionId().toString() + "==clientId" + clientId);
        messageHelper.connected(clientType, clientId, client.getSessionId());
        //通知控制端
        if (clientType != RedisUtil.DEVICE_CONTROL) {
            JSONObject jo = new JSONObject();
            jo.put("online", State.STATE_ONLINE);
            clientType = messageHelper.getClientType(clientId);
            messageHelper.controlData(clientType, clientId, JSON.toJSONString(jo), server);
        }
        logger.info("已连接：" + clientId);
    }

    @OnDisconnect
    public void onDisConnect(SocketIOClient client) {
        String clientId = client.getHandshakeData().getSingleUrlParam("deviceId");
        int clientType = MYUtil.ParseInt(client.getHandshakeData().getSingleUrlParam("clientType"));
        if (clientType != RedisUtil.DEVICE_CONTROL) {
            JSONObject jo = new JSONObject();
            jo.put("online", State.STATE_OFFLINE);
            clientType = messageHelper.getClientType(clientId);
            messageHelper.controlData(clientType, clientId, JSON.toJSONString(jo), server);

            if (RedisUtil.DEVICE_LEADER == clientType) {
                String leadKey = "leader:" + clientId + ":sync";
                redisUtil.del(leadKey);
            }
        }
        messageHelper.disConnect(clientType, clientId);
        logger.info("断开连接：" + clientId);
    }

    @OnEvent(value = "QuitEvent")
    public void quitEvent(SocketIOClient client, AckRequest request, Object data) {
        String clientId = client.getHandshakeData().getSingleUrlParam("deviceId");
        logger.info("QuitEvent:"+clientId);
        String leadKey ="leader:"+clientId+":sync";
        redisUtil.del(leadKey);
        if (request.isAckRequested()) {
            request.sendAckData("yes");
        }
    }

    @OnEvent(value = "SyncEvent")
    public void onEvent(SocketIOClient client, AckRequest request, Object data) {
        String clientId = client.getHandshakeData().getSingleUrlParam("deviceId");
        int clientType = MYUtil.ParseInt(client.getHandshakeData().getSingleUrlParam("clientType"));
//        clientType = clientType == RedisUtil.DEVICE_CONTROL ? clientType : messageHelper.getClientType(clientId);
        if(clientType==RedisUtil.DEVICE_CONTROL){
            JSONObject jsonData=JSONObject.parseObject(data.toString());
            clientId=jsonData.getString("leaderId");
        }else{
            clientType = messageHelper.getClientType(clientId);
        }
        messageHelper.syncData(clientType, clientId, data, server);
        logger.info("收到SyncEvent,from:"+clientId);

        //saveLastEvent(client,data);
    }

    @OnEvent(value = "EmitEvent")
    public void onEmitEvent(SocketIOClient client, AckRequest request, Object data){
        String clientId = client.getHandshakeData().getSingleUrlParam("deviceId");
        int clientType = MYUtil.ParseInt(client.getHandshakeData().getSingleUrlParam("clientType"));
        clientType = clientType == RedisUtil.DEVICE_CONTROL ? clientType : messageHelper.getClientType(clientId);
        messageHelper.syncData(clientType, clientId, data, server);
    }
    /*@Scheduled(cron = "0/5 * *  * * ? ")
    public void saveLastEvent(SocketIOClient client,Object data) {
        String clientId = client.getHandshakeData().getSingleUrlParam("deviceId");
        String leadKey = "leader:" + clientId + ":sync";
        redisUtil.set(leadKey,data);
    }*/


    @OnEvent(value = "ControlEvent")
    public void onControlEvent(SocketIOClient client, AckRequest request, Object data) {
        String clientId = client.getHandshakeData().getSingleUrlParam("deviceId");
        int clientType = MYUtil.ParseInt(client.getHandshakeData().getSingleUrlParam("clientType"));
        clientType = clientType == RedisUtil.DEVICE_CONTROL ? clientType : messageHelper.getClientType(clientId);
        messageHelper.controlData(clientType, clientId, data, server);
    }

    @OnEvent(value = "MessageEvent")
    public void onMessageEvent(SocketIOClient client, AckRequest request, Object data) {
        logger.info("receive message");
        System.out.println(data.toString());
//        server.getBroadcastOperations().sendEvent("MessageEvent", data);
    }

    @OnEvent(value = "ClientEvent")
    public void onClientEvent(SocketIOClient client, AckRequest request, Object data) {
        logger.info("receive client message");
        if (request.isAckRequested()) {
            request.sendAckData("yes");
        }
        String clientId = client.getHandshakeData().getSingleUrlParam("deviceId");
        logger.info("receive client message:"+clientId);
        if (!redisUtil.hasKey(redisUtil.generateKey(clientId, RedisUtil.DEVICE_USER))) {
            int clientType = messageHelper.getClientType(clientId);
            JSONObject jo = new JSONObject();
            jo.put("online", State.STATE_ONLINE);
            messageHelper.controlData(clientType, clientId, JSON.toJSONString(jo), server);
        }
    }

    public void emitControlEvent(UUID sessionId, String data) {
        logger.info("send ControllerEvent:" + data);
        SocketIOClient client = server.getClient(sessionId);
        if (client != null) {
            client.sendEvent("ControlEvent", data);
        }
    }

    public void emitSyncBaseInfoEvent(int clientId, BaseInfo baseInfo) {
        messageHelper.SyncBaseInfo(clientId, baseInfo, server);
    }
}
