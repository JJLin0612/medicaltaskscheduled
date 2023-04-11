package com.graduation.medicaltaskscheduled.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.graduation.medicaltaskscheduled.entity.Admin;
import com.graduation.medicaltaskscheduled.entity.Doctor;
import com.graduation.medicaltaskscheduled.entity.Patient;
import com.graduation.medicaltaskscheduled.entity.dto.ResultCode;
import com.graduation.medicaltaskscheduled.entity.dto.UserLabel;
import com.graduation.medicaltaskscheduled.exception.CustomException;
import com.graduation.medicaltaskscheduled.service.AdminService;
import com.graduation.medicaltaskscheduled.service.DoctorService;
import com.graduation.medicaltaskscheduled.service.LoginRegisterService;
import com.graduation.medicaltaskscheduled.service.PatientService;
import com.graduation.medicaltaskscheduled.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;

/**
 * 统一登录登出注册实现逻辑
 *
 * @author RabbitFaFa
 */
@Slf4j
@Service
public class LoginRegisterServiceImpl implements LoginRegisterService {

    @Resource
    private AdminService adminService;

    @Resource
    private DoctorService doctorService;

    @Resource
    private PatientService patientService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    //统一用户注册
    @Override
    public String unifyUserRegister(String mobile, String pwd, String verifyCode, String userLabel) {
        //TODO 验证验证码
        log.warn("统一用户注册");
        String res = null;
        if (userLabel.equals(UserLabel.LABEL_ADMIN)) {
            res = adminService.adminRegister(mobile, pwd);
        }else if (userLabel.equals(UserLabel.LABEL_DOCTOR)) {
            res = doctorService.doctorRegister(mobile, pwd);
        }else {
            res = patientService.patientRegister(mobile, pwd);
        }
        return res;
    }

    @Override
    public String unifyUserLogin(String mobile, String pwd, String userLabel, HttpServletRequest request) {
        String token = null;
        if (userLabel.equals(UserLabel.LABEL_ADMIN)) {
            token = adminService.adminLogin(mobile, pwd, request);
        }else if (userLabel.equals(UserLabel.LABEL_DOCTOR)) {
            token = doctorService.dcotorLogin(mobile, pwd, request);
        }else {
            token = patientService.patientLogin(mobile, pwd, request);
        }
        return token;
    }

    @Override
    public int logout(String token) {
        int res = 0;
        String id = JwtUtils.getDataIdByTokenStr(token);
        if (!StringUtils.isEmpty(id)) {
            redisTemplate.delete(id);
            res = 1;
        }
        return res;
    }
}
