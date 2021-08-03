
package com.linkbi.datax.db.service;
import java.util.List;
import java.util.Map;
import com.linkbi.datax.db.constant.DBTypeEnum;
import com.linkbi.datax.db.model.ColumnDescription;
import com.linkbi.datax.db.model.JdbcSourceData;
import com.linkbi.datax.db.model.TableDescription;

/**
 * 元信息获取接口定义
 * 
 * @author
 *
 */
public interface IMetaDataService {

	/**
	 * 设置数据库连接的数据库类型
	 * 
	 * @param dbtype 数据库类型
	 */
	public void setDatabaseConnection(DBTypeEnum dbtype);

	/**
	 * 获取数据库的schema模式列表
	 * 
	 * @param jdbcSourceData 数据库连接信息
	 * @return
	 */
	public List<String> querySchemaList(JdbcSourceData jdbcSourceData);

	/**
	 * 获取指定Schema下所有的表列表
	 * 
	 * @param jdbcSourceData 数据库连接信息
	 * @param schemaName 模式名称
	 * @return
	 */
	public List<TableDescription> queryTableList(JdbcSourceData jdbcSourceData, String schemaName,String tableName);

	/**
	 * 获取指定schema.table的表结构字段信息
	 * 
	 * @param jdbcSourceData 数据库连接信息
	 * @param schemaName 模式名称
	 * @param tableName  表或视图名称
	 * @return
	 */
	public List<ColumnDescription> queryTableColumnMeta(JdbcSourceData jdbcSourceData,
			String schemaName, String tableName);

	/**
	 * 获取指定SQL结构字段信息
	 * 
	 * @param jdbcSourceData 数据库连接信息
	 * @param querySql 查询的SQL语句
	 * @return
	 */
	public List<ColumnDescription> querySqlColumnMeta(JdbcSourceData jdbcSourceData,
			String querySql);


	/**
	 * 获取表的主键信息字段列表
	 * 
	 * @param jdbcSourceData 数据库连接信息
	 * @param schemaName Schema模式名称
	 * @param tableName  Table表名称
	 * @return
	 */
	public List<String> queryTablePrimaryKeys(JdbcSourceData jdbcSourceData, String schemaName,
			String tableName);

	/**
	 * 获取表的主键信息字段列表
	 *
	 * @param jdbcSourceData 数据库连接信息
	 * @param schemaName Schema模式名称
	 * @param tableName  Table表名称
	 * @return
	 */
	public Map<String,Object> queryDataList(JdbcSourceData jdbcSourceData, String schemaName, String tableName, String querySql);

	public String queryModelSQL(JdbcSourceData jdbcSourceData, String schemaName, String tableName);
	/**
	 * 测试数据库SQL查询
	 * 
	 * @param jdbcSourceData 数据库连接信息
	 * @param sql      待查询的SQL语句
	 */
	public void testQuerySQL(JdbcSourceData jdbcSourceData, String sql);

	/**
	 * 获取指定SQL结构字段信息
	 *
	 * @param jdbcSourceData 数据库连接信息
	 * @param querySql 查询的SQL语句
	 * @return
	 */
	public void executeUpdate(JdbcSourceData jdbcSourceData,String querySql);
	/**
	 * 测试数据库连接
	 *
	 * @param jdbcSourceData  数据库连接信息
	 */
	public boolean dataSourceTest(JdbcSourceData jdbcSourceData);

	/**
	 * 获取最大主键id
	 *
	 * @param jdbcSourceData  数据库连接信息
	 * @param tbName  连接表信息
	 * @param pK  主键字段
	 */
	public long queryTableMaxId(JdbcSourceData jdbcSourceData,String tbName,String pK);

	/**
	 * 根据字段结构信息组装对应数据库的建表DDL语句
	 * 
	 * @param type        目的数据库类型
	 * @param fieldNames  字段结构信息
	 * @param primaryKeys 主键字段信息
	 * @param tableName   模式名称
	 * @param tableName   表名称
	 * @param autoIncr    是否允许主键自增
	 * @return 对应数据库的DDL建表语句
	 */
	public String getDDLCreateTableSQL(DBTypeEnum type, List<ColumnDescription> fieldNames, List<String> primaryKeys,
                                       String schemaName, String tableName, boolean autoIncr);

}
