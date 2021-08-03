package com.linkbi.datax.api.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.linkbi.datax.api.core.handler.AESEncryptHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * jdbc数据源配置实体类(metadata_data_source)
 *
 * @author
 * @version v1.0
 * @since 2019-07-30
 */

@Data
@ApiModel
@TableName("metadata_data_source")
@EqualsAndHashCode(callSuper=false)
public class MetaDataSource extends Model<MetaDataSource> {

    /**
     * 自增主键
     */
    @TableId
    @ApiModelProperty(value = "自增主键")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 数据源名称
     */
    @ApiModelProperty(value = "数据源名称")
    private String datasourceName;

    /**
     * 数据源
     */
    @ApiModelProperty(value = "数据源")
    private String datasource;

    /**
     * 数据源分组
     */
    @ApiModelProperty(value = "数据源分组")
    private String datasourceGroup;

    /**
     * 用户名
     * AESEncryptHandler 加密类
     * MyBatis Plus 3.0.7.1之前版本没有typeHandler属性，需要升级到最低3.1.2
     */
    @ApiModelProperty(value = "用户名")
    @TableField(typeHandler = AESEncryptHandler.class)
    private String jdbcUsername;

    /**
     * 密码
     */
    @TableField(typeHandler = AESEncryptHandler.class)
    @ApiModelProperty(value = "密码")
    private String jdbcPassword;

    /**
     * jdbc url
     */
    @ApiModelProperty(value = "jdbc url")
    private String jdbcUrl;

    /**
     * jdbc驱动类
     */
    @ApiModelProperty(value = "jdbc驱动类")
    private String jdbcDriverClass;

    /**
     * 状态：0删除 1启用 2禁用
     */
    @TableLogic
    @ApiModelProperty(value = "状态：0删除 1启用 2禁用")
    private Integer status;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人", hidden = true)
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间", hidden = true)
    private Date createDate;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "更新人", hidden = true)
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间", hidden = true)
    private Date updateDate;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", hidden = true)
    private String comments;

    /**
     * zookeeper地址
     */
    @ApiModelProperty(value = "zookeeper地址", hidden = true)
    private String zkAdress;

    /**
     * 数据库名
     */
    @ApiModelProperty(value = "数据库名", hidden = true)
    private String databaseName;
    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}