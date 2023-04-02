package com.graduation.medicaltaskscheduled.service;

/**
 * @author RabbitFaFa
 */
public interface LoginRegisterService {
    String unifyUserRegister(String mobile, String pwd, String verifyCode, String userLabel);

    String unifyUserLogin(String mobile, String pwd, String userLabel);

    int logout(String id);
}
