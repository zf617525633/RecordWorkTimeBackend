<template>
  <div class="home-page">
    <!-- 顶部 Header 区域 -->
    <div class="header-section">
      <div class="header-content">
        <div class="coin-info">
          <div class="coin-block">
            <span class="label">我的金币</span>
            <div class="value-row">
              <span class="large-value">{{ homeData.account.availablePoints }}</span>
              <span class="small-cny">≈{{ homeData.account.cnyEquivalent }}元</span>
            </div>
          </div>
          <div class="coin-block center-block">
            <span class="label">今日金币</span>
            <div class="value-row">
              <span class="medium-value">{{ homeData.account.todayEarnedPoints }}</span>
            </div>
          </div>
        </div>
        <div class="withdraw-action" @click="goWithdraw">
          <div class="withdraw-btn">
            <span>去提现/兑换</span>
            <van-icon name="arrow" />
          </div>
        </div>
      </div>
    </div>

    <!-- 滚动区域主体 -->
    <div class="main-content">
      
      <!-- 新人专享卡片 -->
      <div class="custom-card newcomer-card">
        <div class="card-header">
          <div class="title-with-icon">
            <gift-box theme="filled" size="24" fill="#ff5f2e" class="emoji-icon" />
            <span class="card-title">新人专属 金币包</span>
          </div>
        </div>
        
        <div class="task-block">
          <div class="task-top">
            <div class="task-info">
              <div class="task-title">连续签到领金币，最高得 <span class="highlight">10000金币</span></div>
              <div class="task-sub">连续签到7天，提现秒到账</div>
            </div>
            <div class="task-btn primary-btn" :class="{ 'outline-btn': homeData.newcomerTask.isTodayCheckedIn }" @click="!homeData.newcomerTask.isTodayCheckedIn && handleCheckIn()">{{ homeData.newcomerTask.isTodayCheckedIn ? '已签到' : '去赚钱' }}</div>
          </div>
          
          <div class="progress-envelopes">
            <div class="env-item active">
              <red-envelope theme="two-tone" size="32" :fill="['#ff5f2e', '#ffdeaa']" class="env-icon" />
              <div class="env-day">1天</div>
              <div class="env-coin">1000</div>
            </div>
            <div class="env-item active">
              <red-envelope theme="two-tone" size="32" :fill="['#ff5f2e', '#ffdeaa']" class="env-icon" />
              <div class="env-day">2天</div>
              <div class="env-coin">1000</div>
            </div>
            <div class="env-item">
              <red-envelope theme="two-tone" size="32" :fill="['#ff5f2e', '#ffdeaa']" class="env-icon" />
              <div class="env-day">3天</div>
              <div class="env-coin">1000</div>
            </div>
            <div class="env-item big">
              <red-envelope theme="two-tone" size="48" :fill="['#ff5f2e', '#ffdeaa']" class="env-icon big-icon" />
              <div class="env-day">第4-7天</div>
              <div class="env-coin">10000</div>
            </div>
          </div>
        </div>

        <div class="task-divider"></div>

        <div class="task-block">
          <div class="task-top">
            <div class="task-info">
              <div class="task-title">新人专属提现任务 <span class="highlight">最高赚1144金币</span></div>
              <div class="task-sub">完成任务，现金秒到账</div>
            </div>
            <div class="task-btn outline-btn">去提现</div>
          </div>
          <div class="progress-coins">
            <div class="coin-node completed">
              <div class="c-circle">✓</div>
              <div class="c-day">1天</div>
            </div>
            <div class="coin-line active"></div>
            <div class="coin-node active">
              <div class="c-circle">2000<br/>金币</div>
              <div class="c-day">2天</div>
            </div>
            <div class="coin-line"></div>
            <div class="coin-node">
              <div class="c-circle">3000<br/>金币</div>
              <div class="c-day">3天</div>
            </div>
            <div class="coin-line"></div>
            <div class="coin-node">
              <div class="c-circle">4000<br/>金币</div>
              <div class="c-day">4天</div>
            </div>
            <div class="coin-line"></div>
            <div class="coin-node">
              <div class="c-circle">5000<br/>金币</div>
              <div class="c-day">5天</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 看视频领红包卡片 -->
      <div class="custom-card video-card">
        <div class="card-header">
          <div class="title-with-icon">
            <play-two theme="filled" size="24" fill="#0b8c82" class="emoji-icon" />
            <span class="card-title">看视频领红包</span>
          </div>
        </div>
        
        <div class="task-block">
          <div class="task-top">
            <div class="task-info">
              <div class="task-title">看视频领金币 <span class="highlight">最高得1144金币</span></div>
              <div class="task-sub">点击右侧按钮，每看1次发1次</div>
            </div>
            <div class="task-btn primary-btn" @click="watchVideo">去完成</div>
          </div>
          <div class="envelope-row">
             <div class="env-mini-item" v-for="i in 5" :key="i">
               <red-envelope theme="two-tone" size="24" :fill="['#ff5f2e', '#ffdeaa']" class="env-mini-icon" />
               <div class="env-mini-text">1144</div>
             </div>
          </div>
        </div>
      </div>

      <!-- 日常任务卡片 -->
      <div class="custom-card daily-card">
        <div class="card-header">
          <span class="card-title black-title">日常任务</span>
        </div>
        <div class="task-list">
          <!-- 任务项 -->
          <div class="list-item" v-for="task in homeData.dailyTasks" :key="task.taskId">
            <component :is="task.iconType" theme="two-tone" size="36" :fill="['#ff5f2e', '#ffeae0']" class="item-icon" />
            <div class="item-content">
              <div class="item-title">{{ task.title }} <span class="reward">+{{ task.rewardPoints }}</span></div>
              <div class="item-sub">{{ task.subTitle }}</div>
            </div>
            <div class="item-action">
              <div class="task-btn" 
                   :class="{ 'primary-btn': task.status === 0, 'outline-btn': task.status === 1, 'disabled-btn': task.status === 2 }"
                   @click="task.status !== 2 && claimTask(task.taskId)"
                   :style="task.status === 2 ? 'background: #f0f0f0; color: #999; border:none;' : ''">
                {{ task.status === 0 ? '去完成' : task.status === 1 ? '去领取' : '已领取' }}
              </div>
            </div>
          </div>
        </div>
      </div>


    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { showToast } from 'vant';
