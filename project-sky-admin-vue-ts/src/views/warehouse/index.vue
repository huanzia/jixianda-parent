<template>
  <div class="dashboard-container warehouse-page">
    <el-card shadow="never" class="warehouse-card">
      <div class="toolbar">
        <div class="search-area">
          <label>仓库名称：</label>
          <el-input
            v-model="searchName"
            clearable
            placeholder="请输入仓库名称"
            style="width: 220px"
            @keyup.enter.native="init(true)"
            @clear="init"
          />
          <el-button class="query-btn" @click="init(true)">查询</el-button>
        </div>
        <el-button type="primary" class="add-btn" @click="openDialog()">
          + 新增仓库
        </el-button>
      </div>

      <el-table v-if="tableData.length" :data="tableData" stripe class="tableBox">
        <el-table-column prop="name" label="仓库名称" min-width="140" />
        <el-table-column prop="address" label="地址" min-width="220" />
        <el-table-column prop="location" label="经纬度" min-width="150" />
        <el-table-column prop="contactName" label="负责人" min-width="110">
          <template slot-scope="{ row }">
            <span>{{ row.contactName || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" min-width="140">
          <template slot-scope="{ row }">
            <span>{{ row.phone || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template slot-scope="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              active-color="#07C160"
              @change="changeStatus(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template slot-scope="{ row }">
            <el-button type="text" class="blueBug" @click="openDialog(row.id)">修改</el-button>
            <el-button type="text" class="delBut" @click="deleteHandle(row.id)">删除</el-button>
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

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="520px" @close="resetForm">
      <el-form ref="warehouseFormRef" :model="warehouseForm" :rules="rules" label-width="100px">
        <el-form-item label="仓库名称" prop="name">
          <el-input v-model="warehouseForm.name" maxlength="64" />
        </el-form-item>
        <el-form-item label="仓库地址" prop="address">
          <el-input v-model="warehouseForm.address" maxlength="255" />
        </el-form-item>
        <el-form-item label="经纬度" prop="location">
          <el-input v-model="warehouseForm.location" placeholder="例如: 31.2304,121.4737" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="warehouseForm.contactName" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="warehouseForm.phone" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="warehouseForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import Empty from '@/components/Empty/index.vue'
import {
  addWarehouse,
  deleteWarehouse,
  getWarehouseById,
  getWarehousePage,
  updateWarehouse,
  updateWarehouseStatus
} from '@/api/warehouse'

@Component({
  name: 'Warehouse',
  components: {
    Empty
  }
})
export default class extends Vue {
  private searchName = ''
  private tableData: any[] = []
  private counts = 0
  private page = 1
  private pageSize = 10
  private isSearch = false

  private dialogVisible = false
  private dialogTitle = '新增仓库'
  private warehouseForm: any = this.createDefaultForm()

  get rules() {
    return {
      name: [{ required: true, message: '请输入仓库名称', trigger: 'blur' }],
      address: [{ required: true, message: '请输入仓库地址', trigger: 'blur' }],
      location: [{ required: true, message: '请输入仓库经纬度', trigger: 'blur' }],
      status: [{ required: true, message: '请选择状态', trigger: 'change' }]
    }
  }

  created() {
    this.init()
  }

  private createDefaultForm() {
    return {
      id: undefined,
      name: '',
      address: '',
      location: '',
      status: 1,
      contactName: '',
      phone: ''
    }
  }

  private async init(isSearch?: boolean) {
    this.isSearch = !!isSearch
    const res = await getWarehousePage({
      page: this.page,
      pageSize: this.pageSize,
      name: this.searchName || undefined
    })
    if (res.data.code === 1) {
      this.tableData = (res.data.data && res.data.data.records) || []
      this.counts = Number((res.data.data && res.data.data.total) || 0)
      return
    }
    this.$message.error(res.data.msg || '查询失败')
  }

  private async openDialog(id?: number) {
    if (!id) {
      this.dialogTitle = '新增仓库'
      this.warehouseForm = this.createDefaultForm()
      this.dialogVisible = true
      return
    }
    this.dialogTitle = '修改仓库'
    const res = await getWarehouseById(id)
    if (res.data.code === 1) {
      this.warehouseForm = {
        ...this.createDefaultForm(),
        ...(res.data.data || {})
      }
      this.dialogVisible = true
      return
    }
    this.$message.error(res.data.msg || '查询详情失败')
  }

  private submitForm() {
    ;(this.$refs.warehouseFormRef as any).validate(async (valid: boolean) => {
      if (!valid) return
      const payload = {
        id: this.warehouseForm.id,
        name: this.warehouseForm.name,
        address: this.warehouseForm.address,
        location: this.warehouseForm.location,
        contactName: this.warehouseForm.contactName,
        phone: this.warehouseForm.phone,
        status: this.warehouseForm.status
      }
      const req = payload.id ? updateWarehouse(payload) : addWarehouse(payload)
      const res = await req
      if (res.data.code === 1) {
        this.$message.success(payload.id ? '仓库修改成功' : '仓库新增成功')
        this.dialogVisible = false
        this.init()
        return
      }
      this.$message.error(res.data.msg || '保存失败')
    })
  }

  private async deleteHandle(id: number) {
    this.$confirm('确认删除该仓库吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      const res = await deleteWarehouse(String(id))
      if (res.data.code === 1) {
        this.$message.success('删除成功')
        this.init()
      } else {
        this.$message.error(res.data.msg || '删除失败')
      }
    })
  }

  private async changeStatus(row: any) {
    const res = await updateWarehouseStatus(row.status, row.id)
    if (res.data.code === 1) {
      this.$message.success('状态修改成功')
      return
    }
    row.status = row.status === 1 ? 0 : 1
    this.$message.error(res.data.msg || '状态修改失败')
  }

  private resetForm() {
    this.warehouseForm = this.createDefaultForm()
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
.warehouse-page {
  margin: 30px;
  .warehouse-card {
    border-radius: 10px;
  }
  .toolbar {
    margin-bottom: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .search-area {
    display: flex;
    align-items: center;
    gap: 10px;
  }
  .query-btn {
    background: #111827;
    color: #fff;
    border-radius: 18px;
    border: 0;
    padding: 9px 18px;
  }
  .add-btn {
    background: #07c160;
    border-color: #07c160;
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
