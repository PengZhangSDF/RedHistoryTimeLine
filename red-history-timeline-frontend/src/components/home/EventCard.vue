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
  - 图片路径：/assets/images/{eventId}.png
  
  ========== 函数关联要求 ==========
  - 被 Home.vue 引用（首页事件卡片列表）
  - 使用 Vue Router 进行路由跳转
  - 引用 utils/formatUtils.js（格式化日期）
  - 不依赖其他业务组件
-->
<template>
  <div class="event-card" @click="goToDetail">
    <div class="card-image-wrapper">
      <img 
        :src="imageUrl" 
        :alt="event.title" 
        class="card-image"
        @error="handleImageError"
        @load="handleImageLoad"
      />
      <div v-if="imageError" class="image-placeholder">
        <span class="placeholder-icon">📷</span>
        <span class="placeholder-text">图片加载失败</span>
      </div>
    </div>
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
  data() {
    return {
      imageError: false
    };
  },
  computed: {
    /**
     * 获取事件图片URL
     * 功能要求：根据事件ID生成图片路径
     * 路径规则：/assets/images/{eventId}.png
     * 
     * 修改限制：禁止修改路径规则
     */
    imageUrl() {
      return `/assets/images/${this.event.id}.png`;
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
     * 处理图片加载错误
     * 功能要求：当图片加载失败时，显示占位符
     */
    handleImageError() {
      this.imageError = true;
    },
    
    /**
     * 处理图片加载成功
     * 功能要求：确保图片显示
     */
    handleImageLoad() {
      this.imageError = false;
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

<style scoped lang="scss">
// ========== 事件卡片样式（滑出效果） ==========

.event-card {
  background: var(--bg-white, #FFFFFF);
  border-radius: var(--radius-lg, 12px);
  box-shadow: var(--shadow-card, 0 2px 8px rgba(0, 0, 0, 0.1));
  border: 1px solid var(--border-light, #eeeeee);
  transition: all 0.3s ease;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  cursor: pointer;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: var(--shadow-card, 0 4px 16px rgba(0, 0, 0, 0.15));
  }
}

.card-image-wrapper {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f5f5f5;
  
  .card-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
  }
  
  .image-placeholder {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background: #f5f5f5;
    color: #999;
    
    .placeholder-icon {
      font-size: 3rem;
      margin-bottom: 0.5rem;
    }
    
    .placeholder-text {
      font-size: 0.875rem;
    }
  }
  
  &:hover .card-image {
    transform: scale(1.05);
  }
}

.event-card.active {
  right: 0;
}

// 卡片头部
.card-header {
  background: var(--gradient-red-primary, linear-gradient(135deg, #E74C3C 0%, #C0392B 100%));
  padding: var(--spacing-lg, 1.5rem);
  color: var(--text-white, #FFFFFF);
  flex-shrink: 0;
}

.card-title {
  font-size: var(--font-xl, 1.25rem);
  font-weight: 700;
  margin: 0 0 var(--spacing-xs, 0.25rem) 0;
  line-height: 1.4;
}

.card-subtitle {
  font-size: var(--font-sm, 0.875rem);
  opacity: 0.9;
  margin: 0;
  font-weight: 400;
}

// 卡片内容区
.card-content {
  padding: var(--spacing-lg, 1.5rem);
  flex: 1;
  overflow-y: auto;
  background: var(--bg-white, #FFFFFF);
}

.card-description {
  color: var(--text-dark, #333333);
  font-size: var(--font-base, 1rem);
  line-height: 1.7;
  margin: 0 0 var(--spacing-lg, 1.5rem) 0;
  text-align: justify;
}

.card-meta {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm, 0.5rem);
  margin-bottom: var(--spacing-lg, 1.5rem);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm, 0.5rem);
  font-size: var(--font-sm, 0.875rem);
  color: var(--text-gray, #666666);
}

.meta-label {
  font-weight: 600;
  color: var(--text-dark, #333333);
  white-space: nowrap;
}

// 卡片底部操作区
.card-footer {
  padding: var(--spacing-lg, 1.5rem);
  border-top: 1px solid var(--border-light, #eeeeee);
  background: var(--bg-light, #f8f9fa);
  flex-shrink: 0;
}

.map-button {
  width: 100%;
  padding: var(--spacing-md, 1rem) var(--spacing-xl, 2rem);
  background: var(--gradient-red-primary, linear-gradient(135deg, #E74C3C 0%, #C0392B 100%));
  color: var(--text-white, #FFFFFF);
  border: none;
  border-radius: var(--radius-md, 8px);
  font-size: var(--font-base, 1rem);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm, 0.5rem);
}

.map-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(231, 76, 60, 0.4);
}

.map-button:active {
  transform: translateY(0);
}

// 关闭按钮
.close-button {
  position: absolute;
  top: var(--spacing-md, 1rem);
  right: var(--spacing-md, 1rem);
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-white, #FFFFFF);
  font-size: var(--font-lg, 1.125rem);
  transition: background 0.3s ease;
  z-index: 10;
}

.close-button:hover {
  background: rgba(255, 255, 255, 0.3);
}

// 遮罩层
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 999;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
}

.overlay.active {
  opacity: 1;
  visibility: visible;
}

// ========== 响应式设计 ==========

@media (max-width: 1280px) {
  .event-card {
    width: 350px;
    right: -350px;
  }
  
  .card-header {
    padding: var(--spacing-md, 1rem);
  }
  
  .card-title {
    font-size: var(--font-lg, 1.125rem);
  }
  
  .card-content {
    padding: var(--spacing-md, 1rem);
  }
  
  .card-description {
    font-size: var(--font-sm, 0.875rem);
  }
  
  .card-footer {
    padding: var(--spacing-md, 1rem);
  }
}

@media (max-width: 768px) {
  .event-card {
    width: 100%;
    right: -100%;
    border-radius: 0;
    max-height: 100vh;
  }
  
  .card-header {
    padding: var(--spacing-lg, 1.5rem);
  }
  
  .card-title {
    font-size: var(--font-xl, 1.25rem);
  }
  
  .card-content {
    padding: var(--spacing-lg, 1.5rem);
  }
  
  .card-description {
    font-size: var(--font-base, 1rem);
  }
}

// ========== 动画效果 ==========

@keyframes slideInRight {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.animate-slideIn {
  animation: slideInRight 0.4s ease-out;
}

.animate-fadeIn {
  animation: fadeIn 0.3s ease-out;
}
</style>

