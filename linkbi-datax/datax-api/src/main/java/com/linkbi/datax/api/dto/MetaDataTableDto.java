package com.linkbi.datax.api.dto;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.linkbi.datax.api.domain.MetaDataTable;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * metadata实体类(metadata_table)
 *
 * @author
 * @version v1.0
 * @since 2021-04-22
 */

@Data
@ApiModel
@EqualsAndHashCode(callSuper=false)
public class MetaDataTableDto extends Model<MetaDataTableDto> {

    private MetaDataTable metaData;
    private List<ColumnDescDto> columnList;
}