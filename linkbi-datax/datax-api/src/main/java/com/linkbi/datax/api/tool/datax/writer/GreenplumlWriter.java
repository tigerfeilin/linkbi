package com.linkbi.datax.api.tool.datax.writer;

import java.util.Map;

/**
 * postgresql writer构建类
 *
 * @author
 * @version 1.0
 * @since 2019/8/2
 */
public class GreenplumlWriter extends BaseWriterPlugin implements DataxWriterInterface {
    @Override
    public String getName() {
        return "gpdbwriter";
    }


    @Override
    public Map<String, Object> sample() {
        return null;
    }
}
