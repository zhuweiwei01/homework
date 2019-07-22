package com.gupao.rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RpcNetTransport extends SimpleChannelInboundHandler<Object> {

    private String serviceAddress;

    public RpcNetTransport(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    private Object result;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        this.result = o;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        System.out.println("异常：" + cause);
        ctx.close();
    }

    public Object send(RpcProtocol rpcProtocol) {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                                .addLast(new ObjectEncoder())
                                .addLast(RpcNetTransport.this);
                    }
                }).option(ChannelOption.TCP_NODELAY, true);

        try{
            String[] urls = serviceAddress.split(":");
            ChannelFuture future = bootstrap.connect(urls[0],Integer.parseInt(urls[1])).sync();
            future.channel().writeAndFlush(rpcProtocol).sync();

            if(rpcProtocol != null) {
                future.channel().closeFuture().sync();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }

        return result;
    }

    /*
    Bio
    public Object send(RpcProtocol rpcProtocol, String localhost, int port) {
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            socket = new Socket(localhost, port);
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(rpcProtocol);
            oos.flush();

            ois = new ObjectInputStream(socket.getInputStream());
            Object result = ois.readObject();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }*/
}
