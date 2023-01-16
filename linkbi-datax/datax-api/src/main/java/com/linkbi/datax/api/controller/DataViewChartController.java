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
import com.linkbi.datax.api.domain.MetaDataSource;
import com.linkbi.datax.api.service.DataViewChartService;
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
 * chart控制器层
 *
 * @author
 * @version v1.0
 * @since 2019-07-30
 */
@Api(tags = "图表接口")
@RestController
@RequestMapping("/api/chart")
public class DataViewChartController extends BaseController {
    /**
     * 服务对象
     */
    @Autowired
    private DataViewChartService dataViewChartService;

    @GetMapping("/list")
    //@ApiOperation("获取metadata数据表")
    public AjaxResult listChart(@RequestParam(required = false, defaultValue = "0") int current,
                                             @RequestParam(required = false, defaultValue = "10") int size,
                                                Long modeId, Long sourceId, String chartName, String isPrivate){
        Map<String, Object> list = dataViewChartService.pageChartList((current-1)*size,size,modeId,sourceId,chartName,isPrivate);
        return AjaxResult.success(list);
    }
    @PostMapping("/create")
    @PreAuthorize("@ss.hasPermi('dataview:chart:create')")
    @Log(title = "数据报表", businessType = BusinessType.INSERT)
    public AjaxResult insertChart(@Validated @RequestBody DataViewChart entity){
        if(StringUtils.isAllEmpty(entity))
            return AjaxResult.error("无效数据");
        Long id = IdUtils.getId();
        entity.setId(id);
        if(dataViewChartService.insertChart(entity) > 0)
            return AjaxResult.success("操作成功",id.toString());
        else
            return AjaxResult.error("保存失败");
    }
    @PutMapping("/update")
    @PreAuthorize("@ss.hasPermi('dataview:chart:update')")
    @Log(title = "数据报表", businessType = BusinessType.INSERT)
    public AjaxResult updateChart(@Validated @RequestBody DataViewChart entity){
        return AjaxResult.success(dataViewChartService.updateById(entity));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("@ss.hasPermi('dataview:chart:delete')")
    @Log(title = "数据报表", businessType = BusinessType.INSERT)
    public AjaxResult deleteChart(@PathVariable Long id){
        return AjaxResult.success(dataViewChartService.removeById(id));
    }

    @GetMapping("/getChartById/{id}")
    public AjaxResult getChartById(@PathVariable Long id){
        DataViewChart chart = dataViewChartService.getById(id);
        return AjaxResult.success(chart);
    }

    @GetMapping("/parseSql")
    public AjaxResult parseSql(@RequestParam(required = false, defaultValue = "0") int current,
                               @RequestParam(required = false, defaultValue = "10") int size,
                               Long sourceId, String chartName, String isPrivate){
        //Map<String, Object> list = dataViewChartService.pageChartList((current-1)*size,size,sourceId,chartName,isPrivate);
        return AjaxResult.success();
    }
}