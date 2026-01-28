<!--
  时间轴组件（首页核心组件）
  
  ========== 功能要求 ==========
  1. 渲染历史事件的时间轴可视化
  2. 展示事件列表，按时间顺序排列
  3. 显示事件图片和标题
  4. 支持点击事件跳转到详情页
  5. 支持时间筛选（可选功能）
  
  ========== 修改范围限制 ==========
  - 此文件由时间轴功能负责人维护
  - 禁止修改事件列表的获取逻辑（必须调用eventApi.getEventList）
  - 禁止修改点击跳转逻辑（必须跳转到 /detail/:id）
  - 可以修改时间轴的展示样式和布局
  - 如需添加新功能（如事件筛选、搜索），需团队讨论
  - 其他开发者如需修改，需提交PR
  
  ========== 接口调用要求 ==========
  - 必须调用 api/eventApi.js 的 getEventList 方法
  - 请求参数：{ startDate, endDate, locationId }（可选）
  - 响应数据格式：{ code: 200, data: Array<Event>, total: number }
  - 事件数据格式：{ id, title, date, category, description, ... }
  - 图片路径：/assets/images/{eventId}.jpg（public目录）
  
  ========== 函数关联要求 ==========
  - 被 Home.vue 引用（首页时间轴区域）
  - 引用 api/eventApi.js（获取事件数据）
  - 引用 utils/formatUtils.js（格式化日期）
  - 使用 Vue Router 进行路由跳转
  - 不直接引用其他业务组件
  
  ========== 数据流 ==========
  1. mounted() → loadEvents() → eventApi.getEventList()
  2. 获取数据 → 更新 events 数组
  3. 渲染事件列表 → 显示图片和标题
  4. 点击事件 → goToDetail(eventId) → router.push('/detail/:id')
-->
<template>
  <div class="time-axis">
    <h2>历史时间轴</h2>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else class="axis-container">
      <div 
        v-for="event in events" 
        :key="event.id" 
        class="event-item"
        @click="goToDetail(event.id)"
      >
        <div class="event-date">{{ formatDate(event.date) }}</div>
        <div class="event-content">
          <img :src="getEventImage(event.id)" :alt="event.title" class="event-image" />
          <div class="event-info">
            <h3>{{ event.title }}</h3>
            <p class="event-category">{{ event.category }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getEventList } from '@/api/eventApi';
import { formatDateChinese } from '@/utils/formatUtils';

export default {
  name: 'TimeAxis',
  data() {
    return {
      events: [],
      loading: false,
      error: null
    };
  },
  mounted() {
    // 功能要求：组件挂载时自动加载事件列表
    // 禁止修改此调用逻辑
    this.loadEvents();
  },
  methods: {
    /**
     * 加载事件列表
     * 功能要求：
     * - 调用 eventApi.getEventList() 获取事件数据
     * - 处理加载状态和错误状态
     * - 更新 events 数组
     * 
     * 修改限制：
     * - 禁止修改API调用方式
     * - 可以优化错误处理和加载状态显示
     */
    async loadEvents() {
      this.loading = true;
      this.error = null;
      try {
        const response = await getEventList();
        // 响应格式：{ code: 200, data: Array<Event>, total: number }
        if (response.code === 200) {
          this.events = response.data || [];
        } else {
          this.error = response.msg || '加载失败';
        }
      } catch (error) {
        console.error('加载事件失败:', error);
        this.error = '加载事件失败，请稍后重试';
      } finally {
        this.loading = false;
      }
    },
    
    /**
     * 格式化日期
     * 功能要求：将日期字符串转换为中文格式
     * 修改限制：禁止修改，使用工具函数
     */
    formatDate(dateString) {
      return formatDateChinese(dateString);
    },
    
    /**
     * 获取事件图片路径
     * 功能要求：根据事件ID生成图片路径
     * 图片路径规则：/assets/images/{eventId}.jpg
     * 
     * 修改限制：
     * - 禁止修改路径规则
     * - 如需支持其他图片格式，可以扩展
     */
    getEventImage(eventId) {
      // public目录下的文件在Vite中可以直接通过绝对路径访问
      return `/assets/images/${eventId}.jpg`;
    },
    
    /**
     * 跳转到事件详情页
     * 功能要求：点击事件跳转到详情页
     * 
     * 修改限制：
     * - 禁止修改跳转路径格式（必须 /detail/:id）
     * - 禁止修改跳转方式（必须使用router.push）
     */
    goToDetail(eventId) {
      this.$router.push(`/detail/${eventId}`);
    }
  }
};
</script>

