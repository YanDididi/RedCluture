package com.moyu.redarmy.controller;

import com.moyu.redarmy.core.db.DBHelper;
import com.moyu.redarmy.core.result.Result;
import com.moyu.redarmy.core.result.ResultGenerator;
import com.moyu.redarmy.mappers.ResourceMapper;
import com.moyu.redarmy.mappers.RoomExperiencerMapper;
import com.moyu.redarmy.mappers.RoomMapper;
import com.moyu.redarmy.mappers.VersionMapper;
import com.moyu.redarmy.model.Resource;
import com.moyu.redarmy.model.Room;
import com.moyu.redarmy.model.RoomExperiencer;
import com.moyu.redarmy.model.Version;
import com.moyu.redarmy.util.MYUtil;
import org.apache.ibatis.session.SqlSession;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ResourceController {
    @RequestMapping(path = {"/controller/resource/{id}"}, method = RequestMethod.GET)
    public Result getResource(@PathVariable int id) {
        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        try {
            ResourceMapper mapper = sqlSession.getMapper(ResourceMapper.class);
            Resource resource = mapper.selectResource(id);
            if (resource != null) {
                List<Integer> resIds=new ArrayList<>();
                resIds.add(id);
                VersionMapper versionMapper=sqlSession.getMapper(VersionMapper.class);
                List<Version> versions=versionMapper.selectResourceListLatestVersion(resIds);
                resource.setLatestVersion(versions.get(0));
            }
            return ResultGenerator.success(resource);
        } catch (Exception e) {
            return ResultGenerator.fail(e.toString());
        } finally {
            sqlSession.close();
        }
    }

    @RequestMapping(path = {"/controller/getResourceList"}, method = RequestMethod.GET)
    public Result getResourceList(@RequestParam(value = "categoryId", required = false, defaultValue = "-1") String categoryId, @RequestParam(value = "status", required = false, defaultValue = "-1") String status) {
        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        try {
            ResourceMapper mapper = sqlSession.getMapper(ResourceMapper.class);
            VersionMapper versionMapper = sqlSession.getMapper(VersionMapper.class);
            List<Resource> resources = mapper.selectResources(Integer.parseInt(categoryId), Integer.parseInt(status));
            List<Integer> resourceIds = new ArrayList<>();
            Map<Integer, Resource> resourceMap = new HashMap<>();
            if (resources.size() == 0) {
                return ResultGenerator.success(resources);
            }
            for (int i = 0; i < resources.size(); i++) {
                Resource resourceItem = resources.get(i);
                resourceMap.put(resourceItem.getId(), resourceItem);
                resourceIds.add(resourceItem.getId());
            }
            List<Version> versions = versionMapper.selectResourceListLatestVersion(resourceIds);
            for (int i = 0; i < versions.size(); i++) {
                Version version = versions.get(i);
                Resource resource = resourceMap.get(version.getResourceId());
                resource.setLatestVersion(version);
                resourceMap.put(version.getResourceId(), resource);
            }
            List<Resource> data = new ArrayList<>();
            for (Resource v : resourceMap.values()) {
                data.add(v);
            }
            return ResultGenerator.success(data);
        } catch (Exception e) {
            return ResultGenerator.fail(e.toString());
        } finally {
            sqlSession.close();
        }
    }

//    @RequestMapping(path = {"/controller/updateResource"}, method = RequestMethod.POST)
//    public Result updateResource(@RequestBody Resource resource) {
//        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
//        try {
//            ResourceMapper mapper = sqlSession.getMapper(ResourceMapper.class);
//            if (resource.getId() > 0) {
//                int result = mapper.updateResource(resource);
//                sqlSession.commit();
//                if (result > 0) {
//                    return ResultGenerator.success(resource);
//                } else {
//                    return ResultGenerator.fail("修改失败");
//                }
//            } else {
//                return ResultGenerator.fail("params empty");
//            }
//        } catch (Exception e) {
//            return ResultGenerator.fail(e.toString());
//        } finally {
//            sqlSession.close();
//        }
//    }

    @RequestMapping(path = {"/controller/resource"}, method = RequestMethod.POST)
    public Result addResource(@RequestBody Map<String, Object> map) {
        String[] needParams = {"name", "versionName", "path", "coverImg", "categoryId"};
        if (!MYUtil.IsExistParams(map, needParams)) {
            return ResultGenerator.fail("params empty");
        }
        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        try {
            ResourceMapper resourceMapper = sqlSession.getMapper(ResourceMapper.class);
            VersionMapper versionMapper = sqlSession.getMapper(VersionMapper.class);
            Resource resource = new Resource();
            Version version = new Version();
            resource.setCoverImg(MYUtil.GetParam(map, "coverImg"));
            resource.setName(MYUtil.GetParam(map, "name"));
            resource.setCreateTime(MYUtil.GetTimeStamps());
            resource.setUpdateTime(MYUtil.GetTimeStamps());
            resource.setDesc(MYUtil.GetParam(map, "desc"));
            resource.setCategoryId(MYUtil.ParseInt(MYUtil.GetParam(map, "categoryId")));
            if (resourceMapper.insertResource(resource) > 0) {
                version.setResourceId(resource.getId());
                version.setPath(MYUtil.GetParam(map, "path"));
                version.setVersionName(MYUtil.GetParam(map, "versionName"));
                version.setCreateTime(MYUtil.GetTimeStamps());
                version.setUpdateTime(MYUtil.GetTimeStamps());
                versionMapper.insertVersion(version);
                sqlSession.commit();
            } else {
                return ResultGenerator.fail("添加资源失败");
            }
            return ResultGenerator.success();
        } catch (Exception e) {
            return ResultGenerator.fail(e.toString());
        } finally {
            sqlSession.close();
        }
    }

    @RequestMapping(path = {"/controller/resource"}, method = RequestMethod.PUT)
    public Result updateResource(@RequestBody Map<String, Object> map) {
        String[] needParams = {"id","name", "versionName", "path", "coverImg", "categoryId"};
        if (!MYUtil.IsExistParams(map, needParams)) {
            return ResultGenerator.fail("params empty");
        }
        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        try {
            ResourceMapper resourceMapper = sqlSession.getMapper(ResourceMapper.class);
            VersionMapper versionMapper = sqlSession.getMapper(VersionMapper.class);
            Resource resource = new Resource();
            resource.setId(MYUtil.ParseInt(MYUtil.GetParam(map,"id")));
            resource.setCoverImg(MYUtil.GetParam(map, "coverImg"));
            resource.setName(MYUtil.GetParam(map, "name"));
            resource.setUpdateTime(MYUtil.GetTimeStamps());
            resource.setDesc(MYUtil.GetParam(map, "desc"));
            resource.setCategoryId(MYUtil.ParseInt(MYUtil.GetParam(map, "categoryId")));
            if (resourceMapper.updateResource(resource) > 0) {
                Version version = new Version();
                version.setResourceId(resource.getId());
                version.setPath(MYUtil.GetParam(map, "path"));
                version.setVersionName(MYUtil.GetParam(map, "versionName"));
                version.setCreateTime(MYUtil.GetTimeStamps());
                version.setUpdateTime(MYUtil.GetTimeStamps());
                versionMapper.insertVersion(version);
                sqlSession.commit();
            } else {
                return ResultGenerator.fail("资源修改失败");
            }
            return ResultGenerator.success();
        } catch (Exception e) {
            return ResultGenerator.fail(e.toString());
        } finally {
            sqlSession.close();
        }
    }

}
