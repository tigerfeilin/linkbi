import request from '@/utils/request'

// datax插件api

export function list(params) {
  return request({
    url: '/api/metaDataSource',
    method: 'get',
    params
  })
}

export function fetched(params) {
  return request({
    url: '/api/metaDataSource/' + params,
    method: 'get'
  })
}

export function updated(data) {
  return request({
    url: '/api/metaDataSource',
    method: 'put',
    data
  })
}

export function created(data) {
  return request({
    url: '/api/metaDataSource',
    method: 'post',
    data
  })
}

export function deleted(data) {
  return request({
    url: '/api/metaDataSource',
    method: 'delete',
    params: data
  })
}

export function test(data) {
  return request({
    url: '/api/metaDataSource/test',
    method: 'post',
    data
  })
}

export function getDataSourceList(params) {
  return request({
    url: '/api/metaDataSource/all',
    method: 'get',
    params
  })
}
