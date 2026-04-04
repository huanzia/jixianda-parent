<template>
  <div class="dashboard-container">
    <div class="container">
      <div class="tableBar">
        <label>订单号</label>
        <el-input
          v-model="filters.number"
          placeholder="请输入订单号"
          style="width: 180px"
          clearable
          @keyup.enter.native="search"
        />

        <label style="margin-left: 16px">手机号</label>
        <el-input
          v-model="filters.phone"
          placeholder="请输入手机号"
          style="width: 160px"
          clearable
          @keyup.enter.native="search"
        />

        <label style="margin-left: 16px">仓库</label>
        <el-select v-model="filters.warehouseId" clearable placeholder="请选择" style="width: 180px" @change="search">
          <el-option label="全部" :value="undefined" />
          <el-option v-for="item in warehouseOptions" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>

        <label style="margin-left: 16px">下单时间</label>
        <el-date-picker
          v-model="timeRange"
          clearable
          value-format="yyyy-MM-dd HH:mm:ss"
          range-separator="至"
          :default-time="['00:00:00', '23:59:59']"
          type="daterange"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          style="width: 280px"
        />

        <el-button class="normal-btn continue" @click="search">查询</el-button>
      </div>

      <el-table :data="tableData" stripe class="tableBox">
        <el-table-column prop="number" label="订单号" min-width="180" />
        <el-table-column prop="consignee" label="收货人" min-width="120" />
        <el-table-column prop="phone" label="手机号" min-width="120" />
        <el-table-column prop="address" label="地址" min-width="180" show-overflow-tooltip />
        <el-table-column prop="amount" label="订单金额" width="110" />
        <el-table-column prop="orderTime" label="下单时间" min-width="170" />
        <el-table-column label="状态" width="100">
          <template slot-scope="{ row }">{{ statusText(row.status) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center">
          <template slot-scope="{ row }">
            <el-button v-if="row.status === 3" type="text" class="blueBug" @click="delivery(row.id)">派送</el-button>
            <el-button v-if="row.status === 4" type="text" class="blueBug" @click="complete(row.id)">完成</el-button>
            <el-button v-if="[1,3,4].includes(row.status) && row.status !== 5 && row.status !== 6" type="text" class="delBut" @click="cancel(row.id)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-if="total > 0"
        class="pageList"
        :page-size="pageSize"
        layout="total, prev, pager, next"
        :total="total"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import { getOrderDetailPage, deliveryOrder, completeOrder, orderCancel } from '@/api/order'
import { getWarehouseList } from '@/api/warehouse'

@Component({ name: 'OrderDetails' })
export default class extends Vue {
  private tableData: any[] = []
  private warehouseOptions: any[] = []
  private total = 0
  private page = 1
  private pageSize = 10
  private timeRange: string[] = []

  private filters: any = {
    number: '',
    phone: '',
    warehouseId: undefined,
    status: undefined
  }

  async created() {
    await this.loadWarehouseOptions()
    const queryStatus = this.$route.query.status
    if (queryStatus !== undefined) {
      this.filters.status = Number(queryStatus)
    }
    this.loadData()
  }

  private async loadWarehouseOptions() {
    const res = await getWarehouseList({ status: 1 })
    if (res.data.code === 1) {
      this.warehouseOptions = res.data.data || []
    }
  }

  private buildParams() {
    return {
      page: this.page,
      pageSize: this.pageSize,
      number: this.filters.number || undefined,
      phone: this.filters.phone || undefined,
      warehouseId: this.filters.warehouseId || undefined,
      status: this.filters.status || undefined,
      beginTime: this.timeRange && this.timeRange.length ? this.timeRange[0] : undefined,
      endTime: this.timeRange && this.timeRange.length ? this.timeRange[1] : undefined
    }
  }

  private async loadData() {
    const res = await getOrderDetailPage(this.buildParams())
    if (res.data.code === 1) {
      this.tableData = (res.data.data && res.data.data.records) || []
      this.total = Number((res.data.data && res.data.data.total) || 0)
    } else {
      this.$message.error(res.data.msg || '获取订单列表失败')
    }
  }

  private search() {
    this.page = 1
    this.loadData()
  }

  private handleCurrentChange(val: number) {
    this.page = val
    this.loadData()
  }

  private statusText(status: number) {
    const map: any = {
      1: '待支付',
      2: '待接单',
      3: '待派送',
      4: '派送中',
      5: '已完成',
      6: '已取消'
    }
    return map[status] || '未知'
  }

  private async delivery(id: number) {
    const res = await deliveryOrder({ id })
    if (res.data.code === 1) {
      this.$message.success('派送成功')
      this.loadData()
    } else {
      this.$message.error(res.data.msg || '派送失败')
    }
  }

  private async complete(id: number) {
    const res = await completeOrder({ id })
    if (res.data.code === 1) {
      this.$message.success('订单完成成功')
      this.loadData()
    } else {
      this.$message.error(res.data.msg || '操作失败')
    }
  }

  private async cancel(id: number) {
    const res = await orderCancel({ id, cancelReason: '管理端取消' })
    if (res.data.code === 1) {
      this.$message.success('取消成功')
      this.loadData()
    } else {
      this.$message.error(res.data.msg || '取消失败')
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  margin: 30px;

  .container {
    background: #fff;
    padding: 24px;
    border-radius: 6px;
  }

  .tableBar {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 16px;
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

  .normal-btn {
    margin-left: 8px;
    background: #111827;
    border-color: #111827;
    color: #fff;
  }
}
</style>
