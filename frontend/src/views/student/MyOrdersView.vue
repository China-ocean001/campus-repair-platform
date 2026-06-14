<template>
  <div>
    <div class="page-header"><h2>我的报修</h2></div>
    <el-card shadow="never" style="margin-bottom:16px">
      <el-form inline>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部" style="width:120px">
            <el-option v-for="s in STATUS_OPTIONS" :key="s.value" :label="s.label" :value="s.value" />
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
        <el-table-column prop="location" label="报修地点" />
        <el-table-column prop="categoryName" label="类别" width="100" />
        <el-table-column prop="statusName" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="STATUS_TAG[row.status]" size="small">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="workerName" label="维修工" width="100">
          <template #default="{ row }">{{ row.workerName || '待分配' }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="160" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewDetail(row)">详情</el-button>
            <el-button v-if="row.status === 3 && !row.hasEvaluation" size="small" type="warning"
              @click="openEvaluate(row)">评价</el-button>
            <el-button v-if="row.status === 0" size="small" type="danger" @click="cancelOrder(row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" layout="total,prev,pager,next"
        :total="total" v-model:current-page="query.page" :page-size="query.size" @current-change="loadOrders" />
    </el-card>

    <!-- 详情 -->
    <el-dialog v-model="detailVisible" title="报修详情" width="560px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="工单号" :span="2">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="报修地点" :span="2">{{ currentOrder.location }}</el-descriptions-item>
        <el-descriptions-item label="故障描述" :span="2">{{ currentOrder.description }}</el-descriptions-item>
        <el-descriptions-item label="类别">{{ currentOrder.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ currentOrder.statusName }}</el-descriptions-item>
        <el-descriptions-item label="维修工">{{ currentOrder.workerName || '待分配' }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ currentOrder.finishTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="维修备注" :span="2">{{ currentOrder.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 评价 -->
    <el-dialog v-model="evalVisible" title="评价维修服务" width="420px">
      <el-form :model="evalForm" label-width="80px">
        <el-form-item label="服务评分">
          <el-rate v-model="evalForm.score" :max="5" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="evalForm.content" type="textarea" :rows="3" placeholder="请分享您的体验" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="evalVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitEval">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '@/api'

const STATUS_OPTIONS = [
  { label: '待派单', value: 0 }, { label: '已派单', value: 1 },
  { label: '处理中', value: 2 }, { label: '已完成', value: 3 },
  { label: '已评价', value: 4 }, { label: '已取消', value: 5 },
]
const STATUS_TAG: any = { 0: 'info', 1: '', 2: 'warning', 3: 'success', 4: 'success', 5: 'danger' }

const query = reactive({ status: null as any, page: 1, size: 10 })
const orders = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const detailVisible = ref(false)
const evalVisible = ref(false)
const submitting = ref(false)
const currentOrder = ref<any>(null)
const evalForm = reactive({ orderId: 0, score: 5, content: '' })

async function loadOrders() {
  loading.value = true
  try {
    const res = await orderApi.myOrders(query)
    orders.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function viewDetail(row: any) {
  currentOrder.value = row
  detailVisible.value = true
}

function openEvaluate(row: any) {
  evalForm.orderId = row.id
  evalForm.score = 5
  evalForm.content = ''
  evalVisible.value = true
}

async function submitEval() {
  submitting.value = true
  try {
    await orderApi.evaluate(evalForm)
    ElMessage.success('评价成功')
    evalVisible.value = false
    loadOrders()
  } finally {
    submitting.value = false
  }
}

async function cancelOrder(row: any) {
  await ElMessageBox.confirm('确认取消该报修单？', '提示', { type: 'warning' })
  await orderApi.cancel(row.id)
  ElMessage.success('已取消')
  loadOrders()
}

onMounted(loadOrders)
</script>
