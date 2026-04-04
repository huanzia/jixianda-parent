<template>
  <div class="addBrand-container">
    <HeadLable :title="title" :goback="true" />
    <div class="container">
      <el-form
        ref="ruleForm"
        :model="ruleForm"
        :rules="rules"
        :inline="false"
        label-width="180px"
        class="demo-ruleForm"
      >
        <el-form-item label="账号:" prop="username">
          <el-input v-model="ruleForm.username" placeholder="请输入账号" maxlength="20" />
        </el-form-item>

        <el-form-item label="员工姓名:" prop="name">
          <el-input v-model="ruleForm.name" placeholder="请输入员工姓名" maxlength="12" />
        </el-form-item>

        <el-form-item label="手机号:" prop="phone">
          <el-input v-model="ruleForm.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>

        <el-form-item label="性别:" prop="sex">
          <el-radio-group v-model="ruleForm.sex">
            <el-radio label="1">男</el-radio>
            <el-radio label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="身份证号:" prop="idNumber" class="idNumber">
          <el-input v-model="ruleForm.idNumber" placeholder="请输入身份证号" maxlength="18" />
        </el-form-item>

        <el-form-item label="归属仓库:" prop="warehouseId">
          <el-select v-model="ruleForm.warehouseId" clearable placeholder="请选择归属仓库" style="width: 293px">
            <el-option label="总部(无)" :value="null" />
            <el-option v-for="item in warehouseOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>

        <div class="subBox address">
          <el-button @click="() => $router.push('/employee')">取消</el-button>
          <el-button type="primary" :class="{ continue: actionType === 'add' }" @click="submitForm('ruleForm', false)">
            保存
          </el-button>
          <el-button v-if="actionType === 'add'" type="primary" @click="submitForm('ruleForm', true)">
            保存并继续添加
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import HeadLable from '@/components/HeadLable/index.vue'
import { queryEmployeeById, addEmployee, editEmployee } from '@/api/employee'
import { getWarehouseList } from '@/api/warehouse'

@Component({
  name: 'AddEmployee',
  components: { HeadLable }
})
export default class extends Vue {
  private title = '新增员工'
  private actionType = ''
  private warehouseOptions: any[] = []

  private ruleForm: any = {
    username: '',
    name: '',
    phone: '',
    sex: '1',
    idNumber: '',
    warehouseId: null
  }

  get rules() {
    const idCardValidator = (_rule: any, value: string, callback: any) => {
      const idCard = (value || '').trim()
      if (!idCard) {
        callback(new Error('身份证号格式不正确，请输入 18 位身份证号'))
        return
      }
      if (!/^\d{17}(\d|X|x)$/.test(idCard)) {
        callback(new Error('身份证号格式不正确，请输入 18 位身份证号'))
        return
      }
      callback()
    }

    return {
      name: [{ required: true, message: '请输入员工姓名', trigger: 'blur' }],
      username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
      phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
      idNumber: [{ validator: idCardValidator, trigger: 'blur' }]
    }
  }

  async created() {
    await this.loadWarehouseOptions()
    this.actionType = this.$route.query.id ? 'edit' : 'add'
    if (this.$route.query.id) {
      this.title = '编辑员工'
      this.init()
    }
  }

  private async loadWarehouseOptions() {
    const res = await getWarehouseList({ status: 1 })
    if (res.data.code === 1) {
      this.warehouseOptions = res.data.data || []
    }
  }

  private async init() {
    const id = this.$route.query.id
    const res = await queryEmployeeById(id)
    if (res.data.code === 1) {
      const data = res.data.data || {}
      this.ruleForm = {
        ...this.ruleForm,
        ...data,
        sex: String(data.sex),
        warehouseId: data.warehouseId == null ? null : data.warehouseId
      }
    } else {
      this.$message.error(res.data.msg || '获取员工信息失败')
    }
  }

  private submitForm(formName: string, stay: boolean) {
    ;(this.$refs[formName] as any).validate(async (valid: boolean) => {
      if (!valid) return
      const params = {
        ...this.ruleForm,
        warehouseId: this.ruleForm.warehouseId || null
      }
      const req = this.actionType === 'add' ? addEmployee(params) : editEmployee(params)
      const res = await req
      if (res.data.code === 1) {
        this.$message.success(this.actionType === 'add' ? '新增员工成功' : '编辑员工成功')
        if (this.actionType === 'add' && stay) {
          this.ruleForm = { username: '', name: '', phone: '', sex: '1', idNumber: '', warehouseId: null }
        } else {
          this.$router.push({ path: '/employee' })
        }
      } else {
        this.$message.error(res.data.msg || '保存失败')
      }
    })
  }
}
</script>

<style lang="scss" scoped>
.addBrand-container {
  margin: 30px;
  margin-top: 0;

  .container {
    background: #fff;
    padding: 30px;
    border-radius: 4px;

    .subBox {
      padding-top: 30px;
      text-align: center;
      border-top: solid 1px $gray-5;
    }
  }

  .idNumber {
    margin-bottom: 24px;
  }

  .el-form-item {
    margin-bottom: 24px;
  }

  .el-input {
    width: 293px;
  }
}
</style>
