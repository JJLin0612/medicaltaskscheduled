package com.graduation.medicaltaskscheduled.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
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
@ApiModel(value="Webaccesscount对象", description="")
public class Webaccesscount implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "网站访问统计的id")
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "独立IP访问量")
    private Integer ip;

    @ApiModelProperty(value = "独立页面访问量")
    private Integer uv;

    @ApiModelProperty(value = "页面访问量")
    private Integer pv;

    @ApiModelProperty(value = "记录保存时间")
    private Date gmtCreate;


}
