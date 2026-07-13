<template>
  <div class="app-manage-container">
    <el-card>
      <template #header>
        <div class="card-header" style="display: flex; justify-content: space-between; align-items: center;">
          <span>接入应用管理 (多租户配置)</span>
          <el-button type="primary" @click="dialogVisible = true">新增接入应用</el-button>
        </div>
      </template>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="appName" label="应用名称" width="180" />
        <el-table-column prop="appId" label="AppId (系统分配)" width="200">
          <template #default="scope">
            <el-tag type="info">{{ scope.row.appId }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="appSecret" label="AppSecret (鉴权秘钥)" width="280">
          <template #default="scope">
            <el-input 
              v-model="scope.row.appSecret" 
              type="password" 
              show-password 
              readonly 
              style="width: 250px" 
            />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" disabled />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="新增接入应用" width="30%">
      <el-form :model="form" label-width="80px">
        <el-form-item label="应用名称">
          <el-input v-model="form.appName" placeholder="例如: 某某小说App" />
        </el-form-item>
        <p style="color: #999; font-size: 12px; margin-left: 80px;">AppId和秘钥将在创建后自动生成。</p>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="onSubmit">确认创建</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const dialogVisible = ref(false)
const submitLoading = ref(false)
const form = ref({ appName: '' })
const loading = ref(false)

const tableData = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/app/list')
    if (res.code === 200) {
      tableData.value = res.data
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const onSubmit = async () => {
  if (!form.value.appName) {
    ElMessage.warning('请输入应用名称')
    return
  }
  
  submitLoading.value = true
  try {
    const res: any = await request.post('/app/add', {
      appName: form.value.appName
    })
    if (res.code === 200) {
      ElMessage.success('创建成功')
      dialogVisible.value = false
      form.value.appName = ''
      loadData()
    }
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
</style>
