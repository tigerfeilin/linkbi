package com.linkbi.datax.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.linkbi.datax.api.dto.DataXJsonBuildDto;
import com.linkbi.datax.api.domain.MetaDataSource;
import com.linkbi.datax.api.service.DataxJsonService;
import com.linkbi.datax.api.service.MetaDataSourceService;
import com.linkbi.datax.api.tool.datax.DataxJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * com.linkbi.datax json构建实现类
 *
 * @author
 * @ClassName DataxJsonServiceImpl
 * @Version 2.0
 * @since 2020/01/11 17:15
 */
@Service
public class DataxJsonServiceImpl implements DataxJsonService {

    @Autowired
    private MetaDataSourceService jobJdbcDatasourceService;

    @Override
    public String buildJobJson(DataXJsonBuildDto dataXJsonBuildDto) {
        DataxJsonHelper dataxJsonHelper = new DataxJsonHelper();
        // reader
        MetaDataSource readerDatasource = jobJdbcDatasourceService.getById(dataXJsonBuildDto.getReaderDatasourceId());
        // reader plugin init
        dataxJsonHelper.initReader(dataXJsonBuildDto, readerDatasource);
        MetaDataSource writerDatasource = jobJdbcDatasourceService.getById(dataXJsonBuildDto.getWriterDatasourceId());
        dataxJsonHelper.initWriter(dataXJsonBuildDto, writerDatasource);

        return JSON.toJSONString(dataxJsonHelper.buildJob());
    }
}