package com.linkbi.datax.api.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Lists;
import com.linkbi.common.core.redis.RedisCache;
import com.linkbi.datax.api.domain.MetaDataSource;
import com.linkbi.datax.api.service.JDBCQueryService;
import com.linkbi.datax.api.service.MetaDataSourceService;
import com.linkbi.datax.api.util.AESUtil;
import com.linkbi.datax.api.util.ObjectUtils;
import com.linkbi.datax.db.constant.DBTypeEnum;
import com.linkbi.datax.db.model.ColumnDescription;
import com.linkbi.datax.db.model.JdbcSourceData;
import com.linkbi.datax.db.model.TableDescription;
import com.linkbi.datax.db.service.IMetaDataService;
import com.linkbi.datax.db.service.impl.CommonMetaDataServiceImpl;
import com.linkbi.datax.db.util.JdbcUrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * datasource query
 *
 * @author
 * @ClassName JdbcDatasourceQueryServiceImpl
 * @Version 1.0
 * @since 2019/7/31 20:51
 */
@Service
public class JDBCQueryServiceImpl implements JDBCQueryService {

    @Autowired
    private MetaDataSourceService metaDatasourceService;
    @Autowired
    private RedisCache redisCache;

    @Autowired
    private IMetaDataService metaDataService;

    @Override
    public List<String> getDBs(Long id) {
        //获取数据源对象
        MetaDataSource datasource = metaDatasourceService.getById(id);
        if (ObjectUtil.isNull(datasource)) {
            return Lists.newArrayList();
        }
        JdbcSourceData jdbcSourceData = new JdbcSourceData(datasource.getId().toString(),datasource.getDatasourceName(),datasource.getJdbcDriverClass(),datasource.getJdbcUrl(),AESUtil.decrypt(datasource.getJdbcUsername()),
                AESUtil.decrypt(datasource.getJdbcPassword()),datasource.getDatabaseName());
        return metaDataService.querySchemaList(jdbcSourceData);
    }


    @Override
    public List<TableDescription> getTables(Long id, String tableSchema) {
        //获取数据源对象
        MetaDataSource datasource = metaDatasourceService.getById(id);

        if (ObjectUtil.isNull(datasource)) {
            return Lists.newArrayList();
        }
        JdbcSourceData jdbcSourceData = new JdbcSourceData(datasource.getId().toString(),datasource.getDatasourceName(),datasource.getJdbcDriverClass(),datasource.getJdbcUrl(),AESUtil.decrypt(datasource.getJdbcUsername()),
                AESUtil.decrypt(datasource.getJdbcPassword()),datasource.getDatabaseName());
        return metaDataService.queryTableList(jdbcSourceData,tableSchema,null);
    }

    @Override
    public List<String> getTableSchema(Long id) {
        //获取数据源对象
        MetaDataSource datasource = metaDatasourceService.getById(id);

        if (ObjectUtil.isNull(datasource)) {
            return Lists.newArrayList();
        }
        JdbcSourceData jdbcSourceData = new JdbcSourceData(datasource.getId().toString(),datasource.getDatasourceName(),datasource.getJdbcDriverClass(),datasource.getJdbcUrl(),AESUtil.decrypt(datasource.getJdbcUsername()),
                AESUtil.decrypt(datasource.getJdbcPassword()),datasource.getDatabaseName());
        List<String> scList = metaDataService.querySchemaList(jdbcSourceData);
        return scList;
        //BaseQueryTool qTool = QueryToolFactory.getByDbType(datasource);
        //return qTool.getTableSchema();
    }

    @Override
    public List<String> getCollectionNames(long id, String dbName) throws IOException {
        //获取数据源对象
        MetaDataSource datasource = metaDatasourceService.getById(id);

        if (ObjectUtil.isNull(datasource)) {
            return Lists.newArrayList();
        }
        JdbcSourceData jdbcSourceData = new JdbcSourceData(datasource.getId().toString(),datasource.getDatasourceName(),datasource.getJdbcDriverClass(),datasource.getJdbcUrl(),AESUtil.decrypt(datasource.getJdbcUsername()),
                AESUtil.decrypt(datasource.getJdbcPassword()),datasource.getDatabaseName());
        return metaDataService.querySchemaList(jdbcSourceData);
    }


