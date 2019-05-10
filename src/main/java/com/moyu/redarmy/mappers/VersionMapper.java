package com.moyu.redarmy.mappers;


import com.moyu.redarmy.model.Version;

import java.util.List;

public interface VersionMapper {
    int insertVersion(Version version);
    List<Version> selectResourceListLatestVersion(List<Integer> resourceIds);
}
