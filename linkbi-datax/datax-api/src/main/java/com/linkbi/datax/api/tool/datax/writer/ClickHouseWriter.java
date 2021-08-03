package com.linkbi.datax.api.tool.datax.writer;

import java.util.Map;

public class ClickHouseWriter extends BaseWriterPlugin implements DataxWriterInterface {
    @Override
    public String getName() {
        return "clickhousewriter";
    }

    @Override
    public Map<String, Object> sample() {
        return null;
    }
}
