package com.moyu.redarmy.mappers;
import com.moyu.redarmy.model.RoomExperiencer;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RoomExperiencerMapper {
    int insertExperiencer(RoomExperiencer experiencer);
    int batchInsertExperiencer(List<RoomExperiencer> experiencers);
    int selectExperiencerCount(int deviceId);
    Map<String,Object> selectByDeviceId(int deviceId);

    int deleteExperiencer(int deviceId);
    int deleteRoomAllExperiencer(int roomId);
}
