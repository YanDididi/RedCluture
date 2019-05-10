package com.moyu.redarmy.util;


import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public class FileUtil {
    /**
     * 上传文件
     *
     * @param file     文件对应的byte数组流   使用file.getBytes()方法可以获取
     * @param filePath 上传文件路径，不包含文件名
     * @param fileName 上传文件名
     * @throws Exception
     */
    public static void uploadFile(byte[] file, String filePath, String fileName, boolean isNeedUnCompress) throws Exception {
        File targetFile = new File(filePath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(filePath + System.getProperty("file.separator") + fileName);
        out.write(file);
        out.flush();
        out.close();
        if (isNeedUnCompress) {
            String zipFilePath = filePath + System.getProperty("file.separator") + fileName;
            String destDir = filePath;
            ZipUtil.unzip(zipFilePath, destDir);
        }
    }

    public static void uploadBigFile(MultipartFile file, String filePath, String fileName) throws Exception {
        File toFile = null;
        if(file.equals("")||file.getSize()<=0){
            file = null;
        }else {
            InputStream ins = null;
            ins = file.getInputStream();
            inputStreamToFile(ins, filePath,fileName);
            ins.close();
        }
    }

    public static void inputStreamToFile(InputStream ins,String filePath, String fileName) {
        try {
            int bufferSize=64*1024*1024;
            OutputStream os = new FileOutputStream(filePath + System.getProperty("file.separator") + fileName);
            int bytesRead = 0;
            byte[] buffer = new byte[bufferSize];
            while ((bytesRead = ins.read(buffer, 0, bufferSize)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void delFile(String filePath) throws Exception {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }


}
