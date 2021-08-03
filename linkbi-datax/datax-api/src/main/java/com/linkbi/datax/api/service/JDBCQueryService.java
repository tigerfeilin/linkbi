package com.linkbi.datax.api.service;

import com.linkbi.datax.db.model.ColumnDescription;
import com.linkbi.datax.db.model.JdbcSourceData;
import com.linkbi.datax.db.model.TableDescription;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 数据库查询服务
 *
 * @author
 * @ClassName JdbcDatasourceQueryService
 * @Version 1.0
 * @since 2019/7/31 20:50
 */
public interface JDBCQueryService {

    /**
     * 获取db列表
     * @param id
     * @return
     */
    List<String> getDBs(Long id) throws IOException;

    /**
     * 根据数据源表id查询出可用的表
     *
     * @param id
     * @return
     */
    List<TableDescription> getTables(Long id, String tableSchema) throws IOException;

    /**
     * 获取CollectionNames
     * @param dbName
     * @return
     */
    List<String> getCollectionNames(long id, String dbName) throws IOException;

    /**
     * 根据数据源id，表名查询出该表所有字段
     *
     * @param id
     * @return
     */
    List<ColumnDescription> getColumns(Long id, String schemaName, String tableName) throws IOException;

    /**
     * 根据 sql 语句获取字段
     *
     * @param datasourceId
     * @param querySql
     * @return
     */
    List<ColumnDescription> getColumnsByQuerySql(Long datasourceId, String querySql) throws SQLException;

    /**
     * 获取PG table schema
     * @param id
     * @return
     */
    List<String> getTableSchema(Long id);

    /**
     * 创建表结构
     * @param id
     * @return
     */
    String execSqlCommand(Long id,String tableName)throws SQLException;
    public Map<String,Object> queryDataList(int current,int size,Long id,String schemaName, String tableName, String querySql);
    public Map<String,Object> queryDataList(Long id,String schemaName, String tableName, String querySql);
    String queryCreateDBSql(Long datasourceId,Long targetdatasourceId,String schema,String targetschema, String tablename);
    String queryModelSql(Long datasourceId,String schema,String tablename);
}
