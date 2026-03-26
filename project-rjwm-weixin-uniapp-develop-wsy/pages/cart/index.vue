<template>
  <view class="cart_page">
    <view class="cart_header">
      <view class="back_btn" @click="goBack"></view>
      <view class="title">&#36141;&#29289;&#36710;</view>
      <view class="clear_btn" @click="clearCart">&#28165;&#31354;</view>
    </view>

    <scroll-view class="cart_list" scroll-y="true" v-if="cartList.length > 0">
      <view class="cart_item" v-for="(item, index) in cartList" :key="item.id || index">
        <image class="item_img" :src="item.image" mode="aspectFill"></image>
        <view class="item_main">
          <view class="item_name">{{ item.name }}</view>
          <view class="item_spec" v-if="item.dishFlavor">{{ item.dishFlavor }}</view>
          <view class="item_bottom">
            <view class="item_price"><text class="ico">&#65509;</text>{{ item.amount }}</view>
            <view class="item_stepper">
              <image class="step_btn" src="../../static/btn_red.png" mode="" @click="subItem(item)"></image>
              <text class="step_num">{{ item.number || 0 }}</text>
              <image class="step_btn" src="../../static/btn_add.png" mode="" @click="addItem(item)"></image>
            </view>
          </view>
        </view>
      </view>
      <view class="seat"></view>
    </scroll-view>

    <view class="empty" v-else>
      <view class="empty_text">&#36141;&#29289;&#36710;&#26159;&#31354;&#30340;</view>
    </view>

    <view class="bottom_bar">
      <view class="summary">
        <view class="count">{{ totalCount }}&#20214;</view>
        <view class="amount"><text class="ico">&#65509;</text>{{ totalAmount.toFixed(2) }}</view>
      </view>
      <view class="checkout_btn" @click="goOrder">&#21435;&#32467;&#31639;</view>
    </view>
  </view>
</template>

<script>
import { mapMutations } from "vuex";
import {
  getShoppingCartList,
  newAddShoppingCartAdd,
  newShoppingCartSub,
  delShoppingCart,
} from "../api/api.js";

