<template>
  <div class="dashboard-page">
    <div class="cards-grid">
      <el-card shadow="never" class="metric-card hover-effect">
        <div class="card-icon icon-orders">
          <i class="el-icon-s-order" />
        </div>
        <div class="card-content">
          <div class="card-title">今日订单</div>
          <div class="card-value value-green">{{ metrics.todayOrderCount }}</div>
          <div class="card-sub">总订单 {{ metrics.totalOrderCount }}</div>
        </div>
      </el-card>

      <el-card shadow="never" class="metric-card hover-effect">
        <div class="card-icon icon-money">
          <i class="el-icon-money" />
        </div>
        <div class="card-content">
          <div class="card-title">今日营业额</div>
          <div class="card-value value-gold">¥ {{ formatAmount(metrics.todayTurnover) }}</div>
          <div class="card-sub">完成率 {{ formatRate(metrics.orderCompletionRate) }}</div>
        </div>
      </el-card>

      <el-card shadow="never" class="metric-card hover-effect">
        <div class="card-icon icon-warehouse">
          <i class="el-icon-s-home" />
        </div>
        <div class="card-content">
          <div class="card-title">在营仓库数</div>
          <div class="card-value value-blue">{{ metrics.activeWarehouseCount }}</div>
          <div class="card-sub">启用仓库</div>
        </div>
      </el-card>

      <el-card shadow="never" class="metric-card hover-effect">
        <div class="card-icon icon-sku">
          <i class="el-icon-goods" />
        </div>
        <div class="card-content">
          <div class="card-title">总 SKU 数量</div>
          <div class="card-value value-purple">{{ metrics.totalSkuCount }}</div>
          <div class="card-sub">商品总量</div>
        </div>
      </el-card>
    </div>

    <el-card shadow="never" class="trend-card">
      <div class="trend-title">近 7 日订单趋势</div>
      <div ref="orderTrendChart" class="chart-container" />
    </el-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import * as echarts from 'echarts'
import {
  getOrdersStatistics,
  getWorkspaceBusinessData,
  getWorkspaceOrderOverview
} from '@/api/analysis'
import { getWarehouseList } from '@/api/warehouse'
import { getDishPage } from '@/api/dish'

@Component({
  name: 'Dashboard'
})
export default class extends Vue {
  private chartInstance: any = null
  private metrics = {
    todayOrderCount: 0,
    totalOrderCount: 0,
    todayTurnover: 0,
    orderCompletionRate: 0,
    activeWarehouseCount: 0,
    totalSkuCount: 0
  }

  created() {
    this.loadDashboard()
  }

