package com.linkbi.datax.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linkbi.datax.api.domain.MetaDataTable;
import com.linkbi.datax.api.dto.MetaDataTableDto;
import com.linkbi.datax.db.model.TableDescription;

import java.util.List;
import java.util.Map;


/**
 * jdbc数据源配置表服务接口
 *
 * @author
 * @version v2.0
 * @since 2020-01-10
 */
public interface MetaDataTableService extends IService<MetaDataTable> {
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
    Map<String, Object> pageList(int start, int length, Long datasourceId, String tableType, String tableName);
    List<MetaDataTable> listAll(Long datasourceId, String tableType, String tableSchema, String tableName);
    Map<String, Object> getImportTables(int current,int size,Long datasourceId, String tableSchema, String tableName,List<TableDescription> tables);
    int importTable(Long datasourceId, String tableSchema, String tableNames);
    int syncTable(Long tableId);
    int removeTable(Long tableId);
    MetaDataTableDto getTabInfo(Long tableId);
    int editTable(MetaDataTableDto info);

}