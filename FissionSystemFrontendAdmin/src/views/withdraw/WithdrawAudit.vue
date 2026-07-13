<template>
  <div class="withdraw-audit-container">
    <el-card>
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="AppId" v-if="role === '1'">
          <el-select v-model="searchForm.appId" placeholder="选择App" clearable @change="loadData" style="width: 200px">
            <el-option
              v-for="app in appList"
              :key="app.appId"
              :label="app.appName + ' (' + app.appId + ')'"
              :value="app.appId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable @change="loadData" style="width: 200px">
            <el-option label="待审核" :value="0" />
            <el-option label="审核通过" :value="1" />
            <el-option label="打款成功" :value="2" />
            <el-option label="已驳回" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="withdrawNo" label="提现单号" width="180" />
        <el-table-column prop="appId" label="所属应用" width="150">
          <template #default="scope">
            {{ getAppName(scope.row.appId) }}
          </template>
        </el-table-column>
        <el-table-column prop="userId" label="申请人ID" width="100" />
        <el-table-column prop="pointsConsumed" label="消耗积分" width="100" align="center" />
        <el-table-column prop="amount" label="提现金额" width="100" align="center">
          <template #default="scope">
            <span style="color: #F56C6C; font-weight: bold;">¥{{ scope.row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="收款账号信息" min-width="180">
          <template #default="scope">
            <div style="font-size: 12px; line-height: 1.4;">
              <div>类型: <el-tag size="small">{{ scope.row.payType === 1 ? '支付宝' : '微信' }}</el-tag></div>
              <div>姓名: {{ scope.row.accountName }}</div>
              <div>账号: {{ scope.row.accountNo }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 0" type="warning">待审核</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="success">审核通过</el-tag>
            <el-tag v-else-if="scope.row.status === 2" type="success">打款成功</el-tag>
            <el-tag v-else-if="scope.row.status === 3" type="danger">已驳回</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="160" />
        
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button 
              v-if="scope.row.status === 0" 
              type="success" size="small" 
              @click="handleAudit(scope.row, 1)">通过</el-button>
            <el-button 
              v-if="scope.row.status === 0" 
              type="danger" size="small" 
              @click="handleAudit(scope.row, 3)">驳回</el-button>
          </template>
        </el-table-column>
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

    <!-- 驳回弹窗 -->
    <el-dialog v-model="rejectDialogVisible" title="填写驳回原因" width="30%">
      <el-input v-model="rejectReason" type="textarea" rows="3" placeholder="请输入驳回原因 (会通知用户且退还积分)" />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rejectDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmReject">确认驳回</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../../utils/request'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

const searchForm = ref({
  appId: '',
  status: undefined
})

const rejectDialogVisible = ref(false)
const rejectReason = ref('')
const currentWithdrawId = ref<number | null>(null)
const role = localStorage.getItem('admin_role')
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
  const app = appList.value.find(a => a.appId === appId)
  return app ? app.appName : (appId || '未知应用')
}

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/withdraw/list', {
      params: {
        page: page.value,
        size: size.value,
        appId: searchForm.value.appId || undefined,
        status: searchForm.value.status
      }
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

const handleAudit = (row: any, action: number) => {
  if (action === 1) {
    ElMessageBox.confirm(`确认要通过这笔 ¥${row.amount} 的提现申请吗？将会在系统中扣除相应的积分。`, '审核确认', {
      confirmButtonText: '通过并提交打款',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      submitAudit(row.id, 1, '')
    }).catch(() => {})
  } else {
    currentWithdrawId.value = row.id
    rejectReason.value = ''
    rejectDialogVisible.value = true
  }
}

const confirmReject = () => {
  if (!rejectReason.value) {
    ElMessage.warning('请输入驳回原因')
    return
  }
  submitAudit(currentWithdrawId.value!, 3, rejectReason.value)
  rejectDialogVisible.value = false
}

const submitAudit = async (withdrawId: number, action: number, rejectReasonStr: string) => {
  try {
    const res: any = await request.post('/withdraw/audit', null, {
      params: {
        withdrawId,
        action,
        rejectReason: rejectReasonStr,
        adminId: 1 // mock
      }
    })
    if (res.code === 200) {
      ElMessage.success('操作成功')
      loadData()
    }
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadApps()
  loadData()
})
</script>

<style scoped>
</style>
