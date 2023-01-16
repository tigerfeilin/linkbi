package com.linkbi.datax.api.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.linkbi.common.annotation.Log;
import com.linkbi.common.core.domain.AjaxResult;
import com.linkbi.common.enums.BusinessType;
import com.linkbi.datax.api.core.util.JacksonUtil;
import com.linkbi.datax.api.domain.MetaDataMarket;
import com.linkbi.datax.api.domain.MetaDataTable;
import com.linkbi.datax.api.dto.MetaDataTableDto;
import com.linkbi.datax.api.service.JDBCQueryService;
import com.linkbi.datax.api.service.MetaDataMarketService;
import com.linkbi.datax.api.service.MetaDataTableService;
import com.linkbi.datax.db.model.ColumnDescription;
import com.linkbi.datax.db.model.TableDescription;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 查询数据库表名，字段的控制器
 *
 * @author
 * @ClassName MetadataController
 * @Version 2.1.2
 * @since 2020/05/31 20:48
 */
@RestController
@RequestMapping("api/metadata")
@Api(tags = "jdbc数据库查询接口")
public class MetaDataController extends BaseController {

    @Autowired
    private JDBCQueryService JDBCQueryService;

    @Autowired
    private MetaDataTableService metaDataTableService;
    @Autowired
    private MetaDataMarketService metaDataMarketService;
    /**
     * 根据数据源id获取mongo库名
     *
     * @param datasourceId
     * @return
     */
    @GetMapping("/getDBs")
    //@ApiOperation("根据数据源id获取mongo库名")
    public R<List<String>> getDBs(Long datasourceId) throws IOException {
        return success(JDBCQueryService.getDBs(datasourceId));
    }


    /**
     * 根据数据源id,dbname获取CollectionNames
     *
     * @param datasourceId
     * @return
     */
    @GetMapping("/collectionNames")
    //@ApiOperation("根据数据源id,dbname获取CollectionNames")
    public R<List<String>> getCollectionNames(Long datasourceId,String dbName) throws IOException {
        return success(JDBCQueryService.getCollectionNames(datasourceId,dbName));
    }

    /**
     * 获取PG table schema
     *
     * @param datasourceId
     * @return
     */
    @GetMapping("/getDBSchema")
    //@ApiOperation("根据数据源id获取 db schema")
    public R<List<String>> getTableSchema(Long datasourceId) {
        return success(JDBCQueryService.getTableSchema(datasourceId));
    }

    /**
     * 根据数据源id获取可用表名
     *
     * @param datasourceId
     * @return
     */
    @GetMapping("/getTables")
    //@ApiOperation("根据数据源id获取可用表名")
    public R<List<String>> getTableNames(Long datasourceId, String tableSchema) throws IOException {
        List<TableDescription> tables = JDBCQueryService.getTables(datasourceId,tableSchema);
        return success(tables.stream().map(TableDescription::getTableName).collect(Collectors.toList()));
    }

    /**
     * 根据数据源id获取可用表名
     *
     * @param datasourceId
     * @return
     */
    @GetMapping("/getTables_V2")
    //@ApiOperation("根据数据源id获取可用表名")
    public R<List<TableDescription>> getTableNames_V2(Long datasourceId, String tableSchema) throws IOException {
        List<TableDescription> tables = JDBCQueryService.getTables(datasourceId,tableSchema);
        return success(tables);
    }

    /**
     * 根据数据源id和表名获取所有字段
     *
     * @param datasourceId 数据源id
     * @param tableName    表名
     * @return
     */
    @GetMapping("/getColumns")
    //@ApiOperation("根据数据源id和表名获取所有字段")
    public R<List<String>> getColumns(Long datasourceId, String tableSchema, String tableName) throws IOException {
        List<ColumnDescription> columns = JDBCQueryService.getColumns(datasourceId,tableSchema,tableName);
        return success(columns.stream().map(ColumnDescription::getFieldName).collect(Collectors.toList()));
    }

    /**
     * 根据数据源id和表名获取所有字段
     *
     * @param datasourceId 数据源id
     * @param tableName    表名
     * @return
     */
    @GetMapping("/getColumns_V2")
    //@ApiOperation("根据数据源id和表名获取所有字段")
    public R<List<ColumnDescription>> getColumns_V2(Long datasourceId, String tableSchema, String tableName) throws IOException {
        List<ColumnDescription> columns = JDBCQueryService.getColumns(datasourceId,tableSchema,tableName);
        return success(columns);
    }

    /**
     * 根据数据源id和sql语句获取所有字段
     *
     * @param datasourceId 数据源id
     * @param querySql     表名
     * @return
     */
    @GetMapping("/getColumnsByQuerySql")
    //@ApiOperation("根据数据源id和sql语句获取所有字段")
    public R<List<String>> getColumnsByQuerySql(Long datasourceId, String querySql) throws SQLException {
        return success(JDBCQueryService.getColumnsByQuerySql(datasourceId, querySql).stream().map(ColumnDescription::getFieldName).collect(Collectors.toList()));
    }

