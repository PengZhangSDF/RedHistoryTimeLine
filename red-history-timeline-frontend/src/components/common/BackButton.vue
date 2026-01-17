<!--
  返回按钮组件（通用组件）
  
  ========== 功能要求 ==========
  1. 提供统一的返回按钮样式和行为
  2. 点击按钮返回首页（路由跳转到 /）
  3. 可复用组件，用于详情页等需要返回的场景
  
  ========== 修改范围限制 ==========
  - 此文件由UI组件负责人维护
  - 禁止修改按钮的点击行为（必须返回首页）
  - 可以修改样式，但需保持红色主题
  - 如需添加其他功能（如返回上一页），需团队讨论
  - 其他开发者可以引用此组件，但禁止修改
  
  ========== 接口调用要求 ==========
  - 不调用任何API接口
  - 只处理路由跳转
  
  ========== 函数关联要求 ==========
  - 被 EventDetail.vue 引用（详情页返回按钮）
  - 使用 Vue Router 进行路由跳转
  - 不依赖其他业务组件
  - 不依赖API调用
-->
<template>
  <button class="back-button" @click="goBack">
    ← 返回
  </button>
</template>

<script>
export default {
  name: 'BackButton',
  methods: {
    goBack() {
      // 功能要求：必须跳转到首页
      // 禁止修改此逻辑，如需其他跳转行为，创建新组件
      this.$router.push('/');
    }
  }
};
</script>

<style scoped>
.back-button {
  padding: 0.5rem 1rem;
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: background 0.2s;
}

.back-button:hover {
  background: #c0392b;
}
</style>

