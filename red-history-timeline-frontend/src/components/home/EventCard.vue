<!--
  事件卡片组件（首页组件）
  
  ========== 功能要求 ==========
  1. 展示单个事件的卡片样式
  2. 显示事件图片和标题
  3. 显示事件日期
  4. 点击卡片跳转到详情页
  
  ========== 修改范围限制 ==========
  - 此文件由UI组件负责人维护
  - 禁止修改点击跳转逻辑（必须跳转到 /detail/:id）
  - 可以修改卡片样式和布局
  - 可以添加新字段显示（如重要性、类别等）
  - 其他开发者可以引用此组件，但禁止修改核心逻辑
  
  ========== 接口调用要求 ==========
  - 不直接调用API接口
  - 接收父组件传入的event数据
  - 事件数据格式：{ id, title, date, category, ... }
  - 图片路径：/assets/images/{eventId}.jpg
  
  ========== 函数关联要求 ==========
  - 被 Home.vue 引用（首页事件卡片列表）
  - 使用 Vue Router 进行路由跳转
  - 引用 utils/formatUtils.js（格式化日期）
  - 不依赖其他业务组件
-->
<template>
  <div class="event-card" @click="goToDetail">
    <img :src="imageUrl" :alt="event.title" class="card-image" />
    <div class="card-content">
      <h3>{{ event.title }}</h3>
      <p class="card-date">{{ formatDate(event.date) }}</p>
      <p v-if="event.category" class="card-category">{{ event.category }}</p>
    </div>
  </div>
</template>

<script>
import { formatDateChinese } from '@/utils/formatUtils';

export default {
  name: 'EventCard',
  props: {
    // 事件数据对象
    // 必须包含：id, title, date
    // 可选包含：category, importance, description
    event: {
      type: Object,
      required: true,
      validator(value) {
        return value.id && value.title && value.date;
      }
    }
  },
  computed: {
    /**
     * 获取事件图片URL
     * 功能要求：根据事件ID生成图片路径
     * 路径规则：/assets/images/{eventId}.jpg
     * 
     * 修改限制：禁止修改路径规则
     */
    imageUrl() {
      return `/assets/images/${this.event.id}.jpg`;
    }
  },
  methods: {
    /**
     * 格式化日期
     * 功能要求：将日期转换为中文格式
     * 修改限制：禁止修改，使用工具函数
     */
    formatDate(dateString) {
      return formatDateChinese(dateString);
    },
    
    /**
     * 跳转到详情页
     * 功能要求：点击卡片跳转到事件详情页
     * 
     * 修改限制：
     * - 禁止修改跳转路径格式
     * - 禁止修改跳转方式
     */
    goToDetail() {
      this.$router.push(`/detail/${this.event.id}`);
    }
  }
};
</script>

<style scoped>
.event-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  background: white;
}

.event-card:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.card-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  display: block;
}

.card-content {
  padding: 1rem;
}

.card-content h3 {
  margin: 0 0 0.5rem 0;
  color: #333;
  font-size: 1.1rem;
}

.card-date {
  color: #e74c3c;
  font-weight: bold;
  margin: 0.5rem 0;
  font-size: 0.9rem;
}

.card-category {
  color: #666;
  font-size: 0.85rem;
  margin: 0.5rem 0 0 0;
}
</style>

