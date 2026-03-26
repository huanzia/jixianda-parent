<template>
  <view class="success_content">
    <view class="success_info">
      <image class="success_icon" src="../../static/success.png" mode=""></image>
      <view class="success_title">&#19979;&#21333;&#25104;&#21151;</view>
      <view class="word-box">
        <text class="word_bottom">
          &#39044;&#35745;<text class="word_date">{{ arrivalTime }}</text>&#36865;&#36798;
        </text>
      </view>
      <view class="success_desc">
        &#21518;&#21416;&#24050;&#24320;&#22987;&#30127;&#29378;&#22791;&#39184;&#20013;, &#35831;&#32784;&#24515;&#31561;&#24453;~
      </view>
      <view class="btns">
        <view class="go_dish defaultBtn" @click="goIndex()">
          &#36820;&#22238;&#39318;&#39029;
        </view>
        <view class="go_dish" @click="goOrder">
          &#26597;&#30475;&#35746;&#21333;
        </view>
      </view>
    </view>
  </view>
</template>
<script>
import { mapState } from "vuex";
export default {
  data() {
    return {
      arrivalTime: "",
      orderId: null,
    };
  },
  computed: {
    tableInfo: function () {
      return this.shopInfo();
    },
  },
  onLoad(options) {
    this.getHarfAnOur();
    this.orderId = options.orderId;
  },
  methods: {
    ...mapState(["shopInfo", "arrivals"]),
    goIndex() {
      uni.navigateTo({
        url: "/pages/index/index?status=" + "\u4e0d\u9a8c\u8bc1",
      });
    },
    goOrder() {
      uni.navigateTo({
        url: "/pages/details/index?orderId=" + this.orderId,
      });
    },
    getHarfAnOur() {
      this.arrivalTime = this.arrivals();
    },
  },
};
</script>
<style src="./../common/Navbar/navbar.scss" lang="scss" scoped></style>
<style lang="scss" scoped>
.success_content {
  min-height: 100vh;
  background: linear-gradient(180deg, rgba(24, 178, 107, 0.16) 0%, rgba(246, 247, 251, 0.94) 22%, #f6f7fb 100%);
  padding: 180rpx 30rpx 40rpx;
  box-sizing: border-box;

  .success_info {
    background: #ffffff;
    border-radius: 24rpx;
    box-shadow: 0 14rpx 30rpx rgba(39, 43, 54, 0.12);
    padding: 54rpx 36rpx 46rpx;
    text-align: center;

    .success_icon {
      width: 150rpx;
      height: 144rpx;
      text-align: center;
    }

    .success_title {
      font-size: 40rpx;
      font-weight: 600;
      color: #1f2a33;
      margin-top: 20rpx;
      height: 56rpx;
      line-height: 56rpx;
      letter-spacing: 0.6rpx;
    }

    .success_desc {
      font-size: 28rpx;
      margin-bottom: 38rpx;
      font-weight: 400;
      text-align: center;
      color: #666666;
      line-height: 42rpx;
      margin-top: 24rpx;
    }

    .word-box {
      margin-top: 30rpx;
      height: 44rpx;
      line-height: 44rpx;
    }

    .word_bottom {
      font-weight: 400;
      text-align: center;
      font-size: 32rpx;
      color: #666666;

      .word_date {
        color: #18b26b;
      }
    }

    .btns {
      width: 100%;
      display: flex;
      justify-content: center;

      .go_dish {
        position: relative;
        font-size: 30rpx;
        width: 236rpx;
        height: 80rpx;
        line-height: 80rpx;
        margin-top: 14rpx;
        background: #ffc200;
        border-radius: 40rpx;
        font-weight: 600;
        color: #333;

        & + .go_dish {
          margin-left: 28rpx;
        }
      }

      .defaultBtn {
        border: 1px solid #e3e4e8;
        background: #ffffff;
      }

      .go_dish:not(.defaultBtn) {
        background: linear-gradient(90deg, #ffd54a 0%, #ffc200 100%);
      }
    }
  }
}
</style>
