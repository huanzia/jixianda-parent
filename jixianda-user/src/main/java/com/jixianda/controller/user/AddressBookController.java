package com.jixianda.controller.user;

import com.jixianda.context.BaseContext;
import com.jixianda.entity.AddressBook;
import com.jixianda.result.Result;
import com.jixianda.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Api(tags = "C端地址簿接口")
/**
 * 用户端地址
 * 这个类属于接口层，负责把前端地址簿页面的增删改查请求转给服务层，
 * 并配合网关的鉴权过滤器使用当前登录用户的身份信息，保证每个用户只能操作自己的地址。
 */
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 查询当前登录用户的全部地址。
     * 服务于地址管理页面，也会被下单页面用于展示可选地址列表。
     *
     * @return 当前用户的地址集合；未登录时返回错误结果。
     */
    @GetMapping("/list")
    @ApiOperation(value = "查询当前登录用户的全部地址", notes = "需要在 authentication 请求头中携带登录令牌。")
    public Result<List<AddressBook>> list() {
        Long currentId = BaseContext.getCurrentId();
        if (currentId == null) {
            return Result.error("user not login");
        }
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(currentId);
        return Result.success(addressBookService.list(addressBook));
    }

    /**
     * 新增一条地址簿记录。
     * 在新增收货地址页面调用，保存后供后续下单直接选择。
     *
     * @param addressBook 前端提交的地址信息
     * @return 成功或失败结果
     */
    @PostMapping
    @ApiOperation(value = "新增地址簿记录", notes = "需要在 authentication 请求头中携带登录令牌。")
    public Result save(@RequestBody AddressBook addressBook) {
        if (BaseContext.getCurrentId() == null) {
            return Result.error("user not login");
        }
        addressBookService.save(addressBook);
        return Result.success();
    }

    /**
     * 根据地址 id 查询单条地址信息。
     *
     * @param id 地址主键
     * @return 地址详情；未找到或无权限时返回错误结果
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据地址ID查询单条地址信息", notes = "需要在 authentication 请求头中携带登录令牌。")
    public Result<AddressBook> getById(@PathVariable Long id) {
        Long currentId = BaseContext.getCurrentId();
        if (currentId == null) {
            return Result.error("user not login");
        }
        AddressBook addressBook = addressBookService.getById(id, currentId);
        if (addressBook == null) {
            return Result.error("address not found or no permission");
        }
        return Result.success(addressBook);
    }

    /**
     * 更新一条已有地址。
     * 前端在编辑收货地址时调用，服务层会按当前用户限制数据范围，避免改到别人的地址。
     *
     * @param addressBook 需要更新的地址信息，必须携带 id
     * @return 成功或失败结果
     */
    @PutMapping
    @ApiOperation(value = "更新一条已有地址", notes = "需要在 authentication 请求头中携带登录令牌。")
    public Result update(@RequestBody AddressBook addressBook) {
        Long currentId = BaseContext.getCurrentId();
        if (currentId == null) {
            return Result.error("user not login");
        }
        if (addressBook == null || addressBook.getId() == null) {
            return Result.error("address id is required");
        }
        boolean updated = addressBookService.update(addressBook, currentId);
        if (!updated) {
            return Result.error("address not found or no permission");
        }
        return Result.success();
    }

    /**
     * 将某条地址设置为默认地址。
     * 默认地址会影响下单页的初始选中项，减少用户每次下单都重新选择的成本。
     *
     * @param addressBook 需要设为默认的地址，必须携带 id
     * @return 成功或失败结果
     */
    @PutMapping("/default")
    @ApiOperation(value = "将地址设置为默认地址", notes = "需要在 authentication 请求头中携带登录令牌。")
    public Result setDefault(@RequestBody AddressBook addressBook) {
        Long currentId = BaseContext.getCurrentId();
        if (currentId == null) {
            return Result.error("user not login");
        }
        if (addressBook == null || addressBook.getId() == null) {
            return Result.error("address id is required");
        }
        boolean updated = addressBookService.setDefault(addressBook, currentId);
        if (!updated) {
            return Result.error("address not found or no permission");
        }
        return Result.success();
    }

    /**
     * 删除一条地址。
     * 这个接口服务于地址管理页，删除后下单页就不会再展示这条地址。
     *
     * @param id 待删除的地址主键
     * @return 成功或失败结果
     */
    @DeleteMapping
    @ApiOperation(value = "删除一条地址", notes = "需要在 authentication 请求头中携带登录令牌。")
    public Result deleteById(Long id) {
        Long currentId = BaseContext.getCurrentId();
        if (currentId == null) {
            return Result.error("user not login");
        }
        boolean deleted = addressBookService.deleteById(id, currentId);
        if (!deleted) {
            return Result.error("address not found or no permission");
        }
        return Result.success();
    }

    /**
     * 查询当前用户的默认地址。
     * 下单页面通常先读取这个接口，把默认地址作为收货地址初始值。
     *
     * @return 默认地址；不存在时返回错误结果
     */
    @GetMapping("/default")
    @ApiOperation(value = "查询当前用户的默认地址", notes = "需要在 authentication 请求头中携带登录令牌。")
    public Result<AddressBook> getDefault() {
        Long currentId = BaseContext.getCurrentId();
        if (currentId == null) {
            return Result.error("user not login");
        }
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(currentId);
        addressBook.setIsDefault(1);
        List<AddressBook> list = addressBookService.list(addressBook);
        if (list != null && !list.isEmpty()) {
            return Result.success(list.get(0));
        }
        return Result.error("default address not found");
    }
}
