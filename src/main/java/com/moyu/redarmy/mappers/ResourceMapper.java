package com.moyu.redarmy.mappers;

import com.moyu.redarmy.model.Resource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ResourceMapper {
    Resource selectResource(int id);
    List<Resource> selectResources(@Param("categoryId")int categoryId,@Param("status")int status);
    int insertResource(Resource resource);
    int updateResource(Resource resource);
}
