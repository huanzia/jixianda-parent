package com.jixianda.service.impl;

import com.jixianda.context.BaseContext;
import com.jixianda.dto.ShoppingCartDTO;
import com.jixianda.entity.Dish;
import com.jixianda.entity.Setmeal;
import com.jixianda.entity.ShoppingCart;
import com.jixianda.entity.WarehouseSku;
import com.jixianda.exception.ShoppingCartBusinessException;
import com.jixianda.mapper.DishMapper;
import com.jixianda.mapper.SetmealMapper;
import com.jixianda.mapper.ShoppingCartMapper;
import com.jixianda.mapper.WarehouseSkuMapper;
import com.jixianda.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
/**
 * 购物车业务实现类。
 * 这个类属于 service 实现层，负责把加购、减购、查询和清空购物车串成完整链路，
 * 并且始终按用户和仓库维度维护购物车数据，避免不同仓的商品被混到同一笔下单里。
 */
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    // 购物车持久化由 mapper 负责，service 主要处理聚合规则和库存校验。
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    // 加购时需要查询菜品基础信息，用来回填名称、图片和价格。
    private DishMapper dishMapper;
    @Autowired
    // 套餐商品也可以加入购物车，因此需要通过套餐表补全展示信息。
    private SetmealMapper setmealMapper;
    @Autowired
    // 仓商品库存和上架状态由仓维度 SKU 表决定，购物车加购前必须校验。
    private WarehouseSkuMapper warehouseSkuMapper;

    @Override
    /**
     * 加入购物车。
     * 这里不能简单 insert 一条记录，因为购物车需要按 userId、warehouseId、dishId / setmealId 和口味做聚合，
     * 同一个商品在同一仓同一规格下应该累加数量，而不是重复生成多条记录。
     */
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        // 1. 基础参数校验：购物车请求参数不能为空
        if (shoppingCartDTO == null) {
            throw new ShoppingCartBusinessException("shopping cart payload is required");
        }

        // 2. 格式化处理菜品口味（统一格式，避免因格式不一致导致重复加购）
        shoppingCartDTO.setDishFlavor(normalizeDishFlavor(shoppingCartDTO.getDishFlavor()));

        // 3. 校验仓库ID：必须指定加购商品所属仓库（核心规则：购物车绑定仓库）
        Long warehouseId = shoppingCartDTO.getWarehouseId();
        if (warehouseId == null) {
            throw new ShoppingCartBusinessException("warehouse id is required");
        }

        // 4. 获取当前登录用户ID（从上下文获取，避免手动传参）
        Long userId = BaseContext.getCurrentId();

        // 5. 校验购物车跨仓规则：用户购物车必须仅归属一个仓库
        // 5.1 查询用户当前购物车所有商品
        List<ShoppingCart> allCartItems = shoppingCartMapper.getListByUserId(userId);
        if (allCartItems != null && !allCartItems.isEmpty()) {
            // 5.2 取购物车中第一个商品的仓库ID（购物车已有商品时，必须和新商品同仓）
            Long cartWarehouseId = allCartItems.get(0).getWarehouseId();
            if (cartWarehouseId != null && !cartWarehouseId.equals(warehouseId)) {
                // 业务规则：跨仓库加购失败，需先清空购物车
                throw new ShoppingCartBusinessException("cross-warehouse add failed, clear cart first");
            }
        }

        // 6. 构建购物车实体对象：将DTO转换为数据库实体（PO）
        ShoppingCart shoppingCart = new ShoppingCart();
        // 6.1 拷贝DTO属性到PO（简化赋值，避免手动set每个字段）
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        // 6.2 补充用户ID、仓库ID（DTO中无，需业务层补充）
        shoppingCart.setUserId(userId);
        shoppingCart.setWarehouseId(warehouseId);

        // 7. 校验是否已加购该商品：同一仓、同一用户、同一商品（含口味）是否已存在
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list != null && !list.isEmpty()) {
            // 7.1 已存在该商品，执行数量累加逻辑
            ShoppingCart cart = list.get(0);
            int nextCount = cart.getNumber() + 1;

            // 7.2 库存校验：累加后数量不能超过仓库商品库存
            // 7.2.1 菜品库存校验
            if (cart.getDishId() != null) {
                WarehouseSku warehouseSku = warehouseSkuMapper.selectByWarehouseAndDishId(warehouseId, cart.getDishId());
                // 校验条件：商品不存在/下架/库存不足 → 加购失败
                if (warehouseSku == null
                        || warehouseSku.getStatus() == null
                        || warehouseSku.getStatus() == 0
                        || warehouseSku.getStock() == null
                        || warehouseSku.getStock() < nextCount) {
                    throw new ShoppingCartBusinessException("商品已售罄或下架");
                }
            }
            // 7.2.2 套餐库存校验（逻辑同菜品，仅查询维度不同）
            if (cart.getSetmealId() != null) {
                WarehouseSku warehouseSku = warehouseSkuMapper.selectByWarehouseAndDishId(warehouseId, cart.getSetmealId());
                if (warehouseSku == null
                        || warehouseSku.getStatus() == null
                        || warehouseSku.getStatus() == 0
                        || warehouseSku.getStock() == null
                        || warehouseSku.getStock() < nextCount) {
                    throw new ShoppingCartBusinessException("商品已售罄或下架");
                }
            }

            // 7.3 更新购物车商品数量（累加）
            cart.setNumber(nextCount);
            shoppingCartMapper.updateNumberById(cart);
            // 累加逻辑完成，直接返回（无需执行后续新增逻辑）
            return;
        }

        // 8. 首次加购该商品：初始化商品信息并新增
        // 8.1 菜品首次加购逻辑
        Long dishId = shoppingCartDTO.getDishId();
        if (dishId != null) {
            // 8.1.1 校验菜品库存（首次加购至少需1件库存）
            WarehouseSku warehouseSku = warehouseSkuMapper.selectByWarehouseAndDishId(warehouseId, dishId);
            if (warehouseSku == null
                    || warehouseSku.getStatus() == null
                    || warehouseSku.getStatus() == 0
                    || warehouseSku.getStock() == null
                    || warehouseSku.getStock() < 1) {
                throw new ShoppingCartBusinessException("商品已售罄或下架");
            }
            // 8.1.2 查询菜品基础信息（填充购物车展示字段：名称、图片、价格）
            Dish dish = dishMapper.getById(dishId);
            if (dish == null) {
                throw new ShoppingCartBusinessException("dish not found");
            }
            // 8.1.3 初始化购物车商品信息
            shoppingCart.setNumber(1); // 首次加购数量为1
            shoppingCart.setCreateTime(LocalDateTime.now()); // 创建时间
            shoppingCart.setImage(dish.getImage()); // 菜品图片
            shoppingCart.setName(dish.getName()); // 菜品名称
            shoppingCart.setAmount(dish.getPrice()); // 菜品单价
        }

        // 8.2 套餐首次加购逻辑（和菜品逻辑一致，仅关联表不同）
        Long setmealId = shoppingCartDTO.getSetmealId();
        if (setmealId != null) {
            // 8.2.1 校验套餐库存
            WarehouseSku warehouseSku = warehouseSkuMapper.selectByWarehouseAndDishId(warehouseId, setmealId);
            if (warehouseSku == null
                    || warehouseSku.getStatus() == null
                    || warehouseSku.getStatus() == 0
                    || warehouseSku.getStock() == null
                    || warehouseSku.getStock() < 1) {
                throw new ShoppingCartBusinessException("商品已售罄或下架");
            }
            // 8.2.2 查询套餐基础信息
            Setmeal setmeal = setmealMapper.getById(setmealId);
            if (setmeal == null) {
                throw new ShoppingCartBusinessException("setmeal not found");
            }
            // 8.2.3 初始化购物车商品信息
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setImage(setmeal.getImage());
            shoppingCart.setName(setmeal.getName());
            shoppingCart.setAmount(setmeal.getPrice());
        }

        // 9. 新增购物车商品（首次加购时执行）
        shoppingCartMapper.insert(shoppingCart);
    }

    /**
     * 查询当前用户购物车。
     * 这个接口服务于购物车页和结算页，需要直接返回当前用户在当前仓里的聚合结果。
     */
    @Override
    public List<ShoppingCart> list(Long userId) {
        userId = BaseContext.getCurrentId();
        return shoppingCartMapper.getListByUserId(userId);
    }

    /**
     * 从购物车减一件。
     * 这个动作对应前端减号按钮，数量减到 1 时直接删除记录，保持购物车数据简洁。
     */
    @Override
    public void subshoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //  格式化菜品口味：统一口味描述的格式（比如"微辣"和"微微辣"标准化）
        //    目的：避免因口味格式不一致，导致查询不到对应的购物车记录（比如加购时是"微辣"，减购时是"微微辣"
        shoppingCartDTO.setDishFlavor(normalizeDishFlavor(shoppingCartDTO.getDishFlavor()));
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        // 根据用户ID+菜品/套餐ID+口味，查询购物车中对应的商品记录
        //    list方法的SQL逻辑：where user_id=? and dish_id=? and setmeal_id=? and dish_flavor=?
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list != null && !list.isEmpty()) {
            // 取第一条记录（购物车中同一商品/口味/用户只会有一条记录，因为加购时做过去重）
            shoppingCart = list.get(0);
            int number = shoppingCart.getNumber();
            if (number == 1) {
                // 减到 0 时直接删除，避免保留无意义的空购物车行。
                shoppingCartMapper.deleteById(shoppingCart.getId());
            } else {
                shoppingCart.setNumber(number - 1);
                shoppingCartMapper.updateNumberById(shoppingCart);
            }
        }
    }

    @Override
    /**
     * 清空购物车。
     * 通常在下单成功后或用户主动清空购物车时使用，保证本次结算链路结束后数据回到干净状态。
     */
    public void cleanShoppingCart(Long userId) {
        userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteAllByUserId(userId);
    }

    /**
     * 规范化口味字段。
     * 购物车聚合要把口味作为条件之一，统一去掉空白值可以避免同规格商品被错误拆成多行。
     */
    private String normalizeDishFlavor(String dishFlavor) {
        if (dishFlavor == null) {
            return null;
        }
        String normalized = dishFlavor.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
