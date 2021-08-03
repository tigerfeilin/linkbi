package com.linkbi.datax.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linkbi.datax.api.domain.MetaDataMarket;
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
public interface MetaDataMarketMapper extends BaseMapper<MetaDataMarket> {
    List<MetaDataMarket> pageMarketList(@Param("offset") int offset,
                                  @Param("pagesize") int pagesize,
                                  @Param("datasourceId") Long datasourceId,
                                  @Param("modeType") String tableType,
                                  @Param("modeName") String tableName);

    int pageMarketListCount(@Param("datasourceId") Long datasourceId,
                      @Param("modeType") String tableType,
                      @Param("modeName") String tableName);
    List<MetaDataMarket> pageSubcribeList(@Param("offset") int offset,
                                          @Param("pagesize") int pagesize,
                                          @Param("userId") Long userId,
                                          @Param("datasourceId") Long datasourceId,
                                          @Param("modeType") String tableType,
                                          @Param("modeName") String tableName);

    int pageSubcribeListCount(@Param("userId") Long userId,
                      @Param("datasourceId") Long datasourceId,
                      @Param("modeType") String tableType,
                      @Param("modeName") String tableName);

    List<MetaDataMarket> listMarketAll(@Param("datasourceId") Long datasourceId,
                                 @Param("modeType") String tableType,
                                 @Param("modeName") String tableName);
    List<MetaDataMarket> listMarketByUser(@Param("userId") Long userId);
    int insertSubcribe(@Param("userId") Long userId,
                       @Param("modeId") Long modeId);
    boolean deleteSubcribe(@Param("userId") Long userId,
                       @Param("modeId") Long modeId);
    boolean deleteSubcribeByModeId(@Param("modeId") Long modeId);
    int listMarketBySql(@Param("sqlQuery") String sqlQuery);
    int listSubcribeById(@Param("userId") Long userId,
                         @Param("modeId") Long modeId);
}