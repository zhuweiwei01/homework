package com.gupao.rpc.provider;

import com.gupao.rpc.provider.registry.IRegistryCenter;
import com.gupao.rpc.provider.registry.RegistryCenterWithZk;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//@Component
public class RpcProxyServer implements ApplicationContextAware, InitializingBean {

    private int port;

    private final ExecutorService pool = Executors.newCachedThreadPool();
    private Map<String ,Object> handlerMap = new HashMap<String, Object>();


    private IRegistryCenter iRegistryCenter = new RegistryCenterWithZk();

    public RpcProxyServer(int port) {
        this.port = port;
    }

    /**
     * 这个方式是实现InitializingBean的方法
     * 在初始化bean的时候spring容器会调用该方法
     * 我们在这里做服务监听
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        //BIO
        /*ServerSocket serverSocket = new ServerSocket(port);
        Socket socket =null;
        try {
            while (true) {
                socket = serverSocket.accept();
                pool.execute(new ProtocolHandler(handlerMap, socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(socket != null) {
                socket.close();
            }
        }*/

        //基于NIO的netty实现
        //接收客户端的连接
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        //处理已经被接收的连接
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boosGroup, workGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().
                                addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null))).
                                addLast(new ObjectEncoder()).
                                addLast(new ProtocolHandler(handlerMap));
                    }
                });
        serverBootstrap.bind(port).sync();
    }

    /**
     * 初始化handleMap,容器启动时初始化
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //获取添加了RPCService注解的类
        Map<String, Object> rpcServiceMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if(rpcServiceMap != null && !rpcServiceMap.isEmpty()) {
            for(Object serviceBean : rpcServiceMap.values()) {
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
                String value = rpcService.value().getName();//备注注解内填写的value值
                String version = rpcService.version();
                if(!StringUtils.isEmpty(version)) {
                    value = value + "-" + version;
                }
                //将服务注册到zookeeper
                iRegistryCenter.register(value, getAddress() + ":" + port);
                handlerMap.put(value, serviceBean);
            }
        }
    }

    private static String getAddress(){
        InetAddress inetAddress=null;
        try {
            inetAddress=InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return inetAddress.getHostAddress();// 获得本机的ip地址
    }
}
