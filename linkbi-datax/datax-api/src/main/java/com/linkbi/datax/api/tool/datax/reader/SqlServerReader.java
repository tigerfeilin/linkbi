package com.linkbi.datax.api.tool.datax.reader;

import java.util.Map;

/**
 * sqlserver reader 构建类
 *
 * @author
 * @version 1.0
 * @since 2019/8/2
 */
public class SqlServerReader extends BaseReaderPlugin implements DataxReaderInterface {
    @Override
    public String getName() {
        return "sqlserverreader";
    }

    @Override
    public Map<String, Object> sample() {
        return null;
    }
}
