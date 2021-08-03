import request from '@/utils/request'

export function createChart(data) {
  return request({
    url: '/api/chart/create',
    method: 'post',
    data:data
  })
}

export function updateChart(data) {
  return request({
    url: '/api/chart/update',
    method: 'put',
    data
  })
}

export function getChartById(id) {
  return request({
    url: '/api/chart/getChartById/' + id,
    method: 'get',
  })
}

export function deleteChart(id) {
  return request({
    url: '/api/chart/delete/' + id,
    method: 'delete',
  })
}

export function getChartList(params) {
  return request({
    url: '/api/chart/list',
    method: 'get',
    params
  })
}
