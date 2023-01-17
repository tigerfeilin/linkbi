package com.linkbi.datax.api.tool.datax.writer;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.linkbi.datatx.core.util.Constants;
import com.linkbi.datax.api.domain.MetaDataSource;
import com.linkbi.datax.api.tool.pojo.DataxHbasePojo;
import com.linkbi.datax.api.tool.pojo.DataxRdbmsPojo;
import com.linkbi.datax.db.util.JdbcUrlUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * mysql writer构建类
 *
 * @author
 * @ClassName MysqlWriter
 * @Version 1.0
 * @since 2019/7/30 23:08
 */
public class DorisWriter extends BaseWriterPlugin implements DataxWriterInterface {
    @Override
    public String getName() {
        return "doriswriter";
    }


    @Override
    public Map<String, Object> sample() {
        return null;
    }

    public Map<String, Object> buildDoris(DataxRdbmsPojo plugin) {
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
        //doris相关参数
        if(StringUtils.isNotEmpty(metaDatasource.getZkAdress()))
            parameterObj.put("loadUrl", metaDatasource.getZkAdress().trim().split(";"));
        //Map<String, Object> loadPropsObj = Maps.newLinkedHashMap();
        //loadPropsObj.put("format", "json");
        //loadPropsObj.put("line_delimiter", "\\x02");
        //loadPropsObj.put("strip_outer_array", "true");
        //parameterObj.put("loadProps", ImmutableList.of(loadPropsObj));

        Map<String, Object> connectionObj = Maps.newLinkedHashMap();
        connectionObj.put("table", plugin.getTables());
        connectionObj.put("jdbcUrl", metaDatasource.getJdbcUrl());
        connectionObj.put("selectedDatabase", JdbcUrlUtils.findDBNameByJdbcUrl(metaDatasource.getJdbcUrl()));
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
}
