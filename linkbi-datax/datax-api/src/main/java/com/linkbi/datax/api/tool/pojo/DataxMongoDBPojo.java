package com.linkbi.datax.api.tool.pojo;

import com.linkbi.datax.api.dto.UpsertInfo;
import com.linkbi.datax.api.domain.MetaDataSource;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 用于传参，构建json
 *
 * @author
 * @ClassName DataxMongoDBPojo
 * @Version 2.0
 * @since 2020/03/14 11:15
 */
@Data
public class DataxMongoDBPojo {

    /**
     * hive列名
     */
    private List<Map<String, Object>> columns;

    /**
     * 数据源信息
     */
    private MetaDataSource jdbcDatasource;

    private String address;

    private String dbName;

    private String readerTable;

    private String writerTable;

    private UpsertInfo upsertInfo;

}