package com.graduation.medicaltaskscheduled.controller.PatientWebController;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.medicaltaskscheduled.annotation.LogRecord;
import com.graduation.medicaltaskscheduled.entity.Patient;
import com.graduation.medicaltaskscheduled.entity.dto.OperateType;
import com.graduation.medicaltaskscheduled.entity.dto.Result;
import com.graduation.medicaltaskscheduled.entity.vo.PatientQuery;
import com.graduation.medicaltaskscheduled.service.PatientService;
import io.swagger.annotations.ApiParam;
import jdk.nashorn.internal.objects.annotations.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @LogRecord(
            operateType = OperateType.MODIFY,
            operateDesc = "根据id修改患者信息",
            userType = OperateType.OTHER
    )
    public Result modifiedPatient(
            @ApiParam(value = "patient", name = "patient")
            @RequestBody Patient patient
    ) {
        log.error("patient="+patient.toString());
        boolean res = patientService.updateById(patient);
        return res ? Result.ok() : Result.error();
    }

    @PostMapping("patientListByQuery")
    @LogRecord(userType = OperateType.USER_TYPE_ADMIN,
            operateType = OperateType.READ,
            operateDesc = "管理员查询患者用户列表")
    public Result patientListQuery(
            @RequestBody PatientQuery patientQuery,
            @RequestParam(value = "current", defaultValue = "1") Long current,
            @RequestParam(value = "pageSize", defaultValue = "6") Long pageSize
    ) {
        Page<Patient> page = new Page<>(current, pageSize);
        patientService.getPatientListByQuery(page, patientQuery);
        Map<String, Object> map = new HashMap<>();
        map.put("total", page.getTotal());
        map.put("pageSize", page.getSize());
        map.put("current", page.getCurrent());
        map.put("patientList", page.getRecords());
        return Result.ok().data("map", map);
    }

    @GetMapping("deletePatient")
    @LogRecord(userType = OperateType.USER_TYPE_ADMIN,
            operateType = OperateType.DELETE,
            operateDesc = "管理员删除患者")
    public Result deletePatient(
            @ApiParam(value = "patientId", name = "patientId")
            @RequestParam String patientId
    ) {
        boolean res = patientService.removeById(patientId);
        return res ? Result.ok() : Result.error();
    }
}

