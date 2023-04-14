package com.graduation.medicaltaskscheduled.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.medicaltaskscheduled.annotation.LogRecord;
import com.graduation.medicaltaskscheduled.entity.Appointment;
import com.graduation.medicaltaskscheduled.entity.dto.OperateType;
import com.graduation.medicaltaskscheduled.entity.dto.Result;
import com.graduation.medicaltaskscheduled.service.AppointmentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author RabbitFaFa
 * @since 2023-04-05
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Resource
    private AppointmentService appointmentService;

    @PostMapping("submitAppointment")
    @LogRecord(userType = OperateType.USER_TYPE_PATIENT,
            operateType = OperateType.ADD,
            operateDesc = "提交预约申请")
    public Result appointment(
            @RequestBody Appointment appointment,
            @RequestParam(value = "token") String token
    ) {
        appointmentService.putTaskQueue(appointment, token);
        return Result.ok();
    }

    @ApiOperation("分页查询预约记录")
    @GetMapping("appointmentListByPage")
    @LogRecord(userType = OperateType.USER_TYPE_PATIENT,
            operateType = OperateType.READ,
            operateDesc = "分页查询用户自身预约记录")
    public Result appointmentPage(
            @ApiParam(value = "pageCurr", name = "pageCurr", defaultValue = "1", required = true)
            @RequestParam(value = "pageCurr") Long pageCurr,
            @ApiParam(value = "pageSize", name = "pageSize", defaultValue = "5", required = true)
            @RequestParam(value = "pageSize") Long pageSize,
            @ApiParam(value = "patientId", name = "patientId", defaultValue = "", required = true)
            @RequestParam(value = "patientId") String patientId
    ) {
        Page<Appointment> page = new Page<>(pageCurr, pageSize);
        appointmentService.appointmentPages(page, patientId);
        List<Appointment> list = page.getRecords();
        log.error(list.toString());
        Map<String, Object> map = new HashMap<>();
        map.put("hasPrevious", page.hasPrevious());
        map.put("total", page.getTotal());
        map.put("pages", page.getPages());
        map.put("current", page.getCurrent());
        map.put("size", page.getSize());
        map.put("hasNext", page.hasNext());
        map.put("appointmentList", page.getRecords());
        log.warn("id=" + patientId);
        log.warn(map.toString());
        return Result.ok().data("map", map);
    }

    @GetMapping("allAppointmentList")
    public Result allAppointmentList(
            @ApiParam(value = "patientId", name = "patientId", defaultValue = "", required = true)
            @RequestParam(value = "patientId") String patientId
    ) {
        List<Appointment> allAppointmentList = appointmentService.getAllAppointmentList(patientId);
        log.error(allAppointmentList.toString());
        return Result.ok().data("allAppointmentList", allAppointmentList);
    }
}