export default {
  data() {
    return {
      cartList: [],
      totalCount: 0,
      totalAmount: 0,
    };
  },
  onShow() {
    this.loadCart();
  },
  methods: {
    ...mapMutations(["initdishListMut"]),
    goBack() {
      uni.navigateBack();
    },
    computeSummary() {
      let count = 0;
      let amount = 0;
      this.cartList.forEach((item) => {
        const number = item.number || 0;
        const price = item.amount || 0;
        count += number;
        amount += number * price;
      });
      this.totalCount = count;
      this.totalAmount = amount;
    },
    normalizePayload(item) {
      const payload = {
        dishFlavor: item.dishFlavor || null,
      };
      if (item.dishId) {
        payload.dishId = item.dishId;
      } else if (item.setmealId) {
        payload.setmealId = item.setmealId;
        delete payload.dishFlavor;
      }
      return payload;
    },
    loadCart() {
      getShoppingCartList()
        .then((res) => {
          if (res && res.code === 1) {
            this.cartList = Array.isArray(res.data) ? res.data : [];
            this.initdishListMut(this.cartList);
            this.computeSummary();
          }
        })
        .catch(() => {});
    },
    addItem(item) {
      const payload = this.normalizePayload(item);
      newAddShoppingCartAdd(payload)
        .then((res) => {
          if (res && res.code === 1) {
            this.loadCart();
          }
        })
        .catch(() => {});
    },
    subItem(item) {
      const payload = this.normalizePayload(item);
      newShoppingCartSub(payload)
        .then((res) => {
          if (res && res.code === 1) {
            this.loadCart();
          }
        })
        .catch(() => {});
    },
    clearCart() {
      delShoppingCart()
        .then((res) => {
          if (res && res.code === 1) {
            this.cartList = [];
            this.totalCount = 0;
            this.totalAmount = 0;
            this.initdishListMut([]);
          }
        })
        .catch(() => {});
    },
    goOrder() {
      if (!this.cartList.length) {
        uni.showToast({
          title: "\u8d2d\u7269\u8f66\u4e3a\u7a7a",
          icon: "none",
        });
        return;
      }
      uni.navigateTo({
        url: "/pages/order/index",
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.cart_page {
  min-height: 100vh;
  padding: 0 24rpx 168rpx;
  box-sizing: border-box;
  background:
    linear-gradient(180deg, rgba(24, 178, 107, 0.14) 0%, rgba(246, 247, 251, 0.94) 24%, #f6f7fb 100%),
    #f6f7fb;
}

.cart_header {
  padding-top: calc(var(--status-bar-height) + 20rpx);
  height: 118rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  .back_btn {
    width: 64rpx;
    height: 64rpx;
    border-radius: 32rpx;
    background: #ffffff;
    box-shadow: 0 6rpx 14rpx rgba(26, 33, 44, 0.1);
    position: relative;
    &::before {
      content: "";
      position: absolute;
      left: 26rpx;
      top: 20rpx;
      width: 16rpx;
      height: 16rpx;
      border-left: 3rpx solid #4a5560;
      border-bottom: 3rpx solid #4a5560;
      transform: rotate(45deg);
    }
  }
  .title {
    font-size: 36rpx;
    font-weight: 600;
    color: #1f2a33;
  }
  .clear_btn {
    min-width: 96rpx;
    height: 52rpx;
    line-height: 52rpx;
    text-align: center;
    border-radius: 26rpx;
    border: 1rpx solid #dbe3e9;
    color: #66717d;
    font-size: 24rpx;
    background: #ffffff;
  }
}

.cart_list {
  height: calc(100vh - 330rpx);
}

.cart_item {
  display: flex;
  background: #ffffff;
  border-radius: 20rpx;
  padding: 18rpx;
  margin-bottom: 16rpx;
  box-shadow: 0 8rpx 20rpx rgba(26, 33, 44, 0.08);
}

.item_img {
  width: 124rpx;
  height: 124rpx;
  border-radius: 14rpx;
  margin-right: 18rpx;
  background: #eef2f4;
}

.item_main {
  flex: 1;
  min-width: 0;
}

.item_name {
  font-size: 30rpx;
  line-height: 40rpx;
  color: #1f2a33;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item_spec {
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 30rpx;
  color: #8a95a1;
}

.item_bottom {
  margin-top: 18rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.item_price {
  font-size: 32rpx;
  color: #f45c43;
  font-weight: 600;
  .ico {
    font-size: 22rpx;
  }
}

.item_stepper {
  display: flex;
  align-items: center;
  background: #f6f9f7;
  border-radius: 34rpx;
  padding: 2rpx 4rpx;
}

.step_btn {
  width: 52rpx;
  height: 52rpx;
  border-radius: 26rpx;
}

.step_num {
  min-width: 34rpx;
  text-align: center;
  font-size: 26rpx;
  color: #25303a;
  font-weight: 500;
  padding: 0 8rpx;
}

.seat {
  height: 120rpx;
}

.empty {
  height: calc(100vh - 330rpx);
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty_text {
  font-size: 28rpx;
  color: #8b96a1;
}

.bottom_bar {
  position: fixed;
  left: 24rpx;
  right: 24rpx;
  bottom: 30rpx;
  height: 98rpx;
  border-radius: 50rpx;
  background: rgba(16, 25, 33, 0.96);
  box-shadow: 0 14rpx 30rpx rgba(0, 0, 0, 0.24);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 12rpx 0 28rpx;
  box-sizing: border-box;
}

.summary {
  display: flex;
  align-items: center;
  color: #ffffff;
}

.count {
  font-size: 26rpx;
  margin-right: 18rpx;
}

.amount {
  font-size: 36rpx;
  font-weight: 600;
  .ico {
    font-size: 24rpx;
  }
}

.checkout_btn {
  width: 214rpx;
  height: 78rpx;
  line-height: 78rpx;
  text-align: center;
  border-radius: 39rpx;
  background: linear-gradient(90deg, #ffd54a 0%, #ffc200 100%);
  color: #2b2f33;
  font-size: 30rpx;
  font-weight: 600;
}
</style>
