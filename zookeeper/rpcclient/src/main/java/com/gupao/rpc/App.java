package com.gupao.rpc;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws InterruptedException {
        /*RpcProxyClient rpcProxyClient=new RpcProxyClient();

        HelloService iHelloService=rpcProxyClient.clientProxy(HelloService.class,"localhost",8080);

        String result=iHelloService.sayHello(null);
        System.out.println(result);*/

        ApplicationContext context=new AnnotationConfigApplicationContext(SpringConfig.class);
        RpcProxyClient rpcProxyClient=context.getBean(RpcProxyClient.class);
        HelloService helloService=rpcProxyClient.clientProxy(HelloService.class,"v2.0");
        for(int i=0;i<100;i++) {
            Thread.sleep(2000);
            System.out.println(helloService.sayHello("aaa"));
        }
    }
}
