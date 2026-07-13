<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="login-title">裂变后台管理系统</h2>
      <el-form :model="loginForm" @submit.prevent="handleLogin" label-width="0">
        <el-form-item>
          <el-input 
            v-model="loginForm.username" 
            placeholder="请输入账号" 
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="请输入密码" 
            :prefix-icon="Lock"
            show-password
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            native-type="submit" 
            class="login-btn" 
            :loading="loading"
            size="large"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import request from '../../utils/request'

const router = useRouter()
const loginForm = ref({
  username: '',
  password: ''
})
const loading = ref(false)

const handleLogin = async () => {
  if (!loginForm.value.username || !loginForm.value.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  
  loading.value = true
  try {
    const res: any = await request.post('/auth/login', {
      username: loginForm.value.username,
      password: loginForm.value.password
    })
    
    if (res.code === 200) {
      ElMessage.success('登录成功')
      localStorage.setItem('admin_token', res.data.token)
      localStorage.setItem('admin_role', res.data.role.toString())
      localStorage.setItem('admin_username', res.data.username)
      router.push('/dashboard')
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #2b3643;
}
.login-card {
  width: 400px;
  padding: 20px;
  border-radius: 8px;
}
.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}
.login-btn {
  width: 100%;
}
</style>
