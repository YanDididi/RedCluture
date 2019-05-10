package com.moyu.redarmy.controller;

import com.moyu.redarmy.core.db.DBHelper;
import com.moyu.redarmy.core.message.MessageHelper;
import com.moyu.redarmy.core.result.Result;
import com.moyu.redarmy.core.result.ResultGenerator;
import com.moyu.redarmy.mappers.DeviceMapper;
import com.moyu.redarmy.mappers.RoomMapper;
import com.moyu.redarmy.model.Device;
import com.moyu.redarmy.model.Room;
import com.moyu.redarmy.model.State;
import com.moyu.redarmy.util.MYUtil;
import com.moyu.redarmy.util.RedisUtil;
import com.moyu.redarmy.util.StateUtil;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class DeviceController {
    @Autowired
    StateUtil stateUtil;
    @Autowired
    RedisUtil redisUtil;
    private final Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @RequestMapping(path = {"/controller/device/{id}"}, method = RequestMethod.GET)
    public Result getDevice(@PathVariable int id) {
        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        try {
            DeviceMapper mapper = sqlSession.getMapper(DeviceMapper.class);
            Device device = mapper.selectDevice(id);
            return ResultGenerator.success(device);
        } catch (Exception e) {
            return ResultGenerator.fail(e.toString());
        } finally {
            sqlSession.close();
        }
    }

    @RequestMapping(path = {"/controller/updateDevice"}, method = RequestMethod.PUT)
    public Result updateDevice(@RequestBody Map<String, Object> map) {
        String[] needParams = {"id", "number"};
        if (!MYUtil.IsExistParams(map, needParams)) {
            return ResultGenerator.fail("params empty");
        }
        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        try {
            int id = MYUtil.ParseInt(MYUtil.GetParam(map, "id"));
            String number = MYUtil.GetParam(map, "number");
            DeviceMapper mapper = sqlSession.getMapper(DeviceMapper.class);
            Device device = mapper.selectDevice(id);
            if (device != null) {
                device.setNumber(number);
                device.setUpdateTime(MYUtil.GetTimeStamps());
                int result = mapper.updateDevice(device);
                sqlSession.commit();
                if (result > 0) {
                    return ResultGenerator.success(device);
                }
            }
            return ResultGenerator.fail("保存失败");
        } catch (Exception e) {
            return ResultGenerator.fail(e.toString());
        } finally {
            sqlSession.close();
        }
    }


    @RequestMapping(path = {"/controller/registerDevice"}, method = RequestMethod.POST)
    public Result registerDevice(@RequestBody Map<String, Object> map) {
        System.out.println("Device login====");
        String[] needParams = {"imei"};
        if (!MYUtil.IsExistParams(map, needParams)) {
            return ResultGenerator.fail("params empty");
        }
        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        try {
            DeviceMapper mapper = sqlSession.getMapper(DeviceMapper.class);
            String imei = MYUtil.GetParam(map, "imei");
            System.out.println(imei);
            int type = MYUtil.ParseInt(MYUtil.GetParam(map, "type"));
            Device device = mapper.selectDeviceByImei(imei);
            if (null == device) {
                device = new Device();
                device.setImei(imei);
                device.setCreateTime(MYUtil.GetTimeStamps());
                device.setUpdateTime(MYUtil.GetTimeStamps());

                int companyId = MYUtil.ParseInt(MYUtil.GetParam(map, "companyId"));
                companyId = companyId > 0 ? companyId : 1;
                device.setCompanyId(companyId);
                //device.setNumber(MYUtil.GetParam(map, "number"));
                String s = mapper.getLastNumber();
                if (null == s) {
                    device.setNumber("E000");
                } else {
                    Integer i = Integer.valueOf(s.substring(1)) + 1;
                    if (0 < i && i <= 9) {
                        String number = "E" + "00" + i;
                        device.setNumber(number);
                    } else if (9 < i && i <= 99) {
                        String number = "E" + "0" + i;
                        device.setNumber(number);
                    } else {
                        String number = "E" + i;
                        device.setNumber(number);
                    }

                }

                //默认初始注册都是体验者
                device.setType(Device.DeviceType.EXPERIENCER.type);
//                device.setType(type);
                int result = mapper.insertDevice(device);
                sqlSession.commit();
                if (result < 0) {
                    return ResultGenerator.success(device);
                }
            }

//            if (device.getType() != type && type > 0) {
//                device.setType(type);
//                mapper.updateDevice(device);
//                sqlSession.commit();
//            }
//            if (type == Device.DeviceType.LEADER.type) {
//                RoomMapper roomMapper = sqlSession.getMapper(RoomMapper.class);
//                int isExistRoom = roomMapper.selectRoomCount(device.getId());
//                if (isExistRoom == 0) {
//                    Room room = new Room();
//                    room.setCompanyId(1);
//                    room.setCreateTime(MYUtil.GetTimeStamps());
//                    room.setLeaderId(device.getId());
//                    room.setUpdateTime(MYUtil.GetTimeStamps());
//                    roomMapper.insertRoom(room);
//                    sqlSession.commit();
//                }
//            }
            return ResultGenerator.success(device);
        } catch (Exception e) {
            return ResultGenerator.fail(e.toString());
        } finally {
            sqlSession.close();
        }
    }

    @RequestMapping(path = {"/controller/getDeviceList"}, method = RequestMethod.POST)
    public Result getDeviceList(@RequestBody Map<String, Object> map) {
        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        try {
            DeviceMapper mapper = sqlSession.getMapper(DeviceMapper.class);
            List<Device> devices = mapper.selectDevices(MYUtil.ParseInt(MYUtil.GetParam(map, "companyId")), MYUtil.ParseInt(MYUtil.GetParam(map, "type")), MYUtil.ParseInt(MYUtil.GetParam(map, "status")));
            devices = stateUtil.SetDeviceState(devices);
            return ResultGenerator.success(devices);
        } catch (Exception e) {
            return ResultGenerator.fail(e.toString());
        } finally {
            sqlSession.close();
        }
    }

    @RequestMapping(path = {"/controller/getAllDeviceDetail"}, method = RequestMethod.POST)
    public Result getAllDeviceDetail(@RequestBody Map<String, Object> map) {
        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        try {
            DeviceMapper mapper = sqlSession.getMapper(DeviceMapper.class);
            List<Map<String, Object>> devices = mapper.selectDevicesDetails(MYUtil.ParseInt(MYUtil.GetParam(map, "companyId")), MYUtil.ParseInt(MYUtil.GetParam(map, "type")), MYUtil.ParseInt(MYUtil.GetParam(map, "status")));
            devices = stateUtil.SetDeviceMapState(devices);
            return ResultGenerator.success(devices);
        } catch (Exception e) {
            return ResultGenerator.fail(e.toString());
        } finally {
            sqlSession.close();
        }
    }

    @RequestMapping(path = {"/controller/controlDevice"}, method = RequestMethod.POST)
    public Result bindExperiencer(@RequestBody Map<String, Object> map) {
        String[] needParams = {"devices", "type", "value"};
        if (!MYUtil.IsExistParams(map, needParams)) {
            return ResultGenerator.fail("params empty");
        }
        List<Map<String, Object>> devices = (List<Map<String, Object>>) map.get("devices");
        int type = MYUtil.ParseInt(MYUtil.GetParam(map, "type"));
        int value = MYUtil.ParseInt(MYUtil.GetParam(map, "value"));
        stateUtil.ControlDeviceState(devices, type, value);
        return ResultGenerator.success();
    }

    @RequestMapping(path = {"/controller/getSyncEvent"}, method = RequestMethod.GET)
    @ResponseBody
    public Result GetSyncEvent(@RequestParam("deviceId") String deviceId, @RequestParam("clientType") int clientType) {
        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        DeviceMapper mapper = sqlSession.getMapper(DeviceMapper.class);
        try {
            if (Device.DeviceType.EXPERIENCER.type == clientType) {
                Integer leadId = mapper.selectLeadIdByExpId(Integer.valueOf(deviceId));
                if (null == leadId) {
                    return ResultGenerator.fail("leadId is null");
                }
                String leadKey = "leader:" + leadId + ":sync";
                if (!StringUtils.isEmpty(redisUtil.get(leadKey))) {
                    Integer result = (Integer) redisUtil.hget("Device:" + deviceId + ":User", "lock");
                    if (null == result) {
                        result = 1;
                    }
                    if (result != 0) {
                        logger.info("GET SyncEvent,from:" + leadKey);
                        return ResultGenerator.success(String.valueOf(redisUtil.get(leadKey)));
                    }
                }
                return ResultGenerator.fail("select by leadKey is null");
            } else if (Device.DeviceType.LEADER.type == clientType) {
                String leadKey = "leader:" + deviceId + ":sync";
                if (!StringUtils.isEmpty(redisUtil.get(leadKey))) {
                    logger.info("GET SyncEvent,from:" + leadKey);
                    return ResultGenerator.success(String.valueOf(redisUtil.get(leadKey)));
                }
                return ResultGenerator.fail("select by leadKey is null");
            } else {
                return ResultGenerator.fail("clientType is err");
            }
        } catch (Exception e) {
            return ResultGenerator.fail(e.toString());
        } finally {
            sqlSession.close();
        }
    }
}
