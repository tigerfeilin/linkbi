
package com.linkbi.datax.db.database.impl;

import java.util.List;
import com.linkbi.datax.db.constant.DBTypeEnum;
import com.linkbi.datax.db.constant.Const;
import com.linkbi.datax.db.database.AbstractDatabase;
import com.linkbi.datax.db.database.IDatabaseInterface;
import com.linkbi.datax.db.model.ColumnDescription;
import com.linkbi.datax.db.model.ColumnMetaData;
import com.linkbi.datax.db.util.JdbcConstants;

/**
 * 支持DB2数据库的元数据操作
 * 
 * @author
 *
 */
public class DatabaseDB2Impl extends AbstractDatabase implements IDatabaseInterface {

	public DatabaseDB2Impl() {
		super(JdbcConstants.DB2_DRIVER);
	}

	@Override
	public List<ColumnDescription> querySelectSqlColumnMeta(String sql) {
		String querySQL = String.format(" %s LIMIT 0 ", sql.replace(";", ""));
		return this.getSelectSqlColumnMeta(querySQL, DBTypeEnum.DB2);
	}

	@Override
	protected String getTableFieldsQuerySQL(String schemaName, String tableName) {
		return String.format("SELECT * FROM \"%s\".\"%s\"  ", schemaName, tableName);
	}

	@Override
	protected String getTestQuerySQL(String sql) {
		return String.format("explain %s", sql.replace(";", ""));
	}

	@Override
	public String formatSQL(String sql) {
		return sql;
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
			retval += "CHARACTER(32)";
			break;
		case ColumnMetaData.TYPE_NUMBER:
		case ColumnMetaData.TYPE_BIGNUMBER:
			if (null != pks && pks.contains(fieldname)) {
				if (useAutoInc) {
					retval += "BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1, NOCACHE)";
				} else {
					retval += "BIGINT NOT NULL";
				}
			} else {
				if (length > 0) {
					retval += "DECIMAL(" + length;
					if (precision > 0) {
						retval += ", " + precision;
					}
					retval += ")";
				} else {
					retval += "FLOAT";
				}
			}
			break;
		case ColumnMetaData.TYPE_INTEGER:
			if (null != pks && pks.contains(fieldname)) {
				if (useAutoInc) {
					retval += "INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1, NOCACHE)";
				} else {
					retval += "INTEGER NOT NULL";
				}
			} else {
				retval += "INTEGER";
			}
			break;
		case ColumnMetaData.TYPE_STRING:
			if (length*3 > 32672) {
				retval += "CLOB";
			} else {
				retval += "VARCHAR";
				if (length > 0) {
					retval += "(" + length*3;
				} else {
					retval += "(";
				}

				retval += ")";
			}
			
			if (null != pks && pks.contains(fieldname)) {
				retval += " NOT NULL";
			}
			
			break;
		case ColumnMetaData.TYPE_BINARY:
			if (length > 32672) {
				retval += "BLOB(" + length + ")";
			} else {
				if (length > 0) {
					retval += "CHAR(" + length + ") FOR BIT DATA";
				} else {
					retval += "BLOB";
				}
			}
			break;
		default:
			retval += "CLOB";
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
