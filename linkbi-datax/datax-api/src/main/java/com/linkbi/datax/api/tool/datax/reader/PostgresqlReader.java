package com.linkbi.datax.api.tool.datax.reader;

import java.util.Map;

/**
 * postgresql 构建类
 *
 * @author
 * @version 1.0
 * @since 2019/8/2
 */
public class PostgresqlReader extends BaseReaderPlugin implements DataxReaderInterface {
    @Override
    public String getName() {
        return "postgresqlreader";
    }

    @Override
    public Map<String, Object> sample() {
        return null;
    }
}
