<template>
  <view>
    <uni-nav-bar
      @clickLeft="goBack"
      left-icon="back"
      leftIcon="arrowleft"
      title="个人中心"
      statusBar="true"
      fixed="true"
      color="#ffffff"
      backgroundColor="#1b9d60"
    ></uni-nav-bar>

    <view class="my-center">
      <view class="profile_card">
        <head
          :psersonUrl="psersonUrl"
          :nickName="nickName"
          :gender="gender"
          :phoneNumber="phoneNumber"
          :getPhoneNum="getPhoneNum"
        ></head>
      </view>

      <view class="container">
        <view class="entry_card">
          <order-info @goAddress="goAddress" @goOrder="goOrder"></order-info>
        </view>

        <view class="recent" v-if="recentOrdersList && recentOrdersList.length > 0">
          <text class="order_line">最近订单</text>
        </view>

        <view class="recent_list_card">
          <order-list
            :scrollH="scrollH"
            @lower="lower"
            @goDetail="goDetail"
            @oneOrderFun="oneOrderFun"
            @getOvertime="getOvertime"
            @statusWord="statusWord"
            @numes="numes"
            :loading="loading"
            :loadingText="loadingText"
            :recentOrdersList="recentOrdersList"
          ></order-list>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getOrderPage, repetitionOrder, delShoppingCart } from "../api/api.js";
import { mapMutations } from "vuex";
import { statusWord, getOvertime } from "@/utils/index.js";

import HeadInfo from "./components/headInfo.vue";
import OrderInfo from "./components/orderInfo.vue";
import OrderList from "./components/orderList.vue";

