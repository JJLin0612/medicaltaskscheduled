package com.graduation.medicaltaskscheduled.controller;


import com.graduation.medicaltaskscheduled.annotation.LogRecord;
import com.graduation.medicaltaskscheduled.entity.dto.OperateType;
import com.graduation.medicaltaskscheduled.entity.dto.Result;
import com.graduation.medicaltaskscheduled.service.IpWhiteService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author RabbitFaFa
 * @since 2023-04-13
 */
@CrossOrigin
@RestController
@RequestMapping("/ipWhite")
public class IpWhiteController {

    @Resource
    private IpWhiteService ipWhiteService;

    @ApiOperation("查询所有白名单IP")
    @GetMapping("whiteIpList")
    @LogRecord(operateType = OperateType.READ, operateDesc = "查询所有白名单IP")
    public Result getWhiteIPList() {
        return Result.ok().data("whiteIPList", ipWhiteService.list(null));
    }

    @ApiOperation("新增用户IP到白名单中")
    @GetMapping("addWhiteList")
    @LogRecord(operateType = OperateType.ADD, operateDesc = "新增用户IP白名单")
    public Result addUserWhiteList(@RequestParam(value = "ipAddr") String ipAddr) {
        boolean res = ipWhiteService.addUserWhiteList(ipAddr);
        return res ? Result.ok() : Result.error();
    }

    @ApiOperation("从白名单中删除此IP")
    @GetMapping("deleteWhiteIP")
    @LogRecord(operateType = OperateType.DELETE, operateDesc = "移除白名单指定IP")
    public Result deleteWhiteIP(@RequestParam(value = "ipAddr") String ipAddr) {
        boolean res = ipWhiteService.deleteUserWhiteList(ipAddr);
        return res ? Result.ok() : Result.error();
    }
}

