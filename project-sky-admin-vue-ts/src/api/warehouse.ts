import request from '@/utils/request'

export const getWarehousePage = (params: any) =>
  request({
    url: '/warehouse/page',
    method: 'get',
    params
  })

export const addWarehouse = (data: any) =>
  request({
    url: '/warehouse',
    method: 'post',
    data
  })

export const updateWarehouse = (data: any) =>
  request({
    url: '/warehouse',
    method: 'put',
    data
  })

export const deleteWarehouse = (ids: string) =>
  request({
    url: '/warehouse',
    method: 'delete',
    params: { ids }
  })

export const getWarehouseById = (id: number | string) =>
  request({
    url: `/warehouse/${id}`,
    method: 'get'
  })

export const updateWarehouseStatus = (status: number, id: number | string) =>
  request({
    url: `/warehouse/status/${status}`,
    method: 'post',
    params: { id }
  })

export const getWarehouseList = (params?: any) =>
  request({
    url: '/warehouse/list',
    method: 'get',
    params
  })
