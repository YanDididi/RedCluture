package com.moyu.redarmy.controller;

import com.alibaba.fastjson.JSONObject;
import com.moyu.redarmy.core.config.FileConfig;
import com.moyu.redarmy.core.db.DBHelper;
import com.moyu.redarmy.core.result.Result;
import com.moyu.redarmy.core.result.ResultGenerator;
import com.moyu.redarmy.mappers.ResourceMapper;
import com.moyu.redarmy.mappers.SyncResourceMapper;
import com.moyu.redarmy.mappers.SyncVersionMapper;
import com.moyu.redarmy.mappers.VersionMapper;
import com.moyu.redarmy.model.Resource;
import com.moyu.redarmy.model.SyncResource;
import com.moyu.redarmy.model.SyncVersion;
import com.moyu.redarmy.model.Version;
import com.moyu.redarmy.util.MYUtil;
import com.moyu.redarmy.util.Md5Util;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SyncResourceController {
    @Autowired
    private FileConfig fileConfig;

    @RequestMapping(path = {"/controller/getSyncResourceList"}, method = RequestMethod.GET)
    public Result getSyncResourceList(@RequestParam(value = "syncersionId", required = false, defaultValue = "-1") int syncversionId, @RequestParam(value = "status", required = false, defaultValue = "-1") int status) {
        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        SyncResourceMapper syncResourceMapper = sqlSession.getMapper(SyncResourceMapper.class);
        String targetPath = fileConfig.getFilePath();
        int indexb = targetPath.lastIndexOf(System.getProperty("file.separator"));
        //String tPath = targetPath.substring(0, indexb) + targetPath.substring(indexb + 1, targetPath.length());
        String tPath = targetPath.substring(0, indexb);
        try {
            List<SyncResource> syncResources = syncResourceMapper.getSyncResourceList(syncversionId, status);
            if (!syncResources.isEmpty() || syncResources.size() != 0) {
                for (SyncResource syncResource : syncResources) {

                    syncResource.setPath(tPath + syncResource.getPath());
                }
                return ResultGenerator.success(syncResources);
            }
            return ResultGenerator.fail("data is null");
        } catch (Exception e) {
            return ResultGenerator.fail(e.toString());
        } finally {
            sqlSession.close();
        }

    }
    /*@RequestMapping(path = {"/controller/syncResource"}, method = RequestMethod.POST)
    public Result addSyncResource(@RequestBody String json) {

        //{"deviceIds": [{"name":1, "path":1,"desc":1,"categoryId":1},{"name":2, "path":2,"desc":2,"categoryId":2},{"name":3, "path":3,"desc":3,"categoryId":3}]}
        //[{"name":1, "path":1,"desc":1,"categoryId":1},{"name":2, "path":2,"desc":2,"categoryId":2},{"name":3, "path":3,"desc":3,"categoryId":3}]
        *//*String[] needParams = {"name", "path", "desc", "categoryId"};
        if (!MYUtil.IsExistParams(map, needParams)) {
            return ResultGenerator.fail("params empty");
        }*//*
        String targetPath = fileConfig.getFilePath();

        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        SyncResourceMapper syncResourceMapper = sqlSession.getMapper(SyncResourceMapper.class);
        SyncVersionMapper syncVersionMapper = sqlSession.getMapper(SyncVersionMapper.class);
        //先insert syncVersion 再 insert syncResource
        //List<Map<String,String>> syncResources =(List<Map<String,String>>)map.get("syncResources");
        List<SyncResource> SyncResources = JSONObject.parseArray(json, SyncResource.class);
        List<SyncResource> SyncResourceLis = new ArrayList<>();
        try {

            SyncVersion syncVersion = new SyncVersion();
            syncVersion.setCreateTime(MYUtil.GetTimeStamps());
            syncVersion.setUpdateTime(MYUtil.GetTimeStamps());
            if (syncVersionMapper.insertSyncVersion(syncVersion) > 0) {
                for (SyncResource syncResource : SyncResources) {
                    syncResource.setCreateTime(MYUtil.GetTimeStamps());
                    syncResource.setUpdateTime(MYUtil.GetTimeStamps());
                    syncResource.setMd5(Md5Util.getMd5ByFile(targetPath+syncResource.getPath()));
                    syncResource.setSyncVersionId(syncVersion.getId());
                    SyncResourceLis.add(syncResource);
                }
                int result=syncResourceMapper.insertSyncResourceLis(SyncResourceLis);
                if(result>0){
                    sqlSession.commit();
                }else{
                    return ResultGenerator.fail("添加SyncResource失败");
                }
            }else {
                return ResultGenerator.fail("添加SyncVersion失败");
            }
            return ResultGenerator.success();
        } catch (Exception e) {
            return ResultGenerator.fail(e.toString());
        } finally {
            sqlSession.close();
        }
    }*/


}
