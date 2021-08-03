package com.linkbi.datax.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linkbi.datax.api.domain.MetaDataTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * metadata配置表数据库访问层
 *
 * @author
 * @version v1.0
 * @since 2021-04-22
 */
@Mapper
public interface MetaDataTableMapper extends BaseMapper<MetaDataTable> {
    List<MetaDataTable> pageList(@Param("offset") int offset,
                                 @Param("pagesize") int pagesize,
                                 @Param("datasourceId") Long datasourceId,
                                 @Param("tableType") String tableType,
                                 @Param("tableName") String tableName);

    int pageListCount(@Param("offset") int offset,
                      @Param("pagesize") int pagesize,
                      @Param("datasourceId") Long datasourceId,
                      @Param("tableType") String tableType,
                      @Param("tableName") String tableName);
    List<MetaDataTable> listAll(@Param("datasourceId") Long datasourceId,
                                @Param("tableType") String tableType,
                                @Param("tableSchema") String tableSchema,
                                @Param("tableName") String tableName);
    int update(MetaDataTable metaDataTable);

}