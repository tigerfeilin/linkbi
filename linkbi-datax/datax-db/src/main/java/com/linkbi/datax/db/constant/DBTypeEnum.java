
package com.linkbi.datax.db.constant;

/**
 * 数据库类型的枚举定义
 * 
 * @author
 *
 */
public enum DBTypeEnum {
	/**
	 * 未知数据库类型
	 */
	UNKOWN(0), 
	
	/**
	 * MySQL数据库类型
	 */
	MYSQL(1), 
	
	/**
	 * Oracle数据库类型
	 */
	ORACLE(2), 
	
	/**
	 * SQLServer 2000数据库类型
	 */
	SQLSERVER2000(3), 
	
	/**
	 * SQLServer数据库类型
	 */
	SQLSERVER(4), 
	
	/**
	 * PostgreSQL数据库类型
	 */
	POSTGRESQL(5), 
	
	/**
	 * Greenplum数据库类型
	 */
	GREENPLUM(6),
	
	/**
	 * MariaDB数据库类型
	 */
	MARIADB(7),
	
	/**
	 * DB2数据库类型
	 */
	DB2(8),

	/**
	 * HIVE数据库类型
	 */
	HIVE(9),

	/**
	 * HBASE数据库类型
	 */
	HBASE(10),

	/**
	 * CLICKHOUSE数据库类型
	 */
	CLICKHOUSE(11),

	/**
	 * MONGODB数据库类型
	 */
	MONGODB(12),

	/**
	 * HBase2.X and Phoenix5.X数据库类型
	 */
	HBASE20X(13),
	/**
	 * ELASTIC_SEARCH数据库类型
	 */
	ELASTIC_SEARCH(14),
	/**
	 * PRESTO数据库类型
	 */
	PRESTO(15);
	private int index;

	DBTypeEnum(int idx) {
		this.index = idx;
	}

	public int getIndex() {
		return index;
	}

}
