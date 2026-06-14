<template>
  <div>
    <div class="page-header"><h2>维修任务</h2></div>

    <!-- 统计行 -->
    <el-row :gutter="16" style="margin-bottom:20px">
      <el-col :span="6" v-for="s in summaryCards" :key="s.label">
        <el-card shadow="hover" class="mini-card">
          <div class="mini-stat">
            <span class="mini-val">{{ s.value }}</span>
            <span class="mini-label">{{ s.label }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-bottom:16px">
      <el-form inline>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部" style="width:130px">
            <el-option label="已派单(待接)" :value="1" />
            <el-option label="处理中" :value="2" />
            <el-option label="已完成" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadOrders">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <el-table :data="orders" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="工单编号" width="180" />
        <el-table-column prop="studentName" label="报修人" width="100" />
        <el-table-column prop="location" label="报修地点" />
        <el-table-column prop="categoryName" label="类别" width="100" />
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
        <el-table-column prop="createTime" label="报修时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewDetail(row)">详情</el-button>
            <el-button v-if="row.status === 1" size="small" type="primary" @click="acceptOrder(row)">接单</el-button>
            <el-button v-if="row.status === 2" size="small" type="success" @click="openFinish(row)">完成</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" layout="total,prev,pager,next"
        :total="total" v-model:current-page="query.page" :page-size="query.size" @current-change="loadOrders" />
    </el-card>

    <!-- 完成备注弹窗 -->
    <el-dialog v-model="finishVisible" title="填写维修结果" width="420px">
      <el-form :model="finishForm" label-width="80px">
        <el-form-item label="维修备注">
          <el-input v-model="finishForm.remark" type="textarea" :rows="4" placeholder="请描述维修情况和结果" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="finishVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitFinish">提交完成</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="工单详情" width="560px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="工单号" :span="2">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="报修人">{{ currentOrder.studentName }}</el-descriptions-item>
        <el-descriptions-item label="报修地点">{{ currentOrder.location }}</el-descriptions-item>
        <el-descriptions-item label="故障描述" :span="2">{{ currentOrder.description }}</el-descriptions-item>
        <el-descriptions-item label="类别">{{ currentOrder.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="优先级">{{ currentOrder.priorityName }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { orderApi } from '@/api'

const STATUS_TAG: any = { 1: '', 2: 'warning', 3: 'success', 4: 'success' }
const PRIORITY_TAG: any = { 1: '', 2: 'warning', 3: 'danger' }

const query = reactive({ status: null as any, page: 1, size: 10 })
const orders = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const finishVisible = ref(false)
const detailVisible = ref(false)
const submitting = ref(false)
const currentOrder = ref<any>(null)
const finishForm = reactive({ orderId: 0, remark: '' })

// 服务端分页统计：分别请求各状态总数
const pendingCount = ref(0)
const processingCount = ref(0)
const finishedCount = ref(0)

const summaryCards = computed(() => [
  { label: '待接单', value: pendingCount.value },
  { label: '处理中', value: processingCount.value },
  { label: '今日完成', value: finishedCount.value },
  { label: '总任务', value: total.value },
])

async function loadOrders() {
  loading.value = true
  try {
    const res = await orderApi.workerOrders(query)
    orders.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function loadSummary() {
  const [r1, r2, r3] = await Promise.all([
    orderApi.workerOrders({ status: 1, page: 1, size: 1 }),
    orderApi.workerOrders({ status: 2, page: 1, size: 1 }),
    orderApi.workerOrders({ status: 3, page: 1, size: 1 }),
  ])
  pendingCount.value = r1.data.total
  processingCount.value = r2.data.total
  finishedCount.value = r3.data.total
}

async function acceptOrder(row: any) {
  await orderApi.accept(row.id)
  ElMessage.success('接单成功，请及时处理')
  loadOrders()
}

function openFinish(row: any) {
  finishForm.orderId = row.id
  finishForm.remark = ''
  finishVisible.value = true
}

async function submitFinish() {
  submitting.value = true
  try {
    await orderApi.finish(finishForm.orderId, finishForm.remark)
    ElMessage.success('已标记为完成，等待学生评价')
    finishVisible.value = false
    loadOrders()
  } finally {
    submitting.value = false
  }
}

function viewDetail(row: any) {
  currentOrder.value = row
  detailVisible.value = true
}

onMounted(() => { loadOrders(); loadSummary() })
</script>

<style scoped>
.mini-card { border-radius: 8px; }
.mini-stat { text-align: center; }
.mini-val { display: block; font-size: 28px; font-weight: 700; color: #1890ff; }
.mini-label { font-size: 13px; color: #8c8c8c; }
</style>
