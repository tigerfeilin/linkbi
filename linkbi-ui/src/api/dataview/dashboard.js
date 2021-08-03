import request from '@/utils/request'

export function addDashboard(data) {
  return request({
    url: '/api/dashboard/create',
    method: 'post',
    data:data
  })
}

export function updateDashboard(data) {
  return request({
    url: '/api/dashboard/update',
    method: 'put',
    data:data
  })
}

export function getDashboardById(id) {
  return request({
    url: '/api/dashboard/getDashboardById/' + id,
    method: 'get',
  })
}

export function deleteDashboard(id) {
  return request({
    url: '/api/dashboard/delete/' + id,
    method: 'delete'
  })
}

export function getDashboardList(params) {
  return request({
    url: '/api/dashboard/list',
    method: 'get',
    params:params
  })
}

export function addChart(params) {
  return request({
    url: '/api/dashboard/addChart',
    method: 'post',
    params
  })
}
export function deleteChart(params) {
  return request({
    url: '/api/dashboard/deleteChart',
    method: 'delete',
    params
  })
}
export function listChartByDashboardId(id) {
  return request({
    url: '/api/dashboard/listChartByDashboardId/' + id,
    method: 'get'
  })
}

export function listDashboardByChartId(id) {
  return request({
    url: '/api/dashboard/listDashboardByChartId/' + id,
    method: 'get'
  })
}


