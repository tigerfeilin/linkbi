package com.linkbi.datax.api.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created 2020/05/24
 */
@Data
public class JobProject {

    @TableId
    @ApiModelProperty("项目Id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty("项目名称")
    private String name;

    @ApiModelProperty("项目描述")
    private String description;

    @ApiModelProperty("用户Id")
    private long userId;

    @ApiModelProperty("标记")
    private Integer flag;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @TableField(exist=false)
    private String userName;

}
