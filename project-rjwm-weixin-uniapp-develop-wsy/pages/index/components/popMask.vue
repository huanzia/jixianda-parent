<template>
  <view class="more_norm_pop">
    <view class="title">{{ moreNormDishdata.name }}</view>
    <scroll-view class="items_cont" scroll-y="true" scroll-top="0rpx">
      <view class="item_row" v-for="(obj, index) in moreNormdata" :key="index">
        <view class="flavor_name">{{ obj.name }}</view>
        <view class="flavor_item">
          <view
            :class="{
              item: true,
              act: flavorDataes.findIndex((it) => item === it) !== -1,
            }"
            v-for="(item, ind) in obj.value"
            :key="ind"
            @click="checkMoreNormPop(obj.value, item)"
          >
            {{ item }}
          </view>
        </view>
      </view>
    </scroll-view>
    <view class="but_item">
      <view class="price">
        <text class="ico">&#65509;</text>
        {{ moreNormDishdata.price }}
      </view>
      <view class="active">
        <view class="dish_card_add" @click="addShop(moreNormDishdata, '\u666e\u901a')">
          &#21152;&#20837;&#36141;&#29289;&#36710;
        </view>
      </view>
    </view>
    <view class="close" @click="closeMoreNorm(moreNormDishdata)">
      <image class="close_img" src="../../../static/but_close.png" mode=""></image>
    </view>
  </view>
</template>
<script>
export default {
  props: {
    moreNormDishdata: {
      type: Object,
      default: () => ({}),
    },
    moreNormdata: {
      type: Array,
      default: () => [],
    },
    flavorDataes: {
      type: Array,
      default: () => [],
    },
  },
  methods: {
    checkMoreNormPop(obj, item) {
      this.$emit("checkMoreNormPop", { obj: obj, item: item });
    },
    addShop(obj) {
      this.$emit("addShop", obj);
    },
    closeMoreNorm(obj) {
      this.$emit("closeMoreNorm", obj);
    },
  },
};
</script>
<style lang="scss" scoped>
.more_norm_pop {
  width: calc(100vw - 140rpx);
  box-sizing: border-box;
  position: relative;
  top: 50%;
  left: 50%;
  padding: 34rpx 34rpx 30rpx;
  transform: translateX(-50%) translateY(-50%);
  background: #fff;
  border-radius: 24rpx;
  box-shadow: 0 18rpx 36rpx rgba(21, 30, 38, 0.22);

  .title {
    font-size: 36rpx;
    line-height: 62rpx;
    text-align: center;
    font-weight: 600;
    color: #1f2a33;
  }

  .items_cont {
    max-height: 50vh;
    margin-top: 8rpx;

    .item_row {
      .flavor_name {
        height: 40rpx;
        font-size: 28rpx;
        font-weight: 400;
        text-align: left;
        color: #5d6974;
        line-height: 40rpx;
        padding-left: 10rpx;
        padding-top: 20rpx;
      }

      .flavor_item {
        display: flex;
        flex-wrap: wrap;

        .item {
          border: 1px solid #d6e2db;
          border-radius: 24rpx;
          margin: 14rpx 10rpx;
          padding: 0 26rpx;
          height: 56rpx;
          line-height: 56rpx;
          font-weight: 400;
          color: #333333;
          background: #f7faf8;
        }

        .act {
          background: #19bb72;
          border: 1px solid #19bb72;
          font-weight: 500;
          color: #ffffff;
        }
      }
    }
  }

  .but_item {
    display: flex;
    position: relative;
    flex: 1;
    margin: 28rpx 0 0;

    .price {
      text-align: left;
      color: #f45c43;
      line-height: 88rpx;
      box-sizing: border-box;
      font-size: 48rpx;
      font-family: DIN, DIN-Medium;
      font-weight: 500;

      .ico {
        font-size: 28rpx;
      }
    }

    .active {
      position: absolute;
      right: 0;
      bottom: 20rpx;
      display: flex;

      .dish_card_add {
        width: 208rpx;
        height: 62rpx;
        line-height: 62rpx;
        text-align: center;
        font-weight: 500;
        font-size: 28rpx;
        background: linear-gradient(90deg, #ffd54a 0%, #ffc200 100%);
        border-radius: 31rpx;
      }
    }
  }
}

.close {
  position: absolute;
  bottom: -180rpx;
  left: 50%;
  transform: translateX(-50%);

  .close_img {
    width: 88rpx;
    height: 88rpx;
  }
}
</style>
