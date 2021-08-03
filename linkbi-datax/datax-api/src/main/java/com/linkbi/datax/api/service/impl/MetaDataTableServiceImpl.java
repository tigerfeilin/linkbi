package com.linkbi.datax.api.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkbi.common.core.redis.RedisCache;
import com.linkbi.common.core.text.Convert;
import com.linkbi.common.utils.SecurityUtils;
import com.linkbi.common.utils.StringUtils;
import com.linkbi.common.utils.uuid.IdUtils;
import com.linkbi.datax.api.core.util.JacksonUtil;
import com.linkbi.datax.api.domain.MetaDataSource;
import com.linkbi.datax.api.domain.MetaDataTable;
import com.linkbi.datax.api.dto.ColumnDescDto;
import com.linkbi.datax.api.dto.MetaDataTableDto;
import com.linkbi.datax.api.mapper.MetaDataTableMapper;
import com.linkbi.datax.api.service.JDBCQueryService;
import com.linkbi.datax.api.service.MetaDataSourceService;
import com.linkbi.datax.api.service.MetaDataTableService;

import com.linkbi.datax.api.util.AESUtil;
import com.linkbi.datax.db.constant.DBTypeEnum;
import com.linkbi.datax.db.model.ColumnDescription;
import com.linkbi.datax.db.model.JdbcSourceData;
import com.linkbi.datax.db.model.TableDescription;
import com.linkbi.datax.db.service.IMetaDataService;
import com.linkbi.datax.db.service.impl.CommonMetaDataServiceImpl;
import com.linkbi.datax.db.util.JdbcUrlUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created 2020/01/30
 */
@Service
@Transactional(readOnly = true)
public class MetaDataTableServiceImpl extends ServiceImpl<MetaDataTableMapper, MetaDataTable> implements MetaDataTableService {

    @Resource
    private MetaDataTableMapper metaDataTableMapper;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private MetaDataSourceService metaDatasourceService;

    @Autowired
    private JDBCQueryService JDBCQueryService;

