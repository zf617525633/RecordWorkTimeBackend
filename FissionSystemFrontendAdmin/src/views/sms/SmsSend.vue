<template>
  <div class="sms-send-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>发送短信验证码</span>
        </div>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入要发送的手机号码" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit" :loading="loading">发送短信</el-button>
          <el-button @click="onReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import axios from 'axios'

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  phone: ''
})

const rules = reactive<FormRules>({
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
})

const onSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 使用原生的 axios 调用公共的短信发送接口，不需要 admin_token
        const response = await axios.post(`/api/sms/send?phone=${form.phone}`)
        if (response.data.code === 200) {
          ElMessage.success('短信发送成功！')
        } else {
          ElMessage.error(response.data.message || '短信发送失败')
        }
      } catch (error: any) {
        ElMessage.error(error.message || '网络或接口错误')
      } finally {
        loading.value = false
      }
    }
  })
}

const onReset = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
}
</script>

<style scoped>
.sms-send-container {
  padding: 20px;
}
.box-card {
  max-width: 500px;
  margin: 0 auto;
}
</style>
