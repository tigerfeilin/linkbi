
package com.linkbi.datax.db.database.impl;

import java.util.List;
import com.linkbi.datax.db.constant.DBTypeEnum;
import com.linkbi.datax.db.model.ColumnDescription;
import com.linkbi.datax.db.util.JdbcConstants;

/**
 * 支持MariaDB数据库的元数据操作
 * 
 * @author
 *
 */
public class DatabaseMariaDBImpl extends DatabaseMysqlImpl {

	public DatabaseMariaDBImpl() {
		super(JdbcConstants.MARIADB_DRIVER);
	}
	
	@Override
	public List<ColumnDescription> querySelectSqlColumnMeta(String sql) {
		String querySQL = String.format(" %s LIMIT 0 ", sql.replace(";", ""));
		return this.getSelectSqlColumnMeta(querySQL, DBTypeEnum.MARIADB);
	}
}
