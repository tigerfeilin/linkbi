package com.linkbi.datax.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linkbi.datax.api.domain.MetaDataMarket;

import java.util.List;
import java.util.Map;


/**
 * jdbc数据源配置表服务接口
 *
 * @author
 * @version v2.0
 * @since 2020-01-10
 */
public interface MetaDataMarketService extends IService<MetaDataMarket> {
    /**
     * page list
     *
     * @param start
     * @param length
     * @param datasourceId
     * @param tableType
     * @param tableName
     * @return
     */
    Map<String, Object> pageMarketList(int start, int length, Long datasourceId, String tableType, String tableName);
    Map<String, Object> pageSubcribeList(int start, int length, Long datasourceId, String tableType, String tableName);
    int insertSubcribe(Long modeId);
    boolean deleteSubcribe(Long modeId);
    boolean deleteSubcribeByModeId(Long modeId);
    int listSubcribeById(Long modeId);
    List<MetaDataMarket> listMarketAll(Long datasourceId, String tableType, String tableName);
    List<MetaDataMarket> listMarketByUser();
    int insertMarket(MetaDataMarket entity);

}