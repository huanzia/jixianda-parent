package com.jixianda.service;

import com.jixianda.entity.Warehouse;
import com.jixianda.vo.NearestWarehouseVO;

public interface WarehouseService {

    /**
     * 根据经纬度查询最近仓库。
     * 负责返回距离最近的仓库实体，供业务侧进一步判断库存和商品展示范围。
     */
    Warehouse findNearestWarehouse(Double longitude, Double latitude);

    /**
     * 根据经纬度查询最近仓库并附带距离信息。
     * 负责给前端返回更完整的结果，支撑页面展示和后续仓维度业务。
     */
    NearestWarehouseVO findNearestWarehouseWithDistance(Double longitude, Double latitude);
}
