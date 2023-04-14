package com.graduation.medicaltaskscheduled.controller;


import com.graduation.medicaltaskscheduled.annotation.LogRecord;
import com.graduation.medicaltaskscheduled.entity.Car;
import com.graduation.medicaltaskscheduled.entity.dto.OperateType;
import com.graduation.medicaltaskscheduled.entity.dto.Result;
import com.graduation.medicaltaskscheduled.entity.dto.ResultCode;
import com.graduation.medicaltaskscheduled.exception.CustomException;
import com.graduation.medicaltaskscheduled.service.CarService;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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
@RequestMapping("/car")
public class CarController {

    @Resource
    private CarService carService;

    /***
     * 无条件查询所有医疗车
     * @return
     */
    @GetMapping("carList")
    @LogRecord(operateType = OperateType.READ,
            operateDesc = "查询所有医疗车")
    public Result getCars() {
        List<Car> carsList = carService.list(null);
        log.warn("获取医疗车");
        return Result.ok().data("carsList", carsList);
    }

    @PostMapping("modifiedCar")
    @LogRecord(operateType = OperateType.MODIFY,
            operateDesc = "修改医疗车信息")
    public Result modifiedCar(@RequestBody Car car) {
        if (StringUtils.isEmpty(car))
            throw new CustomException(ResultCode.REQUESTBODY_NULL_CAR, "未获取到医疗车");
        boolean res = carService.updateById(car);
        return res ? Result.ok() : Result.error();
    }

    @GetMapping("deleteCar")
    @LogRecord(operateType = OperateType.DELETE,
            operateDesc = "弃用医疗车")
    public Result deleteCar(
            @ApiParam(value = "carId", name = "carId", defaultValue = "", required = true)
            @RequestParam(value = "carId") String carId
    ) {
        boolean res = carService.removeById(carId);
        return res ? Result.ok() : Result.error();
    }

    @PostMapping("addCar")
    @LogRecord(operateType = OperateType.ADD,
            operateDesc = "新增医疗车")
    public Result addNewCar(@RequestBody Car car) {
        boolean res = carService.save(car);
        return res ? Result.ok() : Result.error();
    }

}

