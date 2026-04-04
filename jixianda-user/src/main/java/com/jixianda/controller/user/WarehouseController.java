package com.jixianda.controller.user;

import com.jixianda.result.Result;
import com.jixianda.service.WarehouseService;
import com.jixianda.vo.NearestWarehouseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/warehouse")
@Slf4j
@Api(tags = "C端仓库接口")
/**
 * 用户端仓库控制器。
 * 这个类属于接口层，负责给前端提供最近仓查询能力，
 * 并与 gateway 配合通过 /user/warehouse/** 路径对外暴露，支撑商品展示和下单时的仓维度判断。
 */
public class WarehouseController {

    // 仓库业务由 service 层完成，控制器只负责参数校验和结果返回。
    private final WarehouseService warehouseService;

    /**
     * 注入仓库服务。
     * 最近仓计算和距离封装都在 service 层完成，控制器不参与业务计算。
     */
    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    /**
     * 根据经纬度查询最近仓库。
     * 前端会在首页、商品列表页或地址定位完成后调用这个接口，
     * 用返回的仓库信息决定当前展示哪个仓的商品，以及后续购物车和订单按哪个仓去处理。
     *
     * @param longitude 经度
     * @param latitude 纬度
     * @return 最近仓库及距离信息
     */
    @GetMapping("/nearest")
    @ApiOperation(value = "根据经纬度查询最近仓库", notes = "无需登录。必须传入 longitude 和 latitude 参数。")
    public Result<NearestWarehouseVO> nearest(@RequestParam(required = false) Double longitude,
                                              @RequestParam(required = false) Double latitude) {
        if (longitude == null) {
            return Result.error("longitude 不能为空");
        }
        if (latitude == null) {
            return Result.error("latitude 不能为空");
        }
        if (longitude < -180D || longitude > 180D) {
            return Result.error("longitude 超出范围，必须在[-180, 180]");
        }
        if (latitude < -90D || latitude > 90D) {
            return Result.error("latitude 超出范围，必须在[-90, 90]");
        }

        // 最近仓不仅是地理位置结果，也决定了前端后续商品和下单链路采用哪个仓库维度。
        NearestWarehouseVO nearestWarehouse = warehouseService.findNearestWarehouseWithDistance(longitude, latitude);
        if (nearestWarehouse == null) {
            return Result.error("未查询到可用仓库");
        }
        return Result.success(nearestWarehouse);
    }
}
