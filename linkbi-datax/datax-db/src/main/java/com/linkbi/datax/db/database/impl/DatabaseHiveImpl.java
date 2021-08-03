
package com.linkbi.datax.db.database.impl;

import com.linkbi.datax.db.constant.Const;
import com.linkbi.datax.db.database.AbstractDatabase;
import com.linkbi.datax.db.database.IDatabaseInterface;
import com.linkbi.datax.db.model.ColumnDescription;
import com.linkbi.datax.db.model.ColumnMetaData;
import com.linkbi.datax.db.util.JdbcConstants;
import org.apache.hadoop.hbase.client.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 支持HIVE数据库的元数据操作
 * 
 * @author
 *
 */
public class DatabaseHiveImpl extends AbstractDatabase implements IDatabaseInterface {

	public DatabaseHiveImpl() {
		super(JdbcConstants.HIVE_DRIVER);
	}
	private Table table;

	private static int getSize(ResultSet rs) {
		try {
			if (rs.getType() == ResultSet.TYPE_FORWARD_ONLY) {
				return -1;
			}

			rs.last();
			int total = rs.getRow();
			rs.beforeFirst();
			return total;
		} catch (SQLException sqle) {
			return -1;
		} catch (AbstractMethodError ame) {
			return -1;
		}
	}

	@Override
	public List<String> querySchemaList() {
		return new ArrayList<>();
	}

	@Override
	public List<ColumnDescription> querySelectSqlColumnMeta(String sql)
	{
		return new ArrayList<>();
	}
	@Override
	public String formatSQL(String sql)
	{
		return sql;
	}
	@Override
	protected String getTableFieldsQuerySQL(String schemaName, String tableName)
	{
		return "";
	}
	@Override
	protected String getTestQuerySQL(String sql)
	{
		return "";
	}
	@Override
	public String getFieldDefinition(ColumnMetaData v, List<String> pks, boolean useAutoInc, boolean addCr) {
		String fieldname = v.getName();
		int length = v.getLength();
		int precision = v.getPrecision();
		int type = v.getType();

		String retval = " \"" + fieldname + "\"   ";

		switch (type) {
			case ColumnMetaData.TYPE_TIMESTAMP:
				retval += "TIMESTAMP";
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
						retval += "BIGSERIAL";
					} else {
						retval += "BIGINT";
					}
				} else {
					if (length > 0) {
						if (precision > 0 || length > 18) {
							if ((length + precision) > 0 && precision > 0) {
								// Numeric(Precision, Scale): Precision = total length; Scale = decimal places
								retval += "NUMERIC(" + (length + precision) + ", " + precision + ")";
							} else {
								retval += "DOUBLE PRECISION";
							}
						} else {
							if (length > 9) {
								retval += "BIGINT";
							} else {
								if (length < 5) {
									retval += "SMALLINT";
								} else {
									retval += "INTEGER";
								}
							}
						}

					} else {
						retval += "DOUBLE PRECISION";
					}
				}
				break;
			case ColumnMetaData.TYPE_STRING:
				if (length < 1 || length >= AbstractDatabase.CLOB_LENGTH) {
					retval += "TEXT";
				} else {
					retval += "VARCHAR(" + length + ")";
				}
				break;
			case ColumnMetaData.TYPE_BINARY:
				retval += "BYTEA";
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
	public String getTableMaxIdSql(String tableName, String pkName) {
		return String.format("SELECT MAX(%s) FROM %s",pkName, tableName);
	}
}
