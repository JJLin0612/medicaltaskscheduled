package com.graduation.medicaltaskscheduled;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication(exclude={SecurityAutoConfiguration.class})
@ComponentScan(value = {"com.graduation"})
@MapperScan(value = {"com.graduation.medicaltaskscheduled.mapper"})
public class MedicaltaskscheduledApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicaltaskscheduledApplication.class, args);
    }

}
