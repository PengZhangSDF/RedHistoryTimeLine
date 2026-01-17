/**
 * 路由配置文件
 * 
 * ========== 功能要求 ==========
 * 1. 定义应用的所有路由规则
 * 2. 配置页面跳转路径和组件映射
 * 3. 实现路由懒加载（提升性能）
 * 
 * ========== 路由规则 ==========
 * - / → Home.vue (首页)
 * - /detail/:id → EventDetail.vue (事件详情页，id为事件ID)
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由前端路由负责人维护
 * - 添加新路由需团队讨论
 * - 禁止修改现有路由的path和name
 * - 如需添加路由守卫（如登录验证），需团队讨论
 * - 其他开发者如需添加路由，需先提交PR
 * 
 * ========== 接口调用要求 ==========
 * - 不直接调用API
 * - 路由参数通过 $route.params 传递给组件
 * - 组件内部自行处理API调用
 * 
 * ========== 函数关联要求 ==========
 * - 被 main.js 引用并注册到Vue应用
 * - 被 App.vue 通过 router-view 使用
 * - 引用 views/Home.vue
 * - 引用 views/EventDetail.vue
 * - 组件间跳转使用 this.$router.push() 或 <router-link>
 */
import { createRouter, createWebHistory } from 'vue-router';

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/detail/:id',
    name: 'EventDetail',
    component: () => import('../views/EventDetail.vue'),
    props: true // 将路由参数作为props传递给组件
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;