    @Override
    public List<ColumnDescription> getColumns(Long id, String schemaName, String tableName) throws IOException {
        //获取数据源对象
        MetaDataSource datasource = metaDatasourceService.getById(id);

        if (ObjectUtil.isNull(datasource)) {
            return Lists.newArrayList();
        }
        JdbcSourceData jdbcSourceData = new JdbcSourceData(datasource.getId().toString(),datasource.getDatasourceName(),datasource.getJdbcDriverClass(),datasource.getJdbcUrl(),AESUtil.decrypt(datasource.getJdbcUsername()),
                AESUtil.decrypt(datasource.getJdbcPassword()),datasource.getDatabaseName());
        return metaDataService.queryTableColumnMeta(jdbcSourceData,schemaName,tableName);
/*
        if (JdbcConstants.HBASE.equals(datasource.getDatasource())) {
            return new HBaseQueryTool(datasource).getColumns(tableName);
        } else if (JdbcConstants.MONGODB.equals(datasource.getDatasource())) {
            return new MongoDBQueryTool(datasource).getColumns(tableName);
        } else {
            BaseQueryTool queryTool = QueryToolFactory.getByDbType(datasource);
            return queryTool.getColumnNames(tableName, datasource.getDatasource());
        }
 */
    }

    @Override
    public List<ColumnDescription> getColumnsByQuerySql(Long datasourceId, String querySql) {
        //获取数据源对象
        MetaDataSource datasource = metaDatasourceService.getById(datasourceId);

        if (ObjectUtil.isNull(datasource)) {
            return Lists.newArrayList();
        }
        JdbcSourceData jdbcSourceData = new JdbcSourceData(datasource.getId().toString(),datasource.getDatasourceName(),datasource.getJdbcDriverClass(),datasource.getJdbcUrl(),AESUtil.decrypt(datasource.getJdbcUsername()),
                AESUtil.decrypt(datasource.getJdbcPassword()),datasource.getDatabaseName());
        return metaDataService.querySqlColumnMeta(jdbcSourceData,querySql);
        /*
        BaseQueryTool queryTool = QueryToolFactory.getByDbType(jdbcDatasource);
        return queryTool.getColumnsByQuerySql(querySql);
         */
    }
    @Override
    public String execSqlCommand(Long id,String sqlCommand)
    {
        //获取数据源对象
        if(StringUtils.isEmpty(sqlCommand))
            return "";
        MetaDataSource datasource = metaDatasourceService.getById(id);

        if (ObjectUtil.isNull(datasource)) {
            return "";
        }
        JdbcSourceData jdbcSourceData = new JdbcSourceData(datasource.getId().toString(),datasource.getDatasourceName(),datasource.getJdbcDriverClass(),datasource.getJdbcUrl(),AESUtil.decrypt(datasource.getJdbcUsername()),
                AESUtil.decrypt(datasource.getJdbcPassword()),datasource.getDatabaseName());
        try{
            metaDataService.executeUpdate(jdbcSourceData,sqlCommand);
        }
        catch (RuntimeException e)
        {
            return e.getMessage();
        }
        //BaseQueryTool queryTool = QueryToolFactory.getByDbType(jdbcDatasource);
        //queryTool.execSqlCommand(sqlCommand);
        return "";
    }
    @Override
    public Map<String,Object> queryDataList(int current,int size,Long datasourceId, String tableSchema, String tableName, String querySql)
    {
        //获取数据源对象
        Map<String,Object> dataList = new HashMap<>();
        if(datasourceId == null)
            return dataList;
        MetaDataSource datasource = metaDatasourceService.getById(datasourceId);
        if (ObjectUtil.isNull(datasource)) {
            return dataList;
        }
        String cacheKey = "/adhoc/" + datasourceId + "/" + tableSchema + "/" + tableName + "/" + querySql;
        dataList = redisCache.getCacheObject(cacheKey);
        List<Map<String,String>> rList = null;
        if(dataList == null)
        {
            JdbcSourceData jdbcSourceData = new JdbcSourceData(datasource.getId().toString(),datasource.getDatasourceName(),datasource.getJdbcDriverClass(),datasource.getJdbcUrl(),AESUtil.decrypt(datasource.getJdbcUsername()),
                    AESUtil.decrypt(datasource.getJdbcPassword()),datasource.getDatabaseName());
            dataList = metaDataService.queryDataList(jdbcSourceData,tableSchema,tableName,querySql);
            rList = ObjectUtils.cast(dataList.get("rows"));
            if(rList!=null)
                dataList.put("total",rList.size());
            redisCache.setCacheObject(cacheKey,dataList,30, TimeUnit.SECONDS);
        }
        else
            rList = ObjectUtils.cast(dataList.get("rows"));
        if(rList != null && rList.size() > 0)
            rList = rList.stream().skip((current-1) * size).limit(size).collect(Collectors.toList());
        dataList.put("rows",rList);
        return  dataList;
    }
    @Override
    public Map<String,Object> queryDataList(Long datasourceId, String tableSchema, String tableName, String querySql)
    {
        //获取数据源对象
        Map<String,Object> dataList = new HashMap<>();
        if(datasourceId == null)
            return dataList;
        MetaDataSource datasource = metaDatasourceService.getById(datasourceId);
        if (ObjectUtil.isNull(datasource)) {
            return dataList;
        }
        String cacheKey = "/adhoc_nolimit/" + datasourceId + "/" + tableSchema + "/" + tableName + "/" + querySql;
        dataList = redisCache.getCacheObject(cacheKey);
        List<Map<String,String>> rList = null;
        if(dataList == null)
        {
            JdbcSourceData jdbcSourceData = new JdbcSourceData(datasource.getId().toString(),datasource.getDatasourceName(),datasource.getJdbcDriverClass(),datasource.getJdbcUrl(),AESUtil.decrypt(datasource.getJdbcUsername()),
                    AESUtil.decrypt(datasource.getJdbcPassword()),datasource.getDatabaseName());
            //DBTypeEnum sourceDatabaseType = JdbcUrlUtils.getDbType(datasource.getJdbcUrl());
            //metaDataService.setDatabaseConnection(sourceDatabaseType);
            dataList = metaDataService.queryDataList(jdbcSourceData,tableSchema,tableName,querySql);
            rList = ObjectUtils.cast(dataList.get("rows"));
            if(rList!=null)
                dataList.put("total",rList.size());
            redisCache.setCacheObject(cacheKey,dataList,30, TimeUnit.SECONDS);
        }
        else
            rList = ObjectUtils.cast(dataList.get("rows"));
        dataList.put("rows",rList);
        return  dataList;
    }
    @Override
    public String queryCreateDBSql(Long datasourceId,Long targetdatasourceId,String schema,String targetschema, String tablename)
    {
        MetaDataSource datasource = metaDatasourceService.getById(datasourceId);

        if (ObjectUtil.isNull(datasource)) {
            return "";
        }
        JdbcSourceData jdbcSourceData = new JdbcSourceData(datasource.getId().toString(),datasource.getDatasourceName(),datasource.getJdbcDriverClass(),datasource.getJdbcUrl(),AESUtil.decrypt(datasource.getJdbcUsername()),
                AESUtil.decrypt(datasource.getJdbcPassword()),datasource.getDatabaseName());
        List<ColumnDescription> columnDescs = metaDataService.queryTableColumnMeta(jdbcSourceData, schema,
                tablename);
        List<String> primaryKeys = metaDataService.queryTablePrimaryKeys(jdbcSourceData, schema,
                tablename);
        datasource = metaDatasourceService.getById(targetdatasourceId);

        if (ObjectUtil.isNull(datasource)) {
            return "";
        }
        DBTypeEnum targetDatabaseType = JdbcUrlUtils.getDbType(datasource.getJdbcUrl());
        String sqlCreateTable = metaDataService.getDDLCreateTableSQL(targetDatabaseType, columnDescs, primaryKeys,
                targetschema, tablename,false);
        return sqlCreateTable;
    }
    @Override
    public String queryModelSql(Long datasourceId,String schema,String tablename)
    {
        if(datasourceId == null)
            return "";
        MetaDataSource datasource = metaDatasourceService.getById(datasourceId);
        if (ObjectUtil.isNull(datasource)) {
            return "";
        }
        JdbcSourceData jdbcSourceData = new JdbcSourceData(datasource.getId().toString(),datasource.getDatasourceName(),datasource.getJdbcDriverClass(),datasource.getJdbcUrl(),AESUtil.decrypt(datasource.getJdbcUsername()),
                AESUtil.decrypt(datasource.getJdbcPassword()),datasource.getDatabaseName());
        return metaDataService.queryModelSQL(jdbcSourceData,schema,tablename);
    }
}
