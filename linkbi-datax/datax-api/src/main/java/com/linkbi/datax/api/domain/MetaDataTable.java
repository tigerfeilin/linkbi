package com.linkbi.datax.api.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;

/**
 * metadata实体类(metadata_table)
 *
 * @author
 * @version v1.0
 * @since 2021-04-22
 */

@Data
@TableName("metadata_table")
@EqualsAndHashCode(callSuper=false)
public class MetaDataTable extends Model<MetaDataTable> {
    /**
     * 自增主键
     */
    @TableId
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 数据源ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long datasourceId;

    /**
     * 数据源名称
     */
    private String datasourceName;

    /**
     * 数据库名称
     */
    private String databaseName;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 中文名称
     */
    private String tableChName;

    /**
     * 表描述
     */
    private String tableComment;

    /**
     * 表类型
     */
    private String tableType;

    /**
     * 表周期
     */
    private String tableCycle;

    /**
     * 表状态
     */
    private String tableStatus;

    /**
     * 表权限
     */
    private String tablePermission;

    /**
     * 列json
     */
    private String columnJson;
    /**
     * 更新人
     */
    private String updateUser;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    //@JSONField(format = "yyyy/MM/dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}