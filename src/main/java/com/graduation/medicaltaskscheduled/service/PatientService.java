package com.graduation.medicaltaskscheduled.service;

import com.graduation.medicaltaskscheduled.entity.Patient;
import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.medicaltaskscheduled.entity.vo.PatientQuery;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author RabbitFaFa
 * @since 2023-03-29
 */
public interface PatientService extends IService<Patient> {

    String patientRegister(String mobile, String pwd);

    String patientLogin(String mobile, String pwd, HttpServletRequest request);

    Patient getPatientInfo(String token);

    List<Patient> getPatientListByQuery(PatientQuery patientQuery);
}
