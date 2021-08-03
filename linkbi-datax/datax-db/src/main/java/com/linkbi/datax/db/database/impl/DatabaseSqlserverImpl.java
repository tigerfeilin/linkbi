
package com.linkbi.datax.db.database.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.linkbi.datax.db.util.JdbcConstants;
import org.apache.commons.lang3.StringUtils;
import com.alibaba.druid.sql.SQLUtils;
import com.linkbi.datax.db.constant.Const;
import com.linkbi.datax.db.constant.DBTypeEnum;
import com.linkbi.datax.db.database.AbstractDatabase;
import com.linkbi.datax.db.database.IDatabaseInterface;
import com.linkbi.datax.db.model.ColumnDescription;
import com.linkbi.datax.db.model.ColumnMetaData;
import com.linkbi.datax.db.model.TableDescription;
import com.linkbi.datax.db.util.JdbcOperatorUtils;

/**
 * 支持SQLServer数据库的元数据操作
 * 
 * @author
 *
 */
public class DatabaseSqlserverImpl extends AbstractDatabase implements IDatabaseInterface {

	private static Set<String> excludesSchemaNames;

	static {
		excludesSchemaNames = new HashSet<>();
		excludesSchemaNames.add("db_denydatawriter");
		excludesSchemaNames.add("db_datawriter");
		excludesSchemaNames.add("db_accessadmin");
		excludesSchemaNames.add("db_ddladmin");
		excludesSchemaNames.add("db_securityadmin");
		excludesSchemaNames.add("db_denydatareader");
		excludesSchemaNames.add("db_backupoperator");
		excludesSchemaNames.add("db_datareader");
		excludesSchemaNames.add("db_owner");
	}

	public DatabaseSqlserverImpl() {
		super(JdbcConstants.SQL_SERVER_DRIVER_SQLJDBC4);
	}

	public DatabaseSqlserverImpl(String driverName) {
		super(driverName);
	}

