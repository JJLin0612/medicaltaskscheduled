package com.graduation.medicaltaskscheduled.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.medicaltaskscheduled.entity.Appointment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author RabbitFaFa
 * @since 2023-04-05
 */
public interface AppointmentService extends IService<Appointment> {

    void putTaskQueue(Appointment appointment, String token);

    void appointmentPages(Page<Appointment> page, String patientId);

    List<Appointment> getAllAppointmentList(String patientId);
}
