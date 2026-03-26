<template>
  <view>
    <uni-nav-bar
      @clickLeft="goBack"
      left-icon="back"
      leftIcon="arrowleft"
      title="提交订单"
      statusBar="true"
      fixed="true"
      color="#ffffff"
      backgroundColor="#333333"
    ></uni-nav-bar>

    <view class="order_content checkout_page" @touchstart="touchstart">
      <view class="order_content_box">
        <view class="checkout_section address_section">
          <address-pop
            :address="address"
            :tagLabel="tagLabel"
            :addressLabel="addressLabel"
            :nickName="nickName"
            :phoneNumber="phoneNumber"
            :arrivalTime="arrivalTime"
            :popleft="popleft"
            :weeks="weeks"
            :newDateData="newDateData"
            :tabIndex="tabIndex"
            :selectValue="selectValue"
            @change="change"
            @goAddress="goAddress"
            @dateChange="dateChange"
            @timeClick="timeClick"
          ></address-pop>
        </view>

        <view class="checkout_section list_section order_list_cont">
          <dish-detail
            :orderDataes="orderDataes"
            :showDisplay="showDisplay"
            :orderDishNumber="orderDishNumber"
            :orderListDataes="orderListDataes"
            :orderDishPrice="orderDishPrice"
          ></dish-detail>

          <view class="boxPad info_section">
            <dish-info
              ref="dishinfo"
              :remark="remark"
              :tablewareData="tablewareData"
              :radioGroup="radioGroup"
              :activeRadio="activeRadio"
              :baseData="baseData"
              @goRemark="goRemark"
              @openPopuos="openPopuos"
              @change="change"
              @closePopup="closePopup"
              @handlePiker="handlePiker"
              @changeCont="changeCont"
              @handleRadio="handleRadio"
            ></dish-info>
          </view>
        </view>
      </view>

      <view class="footer_order_buttom order_form checkout_footer">
        <view class="order_number checkout_badge">
          <view class="checkout_badge_icon"></view>
          <view class="order_dish_num">{{ orderDishNumber }}</view>
        </view>
        <view class="order_price">
          <text class="order_price_label">合计</text>
          <text class="ico">￥</text> {{ orderDishPrice.toFixed(2) }}
        </view>
        <view class="order_but">
          <view v-if="isHandlePy" class="order_but_rit">去支付</view>
          <view v-else class="order_but_rit" @click="payOrderHandle()">去支付</view>
        </view>
      </view>
    </view>
  </view>
</template>
<script src="./index.js"></script>
<style src="./../common/Navbar/navbar.scss" lang="scss" scoped></style>
<style src="./style.scss" lang="scss" scoped></style>
