package com.graduation.medicaltaskscheduled.controller;

import com.graduation.medicaltaskscheduled.annotation.LogRecord;
import com.graduation.medicaltaskscheduled.entity.dto.OperateType;
import com.graduation.medicaltaskscheduled.entity.dto.Result;
import com.graduation.medicaltaskscheduled.service.LoginRegisterService;
import com.sun.istack.internal.NotNull;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author RabbitFaFa
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/loginRegister")
public class LoginRegisterController {

    @Autowired
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
    @LogRecord(userType = OperateType.OTHER,
            operateType = OperateType.REGISTER,
            operateDesc = "用户注册"
    )
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
    @LogRecord(userType = OperateType.OTHER,
            operateType = OperateType.LOGIN,
            operateDesc = "用户登录"
    )
    public Result unifyLogin(
            @RequestParam(value = "mobile", defaultValue = "") @NotNull @ApiParam("手机号码")
                    String mobile,
            @RequestParam(value = "pwd", defaultValue = "") @NotNull @ApiParam("登录密码")
                    String pwd,
            @RequestParam(value = "userLabel", defaultValue = "0") @NotNull @ApiParam("用户标识(0-管理员 1-医生 2-患者)")
                    String userLabel,
            HttpServletRequest request
    ) {
        log.warn("参数为=" + mobile + " " + pwd + " " + userLabel);
        String token = loginRegisterService.unifyUserLogin(mobile, pwd, userLabel, request);
        return Result.ok().data("token", token);
    }

//    @ApiOperation("统一用户信息获取")
//    @GetMapping("userInfo")
//    public Result unifyGetUserInfo(
//            @RequestParam(value = "token") @ApiParam("用户登录凭证") String token,
//            @RequestParam(value = "userLabel") @ApiParam("用户标识") String userLabel
//    ) {
//         loginRegisterService.unifyGetUserInfo(token, userLabel);
//
//    }

    /***
     * 用户统一登出API
     * @param token
     * @return
     */
    @ApiOperation("用户登出")
    @GetMapping("logout")
    @LogRecord(userType = OperateType.OTHER,
            operateType = OperateType.LOGOUT,
            operateDesc = "用户登出")
    public Result logout(
            @RequestParam(value = "token", defaultValue = "")
            @NotNull
            @ApiParam("token") String token
    ) {
        int res = loginRegisterService.logout(token);
        return res == 1 ? Result.ok() : Result.error();
    }
}