import request from '../../utils/request';
import { GiftBox, RedEnvelope, PlayTwo, Currency, Ticket, Trophy } from '@icon-park/vue-next';
import '@icon-park/vue-next/styles/index.css';
const _icons = [Currency, Ticket, Trophy, GiftBox, RedEnvelope, PlayTwo];
console.log('Registered icons:', _icons.length);

const router = useRouter();
const homeData = ref<any>({
  account: { availablePoints: 0, todayEarnedPoints: 0, cnyEquivalent: '0.00' },
  newcomerTask: { checkInProgress: 0, isTodayCheckedIn: false },
  videoTask: { watchedCount: 0, maxLimit: 5, rewardPerVideo: 1144 },
  dailyTasks: []
});

const loadHomeData = async () => {
  if (!localStorage.getItem('token')) {
    return; // 不要加载数据，或者加载默认数据
  }
  try {
    const res: any = await request.get('/point-center/index', {
      params: { userId: 1, appId: localStorage.getItem('appId') || 'app_fc131d57' }
    });
    if (res.code === 200) {
      homeData.value = res.data;
    }
  } catch (err) {
    console.error(err);
  }
};

const requireLogin = () => {
  if (!localStorage.getItem('token')) {
    showToast('请先登录才能进行操作哦');
    // 可选: 跳转到登录页 router.push('/login');
    return false;
  }
  return true;
};

const handleCheckIn = async () => {
  if (!requireLogin()) return;
  try {
    const res: any = await request.post('/point-center/tasks/checkin', null, {
      params: { userId: 1, appId: localStorage.getItem('appId') || 'app_fc131d57' }
    });
    if (res.code === 200) {
      showToast(`签到成功！获得 ${res.data.rewardPoints} 金币`);
      loadHomeData();
    } else {
      showToast(res.message || '签到失败');
    }
  } catch (err) {
    console.error(err);
  }
};

const watchVideo = async () => {
  if (!requireLogin()) return;
  try {
    const res: any = await request.post('/point-center/tasks/video', { videoId: 'test_video_1', sign: 'test' }, {
      params: { userId: 1, appId: localStorage.getItem('appId') || 'app_fc131d57' }
    });
    if (res.code === 200) {
      showToast(`获得红包 ${res.data.rewardPoints} 金币`);
      loadHomeData();
    } else {
      showToast(res.message || '观看失败');
    }
  } catch (err) {
    console.error(err);
  }
};

const claimTask = async (taskId: string) => {
  if (!requireLogin()) return;
  try {
    const res: any = await request.post('/point-center/tasks/claim', { taskId }, {
      params: { userId: 1, appId: localStorage.getItem('appId') || 'app_fc131d57' }
    });
    if (res.code === 200) {
      showToast(`领取成功！获得 ${res.data.rewardPoints} 金币`);
      loadHomeData();
    } else {
      showToast(res.message || '领取失败');
    }
  } catch (err) {
    console.error(err);
  }
};

const goWithdraw = () => {
  if (!requireLogin()) return;
  router.push('/withdraw');
};

onMounted(() => {
  loadHomeData();
});
</script>

<style scoped>
.home-page {
  background-color: #f5f6f8;
  min-height: 100vh;
  padding-bottom: 30px;
}

