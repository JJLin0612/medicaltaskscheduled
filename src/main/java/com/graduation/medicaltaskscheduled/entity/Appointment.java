package com.graduation.medicaltaskscheduled.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author RabbitFaFa
 * @since 2023-04-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Appointment对象", description="")
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "患者预约记录表id")
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "患者id")
    private String patientId;

    @ApiModelProperty(value = "服务的医疗车id")
    private String carId;

    @ApiModelProperty(value = "患者血压")
    private String pressure;

    @ApiModelProperty(value = "患者心率")
    private String heartRate;

    @ApiModelProperty(value = "患者血糖")
    private String sugar;

    @ApiModelProperty(value = "患者呼吸率")
    private String breathRate;

    @ApiModelProperty(value = "患者体温")
    private String temperature;

    @ApiModelProperty(value = "患者氧饱和度")
    private String oxygen;

    @ApiModelProperty(value = "其他生理数据")
    private String other;

    @ApiModelProperty(value = "位置横坐标")
    private Double locatX;

    @ApiModelProperty(value = "位置纵坐标")
    private Double locatY;

    @ApiModelProperty(value = "0-未服务 1-服务")
    private Boolean isScheduled;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date gmtModified;


}
