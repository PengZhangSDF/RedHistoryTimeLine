<!--
  相关资料跳转组件（通用组件）
  
  ========== 功能要求 ==========
  1. 展示事件相关的资料链接列表
  2. 点击链接在新窗口打开外部资源
  3. 可复用组件，用于详情页展示相关资料
  
  ========== 修改范围限制 ==========
  - 此文件由UI组件负责人维护
  - 禁止修改链接打开方式（必须新窗口打开）
  - 可以修改样式和布局
  - 如需添加其他功能（如链接预览），需团队讨论
  - 其他开发者可以引用此组件，但禁止修改核心逻辑
  
  ========== 接口调用要求 ==========
  - 不调用任何API接口
  - 接收父组件传入的links数据
  - links格式：Array<{ id, title, url, type }>
  
  ========== 函数关联要求 ==========
  - 被 EventDetail.vue 引用（详情页相关资料）
  - 接收父组件传入的props
  - 不依赖其他业务组件
  - 不依赖API调用
-->
<template>
  <div class="link-jump">
    <h3>相关资料</h3>
    <ul v-if="links && links.length > 0">
      <li v-for="link in links" :key="link.id">
        <a :href="link.url" target="_blank" rel="noopener noreferrer">
          {{ link.title }}
          <span class="link-type">[{{ link.type }}]</span>
        </a>
      </li>
    </ul>
    <p v-else class="no-links">暂无相关资料</p>
  </div>
</template>

<script>
export default {
  name: 'LinkJump',
  props: {
    // 资料链接列表
    // 格式：[{ id: 'mat-001', title: '资料标题', url: 'https://...', type: '历史文献' }]
    links: {
      type: Array,
      default: () => []
    }
  }
};
</script>

<style scoped>
.link-jump {
  margin-top: 2rem;
  padding: 1rem;
  background: #f5f5f5;
  border-radius: 4px;
}

.link-jump h3 {
  margin-bottom: 1rem;
  color: #333;
}

.link-jump ul {
  list-style: none;
  padding: 0;
}

.link-jump li {
  margin: 0.5rem 0;
}

.link-jump a {
  color: #e74c3c;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.link-jump a:hover {
  text-decoration: underline;
}

.link-type {
  font-size: 0.8rem;
  color: #666;
}

.no-links {
  color: #999;
  font-style: italic;
}
</style>

