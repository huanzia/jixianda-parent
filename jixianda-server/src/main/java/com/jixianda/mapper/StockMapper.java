package com.jixianda.mapper;

import com.jixianda.vo.StockVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockMapper {

    long count(@Param("warehouseId") Long warehouseId, @Param("name") String name);

    List<StockVO> pageQuery(@Param("warehouseId") Long warehouseId,
                            @Param("name") String name,
                            @Param("offset") Integer offset,
                            @Param("pageSize") Integer pageSize);
}
