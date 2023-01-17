package com.linkbi.datax.api.tool.datax.writer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.linkbi.datatx.core.util.Constants;
import com.linkbi.datax.api.domain.MetaDataSource;
import com.linkbi.datax.api.tool.datax.BaseDataxPlugin;
import com.linkbi.datax.api.tool.pojo.DataxHbasePojo;
import com.linkbi.datax.api.tool.pojo.DataxHivePojo;
import com.linkbi.datax.api.tool.pojo.DataxMongoDBPojo;
import com.linkbi.datax.api.tool.pojo.DataxRdbmsPojo;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * datax writer base
 *
 * @author
 * @ClassName BaseWriterPlugin
 * @Version 1.0
 * @since 2019/8/2 16:28
 */
public abstract class BaseWriterPlugin extends BaseDataxPlugin {
    @Override
    public Map<String, Object> build(DataxRdbmsPojo plugin) {
        Map<String, Object> writerObj = Maps.newLinkedHashMap();
        writerObj.put("name", getName());

        Map<String, Object> parameterObj = Maps.newLinkedHashMap();
//        parameterObj.put("writeMode", "insert");
        MetaDataSource metaDatasource = plugin.getMetaDatasource();
        parameterObj.put("username", metaDatasource.getJdbcUsername());
        parameterObj.put("password", metaDatasource.getJdbcPassword());
        parameterObj.put("column", plugin.getRdbmsColumns());
        parameterObj.put("preSql", splitSql(plugin.getPreSql()));
        parameterObj.put("postSql", splitSql(plugin.getPostSql()));

        Map<String, Object> connectionObj = Maps.newLinkedHashMap();
        connectionObj.put("table", plugin.getTables());
        connectionObj.put("jdbcUrl", metaDatasource.getJdbcUrl());

        parameterObj.put("connection", ImmutableList.of(connectionObj));
        writerObj.put("parameter", parameterObj);

        return writerObj;
    }

    private String[] splitSql(String sql) {
        String[] sqlArr = null;
        if (StringUtils.isNotBlank(sql)) {
            Pattern p = Pattern.compile("\r\n|\r|\n|\n\r");
            Matcher m = p.matcher(sql);
            String sqlStr = m.replaceAll(Constants.STRING_BLANK);
            sqlArr = sqlStr.split(Constants.SPLIT_COLON);
        }
        return sqlArr;
    }

    @Override
    public Map<String, Object> buildDoris(DataxRdbmsPojo dataxDorisPojo) {
        return null;
    }

    @Override
    public Map<String, Object> buildHive(DataxHivePojo dataxHivePojo) {
        return null;
    }


    @Override
    public Map<String, Object> buildHbase(DataxHbasePojo dataxHbasePojo) {
        return null;
    }

    @Override
    public Map<String, Object> buildMongoDB(DataxMongoDBPojo plugin) {
        return null;
    }
}
