import request from '@/utils/request'

export const getWorkspaceBusinessData = () =>
  request({
    url: '/workspace/businessData',
    method: 'get'
  })

export const getWorkspaceOrderOverview = () =>
  request({
    url: '/workspace/overviewOrders',
    method: 'get'
  })

export const getWorkspaceDishOverview = () =>
  request({
    url: '/workspace/overviewDishes',
    method: 'get'
  })

export const getOrdersStatistics = (params: any) =>
  request({
    url: '/report/ordersStatistics',
    method: 'get',
    params
  })

export const getTurnoverStatistics = (params: any) =>
  request({
    url: '/report/turnoverStatistics',
    method: 'get',
    params
  })
