# 积分中心后端接口设计文档 (Points Center API Design)

为了支撑全新高颜值、游戏化的 C端 H5 积分中心（`Home.vue`），我们需要将原本简单的写死数据替换为后端真实数据。
本文档详细规定了为了渲染该页面所需的一系列 API 接口。你（或者我）可以按照这份文档在 `FissionSystemBackend` 中进行具体实现。

---

## 1. 积分中心首页数据初始化
此接口用于一次性拉取整个积分中心首页所需的所有状态，避免前端发起过多零散的网络请求。

- **接口地址**: `GET /api/point-center/index`
- **需要 Token**: 是
- **描述**: 返回用户当前可用积分、今日赚取、各项任务（新人任务、视频任务、日常任务）的完成进度。

### 请求参数
无（通过 Header 中的 `Authorization: Bearer <token>` 识别用户身份和所属 AppId）

### 响应数据结构
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "account": {
      "availablePoints": 6016,
      "todayEarnedPoints": 0,
      "cnyEquivalent": "0.60"  // 折合人民币（基于平台配置的汇率，如 10000金币 = 1元）
    },
    "newcomerTask": {
      "checkInProgress": 2,    // 连续签到天数 (0-7)
      "isTodayCheckedIn": false, // 今日是否已签到
      "withdrawProgress": 1,   // 新人提现任务进度天数
      "isTodayWithdrawn": true // 今日是否已完成提现
    },
    "videoTask": {
      "watchedCount": 2,       // 今日已看视频次数
      "maxLimit": 5,           // 每日最高可领次数
      "rewardPerVideo": 1144   // 每次观看奖励金币
    },
    "dailyTasks": [
      {
        "taskId": "task_daily_login",
        "title": "天天领金币",
        "subTitle": "每天领取一次，金币天天送不停",
        "rewardPoints": 400,
        "iconType": "Coins",
        "status": 0            // 状态：0-去完成(未完成), 1-去领取(已达标未领奖), 2-已领取
      },
      {
        "taskId": "task_lottery_ticket",
        "title": "签到天天抽奖券",
        "subTitle": "签到天天得奖券",
        "rewardPoints": 1000,
        "iconType": "Ticket",
        "status": 1
      },
      {
        "taskId": "task_lucky_wheel",
        "title": "金币大转盘",
        "subTitle": "最高可得10000金币",
        "rewardPoints": 10000,
        "iconType": "Trophy",
        "status": 0
      }
    ]
  }
}
```

---

## 2. 每日签到接口 (原 `/point/checkIn` 升级)
当用户点击“去赚钱”（签到）时调用。

- **接口地址**: `POST /api/point-center/tasks/checkin`
- **需要 Token**: 是
- **描述**: 执行每日签到，自动根据连续签到天数发放不同额度的金币（例如第1-3天1000金币，第4-7天大红包）。

### 请求参数
无

### 响应数据
```json
{
  "code": 200,
  "message": "签到成功",
  "data": {
    "rewardPoints": 1000,
    "continuousDays": 3
  }
}
```

---

## 3. 看视频回调接口
当用户看完广告视频后，由前端（或第三方广告 SDK 服务端回调）通知后端发放金币。

- **接口地址**: `POST /api/point-center/tasks/video`
- **需要 Token**: 是
- **描述**: 记录一次视频观看并下发对应的红包金币。若超过单日上限则返回错误或0金币。

### 请求体 (Body - application/json)
```json
{
  "videoId": "vid_xxx123", // 可选，如果有具体视频/广告标识
  "sign": "xxxxx"          // 可选，防刷防篡改签名
}
```

### 响应数据
```json
{
  "code": 200,
  "message": "领取成功",
  "data": {
    "rewardPoints": 1144,
    "currentWatchedCount": 3
  }
}
```

---

## 4. 领取日常任务奖励
对于日常任务列表中状态为 `status: 1`（去领取）的任务，点击时调用此接口。

- **接口地址**: `POST /api/point-center/tasks/claim`
- **需要 Token**: 是
- **描述**: 验证任务是否已完成，验证通过则将金币入账并将任务状态置为 2（已领取）。

### 请求体 (Body - application/json)
```json
{
  "taskId": "task_lottery_ticket"
}
```

### 响应数据
```json
{
  "code": 200,
  "message": "领取成功",
  "data": {
    "rewardPoints": 1000,
    "newBalance": 7016
  }
}
```

---

## 5. 新增数据表设计 (MySQL)

为了支持以上接口，除了已有的 `fission_user` 和 `fission_account`，我们还需要新增以下两张表来记录“连续签到”和“每日任务”状态。

### 5.1 用户签到表 (`fission_user_checkin`)
用于记录用户的连续签到天数和最后一次签到日期。

```sql
CREATE TABLE `fission_user_checkin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_id` varchar(64) NOT NULL COMMENT '接入应用app_id',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `continuous_days` int(11) NOT NULL DEFAULT '0' COMMENT '连续签到天数',
  `last_checkin_date` date DEFAULT NULL COMMENT '最后一次签到日期(格式: YYYY-MM-DD)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_app_id` (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户签到记录表';
```

### 5.2 用户每日任务进度表 (`fission_user_task_log`)
用于记录用户当天各项任务（看视频、日常任务等）的完成情况。每天的数据会根据 `record_date` 重新计算。

```sql
CREATE TABLE `fission_user_task_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_id` varchar(64) NOT NULL COMMENT '接入应用app_id',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `task_id` varchar(64) NOT NULL COMMENT '任务唯一标识(如: task_video, task_daily_login)',
  `record_date` date NOT NULL COMMENT '任务记录日期(格式: YYYY-MM-DD)',
  `progress_count` int(11) NOT NULL DEFAULT '0' COMMENT '当前完成进度(如看了2次视频)',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态: 0-未完成, 1-待领取, 2-已领取',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_task_date` (`user_id`, `task_id`, `record_date`),
  KEY `idx_app_id_date` (`app_id`, `record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户每日任务进度表';
```

> [!IMPORTANT]  
> **给后端开发的提示**  
> 1. 这些接口的数据来源应当建立在 `fission_account` 和新建的 `fission_user_task_log` 等任务记录表之上。
> 2. `cnyEquivalent` (折合人民币) 的计算逻辑建议放在后端进行，前端仅负责渲染，避免因汇率调整导致前端需要发版。
> 3. 高并发场景下，签到和任务领取（更新 `fission_account.available_points`）必须使用**乐观锁（版本号机制）**或**分布式锁**，防止用户恶意并发刷金币。
