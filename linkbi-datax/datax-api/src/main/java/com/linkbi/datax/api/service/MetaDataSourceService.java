package com.linkbi.datax.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linkbi.datax.api.domain.MetaDataSource;

import java.io.IOException;
import java.util.List;

/**
 * jdbc数据源配置表服务接口
 *
 * @author
 * @version v2.0
 * @since 2020-01-10
 */
public interface MetaDataSourceService extends IService<MetaDataSource> {
    /**
     * 测试数据源
     * @param jdbcDatasource
     * @return
     */
    Boolean dataSourceTest(MetaDataSource jdbcDatasource) throws IOException;

    /**
     *更新数据源信息
     * @param datasource
     * @return
     */
    int update(MetaDataSource datasource);

    /**
     * 获取所有数据源
     * @return
     */
    List<MetaDataSource> selectAllDatasource();
}