<style scoped>
// ========== 水平时间轴样式 ==========

.time-axis {
  padding: var(--spacing-xl, 2rem);
  background: var(--bg-white, #FFFFFF);
  border-radius: var(--radius-lg, 12px);
  margin-bottom: var(--spacing-xl, 2rem);
  box-shadow: var(--shadow-md, 0 4px 8px rgba(0, 0, 0, 0.12));
  border: 1px solid var(--border-light, #eeeeee);
  animation: fadeIn 0.6s ease-out;
}

// 顶部导航栏样式
.nav-bar {
  background: var(--gradient-nav, linear-gradient(90deg, #4A6CF7 0%, #7B61FF 100%));
  padding: var(--spacing-xl, 2rem) var(--spacing-lg, 1.5rem);
  border-radius: var(--radius-lg, 12px);
  margin-bottom: var(--spacing-xl, 2rem);
  text-align: center;
}

.nav-title {
  color: var(--text-white, #FFFFFF);
  font-size: var(--font-3xl, 1.875rem);
  font-weight: 700;
  margin: 0 0 var(--spacing-sm, 0.5rem) 0;
  letter-spacing: 2px;
}

.nav-subtitle {
  color: rgba(255, 255, 255, 0.9);
  font-size: var(--font-base, 1rem);
  margin: 0;
  font-weight: 400;
}

// 筛选区样式
.filter-section {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg, 1.5rem);
  margin-bottom: var(--spacing-xxl, 3rem);
  padding: var(--spacing-lg, 1.5rem);
  background: var(--bg-light, #f8f9fa);
  border-radius: var(--radius-md, 8px);
  flex-wrap: wrap;
}

.filter-label {
  color: var(--text-dark, #333333);
  font-size: var(--font-base, 1rem);
  font-weight: 500;
  white-space: nowrap;
}

.filter-select,
.filter-input {
  padding: var(--spacing-sm, 0.5rem) var(--spacing-md, 1rem);
  border: 1px solid var(--border-gray, #CCCCCC);
  border-radius: var(--radius-sm, 4px);
  font-size: var(--font-base, 1rem);
  min-width: 180px;
  background: var(--bg-white, #FFFFFF);
  transition: border-color 0.3s ease;
}

.filter-select:focus,
.filter-input:focus {
  outline: none;
  border-color: var(--primary-blue, #4A6CF7);
  box-shadow: 0 0 0 2px rgba(74, 108, 247, 0.1);
}

.filter-divider {
  color: var(--text-light, #999999);
  font-size: var(--font-sm, 0.875rem);
}

.filter-button {
  margin-left: auto;
  padding: var(--spacing-sm, 0.5rem) var(--spacing-xl, 2rem);
  background: var(--gradient-button, linear-gradient(135deg, #4A6CF7 0%, #7B61FF 100%));
  color: var(--text-white, #FFFFFF);
  border: none;
  border-radius: var(--radius-sm, 4px);
  font-size: var(--font-base, 1rem);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.filter-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(74, 108, 247, 0.4);
}

// 水平时间轴线样式
.axis-container {
  position: relative;
  padding: var(--spacing-xxl, 3rem) 0 var(--spacing-xl, 2rem) 0;
  overflow-x: auto;
  overflow-y: hidden;
  min-height: 200px;
}

.axis-line {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 60px;
  height: 2px;
  background: var(--border-gray, #CCCCCC);
  z-index: 1;
}

// 年份刻度样式
.year-labels {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 20px;
  display: flex;
  justify-content: space-between;
  padding: 0 var(--spacing-lg, 1.5rem);
  z-index: 2;
}

.year-label {
  color: var(--text-gray, #666666);
  font-size: var(--font-sm, 0.875rem);
  font-weight: 500;
  white-space: nowrap;
}

// 事件节点列表
.event-nodes {
  position: relative;
  display: flex;
  justify-content: space-around;
  gap: var(--spacing-lg, 1.5rem);
  z-index: 3;
  padding: 0 var(--spacing-lg, 1.5rem);
}

// 事件节点样式
.event-node {
  position: relative;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 120px;
}

.node-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: var(--node-orange, #FF9500);
  position: relative;
  transition: all 0.3s ease;
  z-index: 4;
}

.event-node:hover .node-dot {
  width: 14px;
  height: 14px;
  transform: scale(1.2);
  box-shadow: 0 0 0 4px rgba(255, 149, 0, 0.2);
}

.event-node.active .node-dot {
  width: 14px;
  height: 14px;
  background: var(--node-red, #E74C3C);
  box-shadow: 0 0 0 3px var(--node-white, #FFFFFF), 0 0 0 6px var(--node-red, #E74C3C);
}

// 悬浮事件名样式
.node-tooltip {
  position: absolute;
  bottom: 30px;
  background: var(--bg-white, #FFFFFF);
  border: 1px solid var(--border-gray, #CCCCCC);
  padding: var(--spacing-sm, 0.5rem) var(--spacing-md, 1rem);
  border-radius: var(--radius-sm, 4px);
  color: var(--text-dark, #333333);
  font-size: var(--font-sm, 0.875rem);
  font-weight: 500;
  white-space: nowrap;
  box-shadow: var(--shadow-sm, 0 2px 4px rgba(0, 0, 0, 0.1));
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  z-index: 5;
}

.event-node:hover .node-tooltip {
  opacity: 1;
  visibility: visible;
  bottom: 35px;
}

.node-tooltip::after {
  content: '';
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  border: 6px solid transparent;
  border-top-color: var(--bg-white, #FFFFFF);
  border-bottom: none;
}

// 事件名称（节点下方）
.node-label {
  position: absolute;
  bottom: -40px;
  color: var(--text-dark, #333333);
  font-size: var(--font-sm, 0.875rem);
  font-weight: 500;
  white-space: nowrap;
  text-align: center;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
}

// 底部统计信息
.footer-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--spacing-xl, 2rem);
  border-top: 1px solid var(--border-light, #eeeeee);
  margin-top: var(--spacing-xl, 2rem);
}

.stat-info {
  color: var(--text-gray, #666666);
  font-size: var(--font-base, 1rem);
  font-weight: 500;
}

.hint-info {
  color: var(--text-light, #999999);
  font-size: var(--font-sm, 0.875rem);
}

// 加载和错误状态
.loading,
.error {
  text-align: center;
  padding: var(--spacing-xl, 2rem);
  color: var(--text-gray, #666666);
  background: var(--bg-light, #f8f9fa);
  border-radius: var(--radius-md, 8px);
  margin: var(--spacing-md, 1rem) 0;
  border: 1px solid var(--border-light, #eeeeee);
}

// ========== 响应式设计 ==========

@media (max-width: 1280px) {
  .time-axis {
    padding: var(--spacing-lg, 1.5rem);
  }
  
  .nav-title {
    font-size: var(--font-2xl, 1.5rem);
  }
  
  .filter-section {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-button {
    margin-left: 0;
    width: 100%;
  }
  
  .event-nodes {
    gap: var(--spacing-md, 1rem);
  }
  
  .event-node {
    min-width: 100px;
  }
  
  .node-label {
    font-size: var(--font-xs, 0.75rem);
    max-width: 100px;
  }


@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>

