package com.graduation.medicaltaskscheduled.entity.dto;

/**
 * 操作类型 用户类型 枚举
 *
 * @author RabbitFaFa
 */
public interface OperateType {
    String LOGIN = "Login";
    String LOGOUT = "Logout";
    String REGISTER = "Register";
    String ADD = "Add";
    String DELETE = "Delete";
    String MODIFY = "Modify";
    String READ = "Read";
    String OTHER = "Other";

    String USER_TYPE_ADMIN = "0";
    String USER_TYPE_DOCTOR = "1";
    String USER_TYPE_PATIENT = "2";
}
