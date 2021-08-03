import request from '@/utils/request'

// 数据库信息api

// 获取表名
export function getTables(params) {
  return request({
    url: '/api/metadata/getTables',
    method: 'get',
    params
  })
}
export function getTables_V2(params) {
  return request({
    url: '/api/metadata/getTables_V2',
    method: 'get',
    params
  })
}
// 获取schema
export function getTableSchema(params) {
  return request({
    url: '/api/metadata/getDBSchema',
    method: 'get',
    params
  })
}

// 获取字段
export function getColumns(params) {
  return request({
    url: '/api/metadata/getColumns',
    method: 'get',
    params
  })
}
// 获取字段
export function getColumns_V2(params) {
  return request({
    url: '/api/metadata/getColumns_V2',
    method: 'get',
    params
  })
}
// 根据sql获取字段
export function getColumnsByQuerySql(params) {
  return request({
    url: '/api/metadata/getColumnsByQuerySql',
    method: 'get',
    params
  })
}
// 根据sql获取字段
export function getColumnsByQuerySql_V2(params) {
  return request({
    url: '/api/metadata/getColumnsByQuerySql_V2',
    method: 'get',
    params
  })
}
export function getColumnsByQuerySql_V3(params) {
  return request({
    url: '/api/metadata/getColumnsByQuerySql_V3',
    method: 'get',
    params
  })
}
// 根据datasourceID、tablename创建表【目标端】
export function createTable(params) {
  return request({
    url: '/api/metadata/createTable',
    method: 'post',
    params
  })
}
// 根据datasourceID、tablename创建表【目标端】
export function queryCreateTableSql(params) {
  return request({
    url: '/api/metadata/queryCreateDBSql',
    method: 'get',
    params
  })
}
// 判断字段是否存在，存在，即更新值，否则添加字段
export function updateColumnsValue(query) {
  return request({
    url: '/api/metadata/updateColumnsValue',
    method: 'post',
    data: query
  })
}
// adhoc
export function adhoc(params) {
  return request({
    url: '/api/metadata/adhoc',
    method: 'get',
    params
  })
}
// adhoc_nolimit
export function adhoc_nolimit(params) {
  return request({
    url: '/api/metadata/adhoc_nolimit',
    method: 'get',
    params
  })
}
// listTables
export function listTables(params) {
  return request({
    url: '/api/metadata/listTables',
    method: 'get',
    params
  })
}
//listTableById
export function listTableById(params) {
  return request({
    url: '/api/metadata/listTableById',
    method: 'get',
    params
  })
}
// getImportTables
export function getImportTables(params) {
  return request({
    url: '/api/metadata/getImportTables',
    method: 'get',
    params
  })
}

// importTable
export function importTable(params) {
  return request({
    url: '/api/metadata/importTable',
    method: 'post',
    params
  })
}
export function getTabInfo(params) {
  return request({
    url: '/api/metadata/getTabInfo',
    method: 'get',
    params
  })
}
// edit
export function editTable(data) {
  return request({
    url: '/api/metadata/editTable',
    method: 'put',
    data: data
  })
}
// remove
export function removeTable(params) {
  return request({
    url: '/api/metadata/removeTable',
    method: 'delete',
    params
  })
}
// sync
export function syncTable(params) {
  return request({
    url: '/api/metadata/syncTable',
    method: 'put',
    params
  })
}
// 根据datasourceID、schema、tablename获取查询sql
export function queryModelSql(params) {
  return request({
    url: '/api/metadata/queryModelSql',
    method: 'get',
    params
  })
}
//listMarket
export function listMarket(params) {
  return request({
    url: '/api/metadata/listMarket',
    method: 'get',
    params
  })
}
// insertMarket
export function insertMarket(params) {
  return request({
    url: '/api/metadata/insertMarket',
    method: 'post',
    params
  })
}
// editMarket
export function editMarket(data) {
  return request({
    url: '/api/metadata/editMarket',
    method: 'put',
    data: data
  })
}
// deleteMarket
export function deleteMarket(params) {
  return request({
    url: '/api/metadata/deleteMarket',
    method: 'delete',
    params
  })
}

//listSubcribes
export function listSubcribes(params) {
  return request({
    url: '/api/metadata/listSubcribes',
    method: 'get',
    params
  })
}
// insertSubcribe
export function insertSubcribe(params) {
  return request({
    url: '/api/metadata/insertSubcribe',
    method: 'post',
    params
  })
}
// deleteSubcribe
export function deleteSubcribe(params) {
  return request({
    url: '/api/metadata/deleteSubcribe',
    method: 'delete',
    params
  })
}
