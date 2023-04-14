package com.graduation.medicaltaskscheduled.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.medicaltaskscheduled.entity.Admin;
import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.medicaltaskscheduled.entity.Appointment;
import com.graduation.medicaltaskscheduled.entity.vo.AdminQuery;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author RabbitFaFa
 * @since 2023-03-29
 */
public interface AdminService extends IService<Admin> {

    String adminRegister(String mobile, String pwd);

    String adminLogin(String mobile, String pwd, HttpServletRequest request);

    List<Appointment> getTaskQueue();

    List<Integer> taskScheduled(List<String> carIdList, List<Appointment> appointmentList) throws ExecutionException, InterruptedException;

    void ackScheduled(List<String> appointmentIdList);

    void adminListQuery(Page<Admin> page, AdminQuery adminQuery);

    Admin getAdminByToken(String token);
}
