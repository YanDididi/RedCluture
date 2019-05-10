package com.moyu.redarmy.mappers;


import com.moyu.redarmy.model.SyncResource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SyncResourceMapper {
    int insertSyncResourceLis (@Param("list") List<SyncResource> syncResourceLis);

    List<SyncResource> getSyncResourceList(@Param("syncVersionId") int syncVersionId,@Param("status") int status);

    int delteSyncResourceByMd5(@Param("list") List<SyncResource> syncResourceLis);

}
