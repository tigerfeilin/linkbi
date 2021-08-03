
package com.linkbi.datax.db.database;

import java.util.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.alibaba.druid.pool.DruidDataSource;
import com.linkbi.datax.db.model.JdbcSourceData;
import com.linkbi.datax.db.util.LocalCacheUtil;
import org.apache.commons.lang3.StringUtils;
import com.linkbi.datax.db.constant.DBTypeEnum;
import com.linkbi.datax.db.model.ColumnDescription;
import com.linkbi.datax.db.model.ColumnMetaData;
import com.linkbi.datax.db.model.TableDescription;
import com.linkbi.datax.db.util.JdbcOperatorUtils;

/**
 * 数据库元信息抽象基类
 * 
 * @author
 *
 */
public abstract class AbstractDatabase implements IDatabaseInterface {

	public static final int CLOB_LENGTH = 9999999;

	protected Connection connection = null;
	protected DatabaseMetaData metaData = null;
	private String catalogName = null;

	public AbstractDatabase(String driverClassName) {
		this.catalogName = null;
		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void connect(JdbcSourceData jdbcSourceData) {
		try {
			if (LocalCacheUtil.get(jdbcSourceData.getDatasourceId()) == null) {
				getDataSource(jdbcSourceData);
			} else {
				this.connection = (Connection) LocalCacheUtil.get(jdbcSourceData.getDatasourceId());
				if (!this.connection.isValid(500)) {
					LocalCacheUtil.remove(jdbcSourceData.getJdbcUrl());
					getDataSource(jdbcSourceData);
				}
			}
			LocalCacheUtil.set(jdbcSourceData.getDatasourceId(), this.connection, 4 * 60 * 60 * 1000);
			this.metaData = Objects.requireNonNull(this.connection.getMetaData());
		} catch (SQLException | RuntimeException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public void getDataSource(JdbcSourceData jdbcSourceData){
		try {
			Properties props = new Properties();
			props.put("user", jdbcSourceData.getUserName());
			if(jdbcSourceData.getPassWd() != null)
				props.put("password", jdbcSourceData.getPassWd());
			//props.put("remarksReporting", "true");
			// 设置最大时间
			close();
			DriverManager.setLoginTimeout(12);
			this.connection = DriverManager.getConnection(jdbcSourceData.getJdbcUrl(), props);
			if (Objects.isNull(this.connection)) {
				throw new RuntimeException("数据库连接失败，连接参数为：" + jdbcSourceData.getJdbcUrl());
			}
			this.metaData = Objects.requireNonNull(this.connection.getMetaData());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private DruidDataSource createTargetDataSource(JdbcSourceData jdbcSourceData) {
		DruidDataSource ds = new DruidDataSource();
		ds.setName(jdbcSourceData.getDatasourceId());
		ds.setUrl(jdbcSourceData.getJdbcUrl());
		ds.setDriverClassName(jdbcSourceData.getDriverClassName());
		ds.setUsername(jdbcSourceData.getUserName());
		if(!StringUtils.isEmpty(jdbcSourceData.getPassWd()))
			ds.setPassword(jdbcSourceData.getPassWd());
		ds.setMaxActive(5);
		ds.setMinIdle(2);
		ds.setInitialSize(2);
		ds.setMaxWait(20000);
		ds.setMaxOpenPreparedStatements(5);
		return ds;
	}
	@Override
	public void close() {
		if (null != connection) {
			try {
				connection.close();
			} catch (SQLException ignored) {
			}
			connection = null;
		}
	}

	@Override
	public List<String> querySchemaList() {
		Set<String> ret = new HashSet<>();
		ResultSet schemas = null;
		try {
			schemas = this.metaData.getSchemas();
			while (schemas.next()) {
				ret.add(schemas.getString("TABLE_SCHEM"));
			}
			return new ArrayList<>(ret);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (null != schemas) {
					schemas.close();
				}
			} catch (SQLException ignored) {
			}
		}

	}

	@Override
	public List<TableDescription> queryTableList(String schemaName,String tableNamePat) {
		List<TableDescription> ret = new ArrayList<>();
		Set<String> uniqueSet = new HashSet<>();
		ResultSet tables = null;
		try {
			if(StringUtils.isAllEmpty(schemaName,tableNamePat))
				return ret;
			if(StringUtils.isEmpty(tableNamePat))
				tables = this.metaData.getTables(this.catalogName, schemaName, "%", new String[] { "TABLE", "VIEW" });
			else
				tables = this.metaData.getTables(this.catalogName, schemaName, tableNamePat, new String[] { "TABLE", "VIEW" });
			while (tables.next()) {
				String tableName = tables.getString("TABLE_NAME");
				if (uniqueSet.contains(tableName)) {
					continue;
				} else {
					uniqueSet.add(tableName);
				}
				TableDescription td = new TableDescription();
				td.setSchemaName(schemaName);
				td.setTableName(tableName);
				td.setRemarks(tables.getString("REMARKS"));
				td.setTableType(tables.getString("TABLE_TYPE").toUpperCase());
				ret.add(td);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (null != tables) {
					tables.close();
					tables = null;
				}
			} catch (SQLException ignored) {
			}
		}
		return ret;
	}

	@Override
	public List<ColumnDescription> queryTableColumnMeta(String schemaName, String tableName) {
		String sql = this.getTableFieldsQuerySQL(schemaName, tableName);
		List<ColumnDescription> ret = this.querySelectSqlColumnMeta(sql);
		ResultSet columns = null;
		try {
			columns = this.metaData.getColumns(this.catalogName, schemaName, tableName, null);
			while (columns.next()) {
				String columnName = columns.getString("COLUMN_NAME");
				String remarks = columns.getString("REMARKS");
				for (ColumnDescription cd : ret) {
					if (columnName.equalsIgnoreCase(cd.getFieldName())) {
						cd.setRemarks(remarks);
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (null != columns) {
					columns.close();
					columns = null;
				}
			} catch (SQLException ignored) {
			}
		}

		return ret;
	}

	@Override
	public List<String> queryTablePrimaryKeys(String schemaName, String tableName) {
		Set<String> ret = new HashSet<>();
		ResultSet primarykeys = null;
		try {
			primarykeys = this.metaData.getPrimaryKeys(this.catalogName, schemaName, tableName);
			while (primarykeys.next()) {
				String name = primarykeys.getString("COLUMN_NAME");
				ret.add(name);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (null != primarykeys) {
					primarykeys.close();
				}
			} catch (SQLException ignored) {
			}
		}
		return new ArrayList<>(ret);
	}

	@Override
	public abstract List<ColumnDescription> querySelectSqlColumnMeta(String sql);

	@Override
	public String getQuerySql(String schemaName, String tableName, String querySql)
	{
		String sql = "";
		if(StringUtils.isAllEmpty(tableName,querySql))
			return "";
		else{
			if(StringUtils.isEmpty(querySql))
			{
				if(StringUtils.isEmpty(schemaName))
				{
					sql = String.format("SELECT * FROM %s LIMIT 1000", tableName);
				}
				else
				{
					sql = String.format("SELECT * FROM %s.%s LIMIT 1000",schemaName, tableName);
				}
			}
			else{
				sql = "SELECT * FROM (" + querySql + " ) AS T_ LIMIT 1000";
			}
			return sql;
		}
	}
	@Override
	public String queryModelSQL(String schemaName, String tableName)
	{
		String sql = "";
		if(StringUtils.isAllEmpty(tableName))
			return "";
		else{
			if(StringUtils.isEmpty(schemaName))
			{
				sql = String.format("SELECT * FROM %s ", tableName);
			}
			else
			{
				sql = String.format("SELECT * FROM %s.%s ",schemaName, tableName);
			}
			return sql;
		}
	}
	@Override
	public Map<String,Object> queryDataList(String schemaName, String tableName, String querySql)
	{
		Map<String,Object> dataList = new HashMap<>();
		List<Map<String,String>> rList = new ArrayList<>();
		Map<String,String> map;
		ResultSet rs;
		querySql = getQuerySql(schemaName,tableName,querySql);
		if(StringUtils.isEmpty(querySql))
			return dataList;
		try(PreparedStatement pstmt = this.connection.prepareStatement(querySql)) {
			rs = pstmt.executeQuery();
			ResultSetMetaData rsm = rs.getMetaData();
			int col = rsm.getColumnCount();
			for (int i = 1; i <= col; i++)
			{
				map = new HashMap<>();
				map.put("col_name",rsm.getColumnName(i));
				map.put("col_comment",rsm.getColumnLabel(i));
				rList.add(map);
			}
			dataList.put("cols",rList);
			rList = new ArrayList<>();
			while(rs.next())
			{
				map = new HashMap<>();
				for (int i = 1; i <= col; i++)
				{
					map.put(rsm.getColumnName(i),rs.getString(i));
				}
				rList.add(map);
			}
			dataList.put("rows",rList);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return dataList;
	}
	@Override
	public abstract String getTableMaxIdSql(String tableName, String pkName);
	@Override
	public long queryTableMaxId(String tableName, String pkName)
	{
		ResultSet rs = null;
		long maxVal = -1;
		String wrapperSql = this.getTableMaxIdSql(tableName,pkName);
		try(PreparedStatement pstmt = this.connection.prepareStatement(wrapperSql);) {
			rs = pstmt.executeQuery();
			rs.next();
			maxVal = rs.getLong(1);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return maxVal;
	}
	@Override
	public void testQuerySQL(String sql) {
		String wrapperSql = this.getTestQuerySQL(sql);
		try(PreparedStatement pstmt = this.connection.prepareStatement(wrapperSql);) {
			pstmt.executeQuery();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public void executeUpdate(String sql)
	{
		try(PreparedStatement pstmt = this.connection.prepareStatement(sql);) {
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public String getQuotedSchemaTableCombination(String schemaName, String tableName) {
		return String.format(" \"%s\".\"%s\" ", schemaName, tableName);
	}

	@Override
	public String getFieldDefinition(ColumnMetaData v, List<String> pks, boolean useAutoInc, boolean addCr) {
		throw new RuntimeException("AbstractDatabase Unempliment!");
	}

	@Override
	public String getPrimaryKeyAsString(List<String> pks) {
		if (!pks.isEmpty()) {
			return "\"" +
					StringUtils.join(pks, "\" , \"") +
					"\"";
		}

		return "";
	}

	@Override
	public abstract String formatSQL(String sql);

	/**************************************
	 * internal function
	 **************************************/

	protected abstract String getTableFieldsQuerySQL(String schemaName, String tableName);

	protected abstract String getTestQuerySQL(String sql);

	protected List<ColumnDescription> getSelectSqlColumnMeta(String querySQL, DBTypeEnum dbtype) {
		List<ColumnDescription> ret = new ArrayList<ColumnDescription>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = this.connection.prepareStatement(querySQL);
			rs = pstmt.executeQuery();

			ResultSetMetaData m = rs.getMetaData();
			int columns = m.getColumnCount();
			for (int i = 1; i <= columns; i++) {
				String name = m.getColumnLabel(i);
				if (null == name) {
					name = m.getColumnName(i);
				}

				ColumnDescription cd = new ColumnDescription();
				cd.setFieldName(name);
				cd.setLabelName(name);
				cd.setFieldType(m.getColumnType(i));
				if (0 != cd.getFieldType()) {
					cd.setFieldTypeName(m.getColumnTypeName(i));
					cd.setFiledTypeClassName(m.getColumnClassName(i));
					cd.setDisplaySize(m.getColumnDisplaySize(i));
					cd.setPrecisionSize(m.getPrecision(i));
					cd.setScaleSize(m.getScale(i));
					cd.setAutoIncrement(m.isAutoIncrement(i));
					cd.setNullable(m.isNullable(i) != ResultSetMetaData.columnNoNulls);
				} else {
					// 处理视图中NULL as fieldName的情况
					cd.setFieldTypeName("CHAR");
					cd.setFiledTypeClassName(String.class.getName());
					cd.setDisplaySize(1);
					cd.setPrecisionSize(1);
					cd.setScaleSize(0);
					cd.setAutoIncrement(false);
					cd.setNullable(true);
				}

				boolean signed = false;
				try {
					signed = m.isSigned(i);
				} catch (Exception ignored) {
					// This JDBC Driver doesn't support the isSigned method
					// nothing more we can do here by catch the exception.
				}
				cd.setSigned(signed);
				cd.setDbType(dbtype);

				ret.add(cd);
			}

			return ret;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcOperatorUtils.closeResultSet(rs);
			JdbcOperatorUtils.closeStatement(pstmt);
		}
	}
}