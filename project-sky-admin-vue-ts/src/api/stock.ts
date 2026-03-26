import request from '@/utils/request'

export const getStockPage = (params: any) =>
  request({
    url: '/stock/page',
    method: 'get',
    params
  })

export const updateStock = (data: any) =>
  request({
    url: '/stock',
    method: 'put',
    data
  })

export const updateStockStatus = (status: number, params: any) =>
  request({
    url: `/stock/status/${status}`,
    method: 'post',
    params
  })
