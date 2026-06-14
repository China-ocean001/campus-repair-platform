<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <img src="/vite.svg" alt="logo" class="logo" />
        <h1>校园智能报修服务平台</h1>
        <p>Campus Intelligent Repair Service Platform</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" size="large" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入学工号" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码"
            prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="demo-hint">
        <el-text type="info" size="small">
          演示账号：admin/123456（管理员）&nbsp;&nbsp;worker01/123456（维修工）&nbsp;&nbsp;student01/123456（学生）
        </el-text>
      </div>

      <!-- 公告区域 -->
      <div v-if="notices.length" class="notice-section">
        <div class="notice-title"><el-icon><Bell /></el-icon> 系统公告</div>
        <el-scrollbar max-height="120px">
          <div v-for="n in notices" :key="n.id" class="notice-item">
            <span class="notice-name">{{ n.title }}</span>
            <span class="notice-content">{{ n.content }}</span>
          </div>
        </el-scrollbar>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi, noticeApi } from '@/api'
import { useAuthStore } from '@/store/auth'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)
const notices = ref<any[]>([])

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入学工号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
  await formRef.value?.validate()
  loading.value = true
  try {
    const res = await authApi.login(form)
    authStore.setAuth(res.data.token, res.data.user)
    ElMessage.success('登录成功')
    router.push('/')
  } finally {
    loading.value = false
  }
}

async function loadNotices() {
  try {
    const res = await noticeApi.list()
    notices.value = res.data.slice(0, 5)
  } catch {}
}

onMounted(loadNotices)
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
}
.login-box {
  background: #fff;
  border-radius: 16px;
  padding: 48px 40px;
  width: 420px;
  box-shadow: 0 20px 60px rgba(0,0,0,.15);
}
.login-header {
  text-align: center;
  margin-bottom: 32px;
}
.logo { width: 56px; height: 56px; margin-bottom: 12px; }
.login-header h1 { font-size: 22px; color: #1890ff; margin-bottom: 6px; }
.login-header p { color: #8c8c8c; font-size: 13px; }
.login-btn { width: 100%; }
.demo-hint { margin-top: 16px; text-align: center; }
.notice-section {
  margin-top: 20px;
  border-top: 1px solid #f0f0f0;
  padding-top: 14px;
}
.notice-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: #1890ff;
  margin-bottom: 8px;
}
.notice-item {
  padding: 6px 0;
  border-bottom: 1px dashed #f0f0f0;
  font-size: 12px;
}
.notice-name {
  font-weight: 600;
  color: #303133;
  margin-right: 8px;
}
.notice-content { color: #8c8c8c; }
</style>