    @GetMapping("/getColumnsByQuerySql_V2")
    //@ApiOperation("根据数据源id和sql语句获取所有字段")
    public R<List<ColumnDescription>> getColumnsByQuerySql_V2(Long datasourceId, String querySql) throws SQLException {
        List<ColumnDescription> columns = JDBCQueryService.getColumnsByQuerySql(datasourceId, querySql);
        return success(columns);
    }

    @GetMapping("/getColumnsByQuerySql_V3")
    //@ApiOperation("根据数据源id和sql语句获取所有字段")
    public AjaxResult getColumnsByQuerySql_V3(Long marketId) throws SQLException {
        MetaDataMarket metaDataMarket = this.metaDataMarketService.getById(marketId);
        if(metaDataMarket != null)
        {
            List<ColumnDescription> columns = JDBCQueryService.getColumnsByQuerySql(metaDataMarket.getDatasourceId(), metaDataMarket.getQuerySql());
            return AjaxResult.success(columns);
        }
        else
            return AjaxResult.error("无效信息");

    }
    /**
     * 根据数据源id和sql语句执行sql语句
     *
     * @param datasourceId 数据源id
     * @param querySql     表名
     * @return
     */
    @PostMapping("/createTable")
    //@ApiOperation("根据数据源id执行sql语句")
    @Log(title = "SQL执行", businessType = BusinessType.UPDATE)
    public R<String> executeSql(Long datasourceId, String querySql) throws SQLException {
        return success(JDBCQueryService.execSqlCommand(datasourceId, querySql));
    }

    /**
     * 根据数据源id和sql语句执行sql语句
     *
     * @param datasourceId 数据源id
     * @param querySql     表名
     * @return
     */
    @GetMapping("/adhoc")
    //@ApiOperation("即席查询功能")
    @PreAuthorize("@ss.hasPermi('metadata:adhoc:sqlquery')")
    @Log(title = "即席查询", businessType = BusinessType.EXPORT)
    public Map<String,Object> adhoc(@RequestParam(required = false, defaultValue = "0") int current,
                                    @RequestParam(required = false, defaultValue = "10") int size,
                                    Long datasourceId,String tableSchema, String tableName, String querySql) {
        return JDBCQueryService.queryDataList(current,size,datasourceId,tableSchema,tableName,querySql);
    }

    @GetMapping("/adhoc_nolimit")
    //@ApiOperation("即席查询功能")
    //@PreAuthorize("@ss.hasPermi('metadata:adhoc:sqlquery')")
    public Map<String,Object> adhoc_nolimit(Long datasourceId,String tableSchema, String tableName, String querySql) {
        return JDBCQueryService.queryDataList(datasourceId,tableSchema,tableName,querySql);
    }
    /**
     * 根据数据源id和sql语句执行sql语句
     *
     * @param datasourceId 数据源id
     * @param targetdatasourceId 数据源id
     * @param tablename     表名
     * @return
     */
    @GetMapping("/queryCreateDBSql")
    //@ApiOperation("根据源数据源id、目标数据源id生成目标数据源建表sql语句")
    public R<String> queryCreateDBSql(Long datasourceId,Long targetdatasourceId,String schema,String targetschema, String tablename){
        return success(JDBCQueryService.queryCreateDBSql(datasourceId, targetdatasourceId,schema,targetschema,tablename));
    }

