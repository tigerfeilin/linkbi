package com.linkbi.datax.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linkbi.datax.api.domain.MetaDataSource;
import org.apache.ibatis.annotations.Mapper;

/**
 * jdbc数据源配置表数据库访问层
 *
 * @author
 * @version v1.0
 * @since 2019-07-30
 */
@Mapper
public interface MetaDataSourceMapper extends BaseMapper<MetaDataSource> {
    int update(MetaDataSource datasource);

}