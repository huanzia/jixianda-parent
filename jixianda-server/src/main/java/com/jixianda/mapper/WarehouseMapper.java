package com.jixianda.mapper;

import com.jixianda.entity.Warehouse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WarehouseMapper {

    void insert(Warehouse warehouse);

    void update(Warehouse warehouse);

    Warehouse getById(Long id);

    void deleteBatch(@Param("ids") List<Long> ids);

    List<Warehouse> pageQuery(@Param("name") String name,
                              @Param("offset") Integer offset,
                              @Param("pageSize") Integer pageSize);

    long count(@Param("name") String name);

    void updateStatus(@Param("status") Integer status, @Param("id") Long id);

    List<Warehouse> list(@Param("status") Integer status);
}
