
package com.linkbi.datax.db.database.impl;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.linkbi.datax.db.database.AbstractDatabase;
import com.linkbi.datax.db.database.IDatabaseInterface;
import com.linkbi.datax.db.model.ColumnDescription;
import com.linkbi.datax.db.model.JdbcSourceData;
import com.linkbi.datax.db.model.TableDescription;
import com.linkbi.datax.db.util.JdbcConstants;
import com.linkbi.datax.db.util.LocalCacheUtil;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.net.UnknownHostException;
import java.util.*;

/**
 * 支持MongoDB数据库的元数据操作
 * 
 * @author
 *
 */
public class DatabaseMongoDBImpl extends AbstractDatabase implements IDatabaseInterface {

	public DatabaseMongoDBImpl() {
		super(JdbcConstants.MONGODB);
	}
	private static MongoClient mongoClient = null;
	private static MongoDatabase collections;
	@Override
	public void connect(JdbcSourceData jdbcSourceData) {
		/*
		 * 超时时间设置问题： https://blog.csdn.net/lsunwing/article/details/79461217
		 * https://blog.csdn.net/weixin_34405332/article/details/91664781
		 */
		try {
			/**
			 * Oracle在通过jdbc连接的时候需要添加一个参数来设置是否获取注释
			 */
			if (LocalCacheUtil.get(jdbcSourceData.getDatasourceName()) == null) {
				getDataSource(jdbcSourceData);
			} else {
				mongoClient = (MongoClient) LocalCacheUtil.get(jdbcSourceData.getDatasourceName());
				if (mongoClient == null) {
					LocalCacheUtil.remove(jdbcSourceData.getDatasourceName());
					getDataSource(jdbcSourceData);
				}
			}
			LocalCacheUtil.set(jdbcSourceData.getDatasourceName(), mongoClient, 4 * 60 * 60 * 1000);
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public void getDataSource(JdbcSourceData jdbcSourceData) {
		try{
			if (StringUtils.isBlank(jdbcSourceData.getUserName()) && StringUtils.isBlank(jdbcSourceData.getPassWd())) {
				mongoClient = new MongoClient(new MongoClientURI(jdbcSourceData.getJdbcUrl()));
			} else {
				MongoCredential credential = MongoCredential.createCredential(jdbcSourceData.getUserName(), jdbcSourceData.getDbName(), jdbcSourceData.getPassWd().toCharArray());
				mongoClient = new MongoClient(parseServerAddress(jdbcSourceData.getJdbcUrl()), Arrays.asList(credential));
			}
			collections = mongoClient.getDatabase(jdbcSourceData.getDbName());
		}
		catch (UnknownHostException e)
		{
			throw new RuntimeException(e);
		}
	}
	/**
	 * 转换为mongo地址协议
	 *
	 * @param rawAddress
	 * @return
	 */
	private static List<ServerAddress> parseServerAddress(String rawAddress) throws UnknownHostException {
		List<ServerAddress> addressList = new ArrayList<>();
		for (String address : rawAddress.split(",")) {
			String[] tempAddress = address.split(":");
			try {
				ServerAddress sa = new ServerAddress(tempAddress[0], Integer.parseInt(tempAddress[1]));
				addressList.add(sa);
			} catch (Exception e) {
				throw new UnknownHostException();
			}
		}
		return addressList;
	}
	/**
	 * 判断地址类型是否符合要求
	 *
	 * @param addressList
	 * @return
	 */
	private static boolean isHostPortPattern(List<Object> addressList) {
		for (Object address : addressList) {
			String regex = "(\\S+):([0-9]+)";
			if (!((String) address).matches(regex)) {
				return false;
			}
		}
		return true;
	}
	@Override
	public void close() {
		if (null != mongoClient) {
			mongoClient.close();
			mongoClient = null;
		}
	}
	@Override
	public List<String> querySchemaList() {
		MongoIterable<String> dbs = mongoClient.listDatabaseNames();
		List<String> dbNames = new ArrayList<>();
		dbs.forEach((Block<? super String>) dbNames::add);
		return dbNames;
	}
	/**
	 * 获取Collection名称列表
	 *
	 * @return
	 */
	@Override
	public List<TableDescription> queryTableList(String dbName,String tableName) {
		collections = mongoClient.getDatabase(dbName);
		List<String> collectionNames = new ArrayList<>();
		collections.listCollectionNames().forEach((Block<? super String>) collectionNames::add);
		List<TableDescription> tabList = new ArrayList<>();
		collectionNames.forEach((e)->{
			TableDescription tableDescription = new TableDescription();
			tableDescription.setTableName(e);
			if(StringUtils.isEmpty(tableName))
				tabList.add(tableDescription);
			else if(e.contains(tableName)){
				tabList.add(tableDescription);
			}
		});
		return tabList;
	}
	@Override
	public List<ColumnDescription> queryTableColumnMeta(String schemaName, String tableName)
	{
		MongoCollection<Document> collection = collections.getCollection(tableName);
		Document document = collection.find(new BasicDBObject()).first();
		List<ColumnDescription> list = new ArrayList<>();
		if (null == document || document.size() <= 0) {
			return list;
		}
		document.forEach((k, v) -> {
			if (null != v) {
				String type = v.getClass().getSimpleName();
				ColumnDescription columnDescription = new ColumnDescription();
				columnDescription.setFieldName(k + ":" + type);
				list.add(columnDescription);
			}
		});
		return list;
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
	public String getTableMaxIdSql(String tableName, String pkName) {
		return "";
	}
	@Override
	public long queryTableMaxId(String tableName, String pkName)
	{
		return 0;
	}
}
