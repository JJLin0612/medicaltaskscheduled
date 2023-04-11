package com.graduation.medicaltaskscheduled.service;

import com.graduation.medicaltaskscheduled.entity.Doctor;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author RabbitFaFa
 * @since 2023-03-29
 */
public interface DoctorService extends IService<Doctor> {

    String doctorRegister(String workId, String pwd);

    String dcotorLogin(String workId, String pwd, HttpServletRequest request);
}
