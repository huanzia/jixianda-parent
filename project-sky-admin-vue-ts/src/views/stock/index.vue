<template>
  <div class="dashboard-container stock-page">
    <el-card shadow="never" class="stock-card">
      <div class="toolbar">
        <div class="search-area">
          <label>仓库</label>
          <el-select v-model="warehouseId" placeholder="请选择仓库" style="width: 220px" @change="onWarehouseChange">
            <el-option v-for="item in warehouseOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>

          <label style="margin-left: 12px">商品名称</label>
          <el-input
            v-model="name"
            clearable
            placeholder="请输入商品名称"
            style="width: 220px"
            @keyup.enter.native="init(true)"
            @clear="init"
          />

          <el-button class="query-btn" @click="init(true)">查询</el-button>
        </div>
      </div>

      <el-table v-if="tableData.length" :data="tableData" stripe class="tableBox">
        <el-table-column label="商品图片" width="100">
          <template slot-scope="{ row }">
            <el-image :src="row.image" style="width: 60px; height: 40px">
              <div slot="error" class="image-slot">-</div>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="dishName" label="商品名称" min-width="160" />
        <el-table-column prop="categoryName" label="分类" min-width="120" />
        <el-table-column label="当前库存" min-width="180">
          <template slot-scope="{ row }">
            <el-input-number v-model="row.currentStock" :min="0" :step="1" controls-position="right" />
          </template>
        </el-table-column>
        <el-table-column label="售卖状态" min-width="140" align="center">
          <template slot-scope="{ row }">
            <el-switch
              :value="row.status === 1"
              active-color="#07C160"
              inactive-color="#dcdfe6"
              @change="(val) => changeStatus(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" align="center">
          <template slot-scope="{ row }">
            <el-button type="primary" size="mini" @click="saveStock(row)">保存库存</el-button>
          </template>
        </el-table-column>
      </el-table>
      <Empty v-else :is-search="isSearch" />

      <el-pagination
        v-if="counts > 10"
        class="pageList"
        :page-sizes="[10, 20, 30, 40]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="counts"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import Empty from '@/components/Empty/index.vue'
import { getWarehouseList } from '@/api/warehouse'
import { getStockPage, updateStock, updateStockStatus } from '@/api/stock'

@Component({
  name: 'Stock',
  components: { Empty }
})
export default class extends Vue {
  private warehouseOptions: any[] = []
  private warehouseId: number | null = null
  private name = ''
  private tableData: any[] = []
  private counts = 0
  private page = 1
  private pageSize = 10
  private isSearch = false

  async created() {
    await this.loadWarehouseList()
    if (this.warehouseId) this.init()
  }

  private async loadWarehouseList() {
    const res = await getWarehouseList({ status: 1 })
    if (res.data.code === 1) {
      this.warehouseOptions = res.data.data || []
      if (this.warehouseOptions.length) this.warehouseId = this.warehouseOptions[0].id
    } else {
      this.$message.error(res.data.msg || '获取仓库列表失败')
    }
  }

  private async init(isSearch?: boolean) {
    if (!this.warehouseId) {
      this.$message.warning('请先选择仓库')
      return
    }
    this.isSearch = !!isSearch
    const res = await getStockPage({
      warehouseId: this.warehouseId,
      name: this.name || undefined,
      page: this.page,
      pageSize: this.pageSize
    })
    if (res.data.code === 1) {
      const data = res.data.data || {}
      this.tableData = data.records || []
      this.counts = Number(data.total || 0)
    } else {
      this.$message.error(res.data.msg || '获取库存列表失败')
    }
  }

  private onWarehouseChange() {
    this.page = 1
    this.init()
  }

  private async saveStock(row: any) {
    if (!this.warehouseId) return
    const res = await updateStock({
      warehouseId: this.warehouseId,
      dishId: row.dishId,
      stock: row.currentStock
    })
    if (res.data.code === 1) {
      this.$message.success('保存库存成功')
    } else {
      this.$message.error(res.data.msg || '保存库存失败')
    }
  }

  private async changeStatus(row: any, enabled: boolean) {
    if (!this.warehouseId) return
    const target = enabled ? 1 : 0
    const old = row.status
    row.status = target
    try {
      const res = await updateStockStatus(target, {
        id: row.warehouseSkuId || undefined,
        warehouseId: this.warehouseId,
        dishId: row.dishId
      })
      if (res.data.code !== 1) {
        row.status = old
        this.$message.error(res.data.msg || '更新售卖状态失败')
      }
    } catch (e) {
      row.status = old
      this.$message.error('更新售卖状态失败')
    }
  }

  private handleSizeChange(val: number) {
    this.pageSize = val
    this.init()
  }

  private handleCurrentChange(val: number) {
    this.page = val
    this.init()
  }
}
</script>

<style lang="scss" scoped>
.stock-page {
  margin: 30px;

  .stock-card {
    border-radius: 10px;
  }

  .toolbar {
    margin-bottom: 20px;
  }

  .search-area {
    display: flex;
    align-items: center;
  }

  .query-btn {
    margin-left: 12px;
    background: #111827;
    color: #fff;
    border: 0;
    border-radius: 18px;
    padding: 9px 18px;
  }

  .tableBox {
    width: 100%;
    border: 1px solid $gray-5;
    border-bottom: 0;
  }

  .pageList {
    margin-top: 20px;
    text-align: center;
  }
}
</style>
