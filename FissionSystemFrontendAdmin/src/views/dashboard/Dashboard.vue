<template>
  <div class="dashboard-container">
    <!-- 数据概览卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <div class="card-header">总注册用户</div>
          <div class="card-value">{{ stats.totalUsers || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <div class="card-header">今日新增用户</div>
          <div class="card-value text-success">+{{ stats.todayNewUsers || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <div class="card-header">历史发放总积分</div>
          <div class="card-value text-warning">{{ stats.totalPointsIssued || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <div class="card-header">累计打款金额 (元)</div>
          <div class="card-value text-danger">¥{{ stats.totalWithdrawAmount || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>注册来源渠道占比归因</span>
            </div>
          </template>
          <div ref="pieChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import request from '../../utils/request'

const stats = ref<any>({})
const pieChartRef = ref(null)

const loadStats = async () => {
  try {
    const res: any = await request.get('/dashboard/stats')
    if (res.code === 200) {
      stats.value = res.data
      initChart(res.data.sourceChannels)
    }
  } catch (error) {
    console.error(error)
  }
}

const initChart = (sourceData: Record<string, number>) => {
  if (!pieChartRef.value || !sourceData) return
  const myChart = echarts.init(pieChartRef.value)
  
  const data = Object.keys(sourceData).map(key => ({
    name: key,
    value: sourceData[key]
  }))

  const option = {
    tooltip: { trigger: 'item' },
    legend: { top: 'bottom' },
    series: [
      {
        name: '来源渠道',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: { show: false, position: 'center' },
        emphasis: {
          label: { show: true, fontSize: 20, fontWeight: 'bold' }
        },
        labelLine: { show: false },
        data: data
      }
    ]
  }
  myChart.setOption(option)
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.data-card {
  text-align: center;
}
.card-header {
  font-size: 14px;
  color: #909399;
}
.card-value {
  font-size: 28px;
  font-weight: bold;
  margin-top: 10px;
  color: #303133;
}
.text-success { color: #67C23A; }
.text-warning { color: #E6A23C; }
.text-danger { color: #F56C6C; }
</style>
