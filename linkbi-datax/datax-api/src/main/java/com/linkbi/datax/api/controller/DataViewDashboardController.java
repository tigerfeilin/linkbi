package com.linkbi.datax.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.linkbi.common.annotation.Log;
import com.linkbi.common.core.domain.AjaxResult;
import com.linkbi.common.enums.BusinessType;
import com.linkbi.common.utils.StringUtils;
import com.linkbi.common.utils.uuid.IdUtils;
import com.linkbi.datax.api.domain.DataViewChart;
import com.linkbi.datax.api.domain.DataViewDashboard;
import com.linkbi.datax.api.domain.MetaDataSource;
import com.linkbi.datax.api.service.DataViewDashboardService;
import com.linkbi.datax.api.service.MetaDataSourceService;
import com.linkbi.datax.db.util.LocalCacheUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * dashboard控制器层
 *
 * @author
 * @version v1.0
 * @since 2019-07-30
 */
@Api(tags = "看板接口")
@RestController
@RequestMapping("/api/dashboard")
public class DataViewDashboardController extends BaseController {
    /**
     * 服务对象
     */
    @Autowired
    private DataViewDashboardService dataViewDashboardService;

    @GetMapping("/list")
    public AjaxResult listChart(@RequestParam(required = false, defaultValue = "0") int current,
                                            @RequestParam(required = false, defaultValue = "10") int size,
                                            String name, String isPrivate){
        Map<String, Object> list = dataViewDashboardService.pageDashboardList((current-1)*size,size,name,isPrivate);
        return AjaxResult.success(list);
    }
    @GetMapping("/getDashboardById/{dashboardId}")
    public AjaxResult getDashboardById(@PathVariable Long dashboardId){
        DataViewDashboard dashboard = dataViewDashboardService.getDashboardById(dashboardId);
        return AjaxResult.success(dashboard);
    }
    @PostMapping("/create")
    @PreAuthorize("@ss.hasPermi('dataview:dashboard:create')")
    @Log(title = "数据看板", businessType = BusinessType.INSERT)
    public AjaxResult insertDashboard(@Validated @RequestBody DataViewDashboard entity){
        if(StringUtils.isAllEmpty(entity.getName(),entity.getComment()))
            return AjaxResult.error("无效信息");
        return AjaxResult.success(dataViewDashboardService.insertDashboard(entity));
    }
    @PutMapping("/update")
    @PreAuthorize("@ss.hasPermi('dataview:dashboard:update')")
    @Log(title = "数据看板", businessType = BusinessType.UPDATE)
    public AjaxResult updateDashboard(@Validated @RequestBody DataViewDashboard entity){
        if(StringUtils.isEmpty(entity.getId()))
            return AjaxResult.error("无效信息");
        return AjaxResult.success(dataViewDashboardService.updateById(entity));
    }

    @DeleteMapping("/delete/{dashboardId}")
    @PreAuthorize("@ss.hasPermi('dataview:dashboard:delete')")
    @Log(title = "数据看板", businessType = BusinessType.DELETE)
    public AjaxResult deleteDashboard(@PathVariable Long dashboardId){
        return AjaxResult.success(dataViewDashboardService.removeById(dashboardId));
    }

    @PostMapping("/addChart")
    @PreAuthorize("@ss.hasPermi('dataview:dashboard:update')")
    @Log(title = "数据看板", businessType = BusinessType.INSERT)
    public AjaxResult addChart(Long chartId, Long dashboardId){
        if(StringUtils.isOneEmpty(chartId,dashboardId))
            return AjaxResult.error("无效信息");
        return AjaxResult.success(dataViewDashboardService.addChart(chartId,dashboardId));
    }
    @DeleteMapping("/deleteChart")
    @PreAuthorize("@ss.hasPermi('dataview:dashboard:update')")
    @Log(title = "数据看板", businessType = BusinessType.INSERT)
    public AjaxResult deleteChart(Long chartId, Long dashboardId){
        return AjaxResult.success(dataViewDashboardService.deleteChart(chartId,dashboardId));
    }
    @GetMapping("/listChartByDashboardId/{dashboardId}")
    //@PreAuthorize("@ss.hasPermi('dataview:dashboard:update')")
    public AjaxResult listChartByDashboardId(@PathVariable Long dashboardId){
        return AjaxResult.success(dataViewDashboardService.listChartByDashboardId(dashboardId));
    }
    @GetMapping("/listDashboardByChartId/{chartId}")
    //@PreAuthorize("@ss.hasPermi('dataview:dashboard:update')")
    public AjaxResult listDashboardByChartId(@PathVariable Long chartId){
        return AjaxResult.success(dataViewDashboardService.listDashboardByChartId(chartId));
    }
}