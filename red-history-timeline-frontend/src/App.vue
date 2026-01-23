<!--
  根组件
  
  ========== 功能要求 ==========
  1. 作为应用的根容器组件
  2. 挂载路由视图（router-view），实现页面切换
  3. 提供全局布局结构（如需要）
  
  ========== 修改范围限制 ==========
  - 此文件由项目负责人维护
  - 禁止添加业务逻辑
  - 禁止添加API调用
  - 如需添加全局布局（如Header、Footer），需团队讨论
  - 其他开发者禁止修改此文件
  
  ========== 接口调用要求 ==========
  - 不调用任何API接口
  - 不处理任何业务数据
  
  ========== 函数关联要求 ==========
  - 被 main.js 引用
  - 通过 router-view 渲染 views/ 目录下的页面组件
  - 不直接引用业务组件
-->
<template>
  <div id="app">
    <router-view v-slot="{ Component }">
      <transition name="router-fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<script>
export default {
  name: 'App'
};
</script>

<style>
#app {
  width: 100%;
  height: 100vh;
}

/* 路由过渡动画 */
.router-fade-enter-active,
.router-fade-leave-active {
  transition: all 0.5s ease;
}

.router-fade-enter-from {
  opacity: 0;
  transform: scale(0.95);
}

.router-fade-leave-to {
  opacity: 0;
  transform: scale(1.05);
}

/* 详情页弹窗过渡效果 */
.router-fade-enter-active.detail-page,
.router-fade-leave-active.detail-page {
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.router-fade-enter-from.detail-page {
  opacity: 0;
  transform: scale(0.9) translateY(20px);
}

.router-fade-leave-to.detail-page {
  opacity: 0;
  transform: scale(1.1) translateY(-20px);
}
</style>

