package com.graduation.medicaltaskscheduled.controller;


import com.graduation.medicaltaskscheduled.entity.Car;
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
    public Result getCars() {
        List<Car> carsList = carService.list(null);
        log.warn("获取医疗车");
        return Result.ok().data("carsList", carsList);
    }

    @PostMapping("modifiedCar")
    public Result modifiedCar(@RequestBody Car car) {
        if (StringUtils.isEmpty(car))
            throw new CustomException(ResultCode.REQUESTBODY_NULL_CAR, "未获取到医疗车");
        boolean res = carService.updateById(car);
        return res ? Result.ok() : Result.error();
    }

    @GetMapping("deleteCar")
    public Result deleteCar(
            @ApiParam(value = "carId", name = "carId", defaultValue = "", required = true)
            @RequestParam(value = "carId") String carId
    ) {
        boolean res = carService.removeById(carId);
        return res ? Result.ok() : Result.error();
    }

    @PostMapping("addCar")
    public Result addNewCar(@RequestBody Car car) {
        boolean res = carService.save(car);
        return res ? Result.ok() : Result.error();
    }

}

