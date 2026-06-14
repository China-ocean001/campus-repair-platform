<template>
  <div>
    <div class="page-header"><h2>提交报修</h2></div>
    <el-card shadow="never" style="max-width:680px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" size="large">
        <el-form-item label="报修类别" prop="category">
          <el-select v-model="form.category" placeholder="请选择报修类别" style="width:100%">
            <el-option v-for="c in CATEGORIES" :key="c.value" :label="c.label" :value="c.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="报修地点" prop="location">
          <el-input v-model="form.location" placeholder="如：3号宿舍楼302室" />
        </el-form-item>
        <el-form-item label="故障描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4"
            placeholder="请详细描述故障现象，便于维修工快速定位问题" />
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-radio-group v-model="form.priority">
            <el-radio :value="1">普通</el-radio>
            <el-radio :value="2">紧急</el-radio>
            <el-radio :value="3">非常紧急</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleSubmit">
            提交报修
          </el-button>
          <el-button size="large" @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { orderApi } from '@/api'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const CATEGORIES = [
  { label: '水电维修', value: 1 }, { label: '网络故障', value: 2 },
  { label: '门窗维修', value: 3 }, { label: '电器故障', value: 4 }, { label: '其他', value: 5 },
]

const form = reactive({ category: null as any, location: '', description: '', priority: 1 })
const rules = {
  category: [{ required: true, message: '请选择报修类别', trigger: 'change' }],
  location: [{ required: true, message: '请填写报修地点', trigger: 'blur' }],
  description: [{ required: true, message: '请描述故障情况', trigger: 'blur' },
    { min: 10, message: '描述至少10个字符', trigger: 'blur' }],
}

async function handleSubmit() {
  await formRef.value?.validate()
  loading.value = true
  try {
    await orderApi.create(form)
    ElMessage.success('报修提交成功，请等待管理员派单')
    router.push('/student/orders')
  } finally {
    loading.value = false
  }
}

function resetForm() {
  formRef.value?.resetFields()
}
</script>
