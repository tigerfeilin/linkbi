package com.linkbi.datax.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkbi.datax.api.mapper.MetaDataSourceMapper;
import com.linkbi.datax.api.domain.MetaDataSource;
import com.linkbi.datax.api.service.MetaDataSourceService;
import com.linkbi.datax.api.util.AESUtil;
import com.linkbi.datax.db.model.JdbcSourceData;
import com.linkbi.datax.db.constant.DBTypeEnum;
import com.linkbi.datax.db.service.IMetaDataService;
import com.linkbi.datax.db.service.impl.CommonMetaDataServiceImpl;
import com.linkbi.datax.db.util.JdbcUrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * Created 2020/01/30
 */
@Service
@Transactional(readOnly = true)
public class MetaDataSourceServiceImpl extends ServiceImpl<MetaDataSourceMapper, MetaDataSource> implements MetaDataSourceService {

    @Resource
    private MetaDataSourceMapper datasourceMapper;

    @Autowired
    private IMetaDataService metaDataService;

    @Override
    public Boolean  dataSourceTest(MetaDataSource metaDatasource) {
        String userName = AESUtil.decrypt(metaDatasource.getJdbcUsername());
        //  判断账密是否为密文
        if (userName == null) {
            metaDatasource.setJdbcUsername(AESUtil.encrypt(metaDatasource.getJdbcUsername()));
        }
        String pwd = AESUtil.decrypt(metaDatasource.getJdbcPassword());
        if (pwd == null) {
            metaDatasource.setJdbcPassword(AESUtil.encrypt(metaDatasource.getJdbcPassword()));
        }
        String datasourceId = "";
        if(metaDatasource.getId() != null)
            datasourceId = metaDatasource.getId().toString();
        JdbcSourceData jdbcSourceData = new JdbcSourceData(datasourceId,metaDatasource.getDatasourceName(),metaDatasource.getJdbcDriverClass(), metaDatasource.getJdbcUrl(),AESUtil.decrypt(metaDatasource.getJdbcUsername()),
                AESUtil.decrypt(metaDatasource.getJdbcPassword()), metaDatasource.getDatabaseName());
        return metaDataService.dataSourceTest(jdbcSourceData);
    }

    @Override
    public int update(MetaDataSource datasource) {
        return datasourceMapper.update(datasource);
    }

    @Override
    public List<MetaDataSource> selectAllDatasource() {
        return datasourceMapper.selectList(null);
    }

}