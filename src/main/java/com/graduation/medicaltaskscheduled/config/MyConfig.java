package com.graduation.medicaltaskscheduled.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.graduation.medicaltaskscheduled.entity.Appointment;
import com.graduation.medicaltaskscheduled.entity.dto.ResultCode;
import com.graduation.medicaltaskscheduled.utils.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

/**
 * @author RabbitFaFa
 * @date 2022/12/3
 */
@Slf4j
@Configuration
@MapperScan("com.example.fromzerotoexpert.mapper")
public class MyConfig {

    /**
     * 逻辑删除插件
     * @return
     */
    @Bean
    public ISqlInjector iSqlInjector() {
        return new LogicSqlInjector();
    }

    @Bean
    public RSAUtils rsaUtils() {
        return new RSAUtils();
    }

    @Bean
    public ArrayList<Appointment> appointmentList() {
        QueryWrapper<Appointment> wrapper = new QueryWrapper<>();
        wrapper.eq("is_scheduled", ResultCode.APPOINTMENT_IS_NO_SCHEDULED);
        return new ArrayList<>();
    }

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
