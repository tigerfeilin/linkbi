package com.linkbi.datax.api.tool.pojo;

import com.linkbi.datax.api.dto.Range;
import com.linkbi.datax.api.dto.VersionColumn;
import com.linkbi.datax.api.domain.MetaDataSource;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DataxHbasePojo {

  private List<Map<String,Object>> columns;

  /**
   * 数据源信息
   */
  private MetaDataSource jdbcDatasource;


  private String readerHbaseConfig;

  private String readerTable;

  private String readerMode;

  private String readerMaxVersion;

  private Range readerRange;

  private String writerHbaseConfig;

  private String writerTable;

  private String writerMode;

  private VersionColumn writerVersionColumn;

  private String writerRowkeyColumn;
}
