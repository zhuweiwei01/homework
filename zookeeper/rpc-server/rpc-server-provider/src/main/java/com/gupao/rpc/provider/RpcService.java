package com.gupao.rpc.provider;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE) //类/接口
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {

    /**
     * 拿到服务的接口
     * @return
     */
    Class<?> value();

    /**
     * 版本号
     * @return
     */
    String version() default "";
}
