package com.linkbi.datax.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linkbi.datax.api.domain.DataViewChart;
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
public interface DataViewChartMapper extends BaseMapper<DataViewChart> {
    List<DataViewChart> pageChartList(@Param("offset") int offset,
                                        @Param("pagesize") int pagesize,
                                        @Param("modeId") Long modeId,
                                        @Param("sourceId") Long sourceId,
                                        @Param("name") String name,
                                        @Param("isPrivate") String isPrivate,
                                        @Param("usrId") Long usrId);

    int pageChartListCount(@Param("modeId") Long modeId,
                           @Param("sourceId") Long sourceId,
                           @Param("name") String name,
                           @Param("isPrivate") String isPrivate,
                           @Param("usrId") Long usrId);
}