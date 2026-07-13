<template>
  <div class="auth-page">
    <van-loading type="spinner" color="#1989fa" size="24px">
      安全登录鉴权中...
    </van-loading>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { showToast } from 'vant';
import request from '../../utils/request';

const route = useRoute();
const router = useRouter();

onMounted(async () => {
  // 原生App跳转H5时，在URL参数中带上 ticket 和 appId
  const ticket = route.query.ticket as string;
  const appId = (route.query.appId as string) || 'app_test_01';
  
  if (!ticket) {
    showToast('缺少安全票据(Ticket)');
    return;
  }

  try {
    // 换取 Token
    const res: any = await request.post('/auth/exchangeToken', {
      appId,
      ticket,
      deviceFingerprint: navigator.userAgent // 简易防刷指纹，实际应由App生成并传给后端比对
    });
    
    if (res.code === 200 && res.data) {
      localStorage.setItem('token', res.data);
      localStorage.setItem('appId', appId);
      showToast('登录成功');
      
      // 鉴权成功后跳转到首页
      router.replace('/home');
    }
  } catch (err) {
    // request 拦截器已经处理了 toast，这里不重复处理
    console.error('鉴权失败', err);
  }
});
</script>

<style scoped>
.auth-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}
</style>
