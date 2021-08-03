package com.linkbi.datax.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linkbi.datax.api.domain.DataViewChart;
import com.linkbi.datax.api.domain.DataViewDashboard;

import java.util.List;
import java.util.Map;

/**
 * jdbc数据源配置表服务接口
 *
 * @author
 * @version v2.0
 * @since 2020-01-10
 */
public interface DataViewDashboardService extends IService<DataViewDashboard> {
    /**
     * page list
     *
     * @param start
     * @param length
     * @param name
     * @param isPrivate
     * @return
     */
    Map<String, Object> pageDashboardList(int start, int length, String name, String isPrivate);
    int insertDashboard(DataViewDashboard entity);
    boolean addChart(Long chartId, Long dashboardId);
    boolean deleteChart(Long chartId, Long dashboardId);
    DataViewDashboard getDashboardById(Long dashboardId);
    List<DataViewChart> listChartByDashboardId(Long dashboardId);
    List<DataViewDashboard> listDashboardByChartId(Long chartId);
}