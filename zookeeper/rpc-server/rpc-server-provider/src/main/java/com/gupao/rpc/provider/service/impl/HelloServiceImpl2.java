package com.gupao.rpc.provider.service.impl;

import com.gupao.rpc.HelloService;
import com.gupao.rpc.User;
import com.gupao.rpc.provider.RpcService;

@RpcService(value = HelloService.class, version="v2.0")
public class HelloServiceImpl2 implements HelloService{
    @Override
    public String sayHello(String name) {
        return "v2.0 Hello " + name;
    }

    @Override
    public User getUser(String uid) {
        User user = new User();
        user.setAge(18);
        user.setName("v2.0 zww");
        return user;
    }
}
