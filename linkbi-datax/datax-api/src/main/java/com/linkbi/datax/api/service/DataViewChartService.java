package com.linkbi.datax.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linkbi.datax.api.domain.DataViewChart;
import com.linkbi.datax.api.domain.MetaDataMarket;

import java.util.List;
import java.util.Map;


/**
 * jdbc数据源配置表服务接口
 *
 * @author
 * @version v2.0
 * @since 2020-01-10
 */
public interface DataViewChartService extends IService<DataViewChart> {
    /**
     * page list
     *
     * @param start
     * @param length
     * @param sourceId
     * @param chartName
     * @param isPrivate
     * @return
     */
    Map<String, Object> pageChartList(int start, int length,Long modeId, Long sourceId, String chartName, String isPrivate);
    int insertChart(DataViewChart entity);
}