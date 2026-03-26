package com.jixianda.service.impl;

import com.jixianda.context.BaseContext;
import com.jixianda.entity.AddressBook;
import com.jixianda.mapper.AddressBookMapper;
import com.jixianda.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
/**
 * 地址簿业务实现类。
 * 这个类属于 service 实现层，负责把用户地址的新增、查询、更新、默认地址切换和删除串成完整流程，
 * 并配合 controller 与 mapper 为下单页面提供稳定的收货地址数据。
 */
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    // 地址簿数据持久化交给 mapper，service 只负责业务规则和用户边界控制。
    private AddressBookMapper addressBookMapper;

    @Override
    /**
     * 查询当前用户的地址列表。
     * 这个结果会被地址管理页和下单页共同使用，前者用于维护地址，后者用于选择收货地址。
     */
    public List<AddressBook> list(AddressBook addressBook) {
        return addressBookMapper.list(addressBook);
    }

    @Override
    /**
     * 保存新地址，并把它归属到当前登录用户。
     * 新增时默认不设为默认地址，避免用户新增一个地址就覆盖掉原有默认收货地址。
     */
    public void save(AddressBook addressBook) {
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBook.setIsDefault(0);
        addressBookMapper.insert(addressBook);
    }

    @Override
    /**
     * 按地址 id 和用户 id 查询单条地址。
     * 通过同时校验用户身份，确保只能访问自己的地址记录。
     */
    public AddressBook getById(Long id, Long userId) {
        return addressBookMapper.getById(id, userId);
    }

    @Override
    /**
     * 更新用户自己的地址信息。
     * 地址簿是下单链路的基础数据，所以这里必须限制在当前用户范围内更新，避免串改其他用户地址。
     */
    public boolean update(AddressBook addressBook, Long userId) {
        addressBook.setUserId(userId);
        return addressBookMapper.update(addressBook) > 0;
    }

    @Override
    @Transactional
    /**
     * 切换默认地址。
     * 先把当前用户的其他地址清成非默认，再把目标地址设为默认，保证下单页只会拿到唯一默认地址。
     */
    public boolean setDefault(AddressBook addressBook, Long userId) {
        addressBook.setUserId(userId);
        AddressBook exists = addressBookMapper.getById(addressBook.getId(), userId);
        if (exists == null) {
            return false;
        }
        // 先清掉当前用户的默认标记，再设置新默认地址，避免出现多个默认地址。
        addressBook.setIsDefault(0);
        addressBookMapper.updateIsDefaultByUserId(addressBook);

        addressBook.setIsDefault(1);
        return addressBookMapper.update(addressBook) > 0;
    }

    @Override
    /**
     * 删除用户自己的地址。
     * 删除后，这条地址就不会再出现在地址管理页，也不会作为下单候选地址被选中。
     */
    public boolean deleteById(Long id, Long userId) {
        return addressBookMapper.deleteById(id, userId) > 0;
    }
}
