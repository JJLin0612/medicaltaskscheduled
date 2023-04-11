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
 * @since 2023-03-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Doctor对象", description="")
public class Doctor implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "医生表主键值id")
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "医生的工号")
    private String workId;

    @ApiModelProperty(value = "医生姓名")
    private String name;

    @ApiModelProperty(value = "登录密码")
    private String pwd;

    @ApiModelProperty(value = "医生性别")
    private Boolean sex;

    @ApiModelProperty(value = "医生出生年月日")
    private Date birthday;

    @ApiModelProperty(value = "医生头像")
    private String avatar;

    @ApiModelProperty(value = "是否禁用(0不禁用，1禁用)")
    private Boolean isDisabled;

    @ApiModelProperty(value = "逻辑删除(0不删除，1删除)")
    private Boolean isDeleted;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date gmtCreated;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date gmtModified;


}
