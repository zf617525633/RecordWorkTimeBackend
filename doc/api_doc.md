# 裂变系统 (App 端) API 接口文档

本文档描述了提供给移动端/其他 App 接入的接口，所有接口的基础请求路径为 `/api`（例如 `http://<服务器IP>:<端口>/api/auth/loginByPhone`）。

## 1. 认证与登录 (Auth)

### 1.1 发送短信验证码
- **请求方式**: `POST /auth/sendSmsCode`
- **说明**: 向指定手机号发送验证码。
- **请求参数** (JSON):
  ```json
  {
    "appId": "string, 必填, 你的应用ID",
    "phone": "string, 必填, 手机号"
  }
  ```
- **返回结果**:
  ```json
  {
    "code": 200,
    "message": "验证码发送成功",
    "data": "验证码发送成功"
  }
  ```

### 1.2 手机验证码登录 (支持自动注册)
- **请求方式**: `POST /auth/loginByPhone`
- **说明**: 手机验证码登录。如果该手机号在当前应用(`appId`)下未注册，系统会自动完成注册（初始化积分账户等），并直接返回登录 Token。
- **请求参数** (JSON):
  ```json
  {
    "appId": "string, 必填, 应用ID",
    "phone": "string, 必填, 手机号",
    "smsCode": "string, 必填, 验证码"
  }
  ```
- **返回结果**:
  ```json
  {
    "code": 200,
    "message": "Success",
    "data": "返回的Session Token"
  }
  ```

### 1.3 微信授权登录 (支持自动注册)
- **请求方式**: `POST /auth/loginByWechat`
- **说明**: 微信 Code 登录/注册，同理支持新用户自动注册。
- **请求参数** (JSON):
  ```json
  {
    "appId": "string, 必填, 应用ID",
    "code": "string, 必填, 微信授权获取的code"
  }
  ```
- **返回结果**: 同上

### 1.4 Native-H5 票据交换 (Ticket 机制)
- **请求方式**: `POST /auth/generateTicket` 和 `POST /auth/exchangeToken`
- **说明**: 用于 Native App 向内置 H5 页面安全传递登录状态。App 先调用 `generateTicket` 获得一个临时 ticket 传给 H5，H5 页面再调用 `exchangeToken` 换取正式 Session Token。

---

## 2. 用户与积分 (User & Point)

### 2.1 获取用户信息与资产
- **请求方式**: `GET /user/info`
- **参数**:
  - `userId` (Long, 必传): 当前登录的业务用户 ID (暂时由请求参数传递)
- **返回结果**:
  ```json
  {
    "code": 200,
    "message": "Success",
    "data": {
      "userId": 1,
      "nickname": "用户_a1b2c3",
      "avatar": "...",
      "inviteCode": "8A9B0C1D",
      "availablePoints": 500,
      "totalPoints": 500,
      "frozenPoints": 0
    }
  }
  ```

### 2.2 每日签到
- **请求方式**: `POST /point/checkIn`
- **参数**:
  - `appId` (String, 必传)
  - `userId` (Long, 必传)
- **返回结果**:
  ```json
  {
    "code": 200,
    "message": "Success",
    "data": 10 // 获得的积分数量
  }
  ```

### 2.3 获取积分流水记录
- **请求方式**: `GET /point/logs`
- **参数**:
  - `appId` (String, 必传)
  - `userId` (Long, 必传)
  - `page` (Integer, 选填, 默认1)
  - `size` (Integer, 选填, 默认10)
- **返回结果**:
  ```json
  {
    "code": 200,
    "message": "Success",
    "data": {
      "records": [ ... ],
      "total": 50,
      "size": 10,
      "current": 1
    }
  }
  ```

### 2.4 发起提现申请
- **请求方式**: `POST /account/withdraw`
- **请求参数** (JSON):
  ```json
  {
    "userId": 1,
    "pointsConsumed": 100,
    "amount": 10.00,
    "payType": 1 // 1-支付宝, 2-微信
  }
  ```
- **返回结果**:
  ```json
  {
    "code": 200,
    "message": "Success",
    "data": "提现申请已提交，等待审核"
  }
  ```
