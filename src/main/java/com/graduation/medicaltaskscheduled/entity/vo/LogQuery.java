package com.graduation.medicaltaskscheduled.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author RabbitFaFa
 */
@Data
@ApiModel
public class LogQuery {

    @ApiModelProperty(value = "操作者类别")
    private String userType;

    @ApiModelProperty(value = "操作类型")
    private String opType;

    @ApiModelProperty(value = "操作者账号")
    private String opAcc;

    @ApiModelProperty(value = "操作的结果")
    private String status;

    @ApiModelProperty(value = "操作耗用时间下限")
    private Integer costTimeSuperLimit;

    @ApiModelProperty(value = "操作耗用时间上限")
    private Integer costTimeLowerLimit;

    @ApiModelProperty(value = "起始时间", example = "2019-01-01 10:10:10")
    private Date beginTime;

    @ApiModelProperty(value = "结束时间", example = "2019-01-01 10:10:10")
    private Date endTime;

}
