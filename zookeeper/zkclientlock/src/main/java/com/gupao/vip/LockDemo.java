package com.gupao.vip;

import com.gupao.vip.config.ZkConfig;
import com.gupao.vip.lock.ZKLock;
import org.I0Itec.zkclient.ZkClient;

public class LockDemo {

    public static void main(String[] args) {

        ZkClient client = new ZkClient(ZkConfig.ZK_ADDRESS, ZkConfig.ZK_SEESION_TIMEOUT);
        final ZKLock lock = new ZKLock(client, "/locks");

        for(int i=0;i<10;i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName()+"->尝试获取锁");
                try {
                    lock.lock();
                    System.out.println(Thread.currentThread().getName()+"->获得锁成功");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(4000);
                    lock.unlock();
                    System.out.println(Thread.currentThread().getName()+"->释放锁成功");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "t" + i).start();
        }
    }
}
