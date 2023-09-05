package com.bilibil;

import com.bilibil.service.webSocket.WebSocketService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Date:  2023/8/18
 */
@EnableScheduling // 允许开启定时任务
@EnableAsync   // 开启异步功能
@MapperScan({"com.bilibil.mapper"})
@EnableTransactionManagement //事务管理功能，自动开始、提交或回滚事务
@EnableEurekaClient
@SpringBootApplication
@EnableFeignClients(basePackages = "com.bilibil.service.feign")
public class ApplicationAPI {
    public static void main(String[] args) {

        ApplicationContext run = SpringApplication.run(ApplicationAPI.class, args);
        // 启动使用Websocket
        WebSocketService.setApplicationContext(run);
    }
}
