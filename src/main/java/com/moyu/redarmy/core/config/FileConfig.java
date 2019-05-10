package com.moyu.redarmy.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "com.moyu.redarmy")
public class FileConfig {
    public static String FILE_TYPE_IMAGE="image";
    public static String FILE_TYPE_SCENE="scene";

    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