    @GetMapping("/listTables")
    //@ApiOperation("获取metadata数据表")
    public R<Map<String, Object>> list(@RequestParam(required = false, defaultValue = "0") int current,
                               @RequestParam(required = false, defaultValue = "10") int size,
                               Long datasourceId, String tableType, String tableName){
        return success(metaDataTableService.pageList((current-1)*size,size,datasourceId,tableType,tableName));
    }
    @GetMapping("/listTableById")
    //@ApiOperation("获取metadata数据表")
    public R<MetaDataTable> listTableById(Long tableId){
        return success(metaDataTableService.getById(tableId));
    }
    @GetMapping("/getImportTables")
    //@ApiOperation("获取metadata数据表")
    public R<Map<String, Object>> getImportTables(@RequestParam(required = false, defaultValue = "0") int current,
                                       @RequestParam(required = false, defaultValue = "10") int size,
                                       Long datasourceId, String tableSchema, String tableName) throws IOException {

        //List<TableDescription> tables = datasourceQueryService.getTables(datasourceId,tableSchema);
        return success(metaDataTableService.getImportTables(current,size,datasourceId,tableSchema,tableName,null));
    }
    @GetMapping("/listAllTables")
    //@ApiOperation("获取metadata数据表")
    public R<List<String>> listAllTables(Long datasourceId, String tableType,String tableSchema){
        List<MetaDataTable> list = metaDataTableService.listAll(datasourceId,tableType,tableSchema,null);
        return success(list.stream().map(MetaDataTable::getTableName).collect(Collectors.toList()));
    }
    @GetMapping("/listSchemas")
    //@ApiOperation("获取metadata数据表")
    public R<List<String>> listSchemas(Long datasourceId){
        List<MetaDataTable> list = metaDataTableService.listAll(datasourceId,null,null,null);
        return success(list.stream().map(MetaDataTable::getDatabaseName).collect(Collectors.toList()));
    }
    @PostMapping("/importTable")
    @PreAuthorize("@ss.hasPermi('metadata:table:import')")
    @Log(title = "元数据管理", businessType = BusinessType.INSERT)
    public R<Integer> importTable(Long datasourceId, String tableSchema, String tables){
        return success(metaDataTableService.importTable(datasourceId,tableSchema,tables));
    }
    @PutMapping("/syncTable")
    @PreAuthorize("@ss.hasPermi('metadata:table:import')")
    @Log(title = "元数据管理", businessType = BusinessType.UPDATE)
    public R<Integer> syncTable(Long tableId){
        return success(metaDataTableService.syncTable(tableId));
    }
    @GetMapping("/getTabInfo")
    //@ApiOperation("获取metadata数据表")
    public R<MetaDataTableDto> getTabInfo(Long tableId){
        MetaDataTableDto map = metaDataTableService.getTabInfo(tableId);
        return success(map);
    }
    @PutMapping("/editTable")
    //@ApiOperation("编辑metadata数据表")
    @PreAuthorize("@ss.hasPermi('metadata:table:edit')")
    @Log(title = "元数据管理", businessType = BusinessType.UPDATE)
    public R<Integer> editTable(@Validated @RequestBody MetaDataTableDto info){
        JacksonUtil.writeValueAsString(info.getColumnList());
        return success(metaDataTableService.editTable(info));
    }
    @DeleteMapping("/removeTable")
    //@ApiOperation("删除metadata数据表")
    @PreAuthorize("@ss.hasPermi('metadata:table:remove')")
    @Log(title = "元数据管理", businessType = BusinessType.DELETE)
    public R<Integer> removeTable(Long tableId){
        return success(metaDataTableService.removeTable(tableId));
    }

    @GetMapping("/queryModelSql")
    //@ApiOperation("根据源数据源id、目标数据源id生成目标数据源建表sql语句")
    public R<String> queryModelSql(Long datasourceId,String schema,String tablename){
        return success(JDBCQueryService.queryModelSql(datasourceId, schema,tablename));
    }



    @GetMapping("/listSubcribes")
    //@ApiOperation("获取metadata数据表")
    public R<Map<String, Object>> listSubcribes(@RequestParam(required = false, defaultValue = "0") int current,
                                             @RequestParam(required = false, defaultValue = "10") int size,
                                             Long datasourceId, String modeType,String modeName){
        Map<String, Object> list = metaDataMarketService.pageSubcribeList((current-1)*size,size,datasourceId,modeType,modeName);
        return success(list);
    }
    @PostMapping("/insertSubcribe")
    public R<Integer> insertSubcribe(Long modeId){
        return success(metaDataMarketService.insertSubcribe(modeId));
    }
    @DeleteMapping("/deleteSubcribe")
    public R<Boolean> deleteSubcribe(Long modeId){
        return success(metaDataMarketService.deleteSubcribe(modeId));
    }

    @GetMapping("/listMarket")
    //@ApiOperation("获取metadata数据表")
    public R<Map<String, Object>> listMarket(@RequestParam(required = false, defaultValue = "0") int current,
                                             @RequestParam(required = false, defaultValue = "10") int size,
                                             Long datasourceId, String modeType,String modeName){
        Map<String, Object> list = metaDataMarketService.pageMarketList((current-1)*size,size,datasourceId,modeType,modeName);
        return success(list);
    }
    @PostMapping("/insertMarket")
    //@PreAuthorize("@ss.hasPermi('metadata:market:insert')")
    @Log(title = "数据集市", businessType = BusinessType.INSERT)
    public R<Integer> insertMarket(MetaDataMarket entity){
        return success(metaDataMarketService.insertMarket(entity));
    }
    @PutMapping("/editMarket")
    //@PreAuthorize("@ss.hasPermi('metadata:market:edit')")
    @Log(title = "数据集市", businessType = BusinessType.INSERT)
    public R<Boolean> editMarket(MetaDataMarket entity){
        return success(metaDataMarketService.updateById(entity));
    }
    @DeleteMapping("/deleteMarket")
    //@PreAuthorize("@ss.hasPermi('metadata:market:delete')")
    @Log(title = "数据集市", businessType = BusinessType.INSERT)
    public R<Boolean> deleteMarket(Long marketId){
        if(metaDataMarketService.removeById(marketId))
            return success(metaDataMarketService.deleteSubcribeByModeId(marketId));
        else
            return success(false);
    }
}
