package com.linkbi.datax.api.tool.datax.writer;

import java.util.Map;

/**
 * oracle writer构建类
 *
 * @author
 * @version 1.0
 * @since 2019/8/2
 */
public class OraclelWriter extends BaseWriterPlugin implements DataxWriterInterface {
    @Override
    public String getName() {
        return "oraclewriter";
    }

    @Override
    public Map<String, Object> sample() {
        return null;
    }
}
