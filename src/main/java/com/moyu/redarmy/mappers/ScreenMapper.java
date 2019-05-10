package com.moyu.redarmy.mappers;


import com.moyu.redarmy.model.Screen;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ScreenMapper {
    Screen selectScreen(@Param("deviceId") int deviceId);
    int insertScreen(Screen screen);
    int deleteScreen(@Param("deviceId") int deviceId);
    List<Screen> selectScreenLis(@Param("list") List<Integer> deviceIdLis);

}
