package com.moyu.redarmy.mappers;

import com.moyu.redarmy.model.Room;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RoomMapper {
    int selectRoomCount(int leaderId);
    Room selectRoom(int id);
    Room selectRoomByLeaderId(int leaderId);
    List<Room> selectRooms(@Param("companyId") int companyId,@Param("status") int status);
    List<Room> selectRoomsAndExperiencer(int companyId);
    int insertRoom(Room room);
    int updateRoom(Room room);
    int deleteRoom(int id);
    int deleteRoomByLeaderId(int leaderId);
    int updateRoomStatus(Map<String,Object> map);
}
