package com.moyu.redarmy.util;

import com.alibaba.fastjson.JSON;
import com.moyu.redarmy.core.message.MessageEventHandler;
import com.moyu.redarmy.model.BaseInfo;
import com.moyu.redarmy.model.Device;
import com.moyu.redarmy.model.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.moyu.redarmy.core.message.Command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public final class StateUtil {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    MessageEventHandler messageEventHandler;

    public List<Device> SetDeviceState(List<Device> devices) {
        for (Device device : devices) {
            State state = new State();
            String deviceKey = redisUtil.generateKey(String.valueOf(device.getId()), RedisUtil.DEVICE_EXPERIENCER);
            Map<Object, Object> mapState = redisUtil.hmget(deviceKey);
            if (mapState != null && mapState.size() > 0) {
                state.setOnline(State.STATE_ONLINE);
                state.setLock(mapState.get("lock") == null ? 1 : (int) mapState.get("lock"));
            }
            device.setState(state);
        }
        return devices;
    }

    public List<Map<String, Object>> SetDeviceMapState(List<Map<String, Object>> devices) {
        for (Map<String, Object> map : devices) {
            String deviceId = map.get("id").toString();
            State state = new State();
            String deviceKey = redisUtil.generateKey(deviceId, RedisUtil.DEVICE_EXPERIENCER);
            Map<Object, Object> mapState = redisUtil.hmget(deviceKey);
            if (mapState != null && mapState.size() > 0) {
                state.setOnline(State.STATE_ONLINE);
                state.setLock(mapState.get("lock") == null ? 1 : (int) mapState.get("lock"));
            }
            map.put("state", state);
        }
        return devices;
    }

    public void ControlDeviceState(List<Map<String, Object>> devices, int type, int val) {
        String setKey = "";
        switch (type) {
            case Command.COMMAND_LOCK: {
                setKey = "lock";
                break;
            }
            default: {
                break;
            }
        }
        if (setKey != "") {
            for (Map<String, Object> map : devices) {
                String deviceId = map.get("deviceId").toString();
                int clientType = MYUtil.ParseInt(MYUtil.GetParam(map, "clientType"));
                String deviceKey = redisUtil.generateKey(deviceId, clientType);
                Map<Object, Object> state = redisUtil.hmget(deviceKey);
                if (state != null && state.get("sessionId") != null) {
                    Map<String, Object> stateMap = new HashMap<>();
                    stateMap.put(setKey, val);
                    redisUtil.hmset(deviceKey, stateMap);
                    messageEventHandler.emitControlEvent(UUID.fromString(state.get("sessionId").toString()), JSON.toJSONString(stateMap));
                }
            }
        }

    }

    public void syncBaseInfo(int clientId,BaseInfo baseInfo){
        messageEventHandler.emitSyncBaseInfoEvent(clientId,baseInfo);
    }

}
