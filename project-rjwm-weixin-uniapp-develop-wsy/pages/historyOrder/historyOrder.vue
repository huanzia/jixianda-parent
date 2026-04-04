<template>
  <view class="history_order">
    <uni-nav-bar
      @clickLeft="goBack"
      left-icon="back"
      leftIcon="arrowleft"
      title="历史订单"
      statusBar="true"
      fixed="true"
      color="#ffffff"
      backgroundColor="#333333"
    ></uni-nav-bar>

    <view class="history_shell">
      <scroll-view
        scroll-x
        class="order_tabs"
        :scroll-into-view="scrollinto"
        :scroll-with-animation="true"
        enable-flex
      >
        <view
          v-for="(item, index) in tabBars"
          :key="index"
          :id="'tab' + index"
          class="tab_item"
          @click="changeTab(index)"
        >
          <view :class="tabIndex == index ? 'tab_item_act' : ''">
            <text class="tab_text">{{ item }}</text>
            <text class="tab_line"></text>
          </view>
        </view>
      </scroll-view>

      <swiper :current="tabIndex" @change="onChangeSwiperTab" :style="{ height: scrollH + 'px' }">
        <swiper-item v-for="(item, index) in tabBars" :key="index">
          <scroll-view scroll-y="true" :style="{ height: scrollH + 'px' }" @scrolltolower="lower">
            <view class="main recent_orders" v-if="recentOrdersList && recentOrdersList.length > 0">
              <view
                class="order_card"
                v-for="(item, index) in recentOrdersList"
                :key="index"
                :class="{ 'item-last': Number(index) + 1 === recentOrdersList.length }"
              >
                <view class="card_head">
                  <text class="order_time">{{ item.orderTime }}</text>
                  <text class="order_status" :class="'status_' + item.status">{{ statusWord(item.status) }}</text>
                </view>

                <view class="card_body" @click="goDetail(item.id)">
                  <scroll-view scroll-x="true" class="goods_preview" style="width: 100%; overflow: hidden; white-space: nowrap">
                    <view class="goods_item" v-for="(num, y) in item.orderDetailList" :key="y">
                      <view class="goods_img">
                        <image :src="num.image" mode="aspectFill"></image>
                      </view>
                      <view class="goods_name">{{ num.name }}</view>
                    </view>
                  </scroll-view>

                  <view class="summary_row">
                    <view class="summary_count">共{{ numes(item.orderDetailList).count }}件</view>
                    <view class="summary_amount">¥{{ item.amount.toFixed(2) }}</view>
                  </view>
                </view>

                <view class="card_actions">
                  <button class="action_btn action_light" type="default" @click="oneMoreOrder(item.id)">再来一单</button>
                  <button
                    class="action_btn action_primary"
                    type="default"
                    @click="goDetail(item.id)"
                    v-if="item.status === 1 && getOvertime(item.orderTime) > 0"
                  >
                    去支付
                  </button>
                  <button
                    class="action_btn action_primary"
                    type="default"
                    @click="handleReminder('center', item.id)"
                    v-if="item.status === 2"
                  >
                    催单
                  </button>
                </view>
              </view>
            </view>
          </scroll-view>
        </swiper-item>
      </swiper>
    </view>

    <uni-popup ref="commonPopup" class="comPopupBox">
      <view class="popup-content">
        <view class="text">{{ textTip }}</view>
        <view class="btn" v-if="showConfirm">
          <view @click="closePopup">确认</view>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import {
  getOrderPage,
  repetitionOrder,
  reminderOrder,
  delShoppingCart
} from "../api/api.js";
import { mapMutations } from "vuex";
import Empty from "@/components/empty/empty";
import { statusWord, getOvertime } from "@/utils/index.js";

