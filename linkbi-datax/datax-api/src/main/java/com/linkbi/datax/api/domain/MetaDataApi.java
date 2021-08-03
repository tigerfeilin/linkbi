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
@TableName("metadata_appkey")
@EqualsAndHashCode(callSuper=false)
public class MetaDataApi extends Model<MetaDataApi> {
    /**
     * 自增主键
     */
    @TableId
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * appKey
     */
    private String jobAppkey;

    /**
     * 应用ID
     */
    private String projId;

    /**
     * 到期时间
     */
    private String authTime;

    /**
     * 状态
     */
    private String status;

}