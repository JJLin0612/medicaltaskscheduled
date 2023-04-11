package com.graduation.medicaltaskscheduled.service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author RabbitFaFa
 */
public interface LoginRegisterService {
    String unifyUserRegister(String mobile, String pwd, String verifyCode, String userLabel);

    String unifyUserLogin(String mobile, String pwd, String userLabel, HttpServletRequest request);

    int logout(String id);
}
