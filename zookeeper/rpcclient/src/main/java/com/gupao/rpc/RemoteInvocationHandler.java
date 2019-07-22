package com.gupao.rpc;

import com.gupao.rpc.discover.ServiceDiscover;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RemoteInvocationHandler implements InvocationHandler {

    private String version;
    private ServiceDiscover serviceDiscover;

    public RemoteInvocationHandler(String version, ServiceDiscover serviceDiscover) {
        this.version = version;
        this.serviceDiscover = serviceDiscover;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcProtocol rpcProtocol = new RpcProtocol();
        rpcProtocol.setClassName(method.getDeclaringClass().getName());
        rpcProtocol.setParameters(args);
        rpcProtocol.setMethodName(method.getName());
        rpcProtocol.setParamTypes(method.getParameterTypes());
        rpcProtocol.setVersion(version);
        String serviceName = rpcProtocol.getClassName();
        if(!StringUtils.isEmpty(version)) {
            serviceName = serviceName + "-" + version;
        }
        String serviceAddress = serviceDiscover.discover(serviceName);

        RpcNetTransport rpcNetTransport = new RpcNetTransport(serviceAddress);
        Object result = rpcNetTransport.send(rpcProtocol);
        return result;
    }
}
