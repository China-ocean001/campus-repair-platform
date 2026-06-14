<template>
  <div>
    <div class="page-header">
      <h2>工单管理</h2>
    </div>

    <!-- 筛选栏 -->
    <el-card shadow="never" style="margin-bottom:16px">
      <el-form inline>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部" style="width:120px">
            <el-option v-for="s in STATUS_OPTIONS" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="类别">
          <el-select v-model="query.category" clearable placeholder="全部" style="width:120px">
            <el-option v-for="c in CATEGORY_OPTIONS" :key="c.value" :label="c.label" :value="c.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadOrders">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <el-table :data="orders" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="工单编号" width="180" />
        <el-table-column prop="studentName" label="报修学生" width="100" />
        <el-table-column prop="location" label="报修地点" />
        <el-table-column prop="categoryName" label="类别" width="90" />
        <el-table-column prop="priorityName" label="优先级" width="90">
          <template #default="{ row }">
            <el-tag :type="PRIORITY_TAG[row.priority]" size="small">{{ row.priorityName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="statusName" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="STATUS_TAG[row.status]" size="small">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewDetail(row)">详情</el-button>
            <el-button v-if="row.status === 0" size="small" type="primary" @click="openAssign(row)">派单</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" layout="total,prev,pager,next"
        :total="total" :page-size="query.size" v-model:current-page="query.page" @current-change="loadOrders" />
    </el-card>

    <!-- 派单弹窗 -->
    <el-dialog v-model="assignVisible" title="派单" width="400px">
      <el-form :model="assignForm" label-width="80px">
        <el-form-item label="维修工">
          <el-select v-model="assignForm.workerId" placeholder="请选择维修工" style="width:100%">
            <el-option v-for="w in workers" :key="w.id" :label="w.realName" :value="w.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitAssign">确认派单</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="工单详情" width="600px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="工单编号" :span="2">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="报修学生">{{ currentOrder.studentName }}</el-descriptions-item>
        <el-descriptions-item label="维修工">{{ currentOrder.workerName || '未分配' }}</el-descriptions-item>
        <el-descriptions-item label="报修地点" :span="2">{{ currentOrder.location }}</el-descriptions-item>
        <el-descriptions-item label="故障描述" :span="2">{{ currentOrder.description }}</el-descriptions-item>
        <el-descriptions-item label="报修类别">{{ currentOrder.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="优先级">{{ currentOrder.priorityName }}</el-descriptions-item>
        <el-descriptions-item label="当前状态" :span="2">{{ currentOrder.statusName }}</el-descriptions-item>
        <el-descriptions-item label="维修备注" :span="2">{{ currentOrder.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { orderApi, userApi } from '@/api'

const STATUS_OPTIONS = [
  { label: '待派单', value: 0 }, { label: '已派单', value: 1 },
  { label: '处理中', value: 2 }, { label: '已完成', value: 3 },
  { label: '已评价', value: 4 }, { label: '已取消', value: 5 },
]
const CATEGORY_OPTIONS = [
  { label: '水电维修', value: 1 }, { label: '网络故障', value: 2 },
  { label: '门窗维修', value: 3 }, { label: '电器故障', value: 4 }, { label: '其他', value: 5 },
]
const STATUS_TAG: any = { 0: 'info', 1: '', 2: 'warning', 3: 'success', 4: 'success', 5: 'danger' }
const PRIORITY_TAG: any = { 1: '', 2: 'warning', 3: 'danger' }

const query = reactive({ status: null as any, category: null as any, page: 1, size: 10 })
const orders = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const workers = ref<any[]>([])
const assignVisible = ref(false)
const detailVisible = ref(false)
const submitting = ref(false)
const currentOrder = ref<any>(null)
const assignForm = reactive({ orderId: 0, workerId: null as any })

async function loadOrders() {
  loading.value = true
  try {
    const res = await orderApi.allOrders(query)
    orders.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function loadWorkers() {
  const res = await userApi.workers()
  workers.value = res.data
}

function resetQuery() {
  query.status = null
  query.category = null
  query.page = 1
  loadOrders()
}

function openAssign(row: any) {
  assignForm.orderId = row.id
  assignForm.workerId = null
  assignVisible.value = true
}

async function submitAssign() {
  if (!assignForm.workerId) { ElMessage.warning('请选择维修工'); return }
  submitting.value = true
  try {
    await orderApi.assign(assignForm)
    ElMessage.success('派单成功')
    assignVisible.value = false
    loadOrders()
  } finally {
    submitting.value = false
  }
}

function viewDetail(row: any) {
  currentOrder.value = row
  detailVisible.value = true
}

onMounted(() => { loadOrders(); loadWorkers() })
</script>
