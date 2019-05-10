package com.moyu.redarmy.core.message;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;

@Component
public class ServerRunner implements CommandLineRunner, ServletContextListener {
    private final SocketIOServer server;

    @Autowired
    public ServerRunner(SocketIOServer server) {
        this.server = server;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("websocket server start==");
        try{
            server.start();
        }catch (Exception e){
            e.printStackTrace();
            server.stop();
        }
    }
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("stop");
        //关闭Socketio服务
        if(server!=null){
            server.stop();
        }
    }
}
