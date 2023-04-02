package com.graduation.medicaltaskscheduled.service.impl;

import com.graduation.medicaltaskscheduled.entity.Patient;
import com.graduation.medicaltaskscheduled.mapper.PatientMapper;
import com.graduation.medicaltaskscheduled.service.PatientService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author RabbitFaFa
 * @since 2023-03-29
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

}
