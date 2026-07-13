# Android 内嵌 H5 登录态同步方案指南

在将基于 Vue 构建的 H5 页面内嵌到 Android 客户端（WebView）中时，保持“登录态同步”是混合开发（Hybrid App）中最关键的一环。本文档详细阐述了最佳的登录态同步方案——基于 **JSBridge (JavascriptInterface)**，并提供了 Android 端和 H5 端的代码接入示例。

## 为什么推荐 JSBridge 方案？

1. **绝对安全**：Token 传递通过底层内存交换，不会暴露在 URL 中。即使链接被复制或分享，也不会发生 Token 泄露。
2. **状态实时同步**：Android 端可以随时主动调用 H5 的 JS 方法（如注销登录时清理前端缓存），实现真正的双端联动。
3. **极佳的可扩展性**：搭建好 JSBridge 通道后，后续 H5 调用原生能力（如相机、扫码、定位、支付等）将水到渠成。

---

## 最佳实践方案设计

建议采用 **“主动拉取 + 被动推送”** 相结合的双向机制。

### 1. Android 端 (Native) 开发工作

Android 端需要向 WebView 注入一个全局的原生接口对象，提供获取当前 Token 和 AppId 的方法。

```java
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class WebAppInterface {
    
    // 向 H5 暴露的方法：获取 Token
    @JavascriptInterface
    public String getToken() {
        // 返回保存在 Android 原生 SharedPreferences 或内存中的 Token
        return NativeUserManager.getInstance().getCurrentToken();
    }
    
    // 向 H5 暴露的方法：获取 AppId (用于多租户)
    @JavascriptInterface
    public String getAppId() {
        return "app_test_01"; 
    }
}

// 在 WebView 初始化时注入该对象，命名空间设为 "AppJSBridge"
WebView webView = findViewById(R.id.webview);
webView.getSettings().setJavaScriptEnabled(true);
webView.addJavascriptInterface(new WebAppInterface(), "AppJSBridge");
```

### 2. 前端 (H5 / Vue) 开发工作

在 H5 端，当应用启动或拦截器发起网络请求之前，优先判断当前环境是否处于 Android 原生 App 中，如果在，则通过原生接口获取 Token。

推荐在项目的工具类（如 `src/utils/request.ts`）中封装一段自适应的 Token 获取逻辑：

```javascript
// 获取 Token 的通用方法
export const getAuthToken = () => {
    let token = '';
    
    // 1. 优先尝试从 Android 原生获取 (JSBridge)
    if (window.AppJSBridge && typeof window.AppJSBridge.getToken === 'function') {
        token = window.AppJSBridge.getToken();
        // 可以选择将原生传来的 token 同步覆盖到本地的 localStorage
        if (token) {
            localStorage.setItem('token', token);
        }
    } 
    // 2. 如果不在 App 内（比如浏览器打开），则从普通的 localStorage 中获取
    else {
        token = localStorage.getItem('token') || '';
    }
    
    return token;
};
```

在 Axios 请求拦截器中应用：

```javascript
import axios from 'axios';
import { getAuthToken } from './utils';

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
});

request.interceptors.request.use(
  (config) => {
    // 动态获取 Token（原生优先，缓存兜底）
    const token = getAuthToken();
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);
```

---

## 备选与降级方案：URL 拼接参数

如果由于排期原因暂时无法开发 JSBridge，可以采用 **URL 参数** 作为临时过渡方案。

- **Android 端操作**：加载 WebView 时动态拼接参数。
  ```java
  String url = "http://your-h5-domain.com/home?token=" + token + "&appId=" + appId;
  webView.loadUrl(url);
  ```
- **H5 端操作**：在 `App.vue` 或 `main.ts` 中拦截解析并消除参数：
  ```javascript
  const urlParams = new URLSearchParams(window.location.search);
  const urlToken = urlParams.get('token');
  
  if (urlToken) {
      localStorage.setItem('token', urlToken);
      // 利用 history API 清除地址栏的 token，防止分享泄露
      const cleanUrl = window.location.origin + window.location.pathname;
      window.history.replaceState(null, '', cleanUrl);
  }
  ```

> **总结**：长期项目强烈建议使用 **JSBridge**，它更安全、稳定，并为未来的混合功能（Hybrid）拓展奠定了坚实的基础。
