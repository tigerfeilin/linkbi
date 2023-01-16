import request from '@/utils/request'

// datax插件api

export function getList(params) {
  return request({
    url: 'api/job/pageList',
    method: 'get',
    params
  })
}

export function triggerJob(data) {
  return request({
    url: '/api/job/trigger',
    method: 'post',
    data
  })
}

export function startJob(id) {
  return request({
    url: '/api/job/start?id=' + id,
    method: 'post'
  })
}

export function stopJob(id) {
  return request({
    url: '/api/job/stop?id=' + id,
    method: 'post'
  })
}

export function batchOnlineJob(ids) {
  return request({
    url: '/api/job/batchstart?ids=' + ids,
    method: 'post'
  })
}

export function batchOfflineJob(ids) {
  return request({
    url: '/api/job/batchstop?ids=' + ids,
    method: 'post'
  })
}
export function batchTriggerJob(ids) {
  return request({
    url: '/api/job/batchtrigger?ids=' + ids,
    method: 'post'
  })
}
export function getExecutorList() {
  return request({
    url: 'api/jobGroup/list',
    method: 'get'
  })
}

export function updateJob(data) {
  return request({
    url: '/api/job/update',
    method: 'post',
    data
  })
}

export function createJob(data) {
  return request({
    url: '/api/job/add/',
    method: 'post',
    data
  })
}

export function removeJob(id) {
  return request({
    url: '/api/job/remove/' + id,
    method: 'post'
  })
}

export function nextTriggerTime(cron) {
  return request({
    url: '/api/job/nextTriggerTime?cron=' + cron,
    method: 'get'
  })
}
export function viewJobLog(id) {
  return request({
    url: '/api/log/logDetailCat?id=' + id,
    method: 'get'
  })
}

export function getUsersList(params) {
  return request({
    url: 'api/user/list',
    method: 'get',
    params
  })
}

export function getJobIdList(params) {
  return request({
    url: 'api/job/list',
    method: 'get',
    params
  })
}
// batchAdd
export function batchAddJob(data) {
  return request({
    url: '/api/job/batchAdd',
    method: 'post',
    data
  })
}

