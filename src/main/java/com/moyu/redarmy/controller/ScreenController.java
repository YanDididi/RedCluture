package com.moyu.redarmy.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.moyu.redarmy.core.config.FileConfig;
import com.moyu.redarmy.core.db.DBHelper;
import com.moyu.redarmy.core.result.Result;
import com.moyu.redarmy.core.result.ResultGenerator;
import com.moyu.redarmy.mappers.ScreenMapper;
import com.moyu.redarmy.model.Screen;
import com.moyu.redarmy.util.FileUtil;
import com.moyu.redarmy.util.MYUtil;
import com.moyu.redarmy.util.RedisUtil;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.validation.constraints.Null;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class ScreenController {
    @Autowired
    private FileConfig fileConfig;
    @Autowired
    RedisUtil redisUtil;

    @RequestMapping(path = "/uploadScreen")
    public Result uploadScreen(@RequestParam("file") MultipartFile file,
                               @RequestParam("deviceId") int deviceId) {
        if (file.isEmpty()) {
            return ResultGenerator.fail("文件为空");
        }
        deleteScreen(deviceId);
        String webPath = "/";
        try {

            String rootDirName = "upload/screen";
            String targetPath = fileConfig.getFilePath();
            String fileNames = file.getOriginalFilename();
            String ExtensionName = MYUtil.getExtensionName(fileNames);
            String fileName = "deviceId-" + deviceId + "-TheLast." + ExtensionName;
            boolean isNeedUnCompress = false;
            webPath = webPath + rootDirName + "/" + fileName;

            targetPath += rootDirName;

            FileUtil.uploadFile(file.getBytes(), targetPath, fileName, isNeedUnCompress);
            Map map = new HashMap();
            map.put("deviceId", deviceId);
            map.put("coverImg", webPath);

            addScreen(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.fail("上传失败");
        }

        return ResultGenerator.success(webPath);
    }

    @RequestMapping(path = "/upBaseScreen")
    public Result upBaseScreen(@RequestBody Map<String, String> imgMap) {

        String imgDatas = imgMap.get("imageData");
        String imgData = MYUtil.getBaseFileCode(imgDatas);
        Integer deviceId = Integer.valueOf(imgMap.get("deviceId"));
        if ((imgDatas.isEmpty()) || (null == deviceId)) {
            return ResultGenerator.fail("文件或 deviceId 为空");
        }
        deleteScreen(deviceId);
        String webPath = "/";
        try {
           /* Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String dateStr = dateFormat.format(date);
            String fileName = dateStr + MYUtil.Random(4)  + ".jpg";*/

            String ExtensionName = MYUtil.getBaseFileExtensionName(imgDatas);
            String fileName = "deviceId-" + deviceId + "-TheLast." + ExtensionName;

            //String fileName = String.valueOf(MYUtil.GetTimeStamps()) + ".jpg";

            String rootDirName = "upload/screen";
            String targetPath = fileConfig.getFilePath();
            boolean isNeedUnCompress = false;
            webPath = webPath + rootDirName + "/" + fileName;
            targetPath += rootDirName;

            BASE64Decoder decoder = new BASE64Decoder();
            byte[] b = decoder.decodeBuffer(imgData);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    //调整异常数据
                    b[i] += 256;
                }
            }
            FileUtil.uploadFile(b, targetPath, fileName, isNeedUnCompress);
            Map map = new HashMap();
            map.put("deviceId", deviceId);
            map.put("coverImg", webPath);
            addScreen(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.fail("上传失败");
        }

        return ResultGenerator.success(webPath);
    }


    @RequestMapping(path = {"/controller/screen"}, method = RequestMethod.POST)
    public Result addScreen(@RequestBody Map<String, Object> map) {
        String[] needParams = {"deviceId", "coverImg"};
        if (!MYUtil.IsExistParams(map, needParams)) {
            return ResultGenerator.fail("params empty");
        }
        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        try {
            ScreenMapper screenMapper = sqlSession.getMapper(ScreenMapper.class);
            //VersionMapper versionMapper = sqlSession.getMapper(VersionMapper.class);
            Screen screen = new Screen();
            screen.setDeviceId(MYUtil.ParseInt(MYUtil.GetParam(map, "deviceId")));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            screen.setCreateTime(MYUtil.GetTimeStamps());
            screen.setExpiryTime(screen.getCreateTime() + 2 * 60);
            screen.setCoverImg(MYUtil.GetParam(map, "coverImg"));
            if (screenMapper.insertScreen(screen) > 0) {
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

    @RequestMapping(path = {"/controller/deleteScreen/{id}"}, method = RequestMethod.DELETE)
    public Result deleteScreen(@PathVariable int id) {
        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        try {
            ScreenMapper screenMapper = sqlSession.getMapper(ScreenMapper.class);
            int result = screenMapper.deleteScreen(id);
            if (result > 0) {
                sqlSession.commit();
                return ResultGenerator.success();
            } else {
                return ResultGenerator.fail("删除截图失败");

            }
        } catch (Exception e) {
            return ResultGenerator.fail(e.toString());
        } finally {
            sqlSession.close();
        }
    }

    @RequestMapping(path = {"/controller/getScreenList"}, method = RequestMethod.POST)
    @ResponseBody
    public Result getScreenLis(@RequestBody Map<String, Object> map) {

        String[] needParams = {"deviceIds"};
        if (!MYUtil.IsExistParams(map, needParams)) {
            return ResultGenerator.fail("params empty");
        }
        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        try {
            ScreenMapper mapper = sqlSession.getMapper(ScreenMapper.class);
            List<Map<String, Integer>> deviceIds = (List<Map<String, Integer>>) map.get("deviceIds");
            List<Screen> screenLis;
            List<Integer> deviceIdLis = new ArrayList<>();
            for (int i = 0; i < deviceIds.size(); i++) {
                int deviceId = deviceIds.get(i).get("deviceId");
                deviceIdLis.add(deviceId);
            }
            screenLis = mapper.selectScreenLis(deviceIdLis);

            JSONArray array = JSONArray.parseArray(JSON.toJSONString(screenLis));
            return ResultGenerator.success(array);

        } catch (Exception e) {
            return ResultGenerator.fail(e.toString());
        } finally {
            sqlSession.close();
        }
    }


}
