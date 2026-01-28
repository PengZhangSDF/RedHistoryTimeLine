<!--
  事件详情主体组件（详情页组件）
  
  ========== 功能要求 ==========
  1. 展示事件的完整信息（标题、日期、描述、背景、影响等）
  2. 作为详情页的核心内容组件
  3. 与其他详情页组件（多媒体、地图、人物）配合使用
  
  ========== 修改范围限制 ==========
  - 此文件由事件详情功能负责人维护
  - 禁止修改props的接收方式（必须接收event对象）
  - 可以修改展示样式和布局
  - 可以添加新字段的显示
  - 其他开发者如需修改，需提交PR
  
  ========== 接口调用要求 ==========
  - 不直接调用API接口
  - 接收父组件传入的event数据
  - 事件数据格式：{ id, title, date, category, description, background, impact, ... }
  
  ========== 函数关联要求 ==========
  - 被 EventDetail.vue（页面组件）引用
  - 引用 utils/formatUtils.js（格式化日期）
  - 不依赖其他业务组件
-->
<template>
  <div class="event-detail" v-if="event">
    <h1>{{ event.title }}</h1>
    <div class="event-meta">
      <span class="date">{{ formatDate(event.date) }}</span>
      <span v-if="event.category" class="category">{{ event.category }}</span>
      <span v-if="event.importance" class="importance" :class="`importance-${event.importance}`">
        {{ event.importance }}
      </span>
    </div>
    
    <div class="event-description" v-if="event.description">
      <h2>事件描述</h2>
      <p>{{ event.description }}</p>
    </div>
    
    <div class="event-background" v-if="event.background">
      <h2>历史背景</h2>
      <p>{{ event.background }}</p>
    </div>
    
    <div class="event-process" v-if="event.process">
      <h2>发展经过</h2>
      <p>{{ event.process }}</p>
    </div>
    
    <div class="event-impact" v-if="event.impact">
      <h2>历史影响</h2>
      <p>{{ event.impact }}</p>
    </div>
  </div>
  <div v-else class="loading">加载中...</div>
</template>

<script>
import { formatDateChinese } from '@/utils/formatUtils';

export default {
  name: 'EventDetail',
  props: {
    // 事件数据对象
    // 必须包含：id, title, date
    // 可选包含：category, importance, description, background, process, impact
    event: {
      type: Object,
      default: null
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
    }
  }
};
</script>

<style scoped>
.event-detail {
  padding: 2.5rem;
  max-width: 1200px;
  margin: 0 auto;
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  transform-origin: center;
  animation: eventDetailFadeIn 0.8s cubic-bezier(0.4, 0, 0.2, 1) forwards;
  opacity: 0;
  transform: translateY(30px) scale(0.95);
}

.event-detail h1 {
  color: #e74c3c;
  margin-bottom: 1.5rem;
  font-size: 2.25rem;
  line-height: 1.3;
  text-align: center;
  padding: 1.5rem 0;
  border-bottom: 3px solid #f0f0f0;
  position: relative;
}

.event-detail h1::after {
  content: '';
  position: absolute;
  bottom: -3px;
  left: 50%;
  transform: translateX(-50%);
  width: 100px;
  height: 3px;
  background: #e74c3c;
  border-radius: 2px;
}

.event-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 1.25rem;
  margin-bottom: 2.5rem;
  padding-bottom: 2rem;
  border-bottom: 2px solid #eee;
  justify-content: center;
  animation: eventMetaSlideIn 0.6s cubic-bezier(0.4, 0, 0.2, 1) 0.2s forwards;
  opacity: 0;
  transform: translateY(20px);
}

.event-meta .date {
  font-size: 1.2rem;
  font-weight: bold;
  color: #e74c3c;
  padding: 0.75rem 1.5rem;
  background: rgba(231, 76, 60, 0.1);
  border-radius: 8px;
  border-left: 4px solid #e74c3c;
  box-shadow: 0 4px 12px rgba(231, 76, 60, 0.15);
  transition: all 0.3s ease;
}

.event-meta .date:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(231, 76, 60, 0.2);
}

