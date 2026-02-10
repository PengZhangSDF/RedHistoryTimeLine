/**
 * Vite配置文件
 *
 * 说明：
 * - 配置 Vue 插件
 * - 配置路径别名 @ 指向 src，便于在组件中使用 '@/xxx' 导入
 * - 配置开发服务器代理，解决前后端跨域问题
 */
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { fileURLToPath, URL } from 'node:url';

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
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

