package com.gupao.rpc.provider.service.impl;

import com.gupao.rpc.HelloService;
import com.gupao.rpc.User;
import com.gupao.rpc.provider.RpcService;

@RpcService(value = HelloService.class, version = "v1.0")
public class HelloServiceImpl implements HelloService{
    @Override
    public String sayHello(String name) {
        return "v1.0 Hello " + name;
    }

    @Override
    public User getUser(String uid) {
        User user = new User();
        user.setAge(18);
        user.setName("zww v1.0");
        return user;
    }
}
