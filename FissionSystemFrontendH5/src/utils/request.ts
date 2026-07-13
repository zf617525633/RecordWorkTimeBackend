import axios from 'axios';
import { showToast } from 'vant';

const request = axios.create({
  baseURL: '/api', // Proxy in vite.config.ts
  timeout: 10000,
});

// Request Interceptor
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    const appId = localStorage.getItem('appId') || 'app_test_01'; // Default for demo
    
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    // Always attach appId for multi-tenant backend logic
    config.headers['appId'] = appId;
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response Interceptor
request.interceptors.response.use(
  (response) => {
    const res = response.data;
    if (res.code !== 200) {
      showToast(res.message || 'Error');
      return Promise.reject(new Error(res.message || 'Error'));
    }
    return res;
  },
  (error) => {
    showToast(error.message || 'Network Error');
    return Promise.reject(error);
  }
);

export default request;
