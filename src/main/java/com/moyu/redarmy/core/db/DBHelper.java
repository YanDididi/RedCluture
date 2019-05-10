package com.moyu.redarmy.core.db;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class DBHelper {
   static SqlSessionFactory sqlSessionFactory = null;

    public static SqlSessionFactory getSqlSessionFacttory() {
        if (sqlSessionFactory == null) {
            String resource = "config/mybatis.xml";
            try {
                InputStream inputStream = Resources.getResourceAsStream(resource);
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
        return sqlSessionFactory;
    }
}
