<template>
  <div>
    <div
      v-if="(!item.meta || !item.meta.hidden) && !shouldHide(item)"
      :class="['menu-wrapper', 'full-mode', { 'first-level': isFirstLevel }]"
    >
      <template v-if="theOnlyOneChild && !theOnlyOneChild.children">
        <sidebar-item-link v-if="theOnlyOneChild.meta" :to="resolvePath(theOnlyOneChild.path)">
          <el-menu-item :index="resolvePath(theOnlyOneChild.path)" :class="{ 'submenu-title-noDropdown': isFirstLevel }">
            <i v-if="theOnlyOneChild.meta.icon" :class="iconClass(theOnlyOneChild.meta.icon)" />
            <span v-if="theOnlyOneChild.meta.title" slot="title">{{ theOnlyOneChild.meta.title }}</span>
          </el-menu-item>
        </sidebar-item-link>
      </template>

      <el-submenu v-else :index="resolvePath(item.path)" popper-append-to-body>
        <template slot="title">
          <i v-if="item.meta && item.meta.icon" :class="iconClass(item.meta.icon)" />
          <span v-if="item.meta && item.meta.title" slot="title">{{ item.meta.title }}</span>
        </template>
        <template v-if="item.children">
          <sidebar-item
            v-for="child in visibleChildren"
            :key="child.path"
            :item="child"
            :is-collapse="isCollapse"
            :is-first-level="false"
            :base-path="resolvePath(child.path)"
            class="nest-menu"
          />
        </template>
      </el-submenu>
    </div>
  </div>
</template>

<script lang="ts">
import path from 'path'
import { Component, Prop, Vue } from 'vue-property-decorator'
import { UserModule } from '@/store/modules/user'
import { RouteConfig } from 'vue-router'
import { isExternal } from '@/utils/validate'
import SidebarItemLink from './SidebarItemLink.vue'

@Component({
  name: 'SidebarItem',
  components: {
    SidebarItemLink
  }
})
export default class extends Vue {
  @Prop({ required: true }) private item!: RouteConfig
  @Prop({ default: false }) private isCollapse!: boolean
  @Prop({ default: true }) private isFirstLevel!: boolean
  @Prop({ default: '' }) private basePath!: string

  private readonly restrictedMenuPaths = ['employee', 'warehouse', 'category', 'dish']

  get visibleChildren() {
    if (!this.item.children) return []
    return this.item.children.filter((child) => {
      if (child.meta && child.meta.hidden) return false
      return !this.shouldHide(child)
    })
  }

  get showingChildNumber() {
    return this.visibleChildren.length
  }

  get roles() {
    return UserModule.roles
  }

  get theOnlyOneChild() {
    if (this.showingChildNumber > 1) {
      return null
    }
    if (this.visibleChildren.length === 1) {
      return this.visibleChildren[0]
    }
    return { ...this.item, path: '' }
  }

  private isBranchEmployee() {
    const fromStore = (UserModule.userInfo && (UserModule.userInfo as any).warehouseId)
    const fromLocal = localStorage.getItem('warehouse_id')
    const warehouseId = fromStore != null ? Number(fromStore) : Number(fromLocal || 0)
    return !Number.isNaN(warehouseId) && warehouseId !== 0
  }

  private shouldHide(route: RouteConfig) {
    if (!this.isBranchEmployee()) return false
    const pathText = String(route.path || '').replace(/^\//, '').split('/')[0].toLowerCase()
    return this.restrictedMenuPaths.includes(pathText)
  }

  private resolvePath(routePath: string) {
    if (isExternal(routePath)) {
      return routePath
    }
    if (isExternal(this.basePath)) {
      return this.basePath
    }
    return path.resolve(this.basePath, routePath)
  }

  private iconClass(icon: string) {
    if (!icon) return ''
    if (icon.indexOf('el-icon-') === 0) {
      return icon
    }
    return ['iconfont', icon]
  }
}
</script>
