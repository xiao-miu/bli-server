package com.bilibil;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Date:  2023/8/18
 */
@MapperScan({"com.bilibil.mapper"})
@SpringBootApplication
public class ApplicationAPI {
    public static void main(String[] args) {

        ApplicationContext run = SpringApplication.run(ApplicationAPI.class, args);
    }
}
