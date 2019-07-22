package com.gupao.rpc.provider;

import com.gupao.rpc.HelloService;
import com.gupao.rpc.provider.service.impl.HelloServiceImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    public static void main(String[] args) {
        /*HelloService helloService = new HelloServiceImpl();
        RpcServer server = new RpcServer();
        server.publish(helloService, 8080);
        System.out.println("服务发布成功！");*/

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        context.start();
    }
}
