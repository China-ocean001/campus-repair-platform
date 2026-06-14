<template>
  <div>
    <div class="page-header">
      <h2>数据概览</h2>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom:20px">
      <el-col :span="6" v-for="card in statCards" :key="card.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ background: card.color }">
              <el-icon size="28" color="#fff"><component :is="card.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ card.value }}</div>
              <div class="stat-label">{{ card.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span>工单状态分布</span></template>
          <div ref="pieChartRef" style="height:280px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span>近期工单趋势</span></template>
          <div ref="lineChartRef" style="height:280px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import * as echarts from 'echarts'
import { statisticsApi } from '@/api'

const stats = ref<any>({})
const pieChartRef = ref<HTMLElement>()
const lineChartRef = ref<HTMLElement>()

const statCards = computed(() => [
  { label: '总工单数', value: stats.value.totalOrders ?? 0, icon: 'Document', color: '#1890ff' },
  { label: '待处理', value: stats.value.pendingOrders ?? 0, icon: 'Clock', color: '#faad14' },
  { label: '处理中', value: stats.value.processingOrders ?? 0, icon: 'Loading', color: '#722ed1' },
  { label: '已完成', value: stats.value.finishedOrders ?? 0, icon: 'CircleCheck', color: '#52c41a' },
])

async function loadStats() {
  const res = await statisticsApi.overview()
  stats.value = res.data
  initPieChart()
  initLineChart()
}

function initPieChart() {
  if (!pieChartRef.value) return
  const chart = echarts.init(pieChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { name: '待派单', value: stats.value.pendingOrders },
        { name: '处理中', value: stats.value.processingOrders },
        { name: '已完成', value: stats.value.finishedOrders },
        { name: '已取消', value: stats.value.cancelledOrders },
      ],
    }],
  })
}

function initLineChart() {
  if (!lineChartRef.value) return
  const chart = echarts.init(lineChartRef.value)
  const days = Array.from({ length: 7 }, (_, i) => {
    const d = new Date()
    d.setDate(d.getDate() - (6 - i))
    return `${d.getMonth() + 1}/${d.getDate()}`
  })
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: days },
    yAxis: { type: 'value' },
    series: [{ name: '工单数', type: 'line', smooth: true,
      data: [3, 5, 2, 8, 6, 4, stats.value.todayOrders ?? 0],
      areaStyle: { opacity: 0.3 },
    }],
  })
}

onMounted(loadStats)
</script>

<style scoped>
.stat-card { border-radius: 8px; }
.stat-content { display: flex; align-items: center; gap: 16px; }
.stat-icon { width: 56px; height: 56px; border-radius: 12px; display: flex; align-items: center; justify-content: center; }
.stat-value { font-size: 28px; font-weight: 700; line-height: 1; margin-bottom: 4px; }
.stat-label { color: #8c8c8c; font-size: 13px; }
</style>
