
package com.linkbi.datax.db.database;

import java.util.HashMap;
import java.util.Map;
import com.linkbi.datax.db.constant.DBTypeEnum;
import com.linkbi.datax.db.database.impl.*;

/**
 * 数据库实例构建工厂类
 * @author
 *
 */
public final class DatabaseFactory {
	
	private static final Map<DBTypeEnum,String> DATABASE_MAPPER=new HashMap<DBTypeEnum, String>(){
		
		private static final long serialVersionUID = 9202705534880971997L;

	{  
	      put(DBTypeEnum.MYSQL,DatabaseMysqlImpl.class.getName());
	      put(DBTypeEnum.ORACLE,DatabaseOracleImpl.class.getName());
		  put(DBTypeEnum.DM,DatabaseDMImpl.class.getName());
	      put(DBTypeEnum.SQLSERVER2000,DatabaseSqlserver2000Impl.class.getName());
	      put(DBTypeEnum.SQLSERVER,DatabaseSqlserverImpl.class.getName());
	      put(DBTypeEnum.POSTGRESQL,DatabasePostgresImpl.class.getName());
	      put(DBTypeEnum.GREENPLUM,DatabaseGreenplumImpl.class.getName());
	      put(DBTypeEnum.MARIADB,DatabaseMariaDBImpl.class.getName());
	      put(DBTypeEnum.DB2,DatabaseDB2Impl.class.getName());
		  put(DBTypeEnum.MONGODB, DatabaseMongoDBImpl.class.getName());
		  put(DBTypeEnum.HIVE, DatabaseHiveImpl.class.getName());
		  put(DBTypeEnum.HBASE, DatabaseHBaseImpl.class.getName());
		  put(DBTypeEnum.HBASE20X, DatabaseHBase20XImpl.class.getName());
		  put(DBTypeEnum.CLICKHOUSE, DatabaseClickHouseImpl.class.getName());
		  put(DBTypeEnum.PRESTO, DatabasePrestoImpl.class.getName());
	}}; 
	
	public static AbstractDatabase getDatabaseInstance(DBTypeEnum type) {
		if(DATABASE_MAPPER.containsKey(type)) {
			String className= DATABASE_MAPPER.get(type);
			try {
				return (AbstractDatabase) Class.forName(className).newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		throw new RuntimeException(String.format("Unkown database type (%s)",type.name()));
	}
	
	private DatabaseFactory() {
		
	}
}
