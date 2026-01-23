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
.time-axis {
  padding: 2rem;
  background: white;
  border-radius: 8px;
  margin-bottom: 2rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.time-axis h2 {
  margin-bottom: 1.5rem;
  color: #e74c3c;
  font-size: 1.8rem;
  text-align: center;
  padding-bottom: 1rem;
  border-bottom: 2px solid #f0f0f0;
}

.loading,
.error {
  text-align: center;
  padding: 2rem;
  color: #666;
  background: #f9f9f9;
  border-radius: 8px;
  margin: 1rem 0;
}

.axis-container {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.event-item {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  padding: 1.5rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.event-item:hover {
  background: #f5f5f5;
  transform: translateX(10px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.event-date {
  min-width: 120px;
  font-weight: bold;
  color: #e74c3c;
  font-size: 1.1rem;
  text-align: center;
  padding: 0.5rem;
  background: rgba(231, 76, 60, 0.1);
  border-radius: 4px;
}

.event-content {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  flex: 1;
}

.event-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 4px;
  border: 2px solid #ddd;
  transition: all 0.3s ease;
}

.event-item:hover .event-image {
  border-color: #e74c3c;
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(231, 76, 60, 0.3);
}

.event-info h3 {
  margin: 0 0 0.5rem 0;
  color: #333;
  font-size: 1.1rem;
  line-height: 1.4;
}

.event-category {
  color: #666;
  font-size: 0.9rem;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .time-axis {
    padding: 1rem;
    margin-bottom: 1rem;
  }
  
  .time-axis h2 {
    font-size: 1.5rem;
    padding-bottom: 0.5rem;
    margin-bottom: 1rem;
  }
  
  .event-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
    padding: 1.25rem;
  }
  
  .event-date {
    min-width: auto;
    width: 100%;
    text-align: left;
    padding: 0.75rem;
    font-size: 1rem;
  }
  
  .event-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
    width: 100%;
  }
  
  .event-image {
    width: 100%;
    height: 200px;
    max-width: 100%;
  }
  
  .event-info {
    width: 100%;
  }
  
  .event-info h3 {
    font-size: 1.2rem;
    margin-bottom: 0.75rem;
  }
  
  .event-item:hover {
    transform: translateY(5px);
    transform: translateX(5px);
  }
}

/* 平板设备适配 */
@media (min-width: 769px) and (max-width: 1024px) {
  .time-axis {
    padding: 1.5rem;
  }
  
  .event-item {
    gap: 1.25rem;
  }
  
  .event-image {
    width: 100px;
    height: 100px;
  }
  
  .event-date {
    min-width: 100px;
    font-size: 1rem;
  }
}
</style>

