package com.jixianda.service;

import com.jixianda.entity.AddressBook;

import java.util.List;

public interface AddressBookService {

    /**
     * 查询用户的地址列表。
     * 负责按用户维度返回地址簿数据，供地址管理页和下单页读取。
     */
    List<AddressBook> list(AddressBook addressBook);

    /**
     * 保存新的地址信息。
     * 负责把前端新增的收货地址落库，并与当前登录用户绑定。
     */
    void save(AddressBook addressBook);

    /**
     * 按地址 id 和用户 id 查询单条地址。
     * 负责做权限隔离，避免一个用户读取到别人的地址。
     */
    AddressBook getById(Long id, Long userId);

    /**
     * 更新用户自己的地址信息。
     * 负责在权限校验通过后，更新地址簿中的联系人、电话、详细地址等字段。
     */
    boolean update(AddressBook addressBook, Long userId);

    /**
     * 将某个地址设置为默认地址。
     * 负责维护用户默认收货地址，服务下单链路的默认选中逻辑。
     */
    boolean setDefault(AddressBook addressBook, Long userId);

    /**
     * 删除用户自己的地址。
     * 负责清理不再使用的收货地址记录，并保持用户地址簿数据整洁。
     */
    boolean deleteById(Long id, Long userId);
}
