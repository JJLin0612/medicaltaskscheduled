package com.graduation.medicaltaskscheduled.controller;

import com.graduation.medicaltaskscheduled.entity.Admin;
import com.graduation.medicaltaskscheduled.entity.Doctor;
import com.graduation.medicaltaskscheduled.entity.Patient;
import com.graduation.medicaltaskscheduled.entity.dto.Result;
import com.graduation.medicaltaskscheduled.entity.dto.ResultCode;
import com.graduation.medicaltaskscheduled.entity.dto.UserLabel;
import com.graduation.medicaltaskscheduled.exception.CustomException;
import com.graduation.medicaltaskscheduled.service.LoginRegisterService;
import com.sun.istack.internal.NotNull;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author RabbitFaFa
 */
@RestController
@RequestMapping("/loginRegister")
public class LoginRegisterController {

    @Resource
    private LoginRegisterService loginRegisterService;


    /***
     * 用户统一注册API
     * @param mobile
     * @param pwd
     * @param verifyCode
     * @param userLabel
     * @return
     */
    @ApiOperation("统一用户注册")
    @PostMapping("register")
    public Result unifyRegister(
            @RequestParam(value = "mobile", defaultValue = "") @NotNull @ApiParam("手机号码")
                    String mobile,
            @RequestParam(value = "pwd", defaultValue = "") @NotNull @ApiParam("登录密码")
                    String pwd,
            @RequestParam(value = "verifyCode", defaultValue = "") @NotNull @ApiParam("手机验证码")
                    String verifyCode,
            @RequestParam(value = "userLabel", defaultValue = "0") @NotNull @ApiParam("用户标识(0-管理员 1-医生 2-患者)")
                    String userLabel
    ) {
        String acc = loginRegisterService.unifyUserRegister(mobile, pwd, verifyCode, userLabel);
        return Result.ok().data("acc", acc);
    }

    /***
     * 用户统一登录API
     * @param mobile
     * @param pwd
     * @param userLabel
     * @param request
     * @return
     */
    @ApiOperation("统一用户登录")
    @PostMapping("login")
    public Result unifyLogin(
            @RequestParam(value = "mobile", defaultValue = "") @NotNull @ApiParam("手机号码")
                    String mobile,
            @RequestParam(value = "pwd", defaultValue = "") @NotNull @ApiParam("登录密码")
                    String pwd,
            @RequestParam(value = "userLabel", defaultValue = "0") @NotNull @ApiParam("用户标识(0-管理员 1-医生 2-患者)")
                    String userLabel,
            HttpServletRequest request
    ) {
        String token = loginRegisterService.unifyUserLogin(mobile, pwd, userLabel);
        return Result.ok().data("token", token);
    }

    /***
     * 用户统一登出API
     * @param id
     * @return
     */
    @ApiOperation("用户登出")
    @GetMapping("logout")
    public Result logout(
            @RequestParam(value = "id", defaultValue = "")
            @NotNull
            @ApiParam("用户id") String id
    ) {
        int res = loginRegisterService.logout(id);
        return res == 1 ? Result.ok() : Result.error();
    }
}
