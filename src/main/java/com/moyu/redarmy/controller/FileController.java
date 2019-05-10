package com.moyu.redarmy.controller;

import com.alibaba.fastjson.JSONObject;
import com.moyu.redarmy.core.config.FileConfig;
import com.moyu.redarmy.core.db.DBHelper;
import com.moyu.redarmy.core.result.Result;
import com.moyu.redarmy.core.result.ResultGenerator;
import com.moyu.redarmy.mappers.SyncResourceMapper;
import com.moyu.redarmy.mappers.SyncVersionMapper;
import com.moyu.redarmy.model.MyFile;
import com.moyu.redarmy.model.SyncResource;
import com.moyu.redarmy.model.SyncVersion;
import com.moyu.redarmy.util.FileUtil;
import com.moyu.redarmy.util.MYUtil;
import com.moyu.redarmy.util.Md5Util;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class FileController {
    @Autowired
    private FileConfig fileConfig;
    static HashSet<MyFile> set = new HashSet<>();
    @RequestMapping(path = "/upload")
    public Result upload(@RequestParam("file") MultipartFile file, @RequestParam(value = "type", required = false, defaultValue = "") String type) {
        if (file.isEmpty()) {
            return ResultGenerator.fail("文件为空");
        }
        String webPath = "/";
        try {
            String fileName = file.getOriginalFilename();
            String rootDirName = "upload/other";
            String targetPath = fileConfig.getFilePath();

            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String dateStr = dateFormat.format(date);

            boolean isNeedUnCompress = false;
            switch (type) {
                case "image": {
                    rootDirName = "upload/images";
                    fileName = dateStr + MYUtil.Random(4) + "." + MYUtil.getExtensionName(fileName);
                    webPath = webPath + rootDirName + "/" + fileName;
                    break;
                }
                case "video": {
                    isNeedUnCompress = true;
                    rootDirName = "upload/video";
                    fileName = dateStr + MYUtil.Random(4) + "." + MYUtil.getExtensionName(fileName);
                    webPath = webPath + rootDirName + "/" + fileName;
                    break;
                }
                case "scene": {
                    isNeedUnCompress = true;
                    rootDirName = "upload/scene";
                    rootDirName = rootDirName + System.getProperty("file.separator") + dateStr + MYUtil.Random(4);
                    webPath = webPath + rootDirName + "/" + MYUtil.getFileNameNoEx(fileName) + "/";
                    break;
                }
                default: {
                    fileName = dateStr + MYUtil.Random(4) + "." + MYUtil.getExtensionName(fileName);
                    webPath = webPath + rootDirName + "/" + fileName;
                    break;
                }
            }
            targetPath += rootDirName;
            FileUtil.uploadBigFile(file, targetPath, fileName);

        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.fail("上传失败");
        }
        return ResultGenerator.success(webPath);
    }

    @RequestMapping(path = "/batchUpload")
    public Result batchUpload(HttpServletRequest request, @RequestParam(value = "type", required = false, defaultValue = "") String type) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        if (files.isEmpty() || files.size() <= 0) {
            return ResultGenerator.fail("文件为空");
        }
        MultipartFile file;
        String webPath = "/";

        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        SyncResourceMapper syncResourceMapper = sqlSession.getMapper(SyncResourceMapper.class);
        SyncVersionMapper syncVersionMapper = sqlSession.getMapper(SyncVersionMapper.class);
        List<SyncResource> SyncResourceLis = new ArrayList<>();
        //insert SyncVersion
        //SyncVersion syncVersion = new SyncVersion();
        //syncVersion.setCreateTime(MYUtil.GetTimeStamps());
        //syncVersion.setUpdateTime(MYUtil.GetTimeStamps());
        try {
            /*if (syncVersionMapper.insertSyncVersion(syncVersion) > 0) {*/
                SyncResource syncResource;
                for (int i = 0; i < files.size(); ++i) {
                    file = files.get(i);
                    if (!file.isEmpty()) {
                        String fileName = file.getOriginalFilename();
                        String rootDirName = "upload/other";
                        String targetPath = fileConfig.getFilePath();
                        Date date = new Date();
                        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                        String dateStr = dateFormat.format(date);
                        boolean isNeedUnCompress = false;
                        switch (type) {
                            case "image": {
                                rootDirName = "upload/images";
                                //fileName = dateStr + MYUtil.Random(4) + "." + MYUtil.getExtensionName(fileName);
                                webPath = webPath + rootDirName + "/" + fileName;
                                break;
                            }
                            case "video": {
                                rootDirName = "upload/videos";
                                //fileName = dateStr + MYUtil.Random(4) + "." + MYUtil.getExtensionName(fileName);
                                webPath = webPath + rootDirName + "/" + fileName;
                                break;
                            }
                            case "app": {
                                rootDirName = "upload/apps";
                                //fileName = dateStr + MYUtil.Random(4) + "." + MYUtil.getExtensionName(fileName);
                                webPath = webPath + rootDirName + "/" + fileName;
                                break;
                            }
                            case "scene": {
                                isNeedUnCompress = true;
                                rootDirName = "upload/scene";
                                rootDirName = rootDirName + System.getProperty("file.separator") + dateStr + MYUtil.Random(4);
                                webPath = webPath + rootDirName + "/" + MYUtil.getFileNameNoEx(fileName) + "/";
                                break;
                            }
                            default: {
                                //fileName = dateStr + MYUtil.Random(4) + "." + MYUtil.getExtensionName(fileName);
                                webPath = webPath + rootDirName + "/" + fileName;
                                break;
                            }
                        }
                        targetPath += rootDirName;

                        FileUtil.uploadFile(file.getBytes(), targetPath, fileName,isNeedUnCompress);
                        String md5=Md5Util.getMd5ByFile(targetPath+System.getProperty("file.separator")+fileName);
                        //
                        syncResource = new SyncResource();
                        syncResource.setCreateTime(MYUtil.GetTimeStamps());
                        syncResource.setUpdateTime(MYUtil.GetTimeStamps());
                        syncResource.setMd5(md5);
                        syncResource.setPath("/"+rootDirName+"/"+fileName);
                        syncResource.setName(fileName);
                        //syncResource.setSyncVersionId(syncVersion.getId());
                        SyncResourceLis.add(syncResource);

                    } else {
                        return ResultGenerator.fail("第 " + i + " 个文件上传失败因为文件为空");
                    }
                }
                //syncResourceMapper.delteSyncResourceByMd5(SyncResourceLis);
                //insert SyncResource
                if (syncResourceMapper.insertSyncResourceLis(SyncResourceLis) > 0) {


                        sqlSession.commit();
                     return ResultGenerator.success("syncResource 添加成功");
                }else {
                    return ResultGenerator.fail("syncResource 添加失败");
                }

            /*} else {
                return ResultGenerator.fail("syncVersion 添加失败");
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.fail(e.toString());
        }
    }

    @RequestMapping(path = "/down")
    public Result down(HttpServletResponse response, @RequestParam("path") String path) {
        String fileName = MYUtil.getFileName(path);
        String targetPath = fileConfig.getFilePath();
        String filePath = targetPath + path;
        String webPath = "/";
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;

        try {
            if (path != null) {

                File targetFile = new File(filePath);
                if (targetFile.exists()) {
                    response.setContentType("application/force-download");// 设置强制下载不打开
                    response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名
                    fis = new FileInputStream(targetFile);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                    return ResultGenerator.success("下载成功");
                }
            }
            return ResultGenerator.fail("下载失败");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ResultGenerator.fail("下载失败");
    }

    public static void deleteFile(File file){


        File[] files = file.listFiles();
        if(files!=null){
            for(File file2 : files){
                if(file2.isFile()){
                    MyFile myfile = new MyFile(file2.getName(), file2.length(), file2.lastModified());
                    if(set.contains(myfile)){
                        file2.delete();
                    }else{
                        set.add(myfile);
                        System.out.println(myfile);
                    }
                }else{
                    deleteFile(file2);
                }
            }
        }
    }


  /*  @RequestMapping(path = {"/downZip"})
    public Result editTags(@RequestBody String json) {
        //{"cover": [{"cover": 42},{"cover": 43},{"cover": 44},{"cover": 48}]}
       *//* String[] needParams = {"cover"};
        if (!MYUtil.IsExistParams(map, needParams)) {
            return ResultGenerator.fail("params empty");
        }
        List<Map<String, String>> covers = (List<Map<String, String>>) map.get("cover");*//*
        List files = new ArrayList();
        File file;
        List<String> coverLis = JSONObject.parseArray(json, String.class);
        for(String covers : coverLis){
            file= new File(covers);
            files.add(file);
        }



        return null;
    }*/


}
