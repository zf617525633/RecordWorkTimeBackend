<template>
  <div class="withdraw-page">
    <van-nav-bar title="积分提现" left-arrow @click-left="$router.back()" />
    
    <div class="info-card">
      当前可用积分：<strong>{{ availablePoints }}</strong>
    </div>

    <van-form @submit="onSubmit" class="withdraw-form">
      <van-cell-group inset>
        <van-field
          v-model="form.pointsConsumed"
          name="pointsConsumed"
          label="提现积分"
          type="digit"
          placeholder="最低100积分"
          :rules="[{ required: true, message: '请输入要提现的积分' }]"
        />
        <van-field
          name="payType"
          label="提现方式"
        >
          <template #input>
            <van-radio-group v-model="form.payType" direction="horizontal">
              <van-radio :name="1">支付宝</van-radio>
              <van-radio :name="2">微信</van-radio>
            </van-radio-group>
          </template>
        </van-field>
      </van-cell-group>
      
      <div class="submit-btn-wrap">
        <van-button round block type="primary" native-type="submit" :loading="loading">
          提交提现申请 (预计 ￥{{ (Number(form.pointsConsumed) / 100).toFixed(2) }})
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { showToast } from 'vant';
import request from '../../utils/request';
import { useRouter } from 'vue-router';

const router = useRouter();
const availablePoints = ref(0);
const loading = ref(false);

const form = ref({
  userId: 1, // 演示写死
  pointsConsumed: '',
  amount: 0,
  payType: 1
});

const loadUserInfo = async () => {
  try {
    const res: any = await request.get('/user/info?userId=1');
    if (res.code === 200) {
      availablePoints.value = res.data.availablePoints;
    }
  } catch (err) {}
};

const onSubmit = async () => {
  const points = Number(form.value.pointsConsumed);
  if (points < 100) {
    showToast('最低提现需要100积分');
    return;
  }
  if (points > availablePoints.value) {
    showToast('可用积分余额不足');
    return;
  }

  form.value.amount = points / 100; // 模拟比例 100积分=1元
  loading.value = true;
  try {
    const res: any = await request.post('/account/withdraw', {
      userId: form.value.userId,
      pointsConsumed: points,
      amount: form.value.amount,
      payType: form.value.payType
    });
    if (res.code === 200) {
      showToast('提现申请成功，等待审核');
      setTimeout(() => {
        router.back();
      }, 1500);
    }
  } catch (err) {
    // Error handled by interceptor
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadUserInfo();
});
</script>

<style scoped>
.withdraw-page {
  background: #f7f8fa;
  min-height: 100vh;
}
.info-card {
  margin: 16px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  font-size: 14px;
  text-align: center;
}
.info-card strong {
  color: #ee0a24;
  font-size: 18px;
}
.submit-btn-wrap {
  margin: 24px 16px;
}
</style>
