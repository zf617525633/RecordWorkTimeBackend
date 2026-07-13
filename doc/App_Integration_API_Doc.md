# 积分裂变系统 - 客户端 (App) 接入开发文档

本文档提供给各个接入平台（Android/iOS 等原生 App）的开发人员阅读，指导如何将原生 App 的账号体系安全地无缝对接到 H5 积分裂变系统。

## 1. 交互流程概述
为了防止 H5 链接被随意抓包刷分，本系统采用**“一次性票据 (Ticket) 换取 Token”**的静默登录模式。

**整体时序如下：**
1. 用户在 App 内点击“积分中心”入口。
2. 原生 App 拦截点击事件，在后台向裂变系统服务器发起 `/api/auth/generateTicket` 请求。
3. 原生 App 拿到返回的 `ticket`。
4. 原生 App 打开 Webview 加载 H5 首页，并在 URL 后面拼接参数 `?ticket=xxx&appId=xxx`。
5. H5 页面自动解析 URL 获取票据，静默换取长期 Token 并完成最终登录。

---

## 2. API 接口说明

### 2.1 生成一次性安全票据 (Generate Ticket)
由原生 App 在服务端或客户端发起请求，生成用于给 H5 授权的短时效（30秒）票据。

- **接口地址**: `POST /api/auth/generateTicket`
- **Content-Type**: `application/x-www-form-urlencoded`

#### 请求参数 (Request Parameters)

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| `appId` | String | 是 | 贵方产品在我方后台注册分配的 `app_id` (例如: `app_novel_01`) |
| `appUid` | String | 是 | 当前登录用户在贵方系统中的**唯一标识 ID** (请勿传递易变的数据) |
| `deviceFingerprint` | String | 否 | 设备的唯一指纹标识 (IMEI/UUID等)，用于加强防抓包风控比对 |

> [!IMPORTANT]
> **安全规范建议**：在生产环境中，此接口应当只允许贵方的**服务端**调用（然后下发给 App），或者在调用时加入签名校验（Sign），以防止接口被恶意无限调用刷取 Ticket。

#### 响应数据 (Response)

```json
{
  "code": 200,
  "message": "success",
  "data": "9a7f34c2b8d94e11a2f6cc..." // 返回的 Ticket 字符串，有效期 30 秒
}
```

---

---

## 3. C端 H5 独立登录接口

为了支持不依赖原声 App 票据的直接访问（例如微信分享链接直接在外部打开），我们提供了以下独立登录接口。

### 3.1 发送手机验证码 (Send SMS Code)
- **接口地址**: `POST /api/auth/sendSmsCode`
- **Content-Type**: `application/json`

#### 请求参数 (JSON)
| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| `appId` | String | 是 | 接入应用的唯一标识 `app_id` |
| `phone` | String | 是 | 用户手机号码 |

#### 响应数据
```json
{
  "code": 200,
  "message": "验证码发送成功",
  "data": null
}
```

### 3.2 手机验证码登录/注册 (Login by Phone)
- **接口地址**: `POST /api/auth/loginByPhone`
- **Content-Type**: `application/json`

#### 请求参数 (JSON)
| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| `appId` | String | 是 | 接入应用的唯一标识 `app_id` |
| `phone` | String | 是 | 用户手机号码 |
| `smsCode` | String | 是 | 收到的 6 位短信验证码 |

#### 响应数据
```json
{
  "code": 200,
  "message": "success",
  "data": "9a7f34c2b8d94e11a2f6cc..." // 长效的 Session Token
}
```

### 3.3 微信授权登录/注册 (Login by WeChat)
- **接口地址**: `POST /api/auth/loginByWechat`
- **Content-Type**: `application/json`

#### 请求参数 (JSON)
| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| `appId` | String | 是 | 接入应用的唯一标识 `app_id` |
| `code` | String | 是 | 微信授权后返回的临时 `code` |

#### 响应数据
```json
{
  "code": 200,
  "message": "success",
  "data": "9a7f34c2b8d94e11a2f6cc..." // 长效的 Session Token
}
```

### 3.4 退出登录 (Logout)
- **接口地址**: `POST /api/auth/logout`
- **Content-Type**: `application/json` 或无

#### 请求参数
可以在请求头 (Header) 中携带 `Authorization: Bearer <token>`，或者直接在 URL 后拼上 `?token=<token>` 参数。

#### 响应数据
```json
{
  "code": 200,
  "message": "已退出登录",
  "data": null
}
```

---

## 4. Webview 页面加载规范

原生 App 成功获取到 `Ticket` 后，就可以拉起 Webview 加载 H5 积分中心页面了。

### 3.1 基础入口链接拼接
- **H5 生产环境根地址**: `https://www.recordtime.top` (替换为实际域名)
- **跳转路径**: `/auth` (统一鉴权中转页)

请按以下格式将参数拼接在 URL 后并拉起 Webview：
```text
https://www.recordtime.top/auth?appId={您的appId}&ticket={接口返回的ticket}&deviceFingerprint={设备指纹}
```

### 3.2 示例代码 (Android / iOS 伪代码)

**Android 端示例**：
```java
// 1. 请求 Ticket
String ticket = requestGenerateTicket("app_test_01", "user_8899", "dev_abc123");

// 2. 拼接 URL 并打开 Webview
String targetUrl = "https://www.recordtime.top/auth" + 
                   "?appId=app_test_01" + 
                   "&ticket=" + ticket;
                   
WebView webView = findViewById(R.id.webview);
webView.loadUrl(targetUrl);
```

// iOS 端示例：
```swift
// 1. 请求 Ticket
let ticket = requestGenerateTicket(appId: "app_test_01", uid: "user_8899")

// 2. 拼接 URL
let urlString = "https://www.recordtime.top/auth?appId=app_test_01&ticket=\(ticket)"
if let url = URL(string: urlString) {
    let request = URLRequest(url: url)
    self.webView.load(request)
}
```

---

---

## 5. 常见问题 (FAQ)

> [!WARNING]
> **Q: H5 页面提示“鉴权失败：Ticket已失效”？**
> **A:** Ticket 的**有效期只有 30 秒**，并且是**一次性**的（被 H5 用过一次后就会立刻销毁）。请检查是否在获取 Ticket 后过久才打开 Webview，或者用户多次刷新了页面导致 Ticket 被重复使用。如果遇到此情况，App 应捕获错误并重新走一次申请 Ticket 的流程。

> [!TIP]
> **Q: 为什么不在 URL 里直接传 UserID？**
> **A:** 如果直接在 URL 传输明文 `UserId`，黑产可以直接修改链接里的数字去操作别人的账户进行提现。通过后台换取的 Ticket 加密了对应的 UID 关系，极大提升了黑产作恶成本。
