/**
 * Vite配置文件
 * 
 * ========== 功能要求 ==========
 * 1. 配置Vue插件支持
 * 2. 配置开发服务器代理，解决前后端跨域问题
 * 3. 配置构建选项
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由项目负责人或运维人员维护
 * - 其他开发者如需修改代理配置，需先讨论
 * - 禁止修改Vue插件配置，除非框架升级
 * 
 * ========== 接口调用要求 ==========
 * - 所有/api开头的请求会被代理到 http://localhost:3001
 * - 前端代码中直接使用 /api/xxx 即可，无需写完整URL
 */
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:3001',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '/api')
      }
    }
  }
});

