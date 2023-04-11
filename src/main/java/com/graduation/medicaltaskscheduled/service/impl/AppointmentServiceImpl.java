package com.graduation.medicaltaskscheduled.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.medicaltaskscheduled.entity.Appointment;
import com.graduation.medicaltaskscheduled.entity.Patient;
import com.graduation.medicaltaskscheduled.entity.dto.ResultCode;
import com.graduation.medicaltaskscheduled.exception.CustomException;
import com.graduation.medicaltaskscheduled.mapper.AppointmentMapper;
import com.graduation.medicaltaskscheduled.service.AppointmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.medicaltaskscheduled.service.PatientService;
import com.graduation.medicaltaskscheduled.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author RabbitFaFa
 * @since 2023-04-05
 */
@Slf4j
@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {

    @Autowired
    private ArrayList<Appointment> appointmentList;

    @Resource
    private PatientService patientService;


    /***
     * 患者预约处理逻辑
     * @param appointment 预约表单
     */
    @Transactional
    @Override
    public void putTaskQueue(Appointment appointment, String token) {
        //获取患者id
        String id = JwtUtils.getDataIdByTokenStr(token);
        int count = patientService.count(new QueryWrapper<Patient>().eq("id", id));
        if (count != 1) throw new CustomException(ResultCode.USER_UNREGISTERED_ERROR, "用户不存在");
        //持久化至DB
        appointment.setPatientId(id);
        baseMapper.insert(appointment);
        //放入队列
        appointmentList.add(appointment);
    }

    /***
     * 分页查询patientId的患者预约记录
     * @param page
     * @param patientId
     */
    @Override
    public void appointmentPages(Page<Appointment> page, String patientId) {
        QueryWrapper<Appointment> wrapper = new QueryWrapper<>();
        wrapper.eq("patient_id", patientId)
                .orderByDesc("gmt_create");
        baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Appointment> getAllAppointmentList(String patientId) {
        QueryWrapper<Appointment> wrapper = new QueryWrapper<>();
        wrapper.eq("patient_id", patientId).orderByDesc("gmt_create");
        return baseMapper.selectList(wrapper);
    }

}
