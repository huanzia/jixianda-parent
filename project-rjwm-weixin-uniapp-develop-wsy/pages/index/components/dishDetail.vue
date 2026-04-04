<template>
  <view class="dish_detail_pop" v-if="dishDetailes.type == 1">
    <image mode="aspectFill" class="div_big_image" :src="dishDetailes.image"></image>
    <view class="title">{{ dishDetailes.name }}</view>
    <view class="desc">{{ dishDetailes.description }}</view>
    <view class="but_item">
      <view class="price">
        <text class="ico">&#65509;</text>
        {{ dishDetailes.price.toFixed(2) }}
      </view>
      <view class="active" v-if="(!dishDetailes.flavors || dishDetailes.flavors.length === 0) && dishDetailes.dishNumber > 0">
        <image
          src="../../../static/btn_red.png"
          @click="redDishAction(dishDetailes, '\u666e\u901a')"
          class="dish_red"
          mode=""
        ></image>
        <text class="dish_number">{{ dishDetailes.dishNumber }}</text>
        <image
          src="../../../static/btn_add.png"
          class="dish_add"
          @click="addDishAction(dishDetailes, '\u666e\u901a')"
          mode=""
        ></image>
      </view>

      <view class="active" v-if="dishDetailes.flavors && dishDetailes.flavors.length > 0">
        <view class="dish_card_add" @click="moreNormDataesHandle(dishDetailes)">
          &#36873;&#25321;&#35268;&#26684;
        </view>
      </view>
      <view class="active" v-if="dishDetailes.dishNumber === 0 && (!dishDetailes.flavors || dishDetailes.flavors.length === 0)">
        <view class="dish_card_add" @click="addDishAction(dishDetailes, '\u666e\u901a')">
          &#21152;&#20837;&#36141;&#29289;&#36710;
        </view>
      </view>
    </view>
    <view class="close" @click="dishClose">
      <image class="close_img" src="../../../static/but_close.png" mode=""></image>
    </view>
  </view>

  <view class="dish_detail_pop" v-else>
    <scroll-view class="dish_items" scroll-y="true" scroll-top="0rpx">
      <view class="dish_item" v-for="(item, index) in dishMealData" :key="index">
        <image class="div_big_image" :src="item.image" mode=""></image>
        <view class="title">
          {{ item.name }}
          <text>X{{ item.copies }}</text>
        </view>
        <view class="desc">{{ item.description }}</view>
      </view>
    </scroll-view>
    <view class="but_item">
      <view class="price">
        <text class="ico">&#65509;</text>
        {{ dishDetailes.price }}
      </view>
      <view class="active" v-if="dishDetailes.dishNumber && dishDetailes.dishNumber > 0">
        <image
          src="../../../static/btn_red.png"
          @click="redDishAction(dishDetailes, '\u666e\u901a')"
          class="dish_red"
          mode=""
        ></image>
        <text class="dish_number">{{ dishDetailes.dishNumber }}</text>
        <image
          src="../../../static/btn_add.png"
          class="dish_add"
          @click="addDishAction(dishDetailes, '\u666e\u901a')"
          mode=""
        ></image>
      </view>
      <view class="active" v-else-if="dishDetailes.dishNumber == 0">
        <view class="dish_card_add" @click="addDishAction(dishDetailes, '\u666e\u901a')">
          &#21152;&#20837;&#36141;&#29289;&#36710;
        </view>
      </view>
    </view>
    <view class="close" @click="dishClose">
      <image class="close_img" src="../../../static/but_close.png" mode=""></image>
    </view>
  </view>
</template>
<script>
export default {
  props: {
    dishDetailes: {
      type: Object,
      default: () => ({}),
    },
    openDetailPop: {
      type: Boolean,
      default: false,
    },
    dishMealData: {
      type: Array,
      default: () => [],
    },
  },
  methods: {
    addDishAction(obj, item) {
      this.$emit("addDishAction", { obj: obj, item: item });
    },
    redDishAction(obj, item) {
      this.$emit("redDishAction", { obj: obj, item: item });
    },
    moreNormDataesHandle(obj) {
      this.$emit("moreNormDataesHandle", obj);
    },
    dishClose() {
      this.$emit("dishClose");
    },
  },
};
</script>
<style lang="scss" scoped>
.dish_detail_pop {
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

  .div_big_image {
    width: 100%;
    height: 320rpx;
    border-radius: 16rpx;
    background: #eef2f4;
  }

  .title {
    font-size: 36rpx;
    line-height: 62rpx;
    text-align: center;
    font-weight: 600;
    color: #1f2a33;
    margin-top: 8rpx;
  }

  .desc {
    font-size: 24rpx;
    color: #86909b;
    line-height: 34rpx;
    margin-top: 4rpx;
    text-align: center;
  }

  .dish_items {
    height: 56vh;
  }

  .dish_item {
    margin-bottom: 20rpx;
  }

  .but_item {
    display: flex;
    position: relative;
    flex: 1;
    margin-top: 12rpx;

    .price {
      text-align: left;
      color: #f45c43;
      line-height: 88rpx;
      box-sizing: border-box;
      font-size: 48rpx;
      font-weight: 600;

      .ico {
        font-size: 28rpx;
      }
    }

    .active {
      position: absolute;
      right: 0;
      bottom: 14rpx;
      display: flex;
      align-items: center;

      .dish_add,
      .dish_red {
        display: block;
        width: 66rpx;
        height: 66rpx;
        border-radius: 33rpx;
      }

      .dish_number {
        padding: 0 10rpx;
        line-height: 66rpx;
        font-size: 30rpx;
        font-weight: 500;
      }

      .dish_card_add {
        width: 208rpx;
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
