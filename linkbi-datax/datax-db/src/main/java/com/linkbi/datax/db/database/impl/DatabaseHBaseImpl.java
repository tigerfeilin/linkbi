
package com.linkbi.datax.db.database.impl;

import com.linkbi.datax.db.database.AbstractDatabase;
import com.linkbi.datax.db.database.IDatabaseInterface;
import com.linkbi.datax.db.model.ColumnDescription;
import com.linkbi.datax.db.model.JdbcSourceData;
import com.linkbi.datax.db.model.TableDescription;
import com.linkbi.datax.db.util.JdbcConstants;
import com.linkbi.datax.db.util.LocalCacheUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 支持HBASE数据库的元数据操作
 * 
 * @author
 *
 */
public class DatabaseHBaseImpl extends AbstractDatabase implements IDatabaseInterface {

	public DatabaseHBaseImpl() {
		super(JdbcConstants.HBASE);
	}
	private Configuration conf = HBaseConfiguration.create();
	private ExecutorService pool = Executors.newScheduledThreadPool(2);
	private Connection connection = null;
	private Admin admin;
	private Table table;
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
				connection = (Connection) LocalCacheUtil.get(jdbcSourceData.getDatasourceName());
				if (connection == null || connection.isClosed()) {
					LocalCacheUtil.remove(jdbcSourceData.getDatasourceName());
					getDataSource(jdbcSourceData);
				}
			}
			LocalCacheUtil.set(jdbcSourceData.getDatasourceName(), connection, 4 * 60 * 60 * 1000);
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public void getDataSource(JdbcSourceData jdbcSourceData) {
		try{
			String[] zkAdress = jdbcSourceData.getJdbcUrl().split(":");
			conf.set("hbase.zookeeper.quorum", zkAdress[0]);
			conf.set("hbase.zookeeper.property.clientPort", zkAdress[1]);
			connection = ConnectionFactory.createConnection(conf, pool);
			admin = connection.getAdmin();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	@Override
	public void close() {
		try {
			if (admin != null) {
				admin.close();
			}
			if (null != connection) {
				connection.close();
			}
			if (table != null) {
				table.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public List<String> querySchemaList() {
		return new ArrayList<>();
	}
	/**
	 * 获取Collection名称列表
	 *
	 * @return
	 */
	@Override
	public List<TableDescription> queryTableList(String dbName,String tableName) {
		List<TableDescription> tabList = new ArrayList<>();
		try
		{
			List<String> list = new ArrayList<>();
			Admin admin = connection.getAdmin();
			TableName[] names = admin.listTableNames();
			for (int i = 0; i < names.length; i++) {
				list.add(names[i].getNameAsString());
			}
			list.forEach((e)->{
				TableDescription tableDescription = new TableDescription();
				tableDescription.setTableName(e);
				if(StringUtils.isEmpty(tableName))
					tabList.add(tableDescription);
				else if(e.contains(tableName)){
					tabList.add(tableDescription);
				}
			});
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		return tabList;
	}
	@Override
	public List<ColumnDescription> queryTableColumnMeta(String schemaName, String tableName)
	{
		List<ColumnDescription> cdList = new ArrayList<>();
		try{
			List<String> list = new ArrayList<>();
			table = connection.getTable(TableName.valueOf(tableName));
			Scan scan = new Scan();
			//Filter filter = new PageFilter(1);
			//scan.setFilter(filter);
			scan.getStartRow();
			ResultScanner scanner = table.getScanner(scan);
			Iterator<Result> it = scanner.iterator();
			if (it.hasNext()) {
				Result re = it.next();
				List<Cell> listCells = re.listCells();
				for (Cell cell : listCells) {
					list.add(new String(CellUtil.cloneFamily(cell)) + ":" + new String(CellUtil.cloneQualifier(cell)));
				}
			}
			list.forEach((e)->{
				ColumnDescription columnDescription = new ColumnDescription();
				columnDescription.setFieldName(e);
				cdList.add(columnDescription);
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return cdList;
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
