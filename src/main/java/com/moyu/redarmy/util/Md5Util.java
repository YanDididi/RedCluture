package com.moyu.redarmy.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.FileInputStream;
import java.io.IOException;

public class Md5Util {

    public static String getMd5ByFile(String path) throws IOException {
        FileInputStream fis= new FileInputStream(path);
        String md5 = DigestUtils.md5Hex(fis);

        return md5;
    }
}
