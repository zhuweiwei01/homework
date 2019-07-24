package com.gupao.vip.lock;


import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 基于zk实现的可重入分布式锁
 */
public class ZKLock implements Lock {

    private String basePath;
    private ZkClient client;
    /**
     * 当前节点
     */
    private ThreadLocal<String> currentPath = new ThreadLocal<>();

    /**
     * 前一个节点
     */
    private ThreadLocal<String> beforePath = new ThreadLocal<>();

    public ZKLock(ZkClient client, String path) {
        this.client = client;
        this.basePath = path;
        if(!client.exists(path)) {
            client.createPersistent(path);
        }
    }

    @Override
    public void lock() {
        if (!tryLock()) {
            waitForLock();
            lock();
        }

    }

    private void waitForLock() {
        //声明一个计数器
        CountDownLatch cdl = new CountDownLatch(1);
        IZkDataListener listener = new IZkDataListener() {
            @Override
            public void handleDataChange(String arg0, Object arg1) throws Exception {
            }
            @Override
            public void handleDataDeleted(String arg0) throws Exception {
                //计数器减一
                cdl.countDown();
            }
        };

        //完成watcher注册
        this.client.subscribeDataChanges(this.beforePath.get(), listener);
        //阻塞自己
        if (this.client.exists(this.beforePath.get())) {
            try {
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //取消注册
        this.client.unsubscribeDataChanges(this.beforePath.get(), listener);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    private static final String SEPARATOR = "/";

    @Override
    public boolean tryLock() {
        //当前节点为空，说明还没有线程来创建节点
        if(this.currentPath.get() == null) {
            String data = this.client.createEphemeralSequential(this.basePath + SEPARATOR, "data");
            this.currentPath.set(data);
        }
        //获取所有子节点
        List<String> children = this.client.getChildren(this.basePath);
        //排序
        Collections.sort(children);
        //判断当前节点是否是最小的节点
        if(this.currentPath.get().equals(this.basePath + SEPARATOR + children.get(0))) {
            return true;
        } else {
            //获取当前节点的位置
            int curIndex = children.indexOf(this.currentPath.get().substring(this.basePath.length() + 1));
            //设置前一个节点
            beforePath.set(this.basePath + SEPARATOR + children.get(curIndex - 1));
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        this.client.delete(this.currentPath.get());
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