export default {
  data() {
    return {
      psersonUrl: "../../static/btn_waiter_sel.png",
      nickName: "",
      gender: "0",
      phoneNumber: "18500557668",
      recentOrdersList: [],
      sumOrder: {
        amount: 0,
        number: 0,
      },
      status: "",
      scrollH: 0,
      pageInfo: {
        page: 1,
        pageSize: 10,
        total: 0,
      },
      loadingText: "",
      loading: false,
    };
  },
  components: {
    HeadInfo,
    OrderInfo,
    OrderList,
  },
  filters: {
    getPhoneNum(str) {
      return str.replace(/\-/g, "");
    },
  },
  onLoad() {
    this.psersonUrl =
      this.$store.state.baseUserInfo && this.$store.state.baseUserInfo.avatarUrl;
    this.nickName =
      this.$store.state.baseUserInfo && this.$store.state.baseUserInfo.nickName;
    this.gender =
      this.$store.state.baseUserInfo && this.$store.state.baseUserInfo.gender;
    this.phoneNumber = this.$store.state.shopPhone && this.$store.state.shopPhone;
    this.getList();
  },
  created() {},
  onReady() {
    uni.getSystemInfo({
      success: (res) => {
        this.scrollH = res.windowHeight - uni.upx2px(100);
      },
    });
  },
  methods: {
    ...mapMutations(["setAddressBackUrl"]),
    statusWord(obj) {
      return statusWord(obj.status, obj.time);
    },
    getOvertime(time) {
      return getOvertime(time);
    },
    getList() {
      const params = {
        pageSize: 10,
        page: this.pageInfo.page,
      };
      getOrderPage(params).then((res) => {
        if (res.code === 1) {
          this.recentOrdersList = this.recentOrdersList.concat(res.data.records);
          this.pageInfo.total = res.data.total;
          this.loadingText = "";
          this.loading = false;
        }
      });
    },
    goAddress() {
      this.setAddressBackUrl("/pages/my/my");
      uni.redirectTo({
        url: "/pages/address/address?form=" + "my",
      });
    },
    goOrder() {
      uni.navigateTo({
        url: "/pages/historyOrder/historyOrder",
      });
    },
    async oneOrderFun(id) {
      let pages = getCurrentPages();
      let routeIndex = pages.findIndex((item) => item.route === "pages/index/index");
      await delShoppingCart();
      repetitionOrder(id).then((res) => {
        if (res.code === 1) {
          uni.navigateBack({
            delta: routeIndex > -1 ? pages.length - routeIndex : 1,
          });
        }
      });
    },
    quitClick() {},
    goDetail(id) {
      this.setAddressBackUrl("/pages/my/my");
      uni.redirectTo({
        url: "/pages/details/index?orderId=" + id,
      });
    },
    dataAdd() {
      const pages = Math.ceil(this.pageInfo.total / 10);
      if (this.pageInfo.page === pages) {
        this.loadingText = "没有更多了";
        this.loading = true;
      } else {
        this.pageInfo.page++;
        this.getList();
      }
    },

    lower() {
      this.loadingText = "数据加载中..";
      this.loading = true;
      this.dataAdd();
    },
    goBack() {
      uni.redirectTo({
        url: "/pages/index/index",
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.my-center {
  min-height: 100vh;
  background:
    linear-gradient(180deg, rgba(24, 178, 107, 0.16) 0%, rgba(24, 178, 107, 0.06) 220rpx, #f4f6f8 420rpx),
    #f4f6f8;
  padding-bottom: 32rpx;

  .profile_card {
    padding: 18rpx 24rpx 0;
  }

  .container {
    margin-top: 16rpx;
    padding: 0 24rpx;
    box-sizing: border-box;
  }

  .entry_card,
  .recent_list_card {
    background: #ffffff;
    border-radius: 24rpx;
    box-shadow: 0 12rpx 24rpx rgba(20, 32, 42, 0.08);
    overflow: hidden;
  }

  .entry_card {
    padding: 6rpx 0;
  }

  .recent {
    height: 64rpx;
    margin-top: 20rpx;
    display: flex;
    align-items: center;

    .order_line {
      font-size: 30rpx;
      font-weight: 600;
      color: #1f2a33;
      line-height: 40rpx;
      position: relative;
      padding-left: 18rpx;

      &::before {
        content: "";
        width: 8rpx;
        height: 30rpx;
        border-radius: 4rpx;
        background: linear-gradient(180deg, #19bc73 0%, #0f9f5e 100%);
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
      }
    }
  }

  .recent_list_card {
    min-height: 180rpx;
  }

  ::v-deep .my_info {
    width: 100%;
    height: auto;
    border-radius: 24rpx;
    background: linear-gradient(135deg, #1dbf76 0%, #16a565 100%);
    box-shadow: 0 14rpx 28rpx rgba(15, 114, 68, 0.24);
    padding: 20rpx 0;

    .head {
      width: 136rpx;
      height: 136rpx;
      margin-left: 22rpx;

      .head_image {
        width: 108rpx;
        height: 108rpx;
        margin: 14rpx auto;
        border: 3rpx solid rgba(255, 255, 255, 0.85);
        background-color: #ffffff;
      }
    }

    .phone_name {
      .name .name_text,
      .phone .phone_text {
        color: #ffffff;
      }

      .name .name_text {
        font-weight: 600;
      }
    }
  }

  ::v-deep .address_order {
    width: 100%;
    height: auto;
    margin: 0;

    .address,
    .order {
      height: 98rpx;
      line-height: 98rpx;
      margin: 0 22rpx;
      border-top: none;
      border-bottom: 1rpx solid #eef2f4;
      display: flex;
      align-items: center;

      .location {
        padding-left: 0;
        margin-right: 12rpx;
      }

      .address_word,
      .order_word {
        flex: 1;
        text-align: left;
        font-size: 28rpx;
        font-weight: 500;
        color: #22303a;
      }

      .to_right {
        right: 0;
        width: 28rpx;
        height: 28rpx;
      }
    }

    .order {
      border-bottom: none;
    }
  }

  ::v-deep .main.recent_orders {
    padding: 14rpx 14rpx 20rpx;
  }

  ::v-deep .order_lists {
    border-radius: 18rpx;
    background: #f8fbf9;
    margin: 0 0 14rpx;
    padding: 14rpx 14rpx 12rpx;
    box-shadow: none;

    .date_type {
      .time {
        color: #6d7b86;
      }

      .type {
        font-weight: 500;
        font-size: 23rpx;
        color: #6f7f8b;
      }
    }

    .numAndAum {
      text:first-child {
        font-size: 34rpx;
        color: #f1674f;
        font-weight: 650;
      }

      text:last-child {
        font-size: 22rpx;
        color: #8a96a1;
        font-weight: 400;
      }
    }

    .new_btn {
      border-radius: 30rpx;
      height: 58rpx;
      line-height: 58rpx;
      padding: 0 24rpx;
      background: #edf2f6;
      color: #2b3742;

      &::after {
        border: none;
      }

      &.btn {
        background: linear-gradient(90deg, #ffd86a 0%, #ffc53d 100%);
        color: #2c2c2c;
        font-weight: 600;
      }
    }
  }
}

::v-deep .uni-navbar--border {
  border-width: 0 !important;
}
</style>
