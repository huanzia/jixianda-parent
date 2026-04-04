<template>
  <div class="navbar">
    <div class="left-area">
      <hamburger
        id="hamburger-container"
        :is-active="sidebar.opened"
        class="hamburger-container"
        @toggleClick="toggleSideBar"
      />
      <breadcrumb class="breadcrumb-container" />
      <span v-if="status === 1" class="business-btn">营业中</span>
      <span v-else class="business-btn closing">打烊</span>
    </div>

    <div class="right-menu">
      <span class="nav-item operating-state" @click="dialogVisible = true">
        <i class="el-icon-setting" />
        营业状态设置
      </span>

      <div class="avatar-wrapper" @mouseenter="shopShow = true" @mouseleave="shopShow = false">
        <el-button type="primary" :class="{ active: shopShow }">
          {{ name }}
          <i class="el-icon-arrow-down" />
        </el-button>
        <div v-if="shopShow" class="user-list">
          <p class="user-name">管理员</p>
          <p class="disabled-item">修改密码</p>
          <p class="out-login" @click="logout">退出登录</p>
        </div>
      </div>
    </div>

    <el-dialog title="营业状态设置" :visible.sync="dialogVisible" width="420px">
      <el-radio-group v-model="setStatus">
        <el-radio :label="1">营业中<span>当前状态下，用户可正常下单</span></el-radio>
        <el-radio :label="0">打烊<span>当前状态下，用户端将无法下单</span></el-radio>
      </el-radio-group>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import { AppModule } from '@/store/modules/app'
import { UserModule } from '@/store/modules/user'
import Hamburger from '@/components/Hamburger/index.vue'
import Breadcrumb from '@/components/Breadcrumb/index.vue'
import { getStatus, setStatus } from '@/api/users'
import Cookies from 'js-cookie'

@Component({
  name: 'Navbar',
  components: {
    Hamburger,
    Breadcrumb
  }
})
export default class extends Vue {
  private dialogVisible = false
  private shopShow = false
  private status = 1
  private setStatus = 1

  get sidebar() {
    return AppModule.sidebar
  }

  get name() {
    const userInfo: any = UserModule.userInfo || {}
    if (userInfo.name) return userInfo.name
    const cookie = Cookies.get('user_info')
    if (!cookie) return '管理员'
    try {
      return JSON.parse(cookie).name || '管理员'
    } catch (e) {
      return '管理员'
    }
  }

  mounted() {
    this.loadStatus()
  }

  private toggleSideBar() {
    AppModule.ToggleSideBar(false)
  }

  private logout() {
    this.$store.dispatch('LogOut').then(() => {
      this.$router.replace({ path: '/login' })
    })
  }

  private async loadStatus() {
    const { data } = await getStatus()
    if (data && data.code === 1) {
      this.status = data.data
      this.setStatus = this.status
    }
  }

  private async handleSave() {
    const { data } = await setStatus(this.setStatus)
    if (data && data.code === 1) {
      this.dialogVisible = false
      this.status = this.setStatus
      this.$message.success('营业状态更新成功')
    } else {
      this.$message.error((data && data.msg) || '保存失败')
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 60px;
  padding: 0 16px;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  box-shadow: 0 2px 10px rgba(15, 23, 42, 0.06);
  display: flex;
  align-items: center;
  justify-content: space-between;

  .left-area {
    display: flex;
    align-items: center;
    min-width: 0;
  }

  .hamburger-container {
    cursor: pointer;
    margin-right: 8px;
  }

  .breadcrumb-container {
    margin-right: 12px;
  }

  .business-btn {
    height: 24px;
    line-height: 24px;
    background: #07c160;
    border-radius: 12px;
    padding: 0 10px;
    color: #fff;
    font-weight: 600;
    font-size: 12px;
  }

  .closing {
    background: #3b82f6;
  }

  .right-menu {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    height: 100%;
    color: #4b5563;
    font-size: 14px;

    > * {
      display: flex;
      align-items: center;
      height: 100%;
    }
  }

  .nav-item {
    padding: 0 10px;
    border-radius: 6px;
    cursor: pointer;

    &:hover {
      background: #f3f4f6;
    }

    i {
      margin-right: 6px;
      color: #07c160;
    }
  }

  .avatar-wrapper {
    margin-left: 16px;
    position: relative;
    height: 100%;

    .el-button--primary {
      background: #eef2f7;
      color: #111827;
      border: 1px solid #dbe3ec;
      border-radius: 8px;
      height: 32px;
      padding: 0 12px;

      &.active {
        background: #eaf8f0;
        border-color: #07c160;
      }
    }

    .user-list {
      position: absolute;
      right: 0;
      top: 46px;
      min-width: 140px;
      background: #fff;
      border-radius: 8px;
      box-shadow: 0 6px 20px rgba(17, 24, 39, 0.12);
      z-index: 10;

      p {
        margin: 0;
        padding: 10px 12px;
        cursor: pointer;
        color: #374151;

        &:hover {
          background: #f3f4f6;
        }
      }

      .user-name {
        font-weight: 600;
        cursor: default;
      }

      .disabled-item {
        color: #9ca3af;
        cursor: not-allowed;
      }
    }
  }
}
</style>
