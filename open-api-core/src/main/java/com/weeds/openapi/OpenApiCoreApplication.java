package com.weeds.openapi;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 主类（项目启动入口）
 *
 * @author <a href="https://github.com/weedsx">weeds</a>
 * 
 */
@SpringBootApplication
@MapperScan("com.weeds.openapi.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableDubbo
public class OpenApiCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenApiCoreApplication.class, args);
    }

}
