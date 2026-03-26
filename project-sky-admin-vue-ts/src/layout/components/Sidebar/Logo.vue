<template>
  <div class="brand-logo" :class="{ collapse: isCollapse }">
    <div class="logo-icon">
      <i class="el-icon-box" />
    </div>
    <h1 v-if="!isCollapse" class="logo-title">极鲜达 | FreshLink</h1>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator'

@Component({
  name: 'SidebarLogo'
})
export default class extends Vue {
  @Prop({ default: false }) private isCollapse!: boolean
}
</script>

<style lang="scss" scoped>
.brand-logo {
  height: 60px;
  background: #1f2d3d;
  display: flex;
  align-items: center;
  padding: 0 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.logo-icon {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: rgba(7, 193, 96, 0.15);
  color: #07c160;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;

  i {
    font-size: 18px;
    font-weight: 700;
  }
}

.logo-title {
  margin: 0;
  font-size: 18px;
  color: #ffffff;
  font-weight: 700;
  letter-spacing: 0.2px;
}

.collapse {
  justify-content: center;
  padding: 0;

  .logo-icon {
    margin-right: 0;
  }
}
</style>
