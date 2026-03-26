<template>
  <view>
    <navBar></navBar>
    <view class="home_content" :style="{ paddingTop: ht + 'px' }" @touchmove.stop.prevent="disabledScroll">
      <view class="home_top_actions">
        <view class="profile_entry" @click="openMyPage">
          <image class="profile_icon" src="../../static/center.png"></image>
          <text class="profile_text">个人中心</text>
        </view>
      </view>
      <view class="top_brand_card">
        <view class="brand_card_inner">
          <view class="brand_header">
            <view class="brand_logo_wrap">
              <image class="logo_ruiji" src="../../static/img2.jpg"></image>
            </view>
            <view class="brand_header_main">
              <view class="brand_title_row">
                <text class="brand_title_text">{{ shopInfo().shopName || '' }}</text>
                <view class="brand_status_wrap">
                  <view class="businessStatus" v-if="shopStatus === 1">&#33829;&#19994;&#20013;</view>
                  <view class="businessStatus close" v-else>&#20241;&#24687;&#20013;</view>
                </view>
              </view>
              <view class="brand_meta_row">
                <view class="details_flex brand_fee_row">
                  <image class="top_icon" src="../../static/money.png"></image>
                  <text class="icon_text">&#37197;&#36865;&#36153;{{ deliveryFee() }}&#20803;</text>
                </view>
              </view>
            </view>
          </view>
          <view class="brand_body">
            <view class="brand_info_group">
              <view class="brand_intro">&#26497;&#40092;&#36798;&#65306;&#26368;&#26032;&#40092;&#30340;&#39135;&#26448;&#37117;&#21487;&#20197;&#22312;&#36825;&#37324;&#20080;&#21040;</view>
              <view class="brand_addr_row">
                <icon></icon>
                {{ shopInfo().shopAddress || "&#26368;&#36817;&#30340;&#20179;&#24211;&#20301;&#32622;&#33719;&#21462;&#20013;.." }}
              </view>
            </view>
            <view class="brand_phone_wrap">
              <view class="phone" @click="handlePhone('bottom')">
                <icon class="phoneIcon"></icon>
              </view>
            </view>
          </view>
        </view>
      </view>

      <view class="page_main" v-if="shopStatus === 1">
        <view class="category_nav">
          <scroll-view
            scroll-x
            scroll-with-animation
            class="category_scroll menu-scroll-view"
            :scroll-top="scrollTop + 100"
            :scroll-into-view="itemId"
          >
            <view
              class="category_chip"
              id="target"
              :class="[typeIndex == index ? 'active' : '']"
              v-for="(item, index) in typeListData"
              :key="index"
              @tap.stop="swichMenu(item, index)"
            >
              <view class="chip_text" :class="item.name.length > 5 ? 'allLine' : ''">{{ item.name }}</view>
            </view>
          </scroll-view>
        </view>

        <scroll-view class="goods_scroll_area" scroll-y="true" scroll-top="0rpx" v-if="dishListItems && dishListItems.length > 0">
          <view class="goods_list">
            <view class="goods_card" v-for="(item, index) in dishListItems" :key="index">
              <view class="dish_img" @click="openDetailHandle(item)">
                <image mode="aspectFill" :src="item.image" class="dish_img_url"></image>
              </view>
              <view class="dish_info">
                <view class="dish_name" @click="openDetailHandle(item)">{{ item.name }}</view>
                <view class="dish_label" @click="openDetailHandle(item)">{{ item.description || item.name }}</view>
                <view class="dish_label dish_sales" @click="openDetailHandle(item)">&#26376;&#38144;&#37327;0</view>
                <view class="goods_action_row">
                  <view class="dish_price">
                    <text class="ico">&#65509;</text>
                    {{ item.price.toFixed(2) }}
                  </view>
                  <view class="dish_active" v-if="!item.flavors || item.flavors.length === 0">
                    <image
                      v-if="item.dishNumber >= 1"
                      src="../../static/btn_red.png"
                      @click="redDishAction(item, '\u666e\u901a')"
                      class="dish_red"
                    ></image>
                    <text v-if="item.dishNumber > 0" class="dish_number">{{ item.dishNumber }}</text>
                    <image src="../../static/btn_add.png" class="dish_add" @click="addDishAction(item, '\u666e\u901a')"></image>
                  </view>
                  <view class="dish_active_btn" v-else>
                    <view class="check_but" @click="moreNormDataesHandle(item)">&#36873;&#25321;&#35268;&#26684;</view>
                  </view>
                </view>
              </view>
            </view>
          </view>
          <view class="goods_seat"></view>
        </scroll-view>

        <view class="no_dish" v-else>
          <view v-if="typeListData.length > 0">&#35813;&#20998;&#31867;&#19979;&#26242;&#26080;&#33756;&#21697;</view>
        </view>
      </view>

      <view class="restaurant_close" v-else>&#24215;&#38138;&#24050;&#25171;&#28874;</view>

      <view class="bottom_checkout_bar footer_order_buttom" v-if="orderListData().length === 0 || shopStatus !== 1">
        <view class="order_number order_badge_wrap">
          <view class="order_badge_icon"></view>
        </view>
        <view class="order_price">
          <text class="ico">&#65509;</text>
          0
        </view>
        <view class="order_but">&#21435;&#32467;&#31639;</view>
      </view>

      <view class="bottom_checkout_bar footer_order_buttom order_form" v-else>
        <view class="orderCar" @click="openCartPage">
          <view class="order_number order_badge_wrap">
            <view class="order_badge_icon active"></view>
            <view class="order_dish_num">{{ orderDishNumber }}</view>
          </view>
          <view class="order_price">
            <text class="ico">&#65509;</text>
            {{ orderDishPrice.toFixed(2) }}
          </view>
        </view>
        <view class="order_but" @click="goOrder()">&#21435;&#32467;&#31639;</view>
      </view>

      <view class="pop_mask" v-show="openMoreNormPop">
        <popMask
          :moreNormDishdata="moreNormDishdata"
          :moreNormdata="moreNormdata"
          :flavorDataes="flavorDataes"
          @checkMoreNormPop="checkMoreNormPop"
          @addShop="addShop"
          @closeMoreNorm="closeMoreNorm"
        ></popMask>
      </view>

      <view class="pop_mask" v-show="openDetailPop" style="z-index: 9999">
        <dishDetail
          :dishDetailes="dishDetailes"
          :openDetailPop="openDetailPop"
          :dishMealData="dishMealData"
          @redDishAction="redDishAction"
          @addDishAction="addDishAction"
          @moreNormDataesHandle="moreNormDataesHandle"
          @dishClose="dishClose"
        ></dishDetail>
      </view>

      <view class="pop_mask" v-show="loaddingSt">
        <view class="lodding">
          <image class="lodding_ico" src="../../static/lodding.gif" mode=""></image>
        </view>
      </view>

      <phone ref="phone" :phoneData="phoneData" @closePopup="closePopup"></phone>

      <view class="colseShop" v-if="shopStatus === 0">
        <view class="shop">&#26412;&#24215;&#24050;&#25171;&#28874;</view>
      </view>
    </view>
  </view>
</template>
<script>
import page from "./index.js";

page.methods = page.methods || {};
page.methods.openCartPage = function () {
  uni.navigateTo({
    url: "/pages/cart/index",
  });
};
page.methods.openMyPage = function () {
  uni.navigateTo({
    url: "/pages/my/my",
  });
};

export default page;
</script>
<style src="./style.scss" lang="scss" scoped></style>
<style scoped>
/* #ifdef MP-WEIXIN || APP-PLUS */
::v-deep ::-webkit-scrollbar {
  display: none !important;
  width: 0 !important;
  height: 0 !important;
  -webkit-appearance: none;
  background: transparent;
  color: transparent;
}

/* #endif */
</style>
