<template>
  <div class="admin-manage-container">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>管理员账号管理 (仅超级管理员可用)</span>
          <el-button type="primary" @click="dialogVisible = true">新增普通管理员</el-button>
        </div>
      </template>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="登录账号" width="180" />
        <el-table-column prop="role" label="角色">
          <template #default="scope">
            <el-tag v-if="scope.row.role === 1" type="danger">超级管理员</el-tag>
            <el-tag v-else type="info">普通管理员</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="appId" label="管理的应用">
          <template #default="scope">
            <span v-if="scope.row.role === 1">全部应用</span>
            <el-tag v-else type="success">{{ getAppName(scope.row.appId) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
      </el-table>

      <!-- 分页 -->
      <div style="margin-top: 20px; display: flex; justify-content: flex-end;">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next"
          :total="total"
          v-model:current-page="page"
          v-model:page-size="size"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 新增管理员弹窗 -->
    <el-dialog v-model="dialogVisible" title="新增普通管理员" width="400px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="登录账号">
          <el-input v-model="form.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="登录密码">
          <el-input v-model="form.password" type="password" placeholder="请输入初始密码" show-password />
        </el-form-item>
        <el-form-item label="管理的App">
          <el-select v-model="form.appId" placeholder="请选择要分配的App" style="width: 100%">
            <el-option
              v-for="app in appList"
              :key="app.appId"
              :label="app.appName + ' (' + app.appId + ')'"
              :value="app.appId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="onSubmit" :loading="submitLoading">确认创建</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

const dialogVisible = ref(false)
const submitLoading = ref(false)
const form = ref({ username: '', password: '', appId: '' })
const appList = ref<any[]>([])

const loadApps = async () => {
  try {
    const res: any = await request.get('/app/list')
    if (res.code === 200) {
      appList.value = res.data
    }
  } catch (error) {
    console.error('Failed to load apps', error)
  }
}

const getAppName = (appId: string) => {
  if (!appId) return '未绑定'
  const app = appList.value.find(a => a.appId === appId)
  return app ? app.appName : appId
}

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/manage/list', {
      params: { page: page.value, size: size.value }
    })
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const onSubmit = async () => {
  if (!form.value.username || !form.value.password || !form.value.appId) {
    ElMessage.warning('请完整填写账号、密码和AppId')
    return
  }
  
  submitLoading.value = true
  try {
    const res: any = await request.post('/manage/add', {
      username: form.value.username,
      password: form.value.password,
      appId: form.value.appId
    })
    if (res.code === 200) {
      ElMessage.success('创建成功')
      dialogVisible.value = false
      form.value = { username: '', password: '', appId: '' }
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
  loadApps()
})
</script>
