
package com.linkbi.datax.db.util;

import com.linkbi.datax.db.constant.DBTypeEnum;

import java.util.List;

/**
 * 普通工具类
 * 
 * @author
 *
 */
public final class CommonUtils {

	private CommonUtils() {

	}

	public static String getTableFullNameByDatabase(DBTypeEnum dbtype, String schemaName, String tableName) {
		if (dbtype == DBTypeEnum.MYSQL) {
			return String.format("`%s`.`%s`", schemaName, tableName);
		} else if (dbtype == DBTypeEnum.SQLSERVER) {
			return String.format("[%s].[%s]", schemaName, tableName);
		} else {
			return String.format("\"%s\".\"%s\"", schemaName, tableName);
		}
	}

	public static String getSelectColumnsSQL(DBTypeEnum dbtype, String schema, String table, List<String> columns) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		for (int i = 0; i < columns.size(); ++i) {
			String field = columns.get(i);
			String quoteField = quoteString(dbtype, field);
			sb.append(quoteField);

			if (i < columns.size() - 1) {
				sb.append(",");
			}
		}
		sb.append(" FROM ");
		if (null != schema && !schema.isEmpty()) {
			sb.append(quoteString(dbtype, schema));
			sb.append(".");
		}
		sb.append(quoteString(dbtype, table));

		return sb.toString();
	}

	private static String quoteString(DBTypeEnum dbtype, String keyName) {
		if (dbtype == DBTypeEnum.MYSQL) {
			return String.format("`%s`", keyName);
		} else if (dbtype == DBTypeEnum.SQLSERVER) {
			return String.format("[%s]", keyName);
		} else {
			return String.format("\"%s\"", keyName);
		}
	}

	public static String getQuotationChar(DBTypeEnum dbtype) {
		if (dbtype == DBTypeEnum.MYSQL) {
			return "`";
		} else {
			return "\"";
		}
	}
}
