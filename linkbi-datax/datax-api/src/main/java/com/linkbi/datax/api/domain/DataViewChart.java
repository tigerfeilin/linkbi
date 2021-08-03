package com.linkbi.datax.api.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * metadata实体类(metadata_model_sql)
 *
 * @author
 * @version v1.0
 * @since 2021-04-22
 */

@Data
@TableName("dataview_chart")
@EqualsAndHashCode(callSuper=false)
public class DataViewChart extends Model<DataViewChart> {
    /**
     * id
     */
    @TableId
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    /**
     * modeId
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long modeId;
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