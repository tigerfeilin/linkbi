
package com.linkbi.datax.db.model;

import lombok.Data;

/**
 * 数据库表描述符信息定义(Table Description)
 * @author
 *
 */
@Data
public class JdbcSourceData {
	private String datasourceId;
	private String datasourceName;
	private String driverClassName;
	private String dbName;
	private String jdbcUrl;
	private String userName;
	private String passWd;
	public JdbcSourceData(String datasourceId,String datasourceName,String driverClassName,String url,String user,String passwd,String dbname)
	{
		this.datasourceId = datasourceId;
		this.datasourceName = datasourceName;
		this.driverClassName = driverClassName;
		this.dbName = dbname;
		this.jdbcUrl = url;
		this.userName = user;
		this.passWd = passwd;
	}
}
