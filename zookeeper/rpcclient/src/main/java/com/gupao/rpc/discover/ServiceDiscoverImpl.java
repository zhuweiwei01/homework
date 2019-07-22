package com.gupao.rpc.discover;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;

public class ServiceDiscoverImpl implements ServiceDiscover {

    private CuratorFramework curatorFramework;

    {
        curatorFramework = CuratorFrameworkFactory.builder().connectString(ZKConfig.ZK_ADDRESS)
                .sessionTimeoutMs(5000).
                        retryPolicy(new ExponentialBackoffRetry(1000, 3)).
                        namespace("registry")
                .build();
        curatorFramework.start();

    }
    List<String> serviceRepos=new ArrayList<>(); //服务地址的本地缓存
    @Override
    public String discover(String serviceName) {
        String path="/"+serviceName; //registry/com.gupaoedu.demo.HelloService
        if(serviceRepos.isEmpty()) {
            try {
                serviceRepos = curatorFramework.getChildren().forPath(path);
                registryWatch(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //针对已有的地址做负载均衡
        LoadBalanceStrategy loadBalanceStrategy=new RandomLoadBalance();
        return loadBalanceStrategy.selectHost(serviceRepos);
    }

    private void registryWatch(String path) throws Exception {

        PathChildrenCache nodeCache=new PathChildrenCache(curatorFramework,path,true);
        PathChildrenCacheListener nodeCacheListener= (curatorFramework1, pathChildrenCacheEvent) -> {
            System.out.println("客户端收到节点变更的事件");
            serviceRepos=curatorFramework1.getChildren().forPath(path);// 再次更新本地的缓存地址
        };
        nodeCache.getListenable().addListener(nodeCacheListener);
        nodeCache.start();
    }
}
