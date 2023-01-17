
package com.linkbi.datax.db.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.linkbi.datax.db.constant.DBTypeEnum;
import com.linkbi.datax.db.database.AbstractDatabase;
import com.linkbi.datax.db.database.DatabaseFactory;
import com.linkbi.datax.db.model.ColumnDescription;
import com.linkbi.datax.db.model.JdbcSourceData;
import com.linkbi.datax.db.model.TableDescription;
import com.linkbi.datax.db.service.IMetaDataService;
import com.linkbi.datax.db.util.GenerateSqlUtils;
import com.linkbi.datax.db.util.JdbcUrlUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.inject.Scope;
import javax.xml.ws.ServiceMode;

/**
 * 元信息数据查询实现类
 * 
 * @author
 *
 */
@Service
public class CommonMetaDataServiceImpl implements IMetaDataService {

	@Override
	public void setDatabaseConnection(DBTypeEnum dbtype) {
		//this.database = DatabaseFactory.getDatabaseInstance(dbtype);
	}

	@Override
	public List<String> querySchemaList(JdbcSourceData jdbcSourceData) {
		AbstractDatabase db = Objects.requireNonNull(DatabaseFactory.getDatabaseInstance(JdbcUrlUtils.getDbType(jdbcSourceData.getJdbcUrl())), "Please call setDatabaseConnection() first!");
		db.connect(jdbcSourceData);
		return db.querySchemaList();
	}

	@Override
	public List<TableDescription> queryTableList(JdbcSourceData jdbcSourceData, String schemaName,String tableName) {
		AbstractDatabase db = Objects.requireNonNull(DatabaseFactory.getDatabaseInstance(JdbcUrlUtils.getDbType(jdbcSourceData.getJdbcUrl())), "Please call setDatabaseConnection() first!");
		db.connect(jdbcSourceData);
		return db.queryTableList(schemaName, tableName);
	}
	@Override
	public List<ColumnDescription> queryTableColumnMeta(JdbcSourceData jdbcSourceData,
														String schemaName, String tableName) {
		AbstractDatabase db = Objects.requireNonNull(DatabaseFactory.getDatabaseInstance(JdbcUrlUtils.getDbType(jdbcSourceData.getJdbcUrl())), "Please call setDatabaseConnection() first!");
		db.connect(jdbcSourceData);
		return db.queryTableColumnMeta(schemaName, tableName);
	}

	@Override
	public List<ColumnDescription> querySqlColumnMeta(JdbcSourceData jdbcSourceData,
													  String querySql) {
		AbstractDatabase db = Objects.requireNonNull(DatabaseFactory.getDatabaseInstance(JdbcUrlUtils.getDbType(jdbcSourceData.getJdbcUrl())), "Please call setDatabaseConnection() first!");
		db.connect(jdbcSourceData);
		return db.querySelectSqlColumnMeta(querySql);
	}

	@Override
	public List<String> queryTablePrimaryKeys(JdbcSourceData jdbcSourceData, String schemaName,
											  String tableName) {
		AbstractDatabase db = Objects.requireNonNull(DatabaseFactory.getDatabaseInstance(JdbcUrlUtils.getDbType(jdbcSourceData.getJdbcUrl())), "Please call setDatabaseConnection() first!");
		db.connect(jdbcSourceData);
		return db.queryTablePrimaryKeys(schemaName, tableName);
	}
	@Override
	public long queryTableMaxId(JdbcSourceData jdbcSourceData,String tbName,String pK)
	{
		AbstractDatabase db = Objects.requireNonNull(DatabaseFactory.getDatabaseInstance(JdbcUrlUtils.getDbType(jdbcSourceData.getJdbcUrl())), "Please call setDatabaseConnection() first!");
		db.connect(jdbcSourceData);
		return db.queryTableMaxId(tbName, pK);
	}
	@Override
	public Map<String,Object> queryDataList(JdbcSourceData jdbcSourceData, String schemaName, String tableName, String querySql)
	{
		AbstractDatabase db = Objects.requireNonNull(DatabaseFactory.getDatabaseInstance(JdbcUrlUtils.getDbType(jdbcSourceData.getJdbcUrl())), "Please call setDatabaseConnection() first!");
		db.connect(jdbcSourceData);
		return db.queryDataList(schemaName, tableName, querySql);
	}
	@Override
	public String queryModelSQL(JdbcSourceData jdbcSourceData, String schemaName, String tableName)
	{
		AbstractDatabase db = Objects.requireNonNull(DatabaseFactory.getDatabaseInstance(JdbcUrlUtils.getDbType(jdbcSourceData.getJdbcUrl())), "Please call setDatabaseConnection() first!");
		return db.queryModelSQL(schemaName, tableName);
	}
	@Override
	public void testQuerySQL(JdbcSourceData jdbcSourceData, String sql) {
		AbstractDatabase db = Objects.requireNonNull(DatabaseFactory.getDatabaseInstance(JdbcUrlUtils.getDbType(jdbcSourceData.getJdbcUrl())), "Please call setDatabaseConnection() first!");
		db.connect(jdbcSourceData);
		db.testQuerySQL(sql);
	}
	@Override
	public boolean dataSourceTest(JdbcSourceData jdbcSourceData)
	{
		try{
			AbstractDatabase db = Objects.requireNonNull(DatabaseFactory.getDatabaseInstance(JdbcUrlUtils.getDbType(jdbcSourceData.getJdbcUrl())), "Please call setDatabaseConnection() first!");
			db.connect(jdbcSourceData);
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
			//return false;
		}
		return true;
	}
	@Override
	public void executeUpdate(JdbcSourceData jdbcSourceData,String querySql)
	{
		try {
			AbstractDatabase db = Objects.requireNonNull(DatabaseFactory.getDatabaseInstance(JdbcUrlUtils.getDbType(jdbcSourceData.getJdbcUrl())), "Please call setDatabaseConnection() first!");
			db.connect(jdbcSourceData);
			db.executeUpdate(querySql);
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public String getDDLCreateTableSQL(DBTypeEnum type, List<ColumnDescription> fieldNames, List<String> primaryKeys,
									   String schemaName, String tableName, boolean autoIncr) {
		return GenerateSqlUtils.getDDLCreateTableSQL(type, fieldNames, primaryKeys, schemaName, tableName, autoIncr);
	}
}
