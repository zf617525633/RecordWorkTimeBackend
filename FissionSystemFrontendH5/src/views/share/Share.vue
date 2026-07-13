<template>
  <div class="share-page">
    <van-nav-bar title="分享赚钱" left-arrow @click-left="$router.back()" />
    
    <div class="poster-container">
      <div class="poster">
        <h2>邀请好友送积分</h2>
        <p>你的专属邀请码：<strong>{{ inviteCode }}</strong></p>
        <div class="qr-placeholder">
          (此处应生成带参数的二维码)
        </div>
      </div>
    </div>
    
    <div class="share-actions">
      <van-button type="primary" block round @click="shareLink">
        复制专属链接去邀请
      </van-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { showToast } from 'vant';
import request from '../../utils/request';

const inviteCode = ref('');

const loadUserInfo = async () => {
  try {
    const res: any = await request.get('/user/info?userId=1');
    if (res.code === 200) {
      inviteCode.value = res.data.inviteCode;
    }
  } catch (err) {}
};

const shareLink = () => {
  const appId = localStorage.getItem('appId') || 'app_test_01';
  const url = `http://localhost:3000/auth?inviteCode=${inviteCode.value}&source=wechat_moments`;
  // 在真实H5环境中，可调用 navigator.clipboard.writeText(url) 或微信JS-SDK进行分享
  showToast('链接已复制：' + url);
};

onMounted(() => {
  loadUserInfo();
});
</script>

<style scoped>
.share-page {
  background: #ffecf2;
  min-height: 100vh;
}
.poster-container {
  padding: 24px 16px;
}
.poster {
  background: #fff;
  border-radius: 16px;
  padding: 32px 16px;
  text-align: center;
  box-shadow: 0 8px 24px rgba(255, 100, 150, 0.2);
}
.poster h2 {
  color: #ee0a24;
  margin-top: 0;
}
.poster strong {
  font-size: 24px;
  color: #333;
  letter-spacing: 2px;
}
.qr-placeholder {
  width: 150px;
  height: 150px;
  background: #f7f8fa;
  margin: 32px auto 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 12px;
}
.share-actions {
  margin: 0 16px 24px;
}
</style>
