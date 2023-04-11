package com.graduation.medicaltaskscheduled.controller;


import com.graduation.medicaltaskscheduled.entity.Appointment;
import com.graduation.medicaltaskscheduled.entity.dto.Result;
import com.graduation.medicaltaskscheduled.entity.vo.ScheduleVo;
import com.graduation.medicaltaskscheduled.service.AdminService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author RabbitFaFa
 * @since 2023-03-29
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @ApiOperation("管理员信息获取")
    @GetMapping("adminInfo")
    public Result adminInfo(
            @RequestParam(value = "token") @ApiParam("用户登录凭证") String token
    ) {
        return Result.ok().data("roles", "admin")
                .data("name", "JJLin")
                .data("avatar", "JJJJJ");
    }

    /***
     * 获取任务队列
     * @return
     */
    @GetMapping("taskQueue")
    public Result getTaskQueue() {
        List<Appointment> appointmentList = adminService.getTaskQueue();
        log.warn(appointmentList.size() + "");
        return Result.ok().data("taskList", appointmentList);
    }


    @PostMapping("taskScheduled")
    public Result scheduleTask(@RequestBody ScheduleVo scheduleVo) throws ExecutionException, InterruptedException {
        List<Integer> path =
                adminService.taskScheduled(scheduleVo.getCarIdList(), scheduleVo.getAppointmentList());
        log.warn("路径path=" + path.toString());
        return Result.ok().data("path", path);
    }

    @PostMapping("testTransferData")
    public Result testTransferData(@RequestBody List<Appointment> list) {
        log.warn(list.toString());
        return Result.ok();
    }

    @PostMapping("ackScheduled")
    public Result ackScheduled(@RequestBody List<String> appointmentIdList) {
        adminService.ackScheduled(appointmentIdList);
        log.warn(appointmentIdList.toString());
        return Result.ok();
    }
}

