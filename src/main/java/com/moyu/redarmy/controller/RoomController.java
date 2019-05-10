package com.moyu.redarmy.controller;

import com.moyu.redarmy.core.db.DBHelper;
import com.moyu.redarmy.core.message.MessageHelper;
import com.moyu.redarmy.core.result.Result;
import com.moyu.redarmy.core.result.ResultGenerator;
import com.moyu.redarmy.mappers.DeviceMapper;
import com.moyu.redarmy.mappers.RoomExperiencerMapper;
import com.moyu.redarmy.mappers.RoomMapper;
import com.moyu.redarmy.model.*;
import com.moyu.redarmy.util.MYUtil;
import com.moyu.redarmy.util.RedisUtil;
import com.moyu.redarmy.util.StateUtil;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class RoomController {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    StateUtil stateUtil;

    @RequestMapping(path = {"/controller/room/{id}"}, method = RequestMethod.GET)
    public Result getRoom(@PathVariable int id) {
        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        try {
            RoomMapper mapper = sqlSession.getMapper(RoomMapper.class);
            Room room = mapper.selectRoom(id);
            room.setExperiencers(stateUtil.SetDeviceState(room.getExperiencers()));
            return ResultGenerator.success(room);
        } catch (Exception e) {
            return ResultGenerator.fail(e.toString());
        } finally {
            sqlSession.close();
        }
    }

    @RequestMapping(path = {"/controller/getRoomList/{companyId}"}, method = RequestMethod.GET)
    public Result getRoomList(@PathVariable int companyId, @RequestParam(value = "status", required = false, defaultValue = "-1") String status) {
        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        try {
            RoomMapper mapper = sqlSession.getMapper(RoomMapper.class);
            List<Room> rooms = mapper.selectRooms(companyId, MYUtil.ParseInt(status));
            for (Room room : rooms) {
                State state = new State();
                String deviceKey = redisUtil.generateKey(String.valueOf(room.getLeaderId()), RedisUtil.DEVICE_LEADER);

                Map<Object, Object> mapState = redisUtil.hmget(deviceKey);
                if (mapState != null && mapState.size() > 0) {
                    state.setOnline(State.STATE_ONLINE);
                    state.setLock(mapState.get("lock") == null ? 0 : (int) mapState.get("lock"));
                }
                room.getLeader().setState(state);
            }
            return ResultGenerator.success(rooms);
        } catch (Exception e) {
            return ResultGenerator.fail(e.toString());
        } finally {
            sqlSession.close();
        }
    }

    @RequestMapping(path = {"/controller/bindExperiencer"}, method = RequestMethod.POST)
    public Result bindExperiencer(@RequestBody Map<String, Object> map) {
        String[] needParams = {"deviceIds", "roomId", "leaderId"};
        if (!MYUtil.IsExistParams(map, needParams)) {
            return ResultGenerator.fail("params empty");
        }
        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        try {
            RoomExperiencerMapper mapper = sqlSession.getMapper(RoomExperiencerMapper.class);
            List<Map<String, Integer>> deviceIds = (List<Map<String, Integer>>) map.get("deviceIds");
            int roomId = MYUtil.ParseInt(MYUtil.GetParam(map, "roomId"));
            int leaderId = MYUtil.ParseInt(MYUtil.GetParam(map, "leaderId"));
            boolean isCheck = map.get("isCheck") == null ? true : (Boolean) map.get("isCheck");
            RoomMapper roomMapper=sqlSession.getMapper(RoomMapper.class);
            Room room=roomMapper.selectRoom(roomId);
            if(room==null){
                return ResultGenerator.fail("房间不存在");
            }
            List<RoomExperiencer> roomExperiencers = new ArrayList<>();
            for (int i = 0; i < deviceIds.size(); i++) {
                int deviceId = deviceIds.get(i).get("deviceId");
                Map<String, Object> isExist = mapper.selectByDeviceId(deviceId);
                if (isExist != null) {
                    //已绑定其他体验者
                    int delResult = mapper.deleteExperiencer(deviceId);
                    int bindedLeaderId = (int) isExist.get("leaderId");
                    redisUtil.setRemove(redisUtil.generateKey(String.valueOf(bindedLeaderId), RedisUtil.ROOM_EXPERIENCERS), deviceId);
                }
                if (isCheck) {
                    RoomExperiencer roomExperiencer = new RoomExperiencer();
                    roomExperiencer.setDeviceId(deviceId);
                    roomExperiencer.setRoomId(roomId);
                    roomExperiencers.add(roomExperiencer);
                    redisUtil.sSet(redisUtil.generateKey(String.valueOf(leaderId), RedisUtil.ROOM_EXPERIENCERS), deviceId);
                }
            }
            if (isCheck) {
                int result = mapper.batchInsertExperiencer(roomExperiencers);
                if (result > 0) {
                    sqlSession.commit();
                    return ResultGenerator.success();
                } else {
                    return ResultGenerator.fail("绑定失败");
                }
            }
            sqlSession.commit();
            return ResultGenerator.success("cancel success");

        } catch (Exception e) {
            return ResultGenerator.fail(e.toString());
        } finally {
            sqlSession.close();
        }
    }

    @RequestMapping(path = {"/controller/deleteExperiencer/{id}"}, method = RequestMethod.DELETE)
    public Result deleteExperiencer(@PathVariable int id) {
        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        try {
            RoomExperiencerMapper mapper = sqlSession.getMapper(RoomExperiencerMapper.class);
            int result = mapper.deleteExperiencer(id);
            if (result > 0) {
                return ResultGenerator.success();
            } else {
                return ResultGenerator.fail("取消绑定失败");
            }
        } catch (Exception e) {
            return ResultGenerator.fail(e.toString());
        } finally {
            sqlSession.close();
        }
    }

    @RequestMapping(path = {"/controller/getRoomsExperiencers/{id}"}, method = RequestMethod.GET)
    public Result getRoomsExperiencers(@PathVariable int id) {
        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        try {
            RoomMapper mapper = sqlSession.getMapper(RoomMapper.class);
            List<Room> rooms = mapper.selectRoomsAndExperiencer(id);
            for (Room room : rooms) {
                room.setExperiencers(stateUtil.SetDeviceState(room.getExperiencers()));
            }
            return ResultGenerator.success(rooms);
        } catch (Exception e) {
            return ResultGenerator.fail(e.toString());
        } finally {
            sqlSession.close();
        }
    }

    @RequestMapping(path = "/controller/room", method = RequestMethod.PUT)
    public Result updateRoom(@RequestBody Map<String, Object> map) {
        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        try {
            int id = MYUtil.ParseInt(MYUtil.GetParam(map, "id"));
            int resourceId = MYUtil.ParseInt(MYUtil.GetParam(map, "resourceId"));
            RoomMapper mapper = sqlSession.getMapper(RoomMapper.class);
            Room room = new Room();
            room.setId(id);
            room.setResourceId(resourceId);
            room.setUpdateTime(MYUtil.GetTimeStamps());
            mapper.updateRoom(room);
            sqlSession.commit();
            //通知
            return ResultGenerator.success();
        } catch (Exception e) {
            return ResultGenerator.fail(e.toString());
        } finally {
            sqlSession.close();
        }
    }

    @RequestMapping(path = "/controller/updateRoomStatus", method = RequestMethod.PUT)
    public Result updateRoomStatus(@RequestBody Map<String, Object> map) {
        String[] needParams = {"add", "remove"};
        if (!MYUtil.IsExistParams(map, needParams)) {
            return ResultGenerator.fail("params empty");
        }
        List<Integer> addRooms = (ArrayList<Integer>) map.get("add");
        List<Integer> removeRooms = (ArrayList<Integer>) map.get("remove");

        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();

        try {
            RoomMapper mapper = sqlSession.getMapper(RoomMapper.class);
            if (addRooms.size() > 0) {
                Map<String, Object> rooms = new HashMap<>();
                rooms.put("status", 1);
                rooms.put("roomIds", addRooms);
                mapper.updateRoomStatus(rooms);
            }
            if (removeRooms.size() > 0) {
                Map<String, Object> rooms = new HashMap<>();
                rooms.put("status", 0);
                rooms.put("roomIds", removeRooms);
                mapper.updateRoomStatus(rooms);
                mapper.updateRoomStatus(rooms);
            }
            //{add:[1],remove:[1,2,3]}
//            room.setId(id);
//            mapper.updateRoom(room);
            sqlSession.commit();
            //通知
            return ResultGenerator.success();
        } catch (Exception e) {
            return ResultGenerator.fail(e.toString());
        } finally {
            sqlSession.close();
        }
    }

    @RequestMapping(path = {"/controller/changeRole"}, method = RequestMethod.POST)
    public Result changeRole(@RequestBody Map<String, Object> map) {
        String[] needParams = {"deviceId", "type"};
        if (!MYUtil.IsExistParams(map, needParams)) {
            return ResultGenerator.fail("params empty");
        }
        int deviceId = MYUtil.ParseInt(MYUtil.GetParam(map, "deviceId"));
        int type = MYUtil.ParseInt(MYUtil.GetParam(map, "type"));
        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        try {
            DeviceMapper mapper = sqlSession.getMapper(DeviceMapper.class);
            Device device = mapper.selectDevice(deviceId);
            if (device == null) {
                return ResultGenerator.fail("device not exist");
            }
            if (device.getType() == type) {
                return ResultGenerator.success();
            }
            RoomMapper roomMapper = sqlSession.getMapper(RoomMapper.class);
            RoomExperiencerMapper roomExperiencerMapper = sqlSession.getMapper(RoomExperiencerMapper.class);
            if (type == Device.DeviceType.EXPERIENCER.type) {
                //修改为体验者
                Room room = roomMapper.selectRoomByLeaderId(deviceId);
                roomExperiencerMapper.deleteRoomAllExperiencer(room.getId());
                roomMapper.deleteRoom(room.getId());
                redisUtil.del(redisUtil.generateKey(String.valueOf(deviceId), RedisUtil.ROOM_EXPERIENCERS));
                device.setType(type);
                mapper.updateDevice(device);
//                redisUtil.setRemove(redisUtil.generateKey(String.valueOf(deviceId), RedisUtil.ROOM_EXPERIENCERS))
            } else if (type == Device.DeviceType.LEADER.type) {
                //修改为领路人
                device.setType(type);
                Map<String, Object> isExist = roomExperiencerMapper.selectByDeviceId(deviceId);
                if (isExist != null) {
                    //已绑定其他领路人
                    int delResult = roomExperiencerMapper.deleteExperiencer(deviceId);
                    int bindedLeaderId = (int) isExist.get("leaderId");
                    redisUtil.setRemove(redisUtil.generateKey(String.valueOf(bindedLeaderId), RedisUtil.ROOM_EXPERIENCERS), deviceId);
                }
                Room room = new Room();
                room.setCompanyId(1);
                room.setCreateTime(MYUtil.GetTimeStamps());
                room.setLeaderId(device.getId());
                room.setUpdateTime(MYUtil.GetTimeStamps());
                room.setResourceId(1);
                roomMapper.insertRoom(room);
                mapper.updateDevice(device);
            }
            if (redisUtil.set(redisUtil.generateKey(String.valueOf(device.getId()), RedisUtil.TYPE_DEVICE), device.getType())) {
                sqlSession.commit();
                BaseInfo baseInfo=new BaseInfo();
                baseInfo.setClientType(device.getType());
                baseInfo.setDeviceId(deviceId);
                baseInfo.setNumber(device.getNumber());
                stateUtil.syncBaseInfo(device.getId(),baseInfo);
            } else {
                sqlSession.rollback();
                return ResultGenerator.fail("insert fail");
            }
        } catch (Exception e) {
            return ResultGenerator.fail("fail");
        } finally {
            sqlSession.close();
        }

        return ResultGenerator.success();
    }
}
