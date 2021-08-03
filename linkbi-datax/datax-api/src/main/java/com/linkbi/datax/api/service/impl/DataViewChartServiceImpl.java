package com.linkbi.datax.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkbi.common.core.redis.RedisCache;
import com.linkbi.common.utils.SecurityUtils;
import com.linkbi.common.utils.StringUtils;
import com.linkbi.common.utils.uuid.IdUtils;
import com.linkbi.datax.api.domain.DataViewChart;
import com.linkbi.datax.api.domain.MetaDataMarket;
import com.linkbi.datax.api.mapper.DataViewChartMapper;
import com.linkbi.datax.api.mapper.MetaDataMarketMapper;
import com.linkbi.datax.api.service.DataViewChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created 2020/01/30
 */
@Service
@Transactional(readOnly = true)
public class DataViewChartServiceImpl extends ServiceImpl<DataViewChartMapper, DataViewChart> implements DataViewChartService {

    @Resource
    private DataViewChartMapper dataViewChartMapper;
    @Autowired
    private RedisCache redisCache;

    @Override
    public Map<String, Object> pageChartList(int start, int length,Long modeId,  Long sourceId, String chartName, String isPrivate) {
        // page list
        Long usrId = SecurityUtils.getLoginUser().getUser().isAdmin()? null:SecurityUtils.getLoginUser().getUser().getUserId();
        List<DataViewChart> list = this.dataViewChartMapper.pageChartList(start, length,modeId, sourceId, chartName, isPrivate,usrId);
        int list_count = this.dataViewChartMapper.pageChartListCount(modeId,sourceId, chartName, isPrivate,usrId);
        // package result
        Map<String, Object> maps = new HashMap<>();
        maps.put("total", list_count);        // 总记录数
        maps.put("rows", list);                    // 分页列表
        return maps;
    }
    @Override
    @Transactional
    public int insertChart(DataViewChart entity)
    {
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(SecurityUtils.getLoginUser().getUser().getUserId());
        return this.dataViewChartMapper.insert(entity);
    }
}