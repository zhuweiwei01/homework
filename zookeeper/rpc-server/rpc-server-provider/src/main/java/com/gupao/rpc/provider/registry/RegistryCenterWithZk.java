package com.gupao.rpc.provider.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class RegistryCenterWithZk implements IRegistryCenter {

    private CuratorFramework curatorFramework;

    /**
     * 初始化客户端
     */
    {
        curatorFramework = CuratorFrameworkFactory.builder().connectionTimeoutMs(5000)
                .connectString(ZkConfig.ZK_ADDRESS).retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .namespace("registry").build();
        curatorFramework.start();
    }

    @Override
    public void register(String serviceName, String ipAddress) {
        String servicePath = "/" + serviceName;
        try {
            if (curatorFramework.checkExists().forPath(servicePath) == null) {
                //如果节点不存在则创建节点
                curatorFramework.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT).forPath(servicePath);

                String addressPath=servicePath+"/"+ipAddress;
                curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(addressPath);
                System.out.println("服务注册成功");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
