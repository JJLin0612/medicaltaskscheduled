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
@ApiModel(value="Admin对象", description="")
public class Admin implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "管理员表id")
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "管理员手机号码")
    private String mobile;

    @ApiModelProperty(value = "管理员账号")
    private String acc;

    @ApiModelProperty(value = "管理员登录密码")
    private String pwd;

    @ApiModelProperty(value = "管理员姓名")
    private String name;

    @ApiModelProperty(value = "出生日期")
    private Date birthday;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "是否禁用(0不禁用，1禁用)")
    private Boolean isDisabled;

    @ApiModelProperty(value = "是否逻辑删除(0未删除，1删除)")
    private Boolean isDeleted;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date gmtModified;


}
