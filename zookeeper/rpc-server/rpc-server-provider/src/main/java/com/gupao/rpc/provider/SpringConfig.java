package com.gupao.rpc.provider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.gupao.rpc.provider")
public class SpringConfig {

    @Bean
    public RpcProxyServer rpcProxyServer() {
        return new RpcProxyServer(8080);
    }
}
