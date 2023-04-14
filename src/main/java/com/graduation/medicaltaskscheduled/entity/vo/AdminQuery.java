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
public class AdminQuery {
    @ApiModelProperty(value = "昵称")
    private String name;

    @ApiModelProperty(value = "是否禁用")
    private Boolean isDisabled;

    @ApiModelProperty(value = "起始时间", example = "2019-01-01 10:10:10")
    private Date beginTime;

    @ApiModelProperty(value = "结束时间", example = "2019-01-01 10:10:10")
    private Date endTime;
}
