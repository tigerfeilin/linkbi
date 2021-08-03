
package com.linkbi.datax.db.database.impl;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.linkbi.datax.db.util.JdbcConstants;
import org.apache.commons.lang3.StringUtils;
import com.linkbi.datax.db.constant.Const;
import com.alibaba.druid.sql.SQLUtils;
import com.linkbi.datax.db.constant.DBTypeEnum;
import com.linkbi.datax.db.database.AbstractDatabase;
import com.linkbi.datax.db.database.IDatabaseInterface;
import com.linkbi.datax.db.model.ColumnDescription;
import com.linkbi.datax.db.model.ColumnMetaData;
import com.linkbi.datax.db.model.TableDescription;
import com.linkbi.datax.db.util.JdbcOperatorUtils;
import com.linkbi.datax.db.util.JdbcUrlUtils;

/**
 * 支持MySQL数据库的元数据操作
 * 
 * @author
 *
 */
public class DatabaseMysqlImpl extends AbstractDatabase implements IDatabaseInterface {

	public DatabaseMysqlImpl() {
		super(JdbcConstants.MYSQL_DRIVER);
	}
	
	public DatabaseMysqlImpl(String driverClassName) {
		super(driverClassName);
	}

	@Override
	public List<String> querySchemaList() {
		String mysqlJdbcUrl=null;
		try {
			mysqlJdbcUrl = this.metaData.getURL();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		Map<String, String> data=JdbcUrlUtils.findParamsByMySqlJdbcUrl(mysqlJdbcUrl);
		List<String> ret=new ArrayList<String>();
		ret.add(data.get("schema"));
		return ret;
	}
	
	@Override
	public List<TableDescription> queryTableList(String schemaName,String tableName) {
		List<TableDescription> ret = new ArrayList<>();
		if(StringUtils.isEmpty(schemaName))
		{
			List<String> shList = querySchemaList();
			if(shList.size() > 0)
				schemaName = shList.get(0);
			else
				return  ret;
		}
		String sql = String.format(
				"SELECT `TABLE_SCHEMA`,`TABLE_NAME`,`TABLE_TYPE`,`TABLE_COMMENT` FROM `information_schema`.`TABLES` where `TABLE_SCHEMA`='%s'",
				schemaName);
		if(!StringUtils.isEmpty(tableName))
			sql = String.format(
					"SELECT `TABLE_SCHEMA`,`TABLE_NAME`,`TABLE_TYPE`,`TABLE_COMMENT` FROM `information_schema`.`TABLES` where `TABLE_SCHEMA`='%s' AND `TABLE_NAME`= '%s'",
					schemaName,tableName);
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = this.connection.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				TableDescription td=new TableDescription();
				td.setSchemaName(rs.getString("TABLE_SCHEMA"));
				td.setTableName(rs.getString("TABLE_NAME"));
				td.setRemarks(rs.getString("TABLE_COMMENT"));
				String tableType=rs.getString("TABLE_TYPE");
				 if(tableType.equalsIgnoreCase("VIEW")) {
					td.setTableType("VIEW");
				}else {
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
	public String getQuerySql(String schemaName, String tableName, String querySql)
	{
		String sql = "";
		if(StringUtils.isAllEmpty(tableName,querySql))
			return "";
		else{
			if(StringUtils.isEmpty(querySql))
			{
				if(StringUtils.isEmpty(schemaName))
				{
					sql = String.format("SELECT * FROM %s LIMIT 1000", tableName);
				}
				else
				{
					sql = String.format("SELECT * FROM `%s`.`%s` LIMIT 1000",schemaName, tableName);
				}
			}
			else{
				sql = "SELECT * FROM (" + querySql + " ) AS T_ LIMIT 1000";
			}
			return sql;
		}
	}
	@Override
	public String queryModelSQL(String schemaName, String tableName)
	{
		String sql = "";
		if(StringUtils.isAllEmpty(tableName))
			return "";
		else{
			if(StringUtils.isEmpty(schemaName))
			{
				sql = String.format("SELECT * FROM %s ", tableName);
			}
			else
			{
				sql = String.format("SELECT * FROM `%s`.`%s` ",schemaName, tableName);
			}
			return sql;
		}
	}
	@Override
	public List<ColumnDescription> querySelectSqlColumnMeta(String sql) {
		String querySQL = String.format(" %s LIMIT 1", sql.replace(";", ""));
		return this.getSelectSqlColumnMeta(querySQL, DBTypeEnum.MYSQL);
	}

	@Override
	protected String getTableFieldsQuerySQL(String schemaName, String tableName) {
		if(StringUtils.isEmpty(schemaName))
			return String.format("SELECT * FROM `%s` ", tableName);
		else
			return String.format("SELECT * FROM `%s`.`%s` ", schemaName, tableName);
	}

	@Override
	protected String getTestQuerySQL(String sql) {
		return String.format("explain %s", sql.replace(";", ""));
	}

	@Override
	public String getTableMaxIdSql(String tableName, String pkName) {
		return String.format("SELECT MAX(%s) FROM `%s`",pkName, tableName);
	}

	@Override
	public String getQuotedSchemaTableCombination(String schemaName, String tableName) {
		if(StringUtils.isEmpty(schemaName))
			return String.format("  `%s` ", tableName);
		else
			return String.format("  `%s`.`%s` ", schemaName, tableName);
	}
	
	@Override
	public String formatSQL(String sql) {
		return SQLUtils.formatMySql(sql);
	}
	
	@Override
	public String getFieldDefinition(ColumnMetaData v, List<String> pks, boolean useAutoInc, boolean addCr) {
		String fieldname = v.getName();
		int length = v.getLength();
		int precision = v.getPrecision();
		int type = v.getType();
		
		String retval = " `"+fieldname + "`  ";

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
			if (null!=pks && pks.contains(fieldname)) {
				if (useAutoInc) {
					retval += "BIGINT AUTO_INCREMENT NOT NULL";
				} else {
					retval += "BIGINT NOT NULL";
				}
			} else {
				// Integer values...
				if (precision == 0) {
					if (length > 9) {
						if (length < 19) {
							// can hold signed values between -9223372036854775808 and 9223372036854775807
							// 18 significant digits
							retval += "BIGINT";
						} else {
							retval += "DECIMAL(" + length + ")";
						}
					} else {
						retval += "INT";
					}
				} else {
					// Floating point values...
					if (length > 15) {
						retval += "DECIMAL(" + length;
						if (precision > 0) {
							retval += ", " + precision;
						}
						retval += ")";
					} else {
						// A double-precision floating-point number is accurate to approximately 15
						// decimal places.
						// http://mysql.mirrors-r-us.net/doc/refman/5.1/en/numeric-type-overview.html
						retval += "DOUBLE";
					}
				}
			}
			break;
		case ColumnMetaData.TYPE_STRING:
			if (length > 0) {
				if (length == 1) {
					retval += "CHAR(1)";
				} else if (length < 256) {
					retval += "VARCHAR(" + length + ")";
				}else if (null!=pks && pks.contains(fieldname)) {
					/*
					 * MySQL5.6中varchar字段为主键时最大长度为254,例如如下的建表语句在MySQL5.7下能通过，但在MySQL5.6下无法通过：
					 *	create table `t_test`(
					 *	`key` varchar(1024) binary,
					 *	`val` varchar(1024) binary,
					 *	primary key(`key`)
					 * );
					 */
					retval += "VARCHAR(254) BINARY";
				} else if (length < 65536) {
					retval += "TEXT";
				} else if (length < 16777216) {
					retval += "MEDIUMTEXT";
				} else {
					retval += "LONGTEXT";
				}
			} else {
				retval += "TINYTEXT";
			}
			break;
		case ColumnMetaData.TYPE_BINARY:
			retval += "LONGBLOB";
			break;
		default:
			retval += " LONGTEXT";
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
			sb.append("`");
			sb.append(StringUtils.join(pks, "` , `"));
			sb.append("`");
			return sb.toString();
		}

		return "";
	}
	
}
