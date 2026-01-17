/**
 * HTTP请求封装工具
 * 
 * ========== 功能要求 ==========
 * 1. 封装axios实例，统一配置请求和响应拦截器
 * 2. 统一处理错误响应（网络错误、服务器错误等）
 * 3. 添加请求token（如需要认证）
 * 4. 统一处理loading状态（可选）
 * 5. 统一处理响应数据格式
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由API负责人维护
 * - 禁止修改基础配置（baseURL、timeout等），除非后端地址变更
 * - 如需添加请求拦截逻辑（如token刷新），需团队讨论
 * - 响应拦截器的错误处理逻辑可以优化，但需保持统一格式
 * - 其他开发者禁止直接修改此文件，如有需求需提交PR
 * 
 * ========== 接口调用要求 ==========
 * - 所有API调用必须通过此工具，禁止直接使用axios
 * - 请求会自动添加 /api 前缀（通过vite代理）
 * - 响应数据格式：{ code: 200, msg: 'success', data: {...}, total: 0 }
 * - 错误响应格式：{ code: 500, msg: '错误信息', data: null }
 * 
 * ========== 函数关联要求 ==========
 * - 被所有 src/api/ 目录下的文件引用
 * - 不直接引用业务组件
 * - 不引用其他工具类（保持独立性）
 * 
 * ========== 使用示例 ==========
 * import request from '@/utils/request'
 * 
 * // GET请求
 * const response = await request.get('/events', { params: { startDate: '1931-09-18' } })
 * 
 * // POST请求
 * const response = await request.post('/events', { title: '事件标题' })
 */
import axios from 'axios';

const request = axios.create({
  baseURL: '/api', // 通过vite代理到后端
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器
request.interceptors.request.use(
  config => {
    // TODO: 如需添加token，在此处添加
    // const token = localStorage.getItem('token');
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`;
    // }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  response => {
    // 统一处理响应数据
    // 后端返回格式：{ code: 200, msg: 'success', data: {...} }
    const res = response.data;
    if (res.code === 200) {
      return res;
    } else {
      // 业务错误
      console.error('API错误:', res.msg);
      return Promise.reject(new Error(res.msg || '请求失败'));
    }
  },
  error => {
    // 统一错误处理
    if (error.response) {
      // 服务器返回了错误状态码
      console.error('API错误:', error.response.status, error.response.data);
    } else if (error.request) {
      // 请求已发出但没有收到响应
      console.error('网络错误:', error.request);
    } else {
      // 其他错误
      console.error('请求错误:', error.message);
    }
    return Promise.reject(error);
  }
);

export default request;

