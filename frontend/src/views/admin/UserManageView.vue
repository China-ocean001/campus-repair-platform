<template>
  <div>
    <div class="page-header">
      <h2>用户管理</h2>
      <el-button type="primary" @click="openCreate">新增用户</el-button>
    </div>
    <el-card shadow="never" style="margin-bottom:16px">
      <el-form inline>
        <el-form-item label="角色">
          <el-select v-model="query.role" clearable placeholder="全部" style="width:120px">
            <el-option label="学生" :value="0" />
            <el-option label="维修工" :value="1" />
            <el-option label="管理员" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadUsers">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never">
      <el-table :data="users" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="roleName" label="角色" width="90" />
        <el-table-column prop="department" label="院系/部门" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button size="small" :type="row.status === 1 ? 'danger' : 'success'"
              @click="toggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" layout="total,prev,pager,next"
        :total="total" :page-size="query.size" v-model:current-page="query.page" @current-change="loadUsers" />
    </el-card>

    <!-- 新增用户弹窗 -->
    <el-dialog v-model="createVisible" title="新增用户" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="学号/工号" />
        </el-form-item>
        <el-form-item label="初始密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="至少6位" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" style="width:100%">
            <el-option label="学生" :value="0" />
            <el-option label="维修工" :value="1" />
            <el-option label="管理员" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="院系/部门">
          <el-input v-model="form.department" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitCreate">确认新增</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { userApi } from '@/api'

const query = reactive({ role: null as any, page: 1, size: 10 })
const users = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const createVisible = ref(false)
const submitting = ref(false)
const formRef = ref()
const form = reactive({
  username: '', password: '', realName: '', phone: '', role: null as any, department: '',
})
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
}

async function loadUsers() {
  loading.value = true
  try {
    const res = await userApi.list(query)
    users.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function toggleStatus(row: any) {
  const newStatus = row.status === 1 ? 0 : 1
  await userApi.updateStatus(row.id, newStatus)
  ElMessage.success('操作成功')
  row.status = newStatus
}

function openCreate() {
  Object.assign(form, { username: '', password: '', realName: '', phone: '', role: null, department: '' })
  createVisible.value = true
}

async function submitCreate() {
  await formRef.value?.validate()
  submitting.value = true
  try {
    await userApi.create(form)
    ElMessage.success('新增成功')
    createVisible.value = false
    loadUsers()
  } finally {
    submitting.value = false
  }
}

onMounted(loadUsers)
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.page-header h2 { font-size: 20px; color: #303133; }
</style>
