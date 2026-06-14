<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="220px" class="sidebar">
      <div class="sidebar-logo">
        <span class="logo-text">校园报修平台</span>
      </div>
      <el-menu :default-active="$route.path" router background-color="#001529"
        text-color="#ffffffa6" active-text-color="#ffffff">
        <!-- 管理员菜单 -->
        <template v-if="authStore.isAdmin">
          <el-menu-item index="/dashboard">
            <el-icon><DataAnalysis /></el-icon><span>数据概览</span>
          </el-menu-item>
          <el-menu-item index="/admin/orders">
            <el-icon><List /></el-icon><span>工单管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon><span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/notices">
            <el-icon><Bell /></el-icon><span>公告管理</span>
          </el-menu-item>
        </template>
        <!-- 学生菜单 -->
        <template v-if="authStore.isStudent">
          <el-menu-item index="/student/submit">
            <el-icon><EditPen /></el-icon><span>提交报修</span>
          </el-menu-item>
          <el-menu-item index="/student/orders">
            <el-icon><Tickets /></el-icon><span>我的报修</span>
          </el-menu-item>
        </template>
        <!-- 维修工菜单 -->
        <template v-if="authStore.isWorker">
          <el-menu-item index="/worker/orders">
            <el-icon><Tools /></el-icon><span>维修任务</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-badge :value="notifyCount" :hidden="!notifyCount" class="notify-badge">
            <el-icon size="20" style="cursor:pointer"><Bell /></el-icon>
          </el-badge>
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar size="small" :src="authStore.user?.avatar">
                {{ authStore.user?.realName?.charAt(0) }}
              </el-avatar>
              <span class="username">{{ authStore.user?.realName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/store/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const notifyCount = ref(0)
let ws: WebSocket | null = null

const PAGE_TITLES: Record<string, string> = {
  '/dashboard': '数据概览',
  '/admin/orders': '工单管理',
  '/admin/users': '用户管理',
  '/admin/notices': '公告管理',
  '/student/submit': '提交报修',
  '/student/orders': '我的报修',
  '/worker/orders': '维修任务',
}
const currentPageTitle = computed(() => PAGE_TITLES[route.path] || '')

function initWebSocket() {
  const userId = authStore.user?.id
  const token = authStore.token
  if (!userId || !token) return
  const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:'
  ws = new WebSocket(`${protocol}//${location.host}/ws/notify/${userId}?token=${token}`)
  ws.onmessage = (e) => {
    try {
      const data = JSON.parse(e.data)
      if (data.type && data.message) {
        ElMessage.info(data.message)
        notifyCount.value++
      }
    } catch {}
  }
  ws.onerror = () => { /* 静默失败，不影响主功能 */ }
}

async function handleCommand(cmd: string) {
  if (cmd === 'logout') {
    await ElMessageBox.confirm('确认退出登录？', '提示', { type: 'warning' })
    authStore.logout()
    router.push('/login')
  }
}

onMounted(initWebSocket)
onUnmounted(() => ws?.close())
</script>

<style scoped>
.layout-container { height: 100vh; }
.sidebar { background: #001529; overflow-y: auto; }
.sidebar-logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #ffffff1a;
}
.logo-text { color: #fff; font-size: 16px; font-weight: 600; }
.header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
}
.header-right { display: flex; align-items: center; gap: 20px; }
.notify-badge { cursor: pointer; }
.user-info { display: flex; align-items: center; gap: 8px; cursor: pointer; color: #303133; }
.username { font-size: 14px; }
.main-content { background: #f5f7fa; padding: 24px; }
</style>
