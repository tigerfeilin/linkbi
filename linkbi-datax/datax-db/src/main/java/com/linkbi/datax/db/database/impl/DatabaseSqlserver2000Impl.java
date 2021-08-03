
package com.linkbi.datax.db.database.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.linkbi.datax.db.database.IDatabaseInterface;
import com.linkbi.datax.db.model.ColumnDescription;
import com.linkbi.datax.db.model.TableDescription;
import com.linkbi.datax.db.util.JdbcConstants;
import com.linkbi.datax.db.util.JdbcOperatorUtils;

/**
 * 支持SQLServer2000数据库的元数据操作
 * 
 * @author
 *
 */
public class DatabaseSqlserver2000Impl extends DatabaseSqlserverImpl implements IDatabaseInterface {

	public DatabaseSqlserver2000Impl() {
		super(JdbcConstants.SQL_SERVER_DRIVER);
	}
	/*
	@Override
	public List<TableDescription> queryTableList(String schemaName,String tableName) {
		List<TableDescription> ret = new ArrayList<>();
		Set<String> uniqueSet = new HashSet<>();
		ResultSet tables = null;
		try {
			tables = this.metaData.getTables(this.catalogName, schemaName, "%", new String[] { "TABLE","VIEW" });
			while (tables.next()) {
				String tableName = tables.getString("TABLE_NAME");
				if (uniqueSet.contains(tableName)) {
					continue;
				} else {
					uniqueSet.add(tableName);
				}

				TableDescription td = new TableDescription();
				td.setSchemaName(schemaName);
				td.setTableName(tableName);
				td.setRemarks(tables.getString("REMARKS"));
				if (tables.getString("TABLE_TYPE").equalsIgnoreCase("VIEW")) {
					td.setTableType("VIEW");
				} else {
					td.setTableType("TABLE");
				}
				ret.add(td);
			}
			return ret;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcOperatorUtils.closeResultSet(tables);
		}
	}
	
	@Override
	public List<ColumnDescription> queryTableColumnMeta(String schemaName, String tableName) {
		String sql = this.getTableFieldsQuerySQL(schemaName, tableName);
		List<ColumnDescription> ret = this.querySelectSqlColumnMeta(sql);
		ResultSet columns = null;
		try {
			columns = this.metaData.getColumns(this.catalogName, schemaName, tableName, null);
			while (columns.next()) {
				String columnName = columns.getString("COLUMN_NAME");
				String remarks = columns.getString("REMARKS");
				for (ColumnDescription cd : ret) {
					if (columnName.equalsIgnoreCase(cd.getFieldName())) {
						cd.setRemarks(remarks);
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcOperatorUtils.closeResultSet(columns);
		}

		return ret;
	}
	*/
	@Override
	public String getTableMaxIdSql(String tableName, String pkName) {
		return String.format("SELECT MAX(%s) FROM %s",pkName, tableName);
	}
}
