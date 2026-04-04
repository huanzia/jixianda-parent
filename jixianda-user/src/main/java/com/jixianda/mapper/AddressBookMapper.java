package com.jixianda.mapper;

import com.jixianda.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
/**
 * 地址簿数据访问层。
 * 这个 mapper 服务于用户地址管理业务，和 XML 里的动态 SQL 配合完成地址列表、默认地址和权限限定更新删除，
 * 再由 service 层把这些结果组织成下单页面可用的数据。
 */
public interface AddressBookMapper {

    /**
     * 按条件查询地址列表。
     * 由 service 传入用户 id、手机号或默认标记，SQL 决定最终返回哪些地址记录。
     */
    List<AddressBook> list(AddressBook addressBook);

    /**
     * 插入一条新的地址记录。
     * 由 service 在保存时补齐 userId 和默认标记，再落到数据库。
     */
    @Insert("insert into address_book(user_id, consignee, phone, sex, province_code, province_name, city_code, city_name, district_code, district_name, detail, label, is_default) " +
            "values (#{userId}, #{consignee}, #{phone}, #{sex}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}, #{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
    void insert(AddressBook addressBook);

    @Select("select * from address_book where id = #{id} and user_id = #{userId}")
    /**
     * 按 id 和用户 id 查询地址。
     * 这是地址簿权限控制的关键查询，避免跨用户读取地址数据。
     */
    AddressBook getById(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 更新地址信息。
     * 对应 XML 里的动态更新语句，由 service 控制 userId 范围后执行。
     */
    int update(AddressBook addressBook);

    @Update("update address_book set is_default = #{isDefault} where user_id = #{userId}")
    /**
     * 批量修改当前用户地址的默认标记。
     * 切换默认地址时先调用这个方法清理旧默认值。
     */
    void updateIsDefaultByUserId(AddressBook addressBook);

    @Delete("delete from address_book where id = #{id} and user_id = #{userId}")
    /**
     * 删除指定用户自己的地址。
     * 只有 id 和 user_id 同时匹配时才会删除，保证数据隔离。
     */
    int deleteById(@Param("id") Long id, @Param("userId") Long userId);
}