  mounted() {
    window.addEventListener('resize', this.handleResize)
  }

  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    if (this.chartInstance) {
      this.chartInstance.dispose()
      this.chartInstance = null
    }
  }

  private async loadDashboard() {
    const wrap = (p: Promise<any>) => p.then((res) => ({ ok: true, res })).catch(() => ({ ok: false, res: null }))
    const [businessRes, orderOverviewRes, warehouseRes, dishRes] = await Promise.all([
      wrap(getWorkspaceBusinessData()),
      wrap(getWorkspaceOrderOverview()),
      wrap(getWarehouseList({ status: 1 })),
      wrap(getDishPage({ page: 1, pageSize: 1 }))
    ])

    let successCount = 0

    if (businessRes.ok && businessRes.res.data.code === 1) {
      const businessData = businessRes.res.data.data || {}
      this.metrics.todayOrderCount = Number(businessData.validOrderCount || 0)
      this.metrics.todayTurnover = Number(businessData.turnover || 0)
      this.metrics.orderCompletionRate = Number(businessData.orderCompletionRate || 0)
      successCount++
    }

    if (orderOverviewRes.ok && orderOverviewRes.res.data.code === 1) {
      const orderOverview = orderOverviewRes.res.data.data || {}
      this.metrics.totalOrderCount = Number(orderOverview.allOrders || 0)
      successCount++
    }

    if (warehouseRes.ok && warehouseRes.res.data.code === 1) {
      const warehouseList = warehouseRes.res.data.data || []
      this.metrics.activeWarehouseCount = Array.isArray(warehouseList) ? warehouseList.length : 0
      successCount++
    }

    if (dishRes.ok && dishRes.res.data.code === 1) {
      this.metrics.totalSkuCount = Number((dishRes.res.data.data && dishRes.res.data.data.total) || 0)
      successCount++
    }

    if (successCount === 0) {
      this.$message.error('驾驶舱数据加载失败，请检查后端接口或登录态')
    } else if (successCount < 4) {
      this.$message.warning('驾驶舱部分数据加载失败，已展示可用数据')
    }

    await this.loadOrderTrend()
  }

  private async loadOrderTrend() {
    const end = new Date()
    const begin = new Date()
    begin.setDate(end.getDate() - 6)

    try {
      const res = await getOrdersStatistics({
        begin: this.formatDate(begin),
        end: this.formatDate(end)
      })

      if (res.data.code === 1) {
        const trend = res.data.data || {}
        const dateList = this.splitCsv(trend.dateList)
        const orderCountList = this.splitCsv(trend.orderCountList).map((item: string) => Number(item))
        this.renderTrendChart(dateList, orderCountList)
        return
      }
      this.renderTrendChart([], [])
    } catch (error) {
      this.$message.error('趋势数据加载失败')
      this.renderTrendChart([], [])
    }
  }

  private splitCsv(value: string) {
    if (!value) return []
    return value.split(',').map((item: string) => item.trim())
  }

  private renderTrendChart(xData: string[], yData: number[]) {
    this.$nextTick(() => {
      const chartDom = this.$refs.orderTrendChart as HTMLDivElement
      if (!chartDom) return

      if (!this.chartInstance) {
        this.chartInstance = echarts.init(chartDom)
      }

      this.chartInstance.setOption({
        grid: {
          top: 32,
          left: 30,
          right: 20,
          bottom: 30
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: xData,
          axisLine: {
            lineStyle: {
              color: '#9CA3AF'
            }
          },
          axisLabel: {
            color: '#4B5563'
          }
        },
        yAxis: {
          type: 'value',
          axisLine: {
            show: false
          },
          axisLabel: {
            color: '#4B5563'
          },
          splitLine: {
            lineStyle: {
              color: '#E5E7EB'
            }
          }
        },
        series: [
          {
            data: yData,
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 7,
            itemStyle: {
              color: '#07C160'
            },
            lineStyle: {
              color: '#07C160',
              width: 3
            },
            areaStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  { offset: 0, color: 'rgba(7, 193, 96, 0.35)' },
                  { offset: 1, color: 'rgba(7, 193, 96, 0.05)' }
                ]
              }
            }
          }
        ]
      })
    })
  }

  private handleResize = () => {
    if (this.chartInstance) {
      this.chartInstance.resize()
    }
  }

  private formatDate(date: Date) {
    const year = date.getFullYear()
    const month = `${date.getMonth() + 1}`.padStart(2, '0')
    const day = `${date.getDate()}`.padStart(2, '0')
    return `${year}-${month}-${day}`
  }

  private formatAmount(value: number) {
    return Number(value || 0).toFixed(2)
  }

  private formatRate(rate: number) {
    const value = Number(rate || 0) * 100
    return `${value.toFixed(1)}%`
  }
}
</script>

<style lang="scss" scoped>
.dashboard-page {
  padding: 24px;
  background: #f5f7fb;
  min-height: calc(100vh - 84px);
}

.cards-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(180px, 1fr));
  gap: 16px;
  margin-bottom: 18px;
}

.metric-card {
  border: 0;
  border-radius: 12px;
  .el-card__body {
    display: flex;
    align-items: center;
    padding: 18px;
  }
}

.hover-effect {
  transition: all 0.25s ease;
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 24px rgba(31, 41, 55, 0.12);
  }
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  i {
    font-size: 24px;
    color: #fff;
  }
}

.icon-orders {
  background: linear-gradient(135deg, #059669, #07c160);
}

.icon-money {
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
}

.icon-warehouse {
  background: linear-gradient(135deg, #2563eb, #60a5fa);
}

.icon-sku {
  background: linear-gradient(135deg, #7c3aed, #a78bfa);
}

.card-title {
  color: #6b7280;
  font-size: 13px;
  margin-bottom: 6px;
}

.card-value {
  font-size: 26px;
  line-height: 1.1;
  font-weight: 700;
  margin-bottom: 4px;
}

.card-sub {
  color: #9ca3af;
  font-size: 12px;
}

.value-green {
  color: #07c160;
}

.value-gold {
  color: #d97706;
}

.value-blue {
  color: #2563eb;
}

.value-purple {
  color: #7c3aed;
}

.trend-card {
  border-radius: 12px;
  border: 0;
}

.trend-title {
  font-size: 16px;
  color: #111827;
  font-weight: 600;
  margin-bottom: 10px;
}

.chart-container {
  width: 100%;
  height: 360px;
}

@media (max-width: 1200px) {
  .cards-grid {
    grid-template-columns: repeat(2, minmax(180px, 1fr));
  }
}

@media (max-width: 768px) {
  .cards-grid {
    grid-template-columns: 1fr;
  }
}
</style>
