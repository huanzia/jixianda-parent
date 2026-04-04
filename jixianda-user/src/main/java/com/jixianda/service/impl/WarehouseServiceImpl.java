package com.jixianda.service.impl;

import com.jixianda.entity.Warehouse;
import com.jixianda.mapper.WarehouseMapper;
import com.jixianda.service.WarehouseService;
import com.jixianda.vo.NearestWarehouseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
/**
 * 仓库业务实现类。
 * 这个类属于 service 实现层，负责根据用户定位计算最近仓库，
 * 并把仓库结果提供给前端商品展示、购物车和订单链路，用来确定当前业务归属哪个仓维度。
 */
public class WarehouseServiceImpl implements WarehouseService {

    // 仓库数据由 mapper 提供，service 负责筛选、距离计算和返回结构封装。
    private final WarehouseMapper warehouseMapper;

    /**
     * 注入仓库 mapper。
     * 最近仓计算需要先拿到所有可用仓库，再在 service 层完成距离比较。
     */
    public WarehouseServiceImpl(WarehouseMapper warehouseMapper) {
        this.warehouseMapper = warehouseMapper;
    }

    @Override
    /**
     * 根据经纬度获取最近仓实体。
     * 这个方法更偏内部业务使用，方便后续流程拿到仓库对象继续做库存或仓维度判断。
     */
    public Warehouse findNearestWarehouse(Double longitude, Double latitude) {
        NearestWarehouseVO nearestWarehouseVO = findNearestWarehouseWithDistance(longitude, latitude);
        if (nearestWarehouseVO == null || nearestWarehouseVO.getWarehouseId() == null) {
            return null;
        }
        List<Warehouse> warehouses = warehouseMapper.listEnabled();
        if (warehouses == null || warehouses.isEmpty()) {
            return null;
        }
        for (Warehouse warehouse : warehouses) {
            if (warehouse != null && nearestWarehouseVO.getWarehouseId().equals(warehouse.getId())) {
                return warehouse;
            }
        }
        return null;
    }

    @Override
    /**
     * 根据经纬度获取最近仓及距离信息。
     * 前端首页、商品列表页和下单页都会依赖这个结果来确定当前展示哪个仓的商品，
     * 以及后续购物车和订单应该落在哪个仓维度上。
     */
    public NearestWarehouseVO findNearestWarehouseWithDistance(Double longitude, Double latitude) {
        //先过滤掉不可用仓
        List<Warehouse> warehouses = warehouseMapper.listEnabled();
        if (warehouses == null || warehouses.isEmpty()) {
            return null;
        }

        // 先遍历所有可用仓库，再在 service 层计算最短距离，避免把地理计算下放到数据库。
        Warehouse nearestWarehouse = null; // 存储最近的仓库对象
        double[] nearestPoint = null;     // 存储最近仓库的经纬度（[纬度, 经度]）
        double minDistance = Double.MAX_VALUE; // 初始化最小距离为“Double最大值”，方便后续比较
        for (Warehouse warehouse : warehouses) {
            // 跳过无效仓库（对象为空/ID为空）
            if (warehouse == null || warehouse.getId() == null) {
                continue;
            }
            // 解析仓库的位置字符串（比如仓库location字段存的是“39.908823,116.397470”，拆成经纬度数组）
            double[] latLng = parseLocation(warehouse.getLocation());
            if (latLng == null) {
                continue;
            }
            // 核心：计算用户位置与仓库位置的实际距离（单位：米），用haversine算法（球面距离，适合地理计算）
            double distance = haversineMeters(latitude, longitude, latLng[0], latLng[1]);
            if (distance < minDistance) {
                minDistance = distance;
                nearestWarehouse = warehouse;
                nearestPoint = latLng;
            }
        }

        if (nearestWarehouse == null || nearestPoint == null || minDistance == Double.MAX_VALUE) {
            return null;
        }

        NearestWarehouseVO vo = new NearestWarehouseVO();
        vo.setWarehouseId(nearestWarehouse.getId());
        vo.setWarehouseName(nearestWarehouse.getName());
        vo.setAddress(nearestWarehouse.getAddress());
        vo.setPhone(nearestWarehouse.getPhone());
        vo.setStatus(nearestWarehouse.getStatus());
        vo.setDistance(Math.round(minDistance));
        vo.setLatitude(nearestPoint[0]);
        vo.setLongitude(nearestPoint[1]);
        return vo;
    }

    /**
     * 解析仓库坐标字符串。
     * 仓库位置可能以“经度,纬度”或“纬度,经度”形式保存，这里统一转换成计算距离时可直接使用的数组。
     */
    private double[] parseLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            return null;
        }
        String normalized = location.trim()
                .replaceAll("[，、]", ",")
                .replace(" ", "");
        String[] parts = normalized.split(",");
        if (parts.length != 2) {
            return null;
        }
        try {
            double first = Double.parseDouble(parts[0]);
            double second = Double.parseDouble(parts[1]);
            if (isValidLongitude(first) && isValidLatitude(second)) {
                return new double[]{second, first};
            }
            if (isValidLatitude(first) && isValidLongitude(second)) {
                return new double[]{first, second};
            }
            log.warn("Invalid warehouse location range: {}", location);
            return null;
        } catch (NumberFormatException e) {
            log.warn("Invalid warehouse location: {}", location);
            return null;
        }
    }

    /**
     * 判断经度是否合法。
     * 这一步是最近仓计算的前置校验，防止错误坐标影响距离结果。
     */
    private boolean isValidLongitude(double value) {
        return value >= -180D && value <= 180D;
    }

    /**
     * 判断纬度是否合法。
     * 与经度校验配合使用，保证仓库坐标范围正确。
     */
    private boolean isValidLatitude(double value) {
        return value >= -90D && value <= 90D;
    }

    /**
     * 使用 Haversine 公式计算两点间球面距离。
     * 最近仓依赖真实地理距离而不是简单平面距离，所以这里按经纬度计算米级结果。
     */
    private double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        final double earthRadius = 6371000D;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }
}
