<template>
  <div class="dashboard-container">
    <div class="container">
      <div class="tableBar">
        <label style="margin-right: 10px">商品名称：</label>
        <el-input v-model="input"
                  placeholder="请输入商品名称"
                  style="width: 14%"
                  clearable
                  @clear="init"
                  @keyup.enter.native="initFun" />

        <label style="margin-right: 10px; margin-left: 20px">商品分类：</label>
        <el-select v-model="categoryId"
                   style="width: 14%"
                   placeholder="请选择分类"
                   clearable
                   @clear="init">
          <el-option v-for="item in dishCategoryList"
                     :key="item.value"
                     :label="item.label"
                     :value="item.value" />
        </el-select>

        <label style="margin-right: 10px; margin-left: 20px">售卖状态：</label>
        <el-select v-model="dishStatus"
                   style="width: 14%"
                   placeholder="请选择状态"
                   clearable
                   @clear="init">
          <el-option v-for="item in saleStatus"
                     :key="item.value"
                     :label="item.label"
                     :value="item.value" />
        </el-select>
        <el-button class="normal-btn continue"
                   @click="init(true)">
          查询
        </el-button>

        <div class="tableLab">
          <span class="delBut non"
                @click="deleteHandle('batch', null)">批量删除</span>
          <el-button type="primary"
                     style="margin-left: 15px"
                     @click="addDishtype('add')">
            + 新增商品
          </el-button>
        </div>
      </div>
      <el-table v-if="tableData.length"
                :data="tableData"
                stripe
                class="tableBox"
                @selection-change="handleSelectionChange">
        <el-table-column type="selection"
                         width="25" />
        <el-table-column prop="name"
                         label="商品名称" />
        <el-table-column prop="image"
                         label="图片">
          <template slot-scope="{ row }">
            <el-image style="width: 80px; height: 40px; border: none; cursor: pointer"
                      :src="row.image">
              <div slot="error"
                   class="image-slot">
                <img src="./../../assets/noImg.png"
                     style="width: auto; height: 40px; border: none">
              </div>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName"
                         label="分类" />
        <el-table-column label="价格">
          <template slot-scope="scope">
            <span style="margin-right: 10px">￥{{ (scope.row.price).toFixed(2) * 100 / 100 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="售卖状态">
          <template slot-scope="scope">
            <div class="tableColumn-status"
                 :class="{ 'stop-use': String(scope.row.status) === '0' }">
              {{ String(scope.row.status) === '0' ? '已停售' : '售卖中' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime"
                         label="最后更新" />
        <el-table-column label="操作"
                         width="250"
                         align="center">
          <template slot-scope="scope">
            <el-button type="text"
                       size="small"
                       class="blueBug"
                       @click="addDishtype(scope.row.id)">
              编辑
            </el-button>
            <el-button type="text"
                       size="small"
                       class="delBut"
                       @click="deleteHandle('single', scope.row.id)">
              删除
            </el-button>
            <el-button type="text"
                       size="small"
                       class="blueBug non"
                       :class="{
                         blueBug: scope.row.status == '0',
                         delBut: scope.row.status != '0'
                       }"
                       @click="statusHandle(scope.row)">
              {{ scope.row.status == '0' ? '启售' : '停售' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <Empty v-else
             :is-search="isSearch" />
      <el-pagination v-if="counts > 10"
                     class="pageList"
                     :page-sizes="[10, 20, 30, 40]"
                     :page-size="pageSize"
                     layout="total, sizes, prev, pager, next, jumper"
                     :total="counts"
                     @size-change="handleSizeChange"
                     @current-change="handleCurrentChange" />
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import HeadLable from '@/components/HeadLable/index.vue'
import {
  getDishPage,
  editDish,
  deleteDish,
  dishStatusByStatus,
  dishCategoryList
} from '@/api/dish'
import InputAutoComplete from '@/components/InputAutoComplete/index.vue'
import Empty from '@/components/Empty/index.vue'

@Component({
  name: 'DishType',
  components: {
    HeadLable,
    InputAutoComplete,
    Empty
  }
})
export default class extends Vue {
  private input: any = ''
  private counts: number = 0
  private page: number = 1
  private pageSize: number = 10
  private checkList: string[] = []
  private tableData: [] = []
  private dishCategoryList = []
  private categoryId = ''
  private dishStatus = ''
  private isSearch: boolean = false
  private saleStatus: any = [
    {
      value: 0,
      label: '已停售'
    },
    {
      value: 1,
      label: '售卖中'
    }
  ]

  created() {
    this.init()
    this.getDishCategoryList()
  }

  initProp(val) {
    this.input = val
    this.initFun()
  }

  initFun() {
    this.page = 1
    this.init()
  }

  private async init(isSearch?) {
    this.isSearch = isSearch
    await getDishPage({
      page: this.page,
      pageSize: this.pageSize,
      name: this.input || undefined,
      categoryId: this.categoryId || undefined,
      status: this.dishStatus
    })
      .then(res => {
        if (res.data.code === 1) {
          this.tableData = res.data && res.data.data && res.data.data.records
          this.counts = Number(res.data.data.total)
        }
      })
      .catch(err => {
        this.$message.error('请求失败：' + err.message)
      })
  }

  private addDishtype(st: string) {
    if (st === 'add') {
      this.$router.push({ path: '/dish/add' })
    } else {
      this.$router.push({ path: '/dish/add', query: { id: st } })
    }
  }

  private deleteHandle(type: string, id: any) {
    if (type === 'batch' && id === null) {
      if (this.checkList.length === 0) {
        return this.$message.error('请选择需要删除的商品')
      }
    }
    this.$confirm('确认删除该商品吗？', '提示', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      deleteDish(type === 'batch' ? this.checkList.join(',') : id)
        .then(res => {
          if (res && res.data && res.data.code === 1) {
            this.$message.success('删除成功')
            this.init()
          } else {
            this.$message.error(res.data.msg)
          }
        })
        .catch(err => {
          this.$message.error('请求失败：' + err.message)
        })
    })
  }

  private statusHandle(row: any) {
    const params: any = {
      id: row.id,
      status: row.status ? '0' : '1'
    }

    this.$confirm('确认修改商品售卖状态吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      dishStatusByStatus(params)
        .then(res => {
          if (res && res.data && res.data.code === 1) {
            this.$message.success('状态更新成功')
            this.init()
          } else {
            this.$message.error(res.data.msg)
          }
        })
        .catch(err => {
          this.$message.error('请求失败：' + err.message)
        })
    })
  }

  private getDishCategoryList() {
    dishCategoryList({
      type: 1
    })
      .then(res => {
        if (res && res.data && res.data.code === 1) {
          this.dishCategoryList = (
            res.data &&
            res.data.data &&
            res.data.data
          ).map(item => {
            return { value: item.id, label: item.name }
          })
        }
      })
      .catch(() => {})
  }

  private handleSelectionChange(val: any) {
    const checkArr: any[] = []
    val.forEach((n: any) => {
      checkArr.push(n.id)
    })
    this.checkList = checkArr
  }

  private handleSizeChange(val: any) {
    this.pageSize = val
    this.init()
  }

  private handleCurrentChange(val: any) {
    this.page = val
    this.init()
  }
}
</script>
<style lang="scss">
.el-table-column--selection .cell {
  padding-left: 10px;
}
</style>
<style lang="scss" scoped>
.dashboard {
  &-container {
    margin: 30px;
    .container {
      background: #fff;
      position: relative;
      z-index: 1;
      padding: 30px 28px;
      border-radius: 4px;
      .normal-btn {
        background: #333333;
        color: white;
        margin-left: 20px;
      }
      .tableBar {
        margin-bottom: 20px;

        .tableLab {
          display: inline-block;
          float: right;
          span {
            cursor: pointer;
            display: inline-block;
            font-size: 14px;
            padding: 0 20px;
            color: $gray-2;
          }
        }
      }
      .tableBox {
        width: 100%;
        border: 1px solid $gray-5;
        border-bottom: 0;
      }
      .pageList {
        text-align: center;
        margin-top: 30px;
      }
    }
  }
}
</style>
