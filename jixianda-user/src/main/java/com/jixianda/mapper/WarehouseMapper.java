package com.jixianda.mapper;

import com.jixianda.entity.Warehouse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
/**
 * 仓库数据访问层。
 * 这个 mapper 服务于最近仓业务，和 warehouse 表以及 service 层配合，
 * 先把启用中的仓库查出来，再交给 service 计算距离并决定前端当前应该使用哪个仓。
 */
public interface WarehouseMapper {

    /**
     * 查询所有启用中的仓库。
     * 最近仓计算只需要可用仓库，所以这里先过滤掉不可用仓，减少后续业务判断成本。
     */
    @Select("select id, name, location, address, status, contact_name, phone from warehouse where status = 1")
    List<Warehouse> listEnabled();
}
