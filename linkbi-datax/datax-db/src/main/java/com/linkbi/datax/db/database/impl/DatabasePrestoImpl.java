
package com.linkbi.datax.db.database.impl;

import com.linkbi.datax.db.constant.DBTypeEnum;
import com.linkbi.datax.db.database.AbstractDatabase;
import com.linkbi.datax.db.database.IDatabaseInterface;
import com.linkbi.datax.db.model.ColumnDescription;
import com.linkbi.datax.db.model.ColumnMetaData;
import com.linkbi.datax.db.util.JdbcConstants;
import org.apache.commons.lang3.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支持Presto数据库的元数据操作
 * 
 * @author
 *
 */
public class DatabasePrestoImpl extends AbstractDatabase implements IDatabaseInterface {

	public DatabasePrestoImpl() {
		super(JdbcConstants.PRESTO_DRIVER);
	}

	public DatabasePrestoImpl(String driverClassName) {
		super(driverClassName);
	}


	@Override
	public String getQuerySql(String schemaName, String tableName, String querySql)
	{
		if(!StringUtils.isEmpty(querySql)) {
			return "SELECT * FROM (" + querySql + " ) AS T_ LIMIT 1000";
		}
		else
			return querySql;
	}
	@Override
	public Map<String,Object> queryDataList(String schemaName, String tableName, String querySql)
	{
		Map<String,Object> dataList = new HashMap<>();
		List<Map<String,String>> rList = new ArrayList<>();
		Map<String,String> map;
		ResultSet rs;
		querySql = getQuerySql(schemaName,tableName,querySql);
		if(StringUtils.isEmpty(querySql))
			return dataList;
		try(PreparedStatement pstmt = this.connection.prepareStatement(querySql)) {
			rs = pstmt.executeQuery();
			ResultSetMetaData rsm = rs.getMetaData();
			int col = rsm.getColumnCount();
			for (int i = 1; i <= col; i++)
			{
				map = new HashMap<>();
				map.put("col_name",rsm.getColumnName(i));
				map.put("col_comment",rsm.getColumnLabel(i));
				rList.add(map);
			}
			dataList.put("cols",rList);
			rList = new ArrayList<>();
			while(rs.next())
			{
				map = new HashMap<>();
				for (int i = 1; i <= col; i++)
				{
					map.put(rsm.getColumnName(i),rs.getString(i));
				}
				rList.add(map);
			}
			dataList.put("rows",rList);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return dataList;
	}
	@Override
	public List<ColumnDescription> querySelectSqlColumnMeta(String sql) {
		String querySQL = String.format(" %s LIMIT 1", sql.replace(";", ""));
		return this.getSelectSqlColumnMeta(querySQL, DBTypeEnum.PRESTO);
	}

	@Override
	protected String getTableFieldsQuerySQL(String schemaName, String tableName) {
		if(StringUtils.isEmpty(schemaName))
			return String.format("SELECT * FROM %s ", tableName);
		else
			return String.format("SELECT * FROM %s.%s ", schemaName, tableName);
	}

	@Override
	protected String getTestQuerySQL(String sql) {
		return String.format("explain %s", sql.replace(";", ""));
	}

	@Override
	public String getTableMaxIdSql(String tableName, String pkName) {
		return String.format("SELECT MAX(%s) FROM %s",pkName, tableName);
	}
	
	@Override
	public String formatSQL(String sql) {
		return sql;
	}
	
	@Override
	public String getFieldDefinition(ColumnMetaData v, List<String> pks, boolean useAutoInc, boolean addCr) {
		return "";
	}

	@Override
	public String getPrimaryKeyAsString(List<String> pks) {
		return "";
	}
	
}
