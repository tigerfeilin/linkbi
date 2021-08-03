package com.linkbi.datax.api.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * xxl-job log for glue, used to track job code process
 *
 * @author xuxueli 2016-5-19 17:57:46
 */
@Data
public class JobLogGlue {

    @TableId
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty("任务主键ID")
    private Long jobId;

    @ApiModelProperty("GLUE类型\t#com.xxl.job.core.glue.GlueTypeEnum")
    private String glueType;

    @ApiModelProperty("GLUE源代码")
    private String glueSource;

    @ApiModelProperty("GLUE备注")
    private String glueRemark;

    private Date addTime;

    private Date updateTime;

}
