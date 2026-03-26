package com.jixianda.vo;

import lombok.Data;

@Data
/**
 * 最近仓库返回对象。
 * 这个类属于 VO 层，专门承载最近仓接口返回给前端的展示数据，
 * 这样既能避免直接暴露完整仓库实体，也能把前端真正需要的仓库信息一次性返回。
 */
public class NearestWarehouseVO {

    // 最近仓的主键，前端后续选仓、下单和关联仓库时会用到。
    private Long warehouseId;

    // 最近仓名称，供页面展示和用户确认当前选中的仓。
    private String warehouseName;

    // 仓库详细地址，帮助用户确认配送归属。
    private String address;

    // 仓库联系电话，供前端在需要时展示给用户。
    private String phone;

    // 仓库状态，用来表示该仓是否可用。
    private Integer status;

    // 当前坐标到仓库的距离，前端可以据此展示“最近”的业务含义。
    private Long distance;

    // 仓库经度，便于前端地图类展示或调试。
    private Double longitude;

    // 仓库纬度，便于前端地图类展示或调试。
    private Double latitude;
}
