package com.moyu.redarmy.core.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.moyu.redarmy.model.BaseInfo;
import com.moyu.redarmy.model.Device;
import com.moyu.redarmy.model.State;
import com.moyu.redarmy.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class MessageHelper {
    @Autowired
    private RedisUtil redisUtil;

    private final Logger logger = LoggerFactory.getLogger(MessageHelper.class);

    public void connected(int clientType, String clientId, UUID sessionId) {
        switch (clientType) {
//            case Command.CLIENT_EXPERIENCER: {
//                String key = "Device:" + clientId + ":Experiencer";
//                Map<String, Object> map = new HashMap<>();
//                map.put("sessionId", sessionId);
//                redisUtil.hmset(key, map);
//                break;
//            }
//            case Command.CLIENT_LEADER: {
//                String key = "Device:" + clientId + ":Leader";
////                String key = "Device:" + clientId + ":Experiencer";
//                Map<String, Object> map = new HashMap<>();
//                map.put("sessionId", sessionId);
//                redisUtil.hmset(key, map);
//                break;
//            }
            case Command.CLIENT_CONTROLLER: {
                //TODO 获取companyId
                //以公司为单位管理控制端
                String key = "Device:" + 1 + ":Controller";
                Map<String, Object> map = new HashMap<>();
                map.put("sessionId", sessionId);
                redisUtil.hmset(key, map);
                break;
            }
            default: {
//                String key = "Device:" + clientId + ":Experiencer";
                String key = redisUtil.generateKey(clientId, RedisUtil.DEVICE_USER);
                Map<String, Object> map = new HashMap<>();
                map.put("sessionId", sessionId);
                redisUtil.hmset(key, map);
                break;
            }
        }
    }

    public void disConnect(int clientType, String clientId) {
        switch (clientType) {
            case Command.CLIENT_EXPERIENCER: {
                String key = redisUtil.generateKey(clientId, RedisUtil.DEVICE_EXPERIENCER);
                redisUtil.del(key);
                break;
            }
            case Command.CLIENT_LEADER: {
                String key = redisUtil.generateKey(clientId, RedisUtil.DEVICE_LEADER);
                redisUtil.del(key);
                break;
            }
            case Command.CLIENT_CONTROLLER: {
                String key = redisUtil.generateKey(clientId, RedisUtil.DEVICE_CONTROL);
                redisUtil.del(key);
                break;
            }
            default: {

            }
        }
    }

    public UUID getSessionId(int clientType, String clientId) {
        String key = "";
        UUID uuid = null;
        switch (clientType) {
//            case Command.CLIENT_EXPERIENCER: {
//                key = redisUtil.generateKey(clientId, RedisUtil.DEVICE_EXPERIENCER);
//                break;
//            }
//            case Command.CLIENT_LEADER: {
//                key = redisUtil.generateKey(clientId, RedisUtil.DEVICE_LEADER);
//                break;
//            }
            case Command.CLIENT_CONTROLLER: {
                key = redisUtil.generateKey(clientId, RedisUtil.DEVICE_CONTROL);
                break;
            }
            default: {
                key = redisUtil.generateKey(clientId, RedisUtil.DEVICE_USER);
                break;
            }
        }
        if (key != "") {
            Map<Object, Object> map = redisUtil.hmget(key);
            if (map != null && map.get("sessionId") != null) {
                uuid = UUID.fromString(map.get("sessionId").toString());
            }
        }
        return uuid;
    }

    public void syncData(int clientType, String clientId, Object data, SocketIOServer server) {
        String leadKey = "leader:" + clientId + ":sync";
        switch (clientType) {
            case Command.CLIENT_EXPERIENCER: {
                break;
            }
            case Command.CLIENT_LEADER: {
                dispatchDataToExperiencers(clientId, server, data);
                redisUtil.set(leadKey,data);
                break;
            }
            case Command.CLIENT_CONTROLLER: {
                String deviceKey = redisUtil.generateKey(clientId, RedisUtil.DEVICE_USER);
                Map<Object, Object> map = redisUtil.hmget(deviceKey);
                if (map != null && map.get("sessionId") != null) {
                    SocketIOClient leaderClient = server.getClient(UUID.fromString(map.get("sessionId").toString()));
                    if (leaderClient != null)
                        leaderClient.sendEvent("SyncEvent", data);
                    dispatchDataToExperiencers(clientId, server, data);
                    redisUtil.set(leadKey,data);
                }
                break;
            }
            default: {

            }
        }
    }

    private void dispatchDataToExperiencers(String leaderId, SocketIOServer server, Object data) {
        String key = redisUtil.generateKey(leaderId, RedisUtil.ROOM_EXPERIENCERS);
        Set experiencers = redisUtil.sGet(key);
        if (experiencers != null) {
            for (Object s : experiencers) {
                String deviceKey = redisUtil.generateKey(s.toString(), RedisUtil.DEVICE_EXPERIENCER);
                Map<Object, Object> map = redisUtil.hmget(deviceKey);
                if (map != null && map.get("sessionId") != null) {
                    if (map.get("lock") == null || (int) map.get("lock") == 1) {
                        SocketIOClient client = server.getClient(UUID.fromString(map.get("sessionId").toString()));
                        if (client != null) {
                            client.sendEvent("SyncEvent", data);
                        }

                    }
                }

            }
        }
    }

    public void controlData(int clientType, String clientId, Object data, SocketIOServer server) {
        String companyId = "1";
        UUID targetSessionId = null;
        JSONObject joData = JSONObject.parseObject(data.toString());
        switch (clientType) {
            case Command.CLIENT_EXPERIENCER: {
                targetSessionId = getSessionId(Command.CLIENT_CONTROLLER, companyId);
                break;
            }
            case Command.CLIENT_LEADER: {
                targetSessionId = getSessionId(Command.CLIENT_CONTROLLER, companyId);
                break;
            }
            case Command.CLIENT_CONTROLLER: {
                if (joData.getIntValue("targetClientType") == Command.CLIENT_LEADER) {
                    targetSessionId = getSessionId(Command.CLIENT_LEADER, joData.getString("targetClientId"));
                } else {
                    targetSessionId = getSessionId(Command.CLIENT_EXPERIENCER, joData.getString("targetClientId"));
                }
                break;
            }
            default: {
            }
        }
        joData.put("sourceClientId", clientId);
        joData.put("sourceClientType", clientType);
        if (targetSessionId != null) {
            SocketIOClient client = server.getClient(targetSessionId);
            if (client != null) {
                client.sendEvent("ControlEvent", JSON.toJSONString(joData));
            }
        }
    }

    public int getClientType(String clientId) {
        Object clientType = redisUtil.get(redisUtil.generateKey(clientId, RedisUtil.TYPE_DEVICE));
        return clientType == null ? Device.DeviceType.EXPERIENCER.type : (int) clientType;
    }

    public int getClientType(int clientId) {
        Object clientType = redisUtil.get(redisUtil.generateKey(String.valueOf(clientId), RedisUtil.TYPE_DEVICE));
        return clientType == null ? Device.DeviceType.EXPERIENCER.type : (int) clientType;
    }

    public void SyncBaseInfo(int clientId, BaseInfo baseInfo, SocketIOServer server) {
        UUID clientSessionId = getSessionId(Device.DeviceType.USER.type, String.valueOf(clientId));
        if (clientSessionId != null) {
            SocketIOClient client = server.getClient(clientSessionId);
            if (client != null) {
                client.sendEvent("BaseInfoEvent", JSONObject.toJSON(baseInfo));
            }
        }
    }


}
