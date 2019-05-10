package com.moyu.redarmy;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import javax.servlet.MultipartConfigElement;

@SpringBootApplication
@EnableCaching
public class RedArmyApplication {
//    @Value("${wss.server.host}")
//    private String host;
    @Value("${wss.server.port}")
    private Integer port;

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setOrigin(null);   // 注意如果开放跨域设置，需要设置为null而不是"*"
//        config.setMaxFramePayloadLength(2048);
//        config.setWorkerThreads(100);
//        config.setHostname(host);
        config.setPort(port);

//该处可以用来进行身份验证
        config.setAuthorizationListener(new AuthorizationListener() {
            @Override
            public boolean isAuthorized(HandshakeData data) {
                //http://localhost:8081?username=test&password=test
                //例如果使用上面的链接进行connect，可以使用如下代码获取用户密码信息，本文不做身份验证
//				String username = data.getSingleUrlParam("username");
//				String password = data.getSingleUrlParam("password");
                return true;
            }
        });

        final SocketIOServer server = new SocketIOServer(config);

        return server;
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketIOServer) {
        return new SpringAnnotationScanner(socketIOServer);
    }

    public static void main(String[] args) {
        SpringApplication.run(RedArmyApplication.class, args);
    }
}
