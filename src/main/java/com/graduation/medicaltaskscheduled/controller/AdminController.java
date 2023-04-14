package com.graduation.medicaltaskscheduled.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.medicaltaskscheduled.annotation.LogRecord;
import com.graduation.medicaltaskscheduled.entity.Admin;
import com.graduation.medicaltaskscheduled.entity.Appointment;
import com.graduation.medicaltaskscheduled.entity.dto.OperateType;
import com.graduation.medicaltaskscheduled.entity.dto.Result;
import com.graduation.medicaltaskscheduled.entity.vo.AdminQuery;
import com.graduation.medicaltaskscheduled.entity.vo.PatientQuery;
import com.graduation.medicaltaskscheduled.entity.vo.ScheduleVo;
import com.graduation.medicaltaskscheduled.service.AdminService;
import com.graduation.medicaltaskscheduled.utils.JwtUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        Admin admin =  adminService.getAdminByToken(token);
        return Result.ok().data("roles", "admin")
                .data("name", admin.getName())
                .data("avatar", admin.getAvatar());
    }

    @GetMapping("getAdminIdByToken")
    @LogRecord(operateType = OperateType.READ,
            operateDesc = "根据token获取管理员id")
    public Result getByToken(@RequestParam(value = "token") String token) {
        String adminId = JwtUtils.getDataIdByTokenStr(token);
        return Result.ok().data("adminId", adminId);
    }

    @PostMapping("getAdminByQuery")
    @LogRecord(operateType = OperateType.READ,
            operateDesc = "多条件组合分页查询")
    public Result getAdminByQuery(
            @RequestBody AdminQuery adminQuery,
            @RequestParam(value = "current", defaultValue = "1") Long current,
            @RequestParam(value = "pageSize", defaultValue = "6") Long pageSize
    ) {
        Page<Admin> page = new Page<>(current, pageSize);
        adminService.adminListQuery(page, adminQuery);
        Map<String, Object> map = new HashMap();
        map.put("total", page.getTotal());
        map.put("pageSize", page.getSize());
        map.put("current", page.getCurrent());
        map.put("adminList", page.getRecords());
        return Result.ok().data("map", map);
    }

    @PostMapping("modifiedAdmin")
    @LogRecord(userType = OperateType.USER_TYPE_ADMIN,
            operateType = OperateType.MODIFY,
            operateDesc = "修改其他管理员信息")
    public Result modifiedAdmin(@RequestBody Admin admin) {
        boolean res = adminService.updateById(admin);
        return res ? Result.ok() : Result.error();
    }

    /***
     * 获取任务队列
     * @return
     */
    @GetMapping("taskQueue")
    @LogRecord(operateType = OperateType.READ,
            operateDesc = "获取当前预约队列")
    public Result getTaskQueue() {
        List<Appointment> appointmentList = adminService.getTaskQueue();
        log.warn(appointmentList.size() + "");
        return Result.ok().data("taskList", appointmentList);
    }


    @PostMapping("taskScheduled")
    @LogRecord(operateType = OperateType.OTHER,
            operateDesc = "执行任务调度")
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

