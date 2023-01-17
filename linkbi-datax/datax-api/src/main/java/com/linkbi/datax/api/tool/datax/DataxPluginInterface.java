package com.linkbi.datax.api.tool.datax;

import com.linkbi.datax.api.tool.pojo.DataxHbasePojo;
import com.linkbi.datax.api.tool.pojo.DataxHivePojo;
import com.linkbi.datax.api.tool.pojo.DataxMongoDBPojo;
import com.linkbi.datax.api.tool.pojo.DataxRdbmsPojo;

import java.util.Map;

/**
 * 插件基础接口
 *
 * @author
 * @ClassName DataxPluginInterface
 * @Version 1.0
 * @since 2019/7/30 22:59
 */
public interface DataxPluginInterface {
    /**
     * 获取reader插件名称
     *
     * @return
     */
    String getName();

    /**
     * 构建
     *
     * @return dataxPluginPojo
     */
    Map<String, Object> build(DataxRdbmsPojo dataxPluginPojo);


    /**
     * doris json构建
     * @param dataxDorisPojo
     * @return
     */
    Map<String, Object> buildDoris(DataxRdbmsPojo dataxDorisPojo);

    /**
     * hive json构建
     * @param dataxHivePojo
     * @return
     */
    Map<String, Object> buildHive(DataxHivePojo dataxHivePojo);

    /**
     * hbase json构建
     * @param dataxHbasePojo
     * @return
     */
    Map<String, Object> buildHbase(DataxHbasePojo dataxHbasePojo);

    /**
     * mongodb json构建
     * @param dataxMongoDBPojo
     * @return
     */
    Map<String,Object> buildMongoDB(DataxMongoDBPojo dataxMongoDBPojo);

    /**
     * 获取示例
     *
     * @return
     */
    Map<String, Object> sample();
}
