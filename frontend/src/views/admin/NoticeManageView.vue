<template>
  <div>
    <div class="page-header">
      <h2>公告管理</h2>
      <el-button type="primary" @click="openCreate">发布公告</el-button>
    </div>

    <el-card shadow="never">
      <el-table :data="notices" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '已发布' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="160" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'"
              @click="toggleStatus(row)">
              {{ row.status === 1 ? '下架' : '发布' }}
            </el-button>
            <el-button size="small" type="danger" @click="removeNotice(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新建公告弹窗 -->
    <el-dialog v-model="createVisible" title="发布公告" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入公告内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitCreate">确认发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { noticeApi } from '@/api'

const notices = ref<any[]>([])
const loading = ref(false)
const createVisible = ref(false)
const submitting = ref(false)
const formRef = ref()
const form = reactive({ title: '', content: '' })
const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
}

async function loadNotices() {
  loading.value = true
  try {
    const res = await noticeApi.listAll()
    notices.value = res.data
  } finally {
    loading.value = false
  }
}

function openCreate() {
  form.title = ''
  form.content = ''
  createVisible.value = true
}

async function submitCreate() {
  await formRef.value?.validate()
  submitting.value = true
  try {
    await noticeApi.create(form)
    ElMessage.success('公告发布成功')
    createVisible.value = false
    loadNotices()
  } finally {
    submitting.value = false
  }
}

async function toggleStatus(row: any) {
  await noticeApi.toggle(row.id)
  ElMessage.success('操作成功')
  loadNotices()
}

async function removeNotice(row: any) {
  await ElMessageBox.confirm('确认删除该公告？', '提示', { type: 'warning' })
  await noticeApi.remove(row.id)
  ElMessage.success('删除成功')
  loadNotices()
}

onMounted(loadNotices)
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