	private int getDatabaseMajorVersion() {
		int majorVersion = 0;
		try {
			majorVersion = this.metaData.getDatabaseMajorVersion();
			return majorVersion;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<String> querySchemaList() {
		Set<String> ret = new HashSet<String>();
		ResultSet schemas = null;
		try {
			schemas = this.metaData.getSchemas();
			while (schemas.next()) {
				String name = schemas.getString("TABLE_SCHEM");
				if (!excludesSchemaNames.contains(name)) {
					ret.add(name);
				}
			}
			return new ArrayList<>(ret);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (null != schemas) {
					schemas.close();
					schemas = null;
				}
			} catch (SQLException e) {
			}
		}

	}

	@Override
	public List<TableDescription> queryTableList(String schemaName,String tableName) {
		int majorVersion=getDatabaseMajorVersion();
		if(majorVersion<=8) {
			return super.queryTableList(schemaName,tableName);
		}
		
		List<TableDescription> ret = new ArrayList<>();
		String sql = String.format(
				"SELECT DISTINCT t.TABLE_SCHEMA as TABLE_SCHEMA, t.TABLE_NAME as TABLE_NAME, t.TABLE_TYPE as TABLE_TYPE, CONVERT(nvarchar(50),ISNULL(g.[value], '')) as COMMENTS \r\n" + 
				"FROM INFORMATION_SCHEMA.TABLES t LEFT JOIN sysobjects d on t.TABLE_NAME = d.name \r\n" + 
				"LEFT JOIN sys.extended_properties g on g.major_id=d.id and g.minor_id='0' where t.TABLE_SCHEMA='%s'",
				schemaName);
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = this.connection.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				TableDescription td = new TableDescription();
				td.setSchemaName(rs.getString("TABLE_SCHEMA"));
				td.setTableName(rs.getString("TABLE_NAME"));
				td.setRemarks(rs.getString("COMMENTS"));
				String tableType = rs.getString("TABLE_TYPE").trim();
				if (tableType.equalsIgnoreCase("VIEW")) {
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
			JdbcOperatorUtils.closeResultSet(rs);
			JdbcOperatorUtils.closeStatement(pstmt);
		}
	}

	@Override
	public List<ColumnDescription> queryTableColumnMeta(String schemaName, String tableName) {
		int majorVersion = getDatabaseMajorVersion();
		if (majorVersion <= 8) {
			return super.queryTableColumnMeta(schemaName, tableName);
		}
		
		String sql=this.getTableFieldsQuerySQL(schemaName, tableName);
		List<ColumnDescription> ret= this.querySelectSqlColumnMeta(sql);
		String qsql = String.format(
				"SELECT a.name AS COLUMN_NAME,CONVERT(nvarchar(50),ISNULL(g.[value], '')) AS REMARKS FROM sys.columns a\r\n" + 
				"LEFT JOIN sys.extended_properties g ON ( a.object_id = g.major_id AND g.minor_id = a.column_id )\r\n" + 
				"WHERE object_id = (SELECT top 1 object_id FROM sys.tables st INNER JOIN INFORMATION_SCHEMA.TABLES t on st.name=t.TABLE_NAME\r\n" + 
				"WHERE	st.name = '%s' and t.TABLE_SCHEMA='%s')",
				tableName,schemaName);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = this.connection.prepareStatement(qsql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String columnName = rs.getString("COLUMN_NAME");
				String remarks = rs.getString("REMARKS");
				for (ColumnDescription cd : ret) {
					if (columnName.equalsIgnoreCase(cd.getFieldName())) {
						cd.setRemarks(remarks);
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcOperatorUtils.closeResultSet(rs);
			JdbcOperatorUtils.closeStatement(pstmt);
		}

		return ret;
	}
	
	@Override
	public List<ColumnDescription> querySelectSqlColumnMeta(String sql) {
		String querySQL = String.format("SELECT TOP 1 * from (%s) tmp ", sql.replace(";", ""));
		return this.getSelectSqlColumnMeta(querySQL, DBTypeEnum.SQLSERVER);
	}

	@Override
	protected String getTableFieldsQuerySQL(String schemaName, String tableName) {
		return String.format("select top 1 * from [%s].[%s] ", schemaName, tableName);
	}

	@Override
	protected String getTestQuerySQL(String sql) {
		return String.format("SELECT top 1 * from ( %s ) tmp", sql.replace(";", ""));
	}

	@Override
	public String getQuotedSchemaTableCombination(String schemaName, String tableName) {
		return String.format("  [%s].[%s] ", schemaName, tableName);
	}

	@Override
	public String formatSQL(String sql) {
		return SQLUtils.formatSQLServer(sql);
	}

	@Override
	public String getFieldDefinition(ColumnMetaData v, List<String> pks, boolean useAutoInc, boolean addCr) {
		String fieldname = v.getName();
		int length = v.getLength();
		int precision = v.getPrecision();
		int type = v.getType();

		String retval = " [" + fieldname + "]  ";

		switch (type) {
		case ColumnMetaData.TYPE_TIMESTAMP:
			retval += "DATETIME";
			break;
		case ColumnMetaData.TYPE_TIME:
			retval += "TIME";
			break;
		case ColumnMetaData.TYPE_DATE:
			retval += "DATE";
			break;
		case ColumnMetaData.TYPE_BOOLEAN:
			retval += "VARCHAR(32)";
			break;
		case ColumnMetaData.TYPE_NUMBER:
		case ColumnMetaData.TYPE_INTEGER:
		case ColumnMetaData.TYPE_BIGNUMBER:
			if (null != pks && pks.contains(fieldname)) {
				if (useAutoInc) {
					retval += "BIGINT IDENTITY(0,1)";
				} else {
					retval += "BIGINT";
				}
			} else {
				if (precision == 0) {
					if (length > 18) {
						retval += "DECIMAL(" + length + ",0)";
					} else {
						if (length > 9) {
							retval += "BIGINT";
						} else {
							retval += "INT";
						}
					}
				} else {
					if (precision > 0 && length > 0) {
						retval += "DECIMAL(" + length + "," + precision + ")";
					} else {
						retval += "FLOAT(53)";
					}
				}
			}
			break;
		case ColumnMetaData.TYPE_STRING:
			if (length < 8000) {
				// Maybe use some default DB String length in case length<=0
				if (length > 0) {
					// VARCHAR(n)最多能存n个字节，一个中文是两个字节。
					length = 2 * length;
					if (length > 8000) {
						length = 8000;
					}
					retval += "VARCHAR(" + length + ")";
				} else {
					retval += "VARCHAR(100)";
				}
			} else {
				retval += "TEXT"; // Up to 2bilion characters.
			}
			break;
		case ColumnMetaData.TYPE_BINARY:
			retval += "VARBINARY(MAX)";
			break;
		default:
			retval += "TEXT";
			break;
		}

		if (addCr) {
			retval += Const.CR;
		}

		return retval;
	}

	@Override
	public String getPrimaryKeyAsString(List<String> pks) {
		if (!pks.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append(StringUtils.join(pks, "] , ["));
			sb.append("]");
			return sb.toString();
		}

		return "";
	}
	@Override
	public String getTableMaxIdSql(String tableName, String pkName) {
		return String.format("SELECT MAX(%s) FROM %s",pkName, tableName);
	}
}
