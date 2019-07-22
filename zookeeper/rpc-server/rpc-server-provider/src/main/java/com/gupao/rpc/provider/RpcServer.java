package com.gupao.rpc.provider;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcServer {

    private volatile boolean run = true;

    public RpcServer(){}

    private ExecutorService threadPool = Executors.newCachedThreadPool();

    public void publish(Object service, int port) {
        ServerSocket serverSocket = null;
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(port);
            while (run) {
                socket = serverSocket.accept();
                //threadPool.execute(new ProtocolHandler(service, socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        this.run = false;
    }
}
