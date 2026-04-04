<template>
  <view class="cart_pop" @click.stop="openOrderCartList = openOrderCartList">
    <view class="top_title">
      <view class="tit">&#36141;&#29289;&#36710;</view>
      <view class="clear" @click.stop="clearCardOrder()">
        <image class="clear_icon" src="../../../static/clear.png" mode=""></image>
        <text class="clear-des">&#28165;&#31354;</text>
      </view>
    </view>
    <scroll-view class="card_order_list" scroll-y="true" scroll-top="40rpx">
      <view class="type_item_cont" v-for="(item, ind) in orderAndUserInfo" :key="ind">
        <view class="type_item" v-for="(obj, index) in item.dishList" :key="index">
          <view class="dish_img">
            <image mode="aspectFill" :src="obj.image" class="dish_img_url"></image>
          </view>
          <view class="dish_info">
            <view class="dish_name">{{ obj.name }}</view>
            <view class="dish_dishFlavor" v-if="obj.dishFlavor">{{ obj.dishFlavor }}</view>
            <view class="dish_price">
              <text class="ico">&#65509;</text>
              {{ obj.amount }}
            </view>
            <view class="dish_active">
              <image
                v-if="obj.number && obj.number > 0"
                src="../../../static/btn_red.png"
                @click.stop="redDishAction(obj, '\u8d2d\u7269\u8f66')"
                class="dish_red"
                mode=""
              ></image>
              <text v-if="obj.number && obj.number > 0" class="dish_number">{{ obj.number }}</text>
              <image
                src="../../../static/btn_add.png"
                class="dish_add"
                @click.stop="addDishAction(obj, '\u8d2d\u7269\u8f66')"
                mode=""
              ></image>
            </view>
          </view>
        </view>
      </view>
      <view class="seize_seat"></view>
    </scroll-view>
  </view>
</template>
<script>
export default {
  props: {
    orderAndUserInfo: {
      type: Array,
      default: () => [],
    },
    openOrderCartList: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    clearCardOrder() {
      this.$emit("clearCardOrder");
    },
    addDishAction(obj, item) {
      this.$emit("addDishAction", obj, item);
    },
    redDishAction(obj, item) {
      this.$emit("redDishAction", obj, item);
    },
  },
};
</script>
<style lang="scss" scoped>
.cart_pop {
  width: 100%;
  position: absolute;
  bottom: 0;
  left: 0;
  height: 62vh;
  background-color: #fff;
  border-radius: 28rpx 28rpx 0 0;
  padding: 24rpx 30rpx 30rpx;
  box-sizing: border-box;
  box-shadow: 0 -18rpx 38rpx rgba(21, 30, 38, 0.18);

  .top_title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: solid 1px #edf1f4;
    padding-bottom: 24rpx;

    .tit {
      font-size: 34rpx;
      font-weight: 600;
      color: #20232a;
      letter-spacing: 0.4rpx;
    }

    .clear {
      color: #7d8792;
      font-size: 24rpx;
      font-weight: 400;
      display: flex;
      align-items: center;
      background: #f5f8f7;
      border-radius: 28rpx;
      padding: 0 18rpx;
      height: 52rpx;
      border: 1rpx solid #e5ece8;

      .clear_icon {
        width: 30rpx;
        height: 30rpx;
        margin-right: 6rpx;
      }

      .clear-des {
        height: 52rpx;
        line-height: 52rpx;
      }
    }
  }

  .card_order_list {
    background-color: #fff;
    padding-top: 26rpx;
    box-sizing: border-box;
    height: calc(100% - 0rpx);
    flex: 1;
    position: relative;

    .type_item {
      display: flex;
      margin-bottom: 24rpx;
      padding: 12rpx 0;

      .dish_img {
        width: 116rpx;
        margin-right: 20rpx;

        .dish_img_url {
          display: block;
          width: 116rpx;
          height: 116rpx;
          border-radius: 14rpx;
          background: #eff3f6;
        }
      }

      .dish_info {
        position: relative;
        flex: 1;
        min-height: 120rpx;
        padding-bottom: 14rpx;
        border-bottom: solid 1px #f0f2f5;

        .dish_name {
          font-size: 30rpx;
          line-height: 42rpx;
          color: #28313a;
          font-weight: 600;
          max-width: calc(100vw - 360rpx);
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .dish_dishFlavor {
          margin-top: 6rpx;
          color: #8b96a1;
          font-size: 22rpx;
          line-height: 30rpx;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .dish_price {
          font-size: 30rpx;
          color: #f45c43;
          position: absolute;
          bottom: 14rpx;
          font-weight: 600;

          .ico {
            font-size: 24rpx;
          }
        }

        .dish_active {
          position: absolute;
          right: 6rpx;
          bottom: 6rpx;
          display: flex;
          align-items: center;
          background: #f6f9f7;
          border-radius: 34rpx;
          padding: 2rpx 4rpx;

          .dish_add,
          .dish_red {
            display: block;
            width: 56rpx;
            height: 56rpx;
            border-radius: 28rpx;
          }

          .dish_number {
            min-width: 36rpx;
            padding: 0 10rpx;
            line-height: 56rpx;
            font-size: 28rpx;
            text-align: center;
            font-weight: 500;
            color: #25303a;
          }
        }
      }
    }

    &::before {
      content: "";
      position: absolute;
      width: 100vw;
      height: 120rpx;
      z-index: 99;
      background: linear-gradient(0deg, rgba(255, 255, 255, 1) 10%, rgba(255, 255, 255, 0));
      bottom: 0;
      left: 0;
      pointer-events: none;
    }

    .seize_seat {
      width: 100%;
      height: 140rpx;
    }
  }
}
</style>