.event-meta .category,
.event-meta .importance {
  padding: 0.75rem 1.25rem;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.event-meta .category:hover,
.event-meta .importance:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.event-meta .category {
  background: #f0f0f0;
  color: #666;
  border-left: 4px solid #999;
}

.event-meta .importance {
  color: white;
  font-weight: 600;
  border-left: 4px solid rgba(255, 255, 255, 0.3);
}

.importance-极高 {
  background: #e74c3c;
}

.importance-高 {
  background: #f39c12;
}

.importance-中 {
  background: #27ae60;
}

.importance-低 {
  background: #3498db;
}

.event-description,
.event-background,
.event-process,
.event-impact {
  margin: 2.5rem 0;
  padding: 2rem;
  background: #f9f9f9;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid #f0f0f0;
  animation: sectionFadeIn 0.6s cubic-bezier(0.4, 0, 0.2, 1) forwards;
  opacity: 0;
  transform: translateY(20px);
}

/* 为不同部分设置不同的动画延迟 */
.event-description {
  animation-delay: 0.3s;
}

.event-background {
  animation-delay: 0.4s;
}

.event-process {
  animation-delay: 0.5s;
}

.event-impact {
  animation-delay: 0.6s;
}

.event-description:hover,
.event-background:hover,
.event-process:hover,
.event-impact:hover {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  transform: translateY(-4px);
  border-color: #e74c3c;
}

.event-description h2,
.event-background h2,
.event-process h2,
.event-impact h2 {
  color: #333;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 3px solid #e74c3c;
  font-size: 1.4rem;
  font-weight: 600;
  position: relative;
  display: inline-block;
}

.event-description h2::after,
.event-background h2::after,
.event-process h2::after,
.event-impact h2::after {
  content: '';
  position: absolute;
  bottom: -3px;
  left: 0;
  width: 60px;
  height: 3px;
  background: #333;
  border-radius: 2px;
}

.event-description p,
.event-background p,
.event-process p,
.event-impact p {
  line-height: 1.8;
  color: #555;
  text-align: justify;
  font-size: 1rem;
  margin-bottom: 1rem;
}

.event-description p:last-child,
.event-background p:last-child,
.event-process p:last-child,
.event-impact p:last-child {
  margin-bottom: 0;
}

.loading {
  text-align: center;
  padding: 3rem;
  color: #666;
  background: #f9f9f9;
  border-radius: 12px;
  margin: 2rem 0;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  animation: loadingPulse 1.5s ease-in-out infinite;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .event-detail {
    padding: 1.75rem;
    margin: 1rem;
    border-radius: 12px;
    animation: eventDetailMobileFadeIn 0.8s cubic-bezier(0.4, 0, 0.2, 1) forwards;
  }
  
  .event-detail h1 {
    font-size: 1.75rem;
    padding: 1rem 0;
    margin-bottom: 1.25rem;
  }
  
  .event-meta {
    flex-direction: column;
    align-items: stretch;
    gap: 1rem;
    margin-bottom: 2rem;
    padding-bottom: 1.5rem;
  }
  
  .event-meta .date {
    font-size: 1.1rem;
    padding: 0.75rem 1rem;
    width: 100%;
    text-align: center;
  }
  
  .event-meta .category,
  .event-meta .importance {
    padding: 0.6rem 1rem;
    font-size: 0.9rem;
    text-align: center;
  }
  
  .event-description,
  .event-background,
  .event-process,
  .event-impact {
    margin: 1.75rem 0;
    padding: 1.5rem;
    animation: sectionMobileFadeIn 0.6s cubic-bezier(0.4, 0, 0.2, 1) forwards;
  }
  
  .event-description h2,
  .event-background h2,
  .event-process h2,
  .event-impact h2 {
    font-size: 1.2rem;
    margin-bottom: 1.25rem;
    padding-bottom: 0.75rem;
  }
  
  .event-description p,
  .event-background p,
  .event-process p,
  .event-impact p {
    font-size: 0.95rem;
    line-height: 1.7;
    text-align: left;
  }
  
  .loading {
    padding: 2rem;
    margin: 1rem;
  }
}

/* 平板设备适配 */
@media (min-width: 769px) and (max-width: 1024px) {
  .event-detail {
    padding: 2rem;
    margin: 1.5rem;
  }
  
  .event-detail h1 {
    font-size: 2rem;
  }
  
  .event-description p,
  .event-background p,
  .event-process p,
  .event-impact p {
    font-size: 0.975rem;
  }
}

/* 大屏设备适配 */
@media (min-width: 1441px) {
  .event-detail {
    padding: 3rem;
    max-width: 1400px;
  }
  
  .event-detail h1 {
    font-size: 2.5rem;
  }
  
  .event-description h2,
  .event-background h2,
  .event-process h2,
  .event-impact h2 {
    font-size: 1.5rem;
  }
  
  .event-description p,
  .event-background p,
  .event-process p,
  .event-impact p {
    font-size: 1.05rem;
    line-height: 1.9;
  }
}

/* 动画效果 */
@keyframes eventDetailFadeIn {
  0% {
    opacity: 0;
    transform: translateY(30px) scale(0.95);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes eventDetailMobileFadeIn {
  0% {
    opacity: 0;
    transform: translateY(20px) scale(0.98);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes eventMetaSlideIn {
  0% {
    opacity: 0;
    transform: translateY(20px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes sectionFadeIn {
  0% {
    opacity: 0;
    transform: translateY(20px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes sectionMobileFadeIn {
  0% {
    opacity: 0;
    transform: translateY(15px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes loadingPulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.05);
  }
}
</style>

