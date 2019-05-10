package com.moyu.redarmy.mappers;

import com.moyu.redarmy.model.Device;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DeviceMapper {
    Device selectDevice(@Param("id") int id);

    Device selectDeviceByImei(String imei);

    List<Device> selectDevices(@Param("companyId") int companyId, @Param("type") int type, @Param("status") int Status);

    List<Map<String, Object>> selectDevicesDetails(@Param("companyId") int companyId, @Param("type") int type, @Param("status") int Status);

    int insertDevice(Device device);

    int updateDevice(Device device);

    int deleteDevice(int id);

    Integer selectLeadIdByExpId(@Param("expId") int expId);

    String getLastNumber();

    int isLeader(@Param("deviceId") String deviceId);
}
