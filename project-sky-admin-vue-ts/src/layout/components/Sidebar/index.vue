<template>
  <div class="sidebar-wrap">
    <Logo :is-collapse="isCollapse" />
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
        :default-openeds="defOpen"
        :default-active="defAct"
        :collapse="isCollapse"
        :background-color="variables.menuBg"
        :text-color="variables.menuText"
        :active-text-color="variables.menuActiveText"
        :unique-opened="false"
        :collapse-transition="false"
        mode="vertical"
      >
        <sidebar-item
          v-for="route in routes"
          :key="route.path"
          :item="route"
          :base-path="route.path"
          :is-collapse="isCollapse"
        />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import { AppModule } from '@/store/modules/app'
import { UserModule } from '@/store/modules/user'
import SidebarItem from './SidebarItem.vue'
import Logo from './Logo.vue'
import variables from '@/styles/_variables.scss'

@Component({
  name: 'SideBar',
  components: {
    SidebarItem,
    Logo
  }
})
export default class extends Vue {
  get defOpen() {
    const path = ['/']
    this.routes.forEach((n: any) => {
      if (n.meta.roles && n.meta.roles[0] === this.roles[0]) {
        path.splice(0, 1, n.path)
      }
    })
    return path
  }

  get defAct() {
    return this.$route.path
  }

  get sidebar() {
    return AppModule.sidebar
  }

  get roles() {
    return UserModule.roles
  }

  get routes() {
    const routes = JSON.parse(JSON.stringify([...(this.$router as any).options.routes]))
    const menu = routes.find((item) => item.path === '/')
    return menu ? menu.children : []
  }

  get variables() {
    return variables
  }

  get isCollapse() {
    return !this.sidebar.opened
  }
}
</script>

<style lang="scss" scoped>
.sidebar-wrap {
  height: 100%;
  background-color: #1f2d3d;
}

.el-scrollbar {
  height: calc(100% - 60px);
  background-color: #1f2d3d;
}

.el-menu {
  border: none;
  min-height: 100%;
  width: 100% !important;
  padding: 16px 12px 0;
  background-color: #1f2d3d;
}
</style>
