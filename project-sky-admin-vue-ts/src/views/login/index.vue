<template>
  <div class="login">
    <div class="login-box">
      <div class="login-form">
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules">
          <div class="login-form-title">
            <h1 class="title-label">极鲜达前置仓管理系统</h1>
          </div>
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              type="text"
              auto-complete="off"
              placeholder="账号"
              prefix-icon="iconfont icon-user"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              prefix-icon="iconfont icon-lock"
              @keyup.enter.native="handleLogin"
            />
          </el-form-item>
          <el-form-item style="width: 100%">
            <el-button
              :loading="loading"
              class="login-btn"
              size="medium"
              type="primary"
              style="width: 100%"
              @click.native.prevent="handleLogin"
            >
              <span v-if="!loading">登录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator'
import { Route } from 'vue-router'
import { Form as ElForm } from 'element-ui'
import { UserModule } from '@/store/modules/user'

@Component({
  name: 'Login',
})
export default class extends Vue {
  private validateUsername = (rule: any, value: string, callback: Function) => {
    if (!value) {
      callback(new Error('请输入用户名'))
    } else {
      callback()
    }
  }
  private validatePassword = (rule: any, value: string, callback: Function) => {
    if (value.length < 6) {
      callback(new Error('密码必须在6位以上'))
    } else {
      callback()
    }
  }
  private loginForm = {
    username: '',
    password: '',
  } as {
    username: String
    password: String
  }

  loginRules = {
    username: [{ validator: this.validateUsername, trigger: 'blur' }],
    password: [{ validator: this.validatePassword, trigger: 'blur' }],
  }
  private loading = false
  private redirect?: string

  @Watch('$route', { immediate: true })
  private onRouteChange(route: Route) {}

  // 登录
  private handleLogin() {
    ;(this.$refs.loginForm as ElForm).validate(async (valid: boolean) => {
      if (valid) {
        this.loading = true
        await UserModule.Login(this.loginForm as any)
          .then((res: any) => {
            if (String(res.code) === '1') {
              this.$router.push('/')
            } else {
              // this.$message.error(res.msg)
              this.loading = false
            }
          })
          .catch(() => {
            // this.$message.error('用户名或密码错误！')
            this.loading = false
          })
      } else {
        return false
      }
    })
  }
}
</script>

<style lang="scss">
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background: linear-gradient(135deg, #0f172a 0%, #1f2937 55%, #0b3f2a 100%);
}

.login-box {
  width: 440px;
  border-radius: 16px;
  display: flex;
  box-shadow: 0 18px 48px rgba(0, 0, 0, 0.35);
  overflow: hidden;
}

.title {
  margin: 0px auto 10px auto;
  text-align: left;
  color: #707070;
}

.login-form {
  background: rgba(17, 24, 39, 0.9);
  width: 100%;
  border-radius: 16px;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 0;
  .el-form {
    width: 320px;
  }
  .el-form-item {
    margin-bottom: 24px;
  }
  .el-form-item.is-error .el-input__inner {
    border: 0 !important;
    border-bottom: 1px solid #fd7065 !important;
    background: rgba(255, 255, 255, 0.08) !important;
  }
  .input-icon {
    height: 32px;
    width: 18px;
    margin-left: -2px;
  }
  .el-input__inner {
    border: 1px solid rgba(255, 255, 255, 0.14);
    background: rgba(255, 255, 255, 0.08);
    border-radius: 8px;
    font-size: 13px;
    font-weight: 400;
    color: #ffffff;
    height: 40px;
    line-height: 40px;
  }
  .el-input__prefix {
    left: 8px;
  }
  .el-input--prefix .el-input__inner {
    padding-left: 34px;
  }
  .el-input__inner::placeholder {
    color: rgba(255, 255, 255, 0.55);
  }
  .el-form-item--medium .el-form-item__content {
    line-height: 40px;
  }
  .el-input--medium .el-input__icon {
    line-height: 40px;
    color: rgba(255, 255, 255, 0.75);
  }
}

.login-btn {
  border-radius: 22px;
  padding: 11px 20px !important;
  margin-top: 8px;
  font-weight: 500;
  font-size: 14px;
  border: 0;
  color: #ffffff;
  background-color: #07c160;
  &:hover,
  &:focus {
    background-color: #06ad56;
    color: #ffffff;
  }
}
.login-form-title {
  min-height: 56px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 36px;
  .title-label {
    font-weight: 700;
    font-size: 30px;
    color: #ffffff;
    text-align: center;
    line-height: 1.2;
    letter-spacing: 1px;
    margin: 0;
  }
}
</style>
