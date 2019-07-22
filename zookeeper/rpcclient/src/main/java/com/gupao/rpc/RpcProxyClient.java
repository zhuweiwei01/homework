package com.gupao.rpc;

import com.gupao.rpc.discover.ServiceDiscover;
import com.gupao.rpc.discover.ServiceDiscoverImpl;

import java.lang.reflect.Proxy;

public class RpcProxyClient {

    private ServiceDiscover serviceDiscovery=new ServiceDiscoverImpl();

    public <T> T clientProxy(final Class<T> interfaceCls, String version) {
        return (T) Proxy.newProxyInstance(interfaceCls.getClassLoader(),new Class<?>[]{interfaceCls}
                                        ,new RemoteInvocationHandler(version, serviceDiscovery));
    }
}
