package com.graduation.medicaltaskscheduled.controller;


import com.graduation.medicaltaskscheduled.annotation.LogRecord;
import com.graduation.medicaltaskscheduled.entity.Webaccesscount;
import com.graduation.medicaltaskscheduled.entity.dto.OperateType;
import com.graduation.medicaltaskscheduled.entity.dto.Result;
import com.graduation.medicaltaskscheduled.entity.vo.WebAccessCountQuery;
import com.graduation.medicaltaskscheduled.service.WebaccesscountService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author RabbitFaFa
 */
@CrossOrigin
@RestController
@RequestMapping("/webaccesscount")
public class WebaccesscountController {

    @Resource
    private WebaccesscountService accessService;

    /***
     * 根据查询实体查询网站的IP UV PV
     * @param query 查询实体
     * @return 查询日期范围内网站每日/当前的 IP UV PV
     */
    @ApiOperation("根据查询实体查询网站的IP UV PV")
    @PostMapping
    @LogRecord(operateType = OperateType.READ, operateDesc = "查询IP UV PV")
    public Result getAccessCount(@RequestBody WebAccessCountQuery query) {
        List<Webaccesscount> accessList = accessService.getAccessCount(query);
        return Result.ok().data("accessList", accessList);
    }

}

