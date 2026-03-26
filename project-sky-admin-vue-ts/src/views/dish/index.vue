<template>
  <div class="dashboard-container">
    <div class="container">
      <div class="tableBar">
        <label style="margin-right: 10px">Dish Name:</label>
        <el-input v-model="input"
                  placeholder="Please enter dish name"
                  style="width: 14%"
                  clearable
                  @clear="init"
                  @keyup.enter.native="initFun" />

        <label style="margin-right: 10px; margin-left: 20px">Category:</label>
        <el-select v-model="categoryId"
                   style="width: 14%"
                   placeholder="Please select"
                   clearable
                   @clear="init">
          <el-option v-for="item in dishCategoryList"
                     :key="item.value"
                     :label="item.label"
                     :value="item.value" />
        </el-select>

        <label style="margin-right: 10px; margin-left: 20px">Status:</label>
        <el-select v-model="dishStatus"
                   style="width: 14%"
                   placeholder="Please select"
                   clearable
                   @clear="init">
          <el-option v-for="item in saleStatus"
                     :key="item.value"
                     :label="item.label"
                     :value="item.value" />
        </el-select>
        <el-button class="normal-btn continue"
                   @click="init(true)">
          Search
        </el-button>

        <div class="tableLab">
          <span class="delBut non"
                @click="deleteHandle('batch', null)">Batch Delete</span>
          <el-button type="primary"
                     style="margin-left: 15px"
                     @click="addDishtype('add')">
            + New Dish
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
                         label="Dish Name" />
        <el-table-column prop="image"
                         label="Image">
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
                         label="Category" />
        <el-table-column label="Price">
          <template slot-scope="scope">
            <span style="margin-right: 10px">￥{{ (scope.row.price).toFixed(2) * 100 / 100 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Sale Status">
          <template slot-scope="scope">
            <div class="tableColumn-status"
                 :class="{ 'stop-use': String(scope.row.status) === '0' }">
              {{ String(scope.row.status) === '0' ? 'Disabled' : 'Enabled' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime"
                         label="Last Update" />
        <el-table-column label="Actions"
                         width="250"
                         align="center">
          <template slot-scope="scope">
            <el-button type="text"
                       size="small"
                       class="blueBug"
                       @click="addDishtype(scope.row.id)">
              Edit
            </el-button>
            <el-button type="text"
                       size="small"
                       class="delBut"
                       @click="deleteHandle('single', scope.row.id)">
              Delete
            </el-button>
            <el-button type="text"
                       size="small"
                       class="blueBug non"
                       :class="{
                         blueBug: scope.row.status == '0',
                         delBut: scope.row.status != '0'
                       }"
                       @click="statusHandle(scope.row)">
              {{ scope.row.status == '0' ? 'Enable' : 'Disable' }}
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
import { baseUrl } from '@/config.json'

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
      label: 'Disabled'
    },
    {
      value: 1,
      label: 'Enabled'
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
        this.$message.error('Request failed: ' + err.message)
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
        return this.$message.error('Please select records to delete')
      }
    }
    this.$confirm('Confirm delete this dish?', 'Confirm', {
      confirmButtonText: 'Delete',
      cancelButtonText: 'Cancel',
      type: 'warning'
    }).then(() => {
      deleteDish(type === 'batch' ? this.checkList.join(',') : id)
        .then(res => {
          if (res && res.data && res.data.code === 1) {
            this.$message.success('Delete success')
            this.init()
          } else {
            this.$message.error(res.data.msg)
          }
        })
        .catch(err => {
          this.$message.error('Request failed: ' + err.message)
        })
    })
  }

  private statusHandle(row: any) {
    const params: any = {
      id: row.id,
      status: row.status ? '0' : '1'
    }

    this.$confirm('Confirm change dish status?', 'Confirm', {
      confirmButtonText: 'OK',
      cancelButtonText: 'Cancel',
      type: 'warning'
    }).then(() => {
      dishStatusByStatus(params)
        .then(res => {
          if (res && res.data && res.data.code === 1) {
            this.$message.success('Status updated')
            this.init()
          } else {
            this.$message.error(res.data.msg)
          }
        })
        .catch(err => {
          this.$message.error('Request failed: ' + err.message)
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