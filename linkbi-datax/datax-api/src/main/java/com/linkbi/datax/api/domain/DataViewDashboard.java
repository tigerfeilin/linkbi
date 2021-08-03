package com.linkbi.datax.api.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
@Data
@TableName("dataview_dashboard")
@EqualsAndHashCode(callSuper=false)
public class DataViewDashboard {
    @TableId
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private String name;
    private String comment;
    private String content;
    private String isPrivate;
    private String status;
    /**
     * 更新人
     */
    private Long updateUser;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
