package com.linkbi.datax.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkbi.common.core.redis.RedisCache;
import com.linkbi.common.utils.SecurityUtils;
import com.linkbi.common.utils.StringUtils;
import com.linkbi.common.utils.uuid.IdUtils;
import com.linkbi.datax.api.core.util.JacksonUtil;
import com.linkbi.datax.api.domain.DataViewChart;
import com.linkbi.datax.api.domain.DataViewDashboard;
import com.linkbi.datax.api.dto.DashboardContent;
import com.linkbi.datax.api.dto.DashboardLayout;
import com.linkbi.datax.api.mapper.DataViewChartMapper;
import com.linkbi.datax.api.mapper.DataViewDashboardMapper;
import com.linkbi.datax.api.service.DataViewChartService;
import com.linkbi.datax.api.service.DataViewDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created 2020/01/30
 */
@Service
@Transactional(readOnly = true)
public class DataViewDashboardServiceImpl extends ServiceImpl<DataViewDashboardMapper, DataViewDashboard> implements DataViewDashboardService {

    @Resource
    private DataViewDashboardMapper dataViewDashboardMapper;
    @Resource
    private DataViewChartMapper dataViewChartMapper;
    @Autowired
    private RedisCache redisCache;

    @Override
    public Map<String, Object> pageDashboardList(int start, int length, String name, String isPrivate) {
        // page list
        Long usrId = SecurityUtils.getLoginUser().getUser().isAdmin()? null:SecurityUtils.getLoginUser().getUser().getUserId();
        List<DataViewDashboard> list = this.dataViewDashboardMapper.pageDashboardList(start, length, name, isPrivate,usrId);
        int list_count = this.dataViewDashboardMapper.pageDashboardListCount(name, isPrivate,usrId);
        // package result
        Map<String, Object> maps = new HashMap<>();
        maps.put("total", list_count);        // 总记录数
        maps.put("rows", list);                    // 分页列表
        return maps;
    }
    @Override
    @Transactional
    public int insertDashboard(DataViewDashboard entity)
    {
        entity.setId(IdUtils.getId());
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(SecurityUtils.getLoginUser().getUser().getUserId());
        return this.dataViewDashboardMapper.insert(entity);
    }
    @Override
    @Transactional
    public boolean addChart(Long chartId, Long dashboardId)
    {
        DashboardLayout dashboardLayout = new DashboardLayout();
        dashboardLayout.setId(chartId.toString());
        dashboardLayout.setI(chartId.toString());
        DataViewDashboard dashboard = this.getById(dashboardId);
        DashboardContent content = new DashboardContent();
        if(dashboard != null)
        {
            if(StringUtils.isNotEmpty(dashboard.getContent()))
            {
                content = JacksonUtil.readValue(dashboard.getContent(),DashboardContent.class);
                if(content != null)
                {
                    if(content.getLayout()!=null)
                        content.getLayout().add(dashboardLayout);
                }
                else{
                    content = new DashboardContent();
                    List<DashboardLayout> layouts = new ArrayList<>();
                    layouts.add(dashboardLayout);
                    content.setLayout(layouts);
                }
            }
            else{
                List<DashboardLayout> layouts = new ArrayList<>();
                layouts.add(dashboardLayout);
                content.setLayout(layouts);
            }
            String a1 = JacksonUtil.writeValueAsString(dashboardLayout);
            dashboard.setContent(a1);
            dashboard.setContent(JacksonUtil.writeValueAsString(content));
            dashboard.setUpdateTime(new Date());
            dashboard.setUpdateUser(SecurityUtils.getLoginUser().getUser().getUserId());
            return this.updateById(dashboard);
        }
        else
            return false;
    }
    @Override
    @Transactional
    public boolean deleteChart(Long chartId, Long dashboardId)
    {
        DashboardLayout dashboardLayout = new DashboardLayout();
        dashboardLayout.setId(chartId.toString());
        dashboardLayout.setI(chartId.toString());
        DataViewDashboard dashboard = this.getById(dashboardId);
        DashboardContent content = new DashboardContent();
        if(dashboard != null)
        {
            if(StringUtils.isNotEmpty(dashboard.getContent()))
            {
                content = JacksonUtil.readValue(dashboard.getContent(),DashboardContent.class);
                if(content != null)
                {
                    List<DashboardLayout> layouts = content.getLayout();
                    if(layouts != null)
                        layouts.removeIf(p -> p.getId() != null && p.getId().equals(chartId.toString()));
                    content.setLayout(layouts);
                }
                else{
                    return true;
                }
            }
            else{
                return true;
            }
            dashboard.setContent(JacksonUtil.writeValueAsString(content));
            dashboard.setUpdateTime(new Date());
            dashboard.setUpdateUser(SecurityUtils.getLoginUser().getUser().getUserId());
            return this.updateById(dashboard);
        }
        else
            return false;
    }
    @Override
    public List<DataViewChart> listChartByDashboardId(Long dashboardId)
    {
        List<DataViewChart> charts = new ArrayList<>();
        DataViewDashboard dashboard = this.getById(dashboardId);
        if(dashboard != null)
        {
            if(StringUtils.isNotEmpty(dashboard.getContent()))
            {
                DashboardContent content = JacksonUtil.readValue(dashboard.getContent(),DashboardContent.class);
                if(content != null)
                {
                    List<DashboardLayout> layouts = content.getLayout();
                    if(layouts != null)
                    for (DashboardLayout layout:layouts) {
                        DataViewChart chart = dataViewChartMapper.selectById(Long.parseLong(layout.getId()));
                        if(chart != null)
                            charts.add(chart);
                    }
                }
                else{
                    return charts;
                }
            }
            else{
                return charts;
            }
        }
        return charts;
    }
    @Override
    public List<DataViewDashboard> listDashboardByChartId(Long chartId)
    {
        return new ArrayList<>();
    }
    @Override
    public DataViewDashboard getDashboardById(Long dashboardId)
    {
        return this.dataViewDashboardMapper.getDashboardById(dashboardId);
    }
}