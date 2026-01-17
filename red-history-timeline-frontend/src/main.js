/**
 * 应用入口文件
 * 
 * ========== 功能要求 ==========
 * 1. 初始化Vue应用实例
 * 2. 注册Vue Router路由
 * 3. 引入全局样式文件
 * 4. 挂载应用到DOM元素（#app）
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由项目负责人维护
 * - 禁止修改Vue应用初始化逻辑
 * - 如需添加全局插件（如Pinia、Element Plus），需团队讨论
 * - 其他开发者禁止修改此文件
 * 
 * ========== 接口调用要求 ==========
 * - 不直接调用API
 * - 不处理业务逻辑
 * - 仅负责应用初始化
 * 
 * ========== 函数关联要求 ==========
 * - 引用 App.vue（根组件）
 * - 引用 router/index.js（路由配置）
 * - 引用 styles/base.scss（全局样式）
 * - 被 index.html 引用（通过script标签）
 */
import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import './styles/base.scss';

const app = createApp(App);
app.use(router);
app.mount('#app');

