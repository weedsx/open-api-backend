package com.weeds.openapi.provider;

import com.weeds.dubboapi.demo.DemoService;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.stereotype.Component;

/**
 * 示例服务实现类
 *
 * @author weeds
 */
@DubboService
@Component
public class DemoServiceImpl implements DemoService {

    @Override
    public String sayHello(String name) {
        System.out.println("Hello " + name + ", request from consumer: " + RpcContext.getContext().getRemoteAddress());
        return "Hello " + name;
    }

    @Override
    public String sayHello2(String name) {
        return "yupi";
    }


}