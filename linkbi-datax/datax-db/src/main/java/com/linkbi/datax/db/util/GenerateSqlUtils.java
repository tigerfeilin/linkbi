
package com.linkbi.datax.db.util;

import java.util.List;
import java.util.stream.Collectors;

import com.linkbi.datax.db.constant.DBTypeEnum;
import com.linkbi.datax.db.constant.Const;
import com.linkbi.datax.db.database.AbstractDatabase;
import com.linkbi.datax.db.database.DatabaseFactory;
import com.linkbi.datax.db.model.ColumnDescription;
import com.linkbi.datax.db.model.ColumnMetaData;

/**
 * 拼接SQL工具类
 * 
 * @author
 *
 */
public class GenerateSqlUtils {

	public static String getDDLCreateTableSQL(DBTypeEnum type, List<ColumnDescription> fieldNames,
											  List<String> primaryKeys, String schemaName, String tableName, boolean autoIncr) {
		StringBuilder retval = new StringBuilder();
		List<String> pks = fieldNames.stream().filter((cd) -> primaryKeys.contains(cd.getFieldName()))
				.map((cd) -> cd.getFieldName()).collect(Collectors.toList());

		AbstractDatabase db = DatabaseFactory.getDatabaseInstance(type);

		retval.append(Const.CREATE_TABLE);
		// if(ifNotExist && type!=DatabaseType.ORACLE) {
		// retval.append( Const.IF_NOT_EXISTS );
		// }
		retval.append(db.getQuotedSchemaTableCombination(schemaName, tableName) + Const.CR);
		retval.append("(").append(Const.CR);

		for (int i = 0; i < fieldNames.size(); i++) {
			if (i > 0) {
				retval.append(", ");
			} else {
				retval.append("  ");
			}

			ColumnMetaData v = fieldNames.get(i).getMetaData();
			retval.append(db.getFieldDefinition(v, pks, autoIncr, true));
		}

		if (!pks.isEmpty()) {
			String pk = db.getPrimaryKeyAsString(pks);
			retval.append(", PRIMARY KEY (").append(pk).append(")").append(Const.CR);
		}

		retval.append(")").append(Const.CR);
		if (DBTypeEnum.MYSQL == type) {
			retval.append("ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin").append(Const.CR);
		}

		return db.formatSQL(retval.toString());
	}
}
