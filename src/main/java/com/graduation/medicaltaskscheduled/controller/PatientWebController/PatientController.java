package com.graduation.medicaltaskscheduled.controller.PatientWebController;


import com.graduation.medicaltaskscheduled.entity.Patient;
import com.graduation.medicaltaskscheduled.entity.dto.Result;
import com.graduation.medicaltaskscheduled.entity.vo.PatientQuery;
import com.graduation.medicaltaskscheduled.service.PatientService;
import io.swagger.annotations.ApiParam;
import jdk.nashorn.internal.objects.annotations.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author RabbitFaFa
 * @since 2023-03-29
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/patient")
public class PatientController {

    @Resource
    private PatientService patientService;

    @GetMapping("patientInfo")
    public Result patientInfoByToken(@RequestParam(value = "token") String token) {
        Patient patient = patientService.getPatientInfo(token);
        return Result.ok().data("patient", patient);
    }

    @PostMapping("modifiedPatient")
    public Result modifiedPatient(
            @ApiParam(value = "patient", name = "patient")
            @RequestBody Patient patient
    ) {
        log.error("patient="+patient.toString());
        boolean res = patientService.updateById(patient);
        return res ? Result.ok() : Result.error();
    }

    @PostMapping("patientListByQuery")
    public Result patientListQuery(@RequestBody PatientQuery patientQuery) {
        List<Patient> patientList = patientService.getPatientListByQuery(patientQuery);
        return Result.ok().data("patientList", patientList);
    }

    @GetMapping("deletePatient")
    public Result deletePatient(
            @ApiParam(value = "patientId", name = "patientId")
            @RequestParam String patientId
    ) {
        boolean res = patientService.removeById(patientId);
        return res ? Result.ok() : Result.error();
    }
}

