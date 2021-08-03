package com.linkbi.datax.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkbi.common.core.redis.RedisCache;
import com.linkbi.common.utils.SecurityUtils;
import com.linkbi.common.utils.StringUtils;
import com.linkbi.common.utils.uuid.IdUtils;
import com.linkbi.datax.api.domain.MetaDataMarket;
import com.linkbi.datax.api.mapper.MetaDataMarketMapper;
import com.linkbi.datax.api.service.JDBCQueryService;
import com.linkbi.datax.api.service.MetaDataMarketService;
import com.linkbi.datax.api.service.MetaDataSourceService;
import com.linkbi.datax.db.service.IMetaDataService;
import com.linkbi.datax.db.service.impl.CommonMetaDataServiceImpl;
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
public class MetaDataMarketServiceImpl extends ServiceImpl<MetaDataMarketMapper, MetaDataMarket> implements MetaDataMarketService {

    @Resource
    private MetaDataMarketMapper metaDataMarketMapper;

    @Override
    public Map<String, Object> pageMarketList(int start, int length, Long datasourceId, String modelType, String modelName) {
        // page list
        List<MetaDataMarket> list = this.metaDataMarketMapper.pageMarketList(start, length, datasourceId, modelType, modelName);
        int list_count = this.metaDataMarketMapper.pageMarketListCount(datasourceId, modelType, modelName);
        // package result
        Map<String, Object> maps = new HashMap<>();
        maps.put("total", list_count);        // 总记录数
        maps.put("rows", list);                    // 分页列表
        return maps;
    }
    @Override
    public Map<String, Object> pageSubcribeList(int start, int length, Long datasourceId, String modelType, String modelName)
    {
        // page list
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        List<MetaDataMarket> list = this.metaDataMarketMapper.pageSubcribeList(start, length,userId, datasourceId, modelType, modelName);
        int list_count = this.metaDataMarketMapper.pageSubcribeListCount(userId, datasourceId, modelType, modelName);
        // package result
        Map<String, Object> maps = new HashMap<>();
        maps.put("total", list_count);        // 总记录数
        maps.put("rows", list);                    // 分页列表
        return maps;
    }
    @Override
    @Transactional
    public int insertSubcribe(Long modeId)
    {
        if(StringUtils.isEmpty(modeId))
            return -1;
        if(listSubcribeById(modeId) > 0)
            return -1;
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        return this.metaDataMarketMapper.insertSubcribe(userId,modeId);
    }
    @Override
    @Transactional
    public boolean deleteSubcribe(Long modeId)
    {
        if(StringUtils.isEmpty(modeId))
            return false;
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        return this.metaDataMarketMapper.deleteSubcribe(userId,modeId);
    }
    @Override
    public int listSubcribeById(Long modeId)
    {
        if(StringUtils.isEmpty(modeId))
            return 1;
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        return this.metaDataMarketMapper.listSubcribeById(userId,modeId);
    }
    @Override
    @Transactional
    public boolean deleteSubcribeByModeId(Long modeId )
    {
        return this.metaDataMarketMapper.deleteSubcribeByModeId(modeId);
    }
    @Override
    public List<MetaDataMarket> listMarketAll(Long datasourceId, String modelType, String modelName)
    {
        return this.metaDataMarketMapper.listMarketAll(datasourceId,modelType,modelName);
    }
    @Override
    public List<MetaDataMarket> listMarketByUser()
    {
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        return this.metaDataMarketMapper.listMarketByUser(userId);
    }

    @Override
    @Transactional
    public int insertMarket(MetaDataMarket entity)
    {
        if(StringUtils.isEmpty(entity.getQuerySql()) ||
                this.metaDataMarketMapper.listMarketBySql(entity.getQuerySql()) > 0)
        {
            return -1;
        }
        entity.setId(IdUtils.getId());
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(SecurityUtils.getLoginUser().getUser().getUserId());
        return this.metaDataMarketMapper.insert(entity);
    }

}