    @Autowired
    private IMetaDataService metaDataService;
    @Override
    public Map<String, Object> pageList(int start, int length, Long datasourceId, String tableType, String tableName) {
        // page list
        List<MetaDataTable> list = this.metaDataTableMapper.pageList(start, length, datasourceId, tableType, tableName);
        int list_count = this.metaDataTableMapper.pageListCount(start, length, datasourceId, tableType, tableName);
        // package result
        Map<String, Object> maps = new HashMap<>();
        maps.put("total", list_count);        // 总记录数
        maps.put("rows", list);                    // 分页列表
        return maps;
    }
    @Override
    @Transactional
    public int importTable(Long datasourceId, String tableSchema, String tableNames)
    {
        if(StringUtils.isOneEmpty(datasourceId,tableNames))
            return 0;
        String[] tList = Convert.toStrArray(tableNames);
        MetaDataSource datasource = this.metaDatasourceService.getById(datasourceId);

        if (ObjectUtil.isNull(datasource)) {
            return 0;
        }
        JdbcSourceData jdbcSourceData = new JdbcSourceData(datasource.getId().toString(),datasource.getDatasourceName(),datasource.getJdbcDriverClass(),datasource.getJdbcUrl(), AESUtil.decrypt(datasource.getJdbcUsername()),
                AESUtil.decrypt(datasource.getJdbcPassword()),datasource.getDatabaseName());
        List<ColumnDescription> columnDescriptions = null;
        List<TableDescription> tableDescriptions = this.metaDataService.queryTableList(jdbcSourceData,tableSchema,null);
        for (String tableName:tList) {
            Optional<TableDescription> option_tab = tableDescriptions.stream().filter((e)->e.getTableName().equals(tableName)).findFirst();
            if(option_tab.isPresent())
            {
                TableDescription table = option_tab.get();
                long tableId = IdUtils.getId();
                MetaDataTable metaDataTable = new MetaDataTable();
                metaDataTable.setId(tableId);
                metaDataTable.setDatasourceId(datasourceId);
                metaDataTable.setDatasourceName(datasource.getDatasourceName());
                metaDataTable.setDatabaseName(tableSchema);
                metaDataTable.setTableName(tableName);
                metaDataTable.setTableComment(table.getRemarks());
                metaDataTable.setTablePermission("1");
                metaDataTable.setTableCycle("1");
                metaDataTable.setTableStatus("1");
                metaDataTable.setUpdateUser(SecurityUtils.getLoginUser().getUsername());
                metaDataTable.setUpdateTime(new Date());
                columnDescriptions = metaDataService.queryTableColumnMeta(jdbcSourceData,tableSchema,tableName);
                if(columnDescriptions!=null)
                {
                    List<ColumnDescDto> columnDescDtos = new ArrayList<>();
                    for (ColumnDescription columnDescription : columnDescriptions)
                    {
                        ColumnDescDto columnDescDto = new ColumnDescDto();
                        BeanUtils.copyProperties(columnDescription,columnDescDto);
                        columnDescDtos.add(columnDescDto);
                    }
                    metaDataTable.setColumnJson(JacksonUtil.writeValueAsString(columnDescDtos));
                }
                if(this.save(metaDataTable))
                {
                    return 1;
                }
            }
        }
        return 0;
    }
    @Override
    @Transactional
    public int syncTable(Long tableId)
    {
        MetaDataTable metaDataTable = this.getById(tableId);
        if(metaDataTable == null)
            return 0;
        MetaDataSource datasource = this.metaDatasourceService.getById(metaDataTable.getDatasourceId());

        if (ObjectUtil.isNull(datasource)) {
            return 0;
        }
        JdbcSourceData jdbcSourceData = new JdbcSourceData(datasource.getId().toString(),datasource.getDatasourceName(),datasource.getJdbcDriverClass(),datasource.getJdbcUrl(), AESUtil.decrypt(datasource.getJdbcUsername()),
                AESUtil.decrypt(datasource.getJdbcPassword()),datasource.getDatabaseName());
        List<ColumnDescription> columnDescriptions = null;
        List<TableDescription> tableDescriptions = this.metaDataService.queryTableList(jdbcSourceData, metaDataTable.getDatabaseName(), metaDataTable.getTableName());
        Optional<TableDescription> option_tab = tableDescriptions.stream().filter((e)->e.getTableName().equals(metaDataTable.getTableName())).findFirst();
        if(option_tab.isPresent())
        {
            TableDescription tableDescription = option_tab.get();
            metaDataTable.setDatasourceName(datasource.getDatasourceName());
            metaDataTable.setTableComment(tableDescription.getRemarks());
            metaDataTable.setUpdateUser(SecurityUtils.getLoginUser().getUsername());
            metaDataTable.setUpdateTime(new Date());
            columnDescriptions = metaDataService.queryTableColumnMeta(jdbcSourceData, metaDataTable.getDatabaseName(), metaDataTable.getTableName());
            if(columnDescriptions!=null)
            {
                List<ColumnDescDto> columnDescDtos = new ArrayList<>();
                for (ColumnDescription columnDescription : columnDescriptions)
                {
                    ColumnDescDto columnDescDto = new ColumnDescDto();
                    BeanUtils.copyProperties(columnDescription,columnDescDto);
                    columnDescDtos.add(columnDescDto);
                }
                metaDataTable.setColumnJson(JacksonUtil.writeValueAsString(columnDescDtos));
            }
            if(this.updateById(metaDataTable))
            {
                return 1;
            }
        }
        return 0;
    }
    @Override
    @Transactional
    public int removeTable(Long tableId)
    {
        this.removeById(tableId);
        return 0;
    }
    @Override
    public MetaDataTableDto getTabInfo(Long tableId)
    {
        MetaDataTableDto metaDataTableDto = new MetaDataTableDto();
        MetaDataTable metaDataTable = this.getById(tableId);
        if(metaDataTable != null)
        {
            //List<JobTableColumn> jobTableColumns = this.jobTableColumnService.listByTableId(tableId);
            metaDataTableDto.setMetaData(metaDataTable);
            if(!StringUtils.isEmpty(metaDataTable.getColumnJson()))
                metaDataTableDto.setColumnList(JacksonUtil.readValue(metaDataTable.getColumnJson(),List.class,ColumnDescDto.class));
        }
        return metaDataTableDto;
    }
    @Override
    @Transactional
    public int editTable(MetaDataTableDto info)
    {
        MetaDataTable metaDataTable = info.getMetaData();
        metaDataTable.setColumnJson(JacksonUtil.writeValueAsString(info.getColumnList()));
        metaDataTable.setUpdateUser(SecurityUtils.getLoginUser().getUsername());
        metaDataTable.setUpdateTime(new Date());
        //this.jobMetaDataMapper.update(jobMetaData);
        this.updateById(metaDataTable);
        return 0;
    }
    @Override
    public List<MetaDataTable> listAll(Long datasourceId, String tableType, String tableSchema, String tableName)
    {
        return this.metaDataTableMapper.listAll(datasourceId,tableType,tableSchema,tableName);
    }
    @Override
    public Map<String, Object> getImportTables(int current,int size,Long datasourceId, String tableSchema, String tableName,List<TableDescription> tables)
    {
        Map<String,Object> dataList = new HashMap<>();
        List<TableDescription> rList = new ArrayList<>();
        try{
            String cacheKey = "/metadata/getimporttables/" + datasourceId + "/" + tableSchema + "/" +  tableName;
            tables = redisCache.getCacheObject(cacheKey);
            if(tables == null || tables.size() == 0) {
                tables = JDBCQueryService.getTables(datasourceId, tableSchema);
                redisCache.setCacheObject(cacheKey,tables,30, TimeUnit.SECONDS);
            }
            List<String> list = this.metaDataTableMapper.listAll(datasourceId,null,tableSchema,null).stream().map(MetaDataTable::getTableName).collect(Collectors.toList());
            for (TableDescription td:tables) {
                if(list.indexOf(td.getTableName()) < 0)
                {
                    if(!StringUtils.isEmpty(tableName))
                    {
                        if(td.getTableName().toLowerCase().contains(tableName.toLowerCase())) {
                            rList.add(td);
                        }
                    }
                    else
                        rList.add(td);
                }
            }
            dataList.put("total",rList.size());
            if(rList.size() > 0)
                rList = rList.stream().skip((current-1) * size).limit(size).collect(Collectors.toList());
            dataList.put("rows",rList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList;
    }
}