/* Header Area */
.header-section {
  background: linear-gradient(135deg, #ff8c3b, #ff5f2e);
  padding: 20px;
  /* 更大幅度地加大顶部内边距，并将整体最小高度改高 */
  padding-top: calc(80px + env(safe-area-inset-top, 0px));
  padding-bottom: 70px;
  min-height: 240px;
  box-sizing: border-box;
  border-bottom-left-radius: 30px;
  border-bottom-right-radius: 30px;
  color: #fff;
  position: relative;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.coin-info {
  display: flex;
  gap: 30px;
}

.coin-block .label {
  font-size: 12px;
  opacity: 0.9;
  display: block;
  margin-bottom: 4px;
}

.value-row {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.large-value {
  font-size: 32px;
  font-weight: bold;
}

.medium-value {
  font-size: 24px;
  font-weight: bold;
}

.small-cny {
  font-size: 12px;
  opacity: 0.9;
}

.withdraw-action {
  margin-top: 10px;
}

.withdraw-btn {
  background: rgba(255, 255, 255, 0.2);
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 4px;
  backdrop-filter: blur(4px);
}

/* Main Content Area */
.main-content {
  padding: 0 12px;
  margin-top: -40px;
  position: relative;
  z-index: 10;
}

/* Common Card Styles */
.custom-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.card-header {
  margin-bottom: 16px;
}

.title-with-icon {
  display: flex;
  align-items: center;
  gap: 6px;
}

.emoji-icon {
  font-size: 20px;
}

.card-title {
  font-size: 16px;
  font-weight: bold;
  color: #8c4322;
}

.black-title {
  color: #333;
}

.highlight {
  color: #ff5f2e;
  font-weight: bold;
}

/* Buttons */
.task-btn {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: bold;
  text-align: center;
  white-space: nowrap;
}

.primary-btn {
  background: linear-gradient(90deg, #ff7a38, #ff5f2e);
  color: #fff;
  box-shadow: 0 2px 6px rgba(255, 95, 46, 0.3);
}

.outline-btn {
  background: #fff;
  color: #ff5f2e;
  border: 1px solid #ff5f2e;
}

/* Task Block (Generic) */
.task-block {
  margin-bottom: 16px;
}

.task-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.task-title {
  font-size: 15px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.task-sub {
  font-size: 12px;
  color: #999;
}

.task-divider {
  height: 1px;
  background: #f0f0f0;
  margin: 16px 0;
}

/* Progress Envelopes (Newcomer Card) */
.newcomer-card {
  background: linear-gradient(180deg, #fff3eb, #ffffff);
}

.progress-envelopes {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-top: 10px;
}

.env-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #fff;
  border-radius: 8px;
  padding: 8px 4px;
  flex: 1;
  margin: 0 4px;
  box-shadow: 0 2px 4px rgba(255, 95, 46, 0.1);
  border: 1px solid transparent;
}

.env-item.active {
  border-color: #ff5f2e;
  background: #fff9f5;
}

.env-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.env-day {
  font-size: 12px;
  color: #666;
  margin-bottom: 2px;
}

.env-coin {
  font-size: 12px;
  color: #ff5f2e;
  font-weight: bold;
}

.env-item.big {
  flex: 1.5;
  background: linear-gradient(180deg, #fff, #ffeae0);
}

.big-icon {
  font-size: 32px;
}

/* Progress Coins Nodes */
.progress-coins {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 10px;
}

.coin-node {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  z-index: 2;
}

.c-circle {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #f5f5f5;
  color: #999;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  text-align: center;
  line-height: 1.2;
  margin-bottom: 4px;
}

.coin-node.completed .c-circle {
  background: #ff5f2e;
  color: #fff;
  font-size: 16px;
}

.coin-node.active .c-circle {
  background: #ffb400;
  color: #fff;
  font-weight: bold;
}

.c-day {
  font-size: 11px;
  color: #666;
}

.coin-line {
  flex: 1;
  height: 4px;
  background: #f5f5f5;
  margin-top: -16px;
  z-index: 1;
}

.coin-line.active {
  background: #ff5f2e;
}

/* Video Card */
.video-card {
  background: linear-gradient(180deg, #e6fcf9, #ffffff);
}
.video-card .card-title {
  color: #0b8c82;
}
.envelope-row {
  display: flex;
  justify-content: space-between;
  background: #f5fbf9;
  padding: 10px;
  border-radius: 8px;
}
.env-mini-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.env-mini-icon {
  font-size: 20px;
}
.env-mini-text {
  font-size: 11px;
  color: #ff5f2e;
  margin-top: 4px;
}

/* Daily Task List */
.list-item {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f5f5f5;
}

.list-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.item-icon {
  font-size: 32px;
  margin-right: 12px;
}

.item-content {
  flex: 1;
}

.item-title {
  font-size: 15px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.reward {
  color: #ff5f2e;
  font-size: 14px;
  margin-left: 4px;
}

.item-sub {
  font-size: 12px;
  color: #999;
}

.item-action {
  margin-left: 10px;
}


</style>