export default {
  components: {
    Empty,
  },
  data() {
    return {
      recentOrdersList: [],
      pageInfo: {
        page: 1,
        pageSize: 10,
        total: 0,
      },
      status: "",
      payStatus: "",
      loadingType: 0,
      showTitle: false,
      scrollinto: "tab0",
      scrollH: 0,
      tabIndex: 0,
      tabBars: ["全部订单", "待付款"],
      urlMap: {
        0: {
          fn: getOrderPage,
          key: "status",
        },
        1: {
          fn: getOrderPage,
          key: "status",
        },
      },
      textTip: "",
      showConfirm: false,
      isEmpty: false,
    };
  },
  onLoad() {
    this.getList();
  },
  onUnload() {
    this.showTitle = false;
  },
  onReady() {
    uni.getSystemInfo({
      success: (res) => {
        this.scrollH = res.windowHeight - uni.upx2px(100);
      },
    });
  },
  onPullDownRefresh() {
    this.pageInfo.page = 1;
    this.loadingType = 0;
    this.recentOrdersList = [];
    this.finished = false;
    this.getList();
    uni.stopPullDownRefresh();
    this.showTitle = true;
  },
  onReachBottom() {
    if (this.recentOrdersList.length < Number(this.pageInfo.total)) {
      this.pageInfo.page++;
      this.loadingStatus = "loading";
      this.getList(this.status);
      this.showTitle = true;
    }
  },
  methods: {
    ...mapMutations(["setAddressBackUrl"]),
    numes(list) {
      let count = 0;
      let total = 0;
      list.length > 0 &&
        list.forEach((obj) => {
          count += Number(obj.number);
          total += Number(obj.number) * Number(obj.amount);
        });
      return { count: count, total: total };
    },
    statusWord(status) {
      return statusWord(status);
    },
    getOvertime(time) {
      return getOvertime(time);
    },
    getList() {
      const key = this.urlMap[this.tabIndex].key;
      const fn = this.urlMap[this.tabIndex].fn;
      const params = {
        pageSize: 10,
        page: this.pageInfo.page,
      };
      params[key] = this[key];
      uni.showLoading({ title: "加载中", mask: true });
      fn(params).then((res) => {
        if (res.code === 1) {
          setTimeout(function () {
            uni.hideLoading();
          }, 100);
          this.recentOrdersList = this.recentOrdersList.concat(res.data.records);
          this.pageInfo.total = res.data.total;
          this.isEmpty = true;
        }
      });
    },
    async oneMoreOrder(id) {
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
    changeTab(index) {
      if (this.tabIndex == index) {
        return;
      }
      this.tabIndex = index;
      if (index === 1) {
        this.status = 1;
        this.payStatus = 0;
      } else {
        this.status = "";
        this.payStatus = "";
      }
      this.pageInfo.page = 1;
      this.recentOrdersList = [];
      this.getList();
      this.scrollinto = "tab" + index;
    },
    onChangeSwiperTab(e) {
      this.changeTab(e.detail.current);
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
    goDetail(id) {
      this.setAddressBackUrl("/pages/historyOrder/historyOrder");
      uni.navigateTo({ url: "/pages/details/index?orderId=" + id });
    },
    handleReminder(type, id) {
      reminderOrder(id).then((res) => {
        if (res.code === 1) {
          this.showConfirm = true;
          this.textTip = "您的催单信息已发出！";
          this.$refs.commonPopup.open(type);
          this.getList(this.status);
        }
      });
    },
    closePopup(type) {
      this.$refs.commonPopup.close(type);
    },
    goBack() {
      uni.redirectTo({
        url: "/pages/my/my",
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.history_order {
  min-height: 100vh;
  background: linear-gradient(180deg, rgba(24, 178, 107, 0.12) 0%, #f4f6f8 280rpx);
}

.history_shell {
  padding-top: 6rpx;
}

.order_tabs {
  height: 92rpx;
  line-height: 92rpx;
  width: calc(100vw - 48rpx);
  margin: 0 24rpx;
  box-sizing: border-box;
  display: flex;
  white-space: nowrap;
  background: #ffffff;
  border-radius: 20rpx;
  box-shadow: 0 10rpx 22rpx rgba(24, 36, 46, 0.08);
  padding: 0 18rpx;
}

.tab_item {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-right: 34rpx;
  height: 92rpx;
  flex-shrink: 0;

  &:last-child {
    margin-right: 10rpx;
  }

  .tab_item_act {
    position: relative;

    .tab_text {
      color: #1f2a33;
      font-weight: 600;
    }

    .tab_line {
      position: absolute;
      left: 50%;
      transform: translateX(-50%);
      bottom: 10rpx;
      width: 34rpx;
      height: 8rpx;
      border-radius: 8rpx;
      background: linear-gradient(90deg, #18b26b 0%, #63d7a1 100%);
    }
  }

  .tab_text {
    font-size: 28rpx;
    color: #62707b;
  }
}

.recent_orders {
  padding: 16rpx 24rpx 170rpx;
}

.order_card {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 22rpx 22rpx 18rpx;
  margin-bottom: 18rpx;
  box-shadow: 0 10rpx 24rpx rgba(20, 33, 44, 0.08);

  .card_head {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16rpx;

    .order_time {
      font-size: 24rpx;
      color: #73808c;
      line-height: 32rpx;
    }

    .order_status {
      font-size: 24rpx;
      font-weight: 600;
      line-height: 32rpx;
      color: #18b26b;

      &.status_1 {
        color: #ff9f2f;
      }

      &.status_2 {
        color: #18b26b;
      }

      &.status_6 {
        color: #b06a4d;
      }
    }
  }

  .card_body {
    background: #f8fbf9;
    border-radius: 16rpx;
    padding: 14rpx 14rpx 12rpx;
  }

  .goods_preview {
    white-space: nowrap;
    width: 100%;
  }

  .goods_item {
    display: inline-flex;
    align-items: center;
    width: 236rpx;
    margin-right: 14rpx;
    padding: 10rpx;
    border-radius: 12rpx;
    background: #ffffff;
    box-sizing: border-box;

    &:last-child {
      margin-right: 0;
    }

    .goods_img image {
      width: 58rpx;
      height: 58rpx;
      border-radius: 10rpx;
      background: #eef2f4;
      display: block;
    }

    .goods_name {
      margin-left: 10rpx;
      flex: 1;
      min-width: 0;
      font-size: 22rpx;
      color: #2a3641;
      line-height: 30rpx;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }
  }

  .summary_row {
    margin-top: 12rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .summary_count {
      font-size: 22rpx;
      color: #7a8792;
      line-height: 30rpx;
    }

    .summary_amount {
      font-size: 34rpx;
      color: #f45c43;
      font-weight: 600;
      line-height: 42rpx;
    }
  }

  .card_actions {
    margin-top: 16rpx;
    display: flex;
    justify-content: flex-end;
    gap: 12rpx;

    .action_btn {
      margin: 0;
      height: 62rpx;
      line-height: 62rpx;
      padding: 0 28rpx;
      border-radius: 31rpx;
      font-size: 24rpx;
      box-sizing: border-box;

      &::after {
        border: none;
      }
    }

    .action_light {
      background: #eef3f6;
      color: #3b4650;
    }

    .action_primary {
      background: linear-gradient(90deg, #ffd760 0%, #ffc843 100%);
      color: #2b2f33;
      font-weight: 600;
    }
  }
}
</style>
