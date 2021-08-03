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
 * metadata实体类(metadata_data_market)
 *
 * @author
 * @version v1.0
 * @since 2021-04-22
 */

@Data
@TableName("metadata_data_market")
@EqualsAndHashCode(callSuper=false)
public class MetaDataMarket extends Model<MetaDataMarket> {
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
     * schema名称
     */
    private String schemaName;

    /**
     * 模型名称
     */
    private String modeName;

    /**
     * 模型类型
     */
    private String modeType;

    /**
     * 查询类型
     */
    private String queryType;

    /**
     * 查询sql
     */
    private String querySql;

    /**
     * 视图类型
     */
    private String viewType;

    /**
     * 权限类型
     */
    private String permission;
    /**
     * 更新人
     */
    private Long updateUser;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    //@JSONField(format = "yyyy/MM/dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}