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
  - 图片路径：/assets/images/{eventId}.png（public目录）
  
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
    <div v-else class="axis-wrapper">
      <div class="axis-container vertical" ref="axisContainer" @wheel="handleWheelZoom">
        <!-- 竖直时间轴线 -->
        <div class="axis-line"></div>

        <!-- 轴内容（用于测量高度，不包含轴线本身） -->
        <div class="axis-content" ref="axisContent">
          <!-- 筛选提示和重置按钮 -->
          <div v-if="isFiltered" class="filter-banner">
            <span class="filter-text">当前显示：{{ currentLocationName || '筛选结果' }}</span>
            <button class="reset-button" @click="resetFilter">显示全部</button>
          </div>
          
          <!-- 事件列表 -->
          <div 
            v-for="(event, index) in events" 
            :key="event.id" 
            class="event-item"
            :class="{ 'event-item-left': index % 2 === 0, 'event-item-right': index % 2 === 1 }"
            :style="getEventItemStyle(event, index)"
            :data-event-id="event.id"
            @click="goToDetail(event.id)"
          >
            <!-- 时间轴节点 -->
            <div class="event-node">
              <div class="node-dot"></div>
              <div class="node-line"></div>
            </div>
            
            <!-- 事件内容 -->
            <div class="event-content">
              <div class="event-date">{{ formatDate(event.date) }}</div>
              <div class="event-card">
                <div class="event-image-wrapper">
                  <img 
                    :src="getEventImage(event.id)" 
                    :alt="event.title" 
                    class="event-image"
                    @error="handleImageError"
                    @load="handleImageLoad"
                  />
                  <div v-if="imageErrors[event.id]" class="image-placeholder">
                    <span class="placeholder-icon">📷</span>
                    <span class="placeholder-text">图片加载失败</span>
                  </div>
                </div>
                <div class="event-info">
                  <h3>{{ event.title }}</h3>
                  <p v-if="event.category" class="event-category">{{ event.category }}</p>
                  <p v-if="event.description" class="event-description">{{ event.description.substring(0, 100) }}{{ event.description.length > 100 ? '...' : '' }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 缩放控件（位于轴下方） -->
      <div class="zoom-controls" aria-label="时间轴缩放">
        <button class="zoom-btn" type="button" @click="zoomOut" :disabled="zoomLevel <= zoomMin">-</button>
        <div class="zoom-info">
          <div class="zoom-title">间距缩放</div>
          <div class="zoom-subtitle">
            {{ Math.round(zoomLevel * 100) }}% · Alt+滚轮
          </div>
        </div>
        <button class="zoom-btn" type="button" @click="zoomIn" :disabled="zoomLevel >= zoomMax">+</button>
        <button class="zoom-btn secondary" type="button" @click="resetZoom" :disabled="Math.abs(zoomLevel - 1) < 0.001">重置</button>
      </div>
    </div>
  </div>
</template>

<script>
import { getEventList, getEventsByLocation } from '@/api/eventApi';
import { formatDateChinese } from '@/utils/formatUtils';

export default {
  name: 'TimeAxis',
  data() {
    return {
      events: [],
      loading: false,
      error: null,
      imageErrors: {}, // 记录图片加载失败的ID
      resizeTimer: null, // 窗口大小改变防抖定时器
      currentLocationId: null, // 当前筛选的地点ID，null表示显示全部
      isFiltered: false, // 是否处于筛选状态
      currentLocationName: null, // 当前筛选的地点名称

      // ===== 时间轴缩放（改变事件间距）=====
      zoomLevel: 1,
      zoomMin: 0.05,
      zoomMax: 2.0,
      // 间距按“相邻事件时间差”计算：gap = clamp(min,max, daysDelta * pxPerDay * zoomLevel)
      gapMinPx: 28,
      gapMaxPx: 220,
      pxPerDay: 1.6,
      // 半错开位移（估算卡片高度）：右侧事件向上提的基础值
      staggerShiftPx: 320
    };
  },
  mounted() {
    // 功能要求：组件挂载时自动加载事件列表
    // 禁止修改此调用逻辑
    this.loadEvents();
    
    // 监听窗口大小改变，更新轴线高度
    window.addEventListener('resize', this.handleResize);
  },
  beforeUnmount() {
    // 组件销毁前移除事件监听器和清理定时器（Vue3 生命周期）
    window.removeEventListener('resize', this.handleResize);
    if (this.resizeTimer) {
      clearTimeout(this.resizeTimer);
    }
  },
  updated() {
    // 数据更新后，更新轴线高度
    this.$nextTick(() => {
      this.updateAxisHeight();
    });
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
    async loadEvents(locationId = null) {
      this.loading = true;
      this.error = null;
      // 更新筛选状态
      this.currentLocationId = locationId;
      this.isFiltered = !!locationId;
      try {
        let response;
        if (locationId) {
          // 如果提供了地点ID，按地点筛选事件
          response = await getEventsByLocation(locationId);
        } else {
          // 否则获取所有事件
          response = await getEventList();
        }
        // 响应格式：{ code: 200, data: Array<Event>, total: number }
        if (response.code === 200) {
          // 默认按时间升序，确保“相邻事件时间差”计算稳定
          const list = (response.data || []).slice();
          list.sort((a, b) => {
            const ta = this.safeTime(a?.date);
            const tb = this.safeTime(b?.date);
            return ta - tb;
          });
          this.events = list;
          // 如果按地点筛选但没有找到事件，显示提示
          if (locationId && (!this.events || this.events.length === 0)) {
            this.error = '该地点暂无关联事件';
          }
          // 数据加载后，等待DOM更新完成再更新轴线高度
          this.$nextTick(() => {
            this.updateAxisHeight();
          });
        } else {
          this.error = response.msg || '事件获取失败';
        }
      } catch (error) {
        console.error('加载事件失败:', error);
        // 更详细的错误信息
        if (locationId) {
          this.error = `获取地点事件失败，请稍后重试 (地点ID: ${locationId})`;
        } else {
          this.error = '事件获取失败，请稍后重试';
        }
      } finally {
        this.loading = false;
      }
    },

    /**
     * 安全解析日期为时间戳（ms）
     * @param {string} dateString
     */
    safeTime(dateString) {
      const t = Date.parse(dateString);
      return Number.isFinite(t) ? t : 0;
    },

    /**
     * 计算相邻事件时间差（天），至少为1天
     */
    getDaysDelta(index) {
      if (index <= 0) return 1;
      const prev = this.events?.[index - 1];
      const cur = this.events?.[index];
      const tPrev = this.safeTime(prev?.date);
      const tCur = this.safeTime(cur?.date);
      if (!tPrev || !tCur) return 1;
      const diff = Math.abs(tCur - tPrev);
      const days = diff / (24 * 60 * 60 * 1000);
      // 过小的时间差也给一点间距
      return Math.max(1, Math.round(days));
    },

    /**
     * 计算第 index 个事件的间距像素（由前后事件时间间隔决定）
     */
    getEventGapPx(index) {
      const days = this.getDaysDelta(index);
      const raw = days * this.pxPerDay * this.zoomLevel;
      const clamped = Math.max(this.gapMinPx, Math.min(this.gapMaxPx, raw));
      return Math.round(clamped);
    },

    /**
     * 为事件项提供动态样式（CSS变量），用于控制间距与半错开位移
     */
    getEventItemStyle(event, index) {
      const gap = this.getEventGapPx(index);
      return {
        '--event-gap': `${gap}px`,
        '--stagger-shift': `${this.staggerShiftPx}px`
      };
    },

    setZoomLevel(next) {
      const clamped = Math.max(this.zoomMin, Math.min(this.zoomMax, next));
      this.zoomLevel = clamped;
      this.$nextTick(() => {
        this.updateAxisHeight();
      });
    },

    zoomIn() {
      this.setZoomLevel(this.zoomLevel * 1.1);
    },

    zoomOut() {
      this.setZoomLevel(this.zoomLevel / 1.1);
    },

    resetZoom() {
      this.setZoomLevel(1);
    },

    /**
     * Alt + 滚轮缩放事件间距（不影响正常滚动）
     */
    handleWheelZoom(e) {
      if (!e || !e.altKey) return;
      // Alt+滚轮时阻止页面缩放/默认滚动，让交互聚焦在时间轴间距缩放
      e.preventDefault();
      const direction = e.deltaY > 0 ? -1 : 1; // 向上滚放大、向下滚缩小（跟大多数编辑器一致）
      // 需求：滚 1 下相当于现在滚 3 下的量 => 使用 (1.08 ^ 3) 作为一步倍率
      const step = 1.08 ** 3;
      const factor = direction > 0 ? step : 1 / step;
      this.setZoomLevel(this.zoomLevel * factor);
    },
    
    /**
     * 按地点筛选事件
     * 功能要求：
     * - 根据地点ID筛选并显示该地点的事件
     * - 用于地图标记点击时的联动
     * - 筛选后滚动到指定事件或第一个事件
     * 
     * @param {string} locationId - 地点ID
     * @param {string} eventId - 可选，要滚动到的事件ID
     * @param {string} locationName - 可选，地点名称
     */
    async filterByLocation(locationId, eventId = null, locationName = null) {
      if (!locationId) {
        console.warn('地点ID为空，无法筛选事件');
        return;
      }
      // 保存地点名称
      this.currentLocationName = locationName;
      // 重新加载事件，传入地点ID
      await this.loadEvents(locationId);
      
      // 等待DOM更新后，滚动到指定事件或第一个事件
      this.$nextTick(() => {
        // 等待一小段时间确保DOM完全渲染
        setTimeout(() => {
          if (eventId) {
            this.scrollToEvent(eventId);
          } else if (this.events && this.events.length > 0) {
            // 如果没有指定事件ID，滚动到第一个事件
            this.scrollToEvent(this.events[0].id);
          }
        }, 100);
      });
    },

    /**
     * 在“完整时间轴”中按地点定位并滚动（不筛选、不替换事件列表）
     * 功能要求：
     * - 点击地图钉点后保持时间轴始终可交互
     * - 不需要用户点“显示全部”恢复
     *
     * @param {string} locationId - 地点ID
     * @param {string|null} locationName - 地点名称（可选，仅用于调试/扩展）
     */
    scrollToLocation(locationId, locationName = null) {
      if (!locationId) return;

      // 优先使用当前 events（完整列表）进行定位
      const target = (this.events || []).find(e => e && (e.locationId === locationId));
      if (target && target.id) {
        this.scrollToEvent(target.id);
        return;
      }

      // 找不到时给出提示（不改变列表）
      console.warn(`未在完整时间轴中找到该地点的事件 (locationId=${locationId}, name=${locationName || ''})`);
    },
    
    /**
     * 重置筛选，显示全部事件
     * 功能要求：
     * - 清除筛选条件
     * - 重新加载所有事件
     */
    resetFilter() {
      this.currentLocationId = null;
      this.isFiltered = false;
      this.currentLocationName = null;
      this.loadEvents();
    },
    
    /**
     * 滚动到指定事件
     * 功能要求：
     * - 平滑滚动到指定事件的位置
     * - 用于地图点击后的联动效果
     * 
     * @param {string} eventId - 事件ID
     */
    scrollToEvent(eventId) {
      if (!eventId) return;

      const container = this.$refs.axisContainer;
      if (!container) {
        console.warn('未找到时间轴容器');
        return;
      }

      // 在时间轴滚动容器内查找目标事件
      const eventElement = container.querySelector(`[data-event-id="${eventId}"]`);
      if (!eventElement) {
        console.warn(`未找到事件元素 (ID: ${eventId})`);
        return;
      }

      // 计算目标位置（事件元素相对于时间轴容器的位置）
      const containerRect = container.getBoundingClientRect();
      const eventRect = eventElement.getBoundingClientRect();
      const startScrollTop = container.scrollTop;
      const targetScrollTop = startScrollTop + (eventRect.top - containerRect.top) - 100; // 让事件显示在容器顶部附近
      const finalScrollTop = Math.max(0, targetScrollTop);
      
      // 自定义平滑滚动，动画时长1.5秒
      const duration = 1500; // 1.5秒
      const startTime = performance.now();
      
      const animateScroll = (currentTime) => {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1); // 0到1之间的进度
        
        // 使用easeInOutCubic缓动函数，使滚动更平滑
        const easeInOutCubic = progress < 0.5
          ? 4 * progress * progress * progress
          : 1 - Math.pow(-2 * progress + 2, 3) / 2;
        
        const currentScrollTop = startScrollTop + (finalScrollTop - startScrollTop) * easeInOutCubic;
        container.scrollTop = currentScrollTop;
        
        if (progress < 1) {
          requestAnimationFrame(animateScroll);
        }
      };
      
      requestAnimationFrame(animateScroll);
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
     * 图片路径规则：/assets/images/{eventId}.png
     * 
     * 修改限制：
     * - 禁止修改路径规则
     * - 如需支持其他图片格式，可以扩展
     */
    getEventImage(eventId) {
      // public目录下的文件在Vite中可以直接通过绝对路径访问
      // 尝试多种图片格式
      const formats = ['png', 'jpg', 'jpeg', 'webp'];
      // 如果已经知道某个格式失败，跳过它
      const failedFormat = this.imageErrors[eventId];
      if (failedFormat) {
        const formatIndex = formats.indexOf(failedFormat);
        if (formatIndex > -1) {
          formats.splice(formatIndex, 1);
        }
      }
      // 默认返回 png，如果失败会在 @error 事件中处理
      return `/assets/images/${eventId}.png`;
    },
    
    /**
     * 处理图片加载错误
     * 功能要求：当图片加载失败时，显示占位符
     */
    handleImageError(event) {
      const img = event.target;
      const eventId = img.src.match(/\/([^\/]+)\.(jpg|jpeg|png|webp)$/)?.[1];
      if (eventId) {
        // 记录失败的图片ID
        this.$set(this.imageErrors, eventId, img.src.split('.').pop());
        // 隐藏图片，显示占位符
        img.style.display = 'none';
      }
    },
    
    /**
     * 处理图片加载成功
     * 功能要求：确保图片显示
     */
    handleImageLoad(event) {
      const img = event.target;
      img.style.display = 'block';
      // 清除错误记录
      const eventId = img.src.match(/\/([^\/]+)\.(jpg|jpeg|png|webp)$/)?.[1];
      if (eventId && this.imageErrors[eventId]) {
        this.$delete(this.imageErrors, eventId);
      }
      // 图片加载完成后，更新轴线高度（因为图片可能改变了事件项的高度）
      this.$nextTick(() => {
        this.updateAxisHeight();
      });
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
    },
    
    /**
     * 更新轴线高度
     * 功能要求：动态计算最后一个事件项的位置，确保轴线延伸到最底部
     */
    updateAxisHeight() {
      const axisContainer = this.$el?.querySelector('.axis-container.vertical');
      const axisLine = this.$el?.querySelector('.axis-line');
      const eventItems = this.$el?.querySelectorAll('.event-item');
      
      const axisContent = this.$refs.axisContent;

      if (axisContainer && axisLine && axisContent && eventItems && eventItems.length > 0) {
        // 关键：用“内容区”的高度来计算轴线高度，避免轴线自身 height 反向撑大 scrollHeight
        const contentHeight = axisContent.scrollHeight;
        const axisHeight = Math.max(400, contentHeight); // 保底高度
        axisLine.style.height = `${axisHeight}px`;
      }
    },
    
    /**
     * 处理窗口大小改变
     * 功能要求：窗口大小改变时更新轴线高度
     */
    handleResize() {
      // 使用防抖，避免频繁更新
      clearTimeout(this.resizeTimer);
      this.resizeTimer = setTimeout(() => {
        this.updateAxisHeight();
      }, 100);
    }
  }
};
</script>

<style scoped lang="scss">
// ========== 竖直时间轴样式 ==========

.axis-wrapper {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.time-axis {
  padding: var(--spacing-xl, 2rem);
  background: var(--bg-white, #FFFFFF);
  border-radius: var(--radius-lg, 12px);
  margin-bottom: var(--spacing-xl, 2rem);
  box-shadow: var(--shadow-md, 0 4px 8px rgba(0, 0, 0, 0.12));
  border: 1px solid var(--border-light, #eeeeee);
  animation: fadeIn 0.6s ease-out;
  
  h2 {
    color: #e74c3c;
    margin: 0 0 2rem 0;
    font-size: 1.5rem;
    font-weight: 600;
    text-align: center;
  }
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

// 筛选提示栏样式
.filter-banner {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.75rem 1rem;
  margin-bottom: 1rem;
  background: linear-gradient(135deg, rgba(231, 76, 60, 0.1) 0%, rgba(192, 57, 43, 0.1) 100%);
  border: 1px solid rgba(231, 76, 60, 0.3);
  border-radius: 8px;
  backdrop-filter: blur(10px);
  
  .filter-text {
    color: #e74c3c;
    font-size: 0.9rem;
    font-weight: 500;
  }
  
  .reset-button {
    padding: 0.4rem 1rem;
    background: #e74c3c;
    color: white;
    border: none;
    border-radius: 6px;
    font-size: 0.85rem;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      background: #c0392b;
      transform: translateY(-1px);
      box-shadow: 0 2px 8px rgba(231, 76, 60, 0.3);
    }
    
    &:active {
      transform: translateY(0);
    }
  }
}

// 竖直时间轴容器样式
.axis-container.vertical {
  position: relative;
  padding: var(--spacing-xl, 2rem) 0 var(--spacing-xxl, 3rem) 0; // 增加底部内边距，确保轴线延伸
  min-height: 400px;
  overflow-y: auto;
  overflow-x: hidden;
  max-height: calc(100vh - 300px);
  // 启用原生平滑滚动（自定义动画仍会覆盖体验）
  scroll-behavior: smooth;
}

.axis-content {
  position: relative;
  z-index: 2;
  // 给内容区底部一点空间，避免最后一个卡片贴边时轴线显得短
  padding-bottom: 30px;
}

// 竖直时间轴线 - 延长到容器底部
.axis-line {
  position: absolute;
  left: 50%;
  top: 0;
  // 默认高度，JavaScript会动态更新
  height: 100%;
  width: 3px;
  background: linear-gradient(to bottom, #e74c3c 0%, #c0392b 100%);
  transform: translateX(-50%);
  z-index: 1;
  border-radius: 2px;
  box-shadow: 0 0 8px rgba(231, 76, 60, 0.3);
  // 确保轴线至少有一定高度
  min-height: 400px;
}

// 事件项样式（半错开布局）
.event-item {
  position: relative;
  display: flex;
  align-items: flex-start;
  cursor: pointer;
  transition: all 0.3s ease;
  // 关键：事件间距按“相邻事件时间差”计算，并可缩放
  margin-bottom: var(--event-gap, var(--spacing-lg, 1.5rem));
  
  &:hover {
    .event-card {
      transform: translateY(-5px);
      box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
    }
    .node-dot {
      transform: scale(1.3);
      box-shadow: 0 0 0 6px rgba(231, 76, 60, 0.2);
    }
  }
  
  // 左侧事件（偶数索引）- 正常位置
  &.event-item-left {
    .event-content {
      margin-right: auto;
      margin-left: 0;
      width: calc(50% - 2rem);
      padding-right: 2rem;
    }
    .event-node {
      left: calc(50% - 8px);
    }
  }
  
  // 右侧事件（奇数索引）- 半错开位置
  &.event-item-right {
    // 使用负的 margin-top 实现“半错开”，并把“间距（按时间差）”纳入计算
    // margin-top = -staggerShift + eventGap
    margin-top: calc(-1 * var(--stagger-shift, 320px) + var(--event-gap, 1.5rem));
    
    .event-content {
      margin-left: auto;
      margin-right: 0;
      width: calc(50% - 2rem);
      padding-left: 2rem;
    }
    .event-node {
      left: calc(50% - 8px);
    }
  }
}

// 缩放控件（轴下方）
.zoom-controls {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 0.75rem;
  border: 1px solid rgba(231, 76, 60, 0.18);
  border-radius: 10px;
  background: rgba(231, 76, 60, 0.06);
}

.zoom-info {
  flex: 1;
  min-width: 120px;
  text-align: center;
  line-height: 1.2;
}

.zoom-title {
  font-size: 0.9rem;
  font-weight: 700;
  color: #c0392b;
}

.zoom-subtitle {
  margin-top: 0.25rem;
  font-size: 0.8rem;
  color: rgba(51, 51, 51, 0.8);
}

.zoom-btn {
  padding: 0.45rem 0.9rem;
  border: none;
  border-radius: 8px;
  background: #e74c3c;
  color: #fff;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;
}

.zoom-btn.secondary {
  background: rgba(231, 76, 60, 0.18);
  color: #c0392b;
  font-weight: 600;
}

.zoom-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(231, 76, 60, 0.22);
}

.zoom-btn:disabled {
  opacity: 0.55;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

// 时间轴节点
.event-node {
  position: absolute;
  left: 50%;
  // 圆点与图片中央对齐
  // 计算：日期标签高度（约35px，包括margin-bottom 0.5rem）+ 图片高度的一半（200px / 2 = 100px）
  top: calc(35px + 100px); // 约135px，与图片中央对齐
  transform: translateX(-50%) translateY(-50%); // 使用translateY让圆点自身中心对齐
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  // 确保节点在轴线上
  pointer-events: none;
}

.node-dot {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #e74c3c;
  border: 3px solid #ffffff;
  box-shadow: 0 0 0 3px #e74c3c, 0 2px 8px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
  z-index: 3;
}

.node-line {
  width: 2px;
  height: 100%;
  background: transparent;
  margin-top: 8px;
}

// 事件内容
.event-content {
  position: relative;
  z-index: 1;
}

.event-date {
  font-size: 0.9rem;
  color: #e74c3c;
  font-weight: 600;
  margin-bottom: 0.5rem;
  padding: 0.25rem 0.75rem;
  background: rgba(231, 76, 60, 0.1);
  border-radius: 12px;
  display: inline-block;
}

.event-card {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;
  border: 1px solid #eeeeee;
}

.event-image-wrapper {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f5f5f5;
  
  .event-image {
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
}

.event-info {
  padding: 1rem;
  
  h3 {
    margin: 0 0 0.5rem 0;
    font-size: 1.1rem;
    color: #333;
    font-weight: 600;
    line-height: 1.4;
  }
  
  .event-category {
    margin: 0 0 0.5rem 0;
    font-size: 0.875rem;
    color: #666;
    font-weight: 500;
  }
  
  .event-description {
    margin: 0;
    font-size: 0.875rem;
    color: #666;
    line-height: 1.6;
  }
}

// 移除不需要的样式

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

// 平板设备适配
@media (max-width: 1024px) {
  .event-item {
    &.event-item-right {
      // 平板设备调整半错开距离（图片高度变小了）
      margin-top: calc(-280px + var(--spacing-lg, 1.5rem));
    }
    
    &.event-item-left,
    &.event-item-right {
      .event-content {
        width: calc(50% - 1.5rem);
      }
    }
  }
  
  .event-image-wrapper {
    height: 180px;
  }
  
  .event-node {
    // 平板设备：图片高度180px，圆点与图片中央对齐
    // 日期标签约35px + 图片高度的一半（180px / 2 = 90px）= 125px
    top: calc(35px + 90px);
  }
}

// 移动端适配
@media (max-width: 768px) {
  .time-axis {
    padding: var(--spacing-md, 1rem);
    
    h2 {
      font-size: 1.25rem;
      margin-bottom: 1.5rem;
    }
  }
  
  .axis-container.vertical {
    padding: var(--spacing-lg, 1.5rem) 0;
    max-height: calc(100vh - 200px);
  }
  
  .axis-line {
    left: 20px;
    width: 2px;
  }
  
  .event-item {
    margin-bottom: var(--spacing-xl, 2rem);
    
    &.event-item-left,
    &.event-item-right {
      // 移动端取消半错开效果，恢复正常间距
      margin-top: 0;
      margin-bottom: var(--spacing-xl, 2rem);
      
      .event-content {
        width: calc(100% - 3rem);
        margin-left: 3rem;
        margin-right: 0;
        padding-left: 1rem;
        padding-right: 0;
      }
    }
    
    .event-node {
      left: 20px;
      // 移动端：图片高度150px，圆点与图片中央对齐
      // 日期标签约35px + 图片高度的一半（150px / 2 = 75px）= 110px
      top: calc(35px + 75px);
    }
  }
  
  .event-image-wrapper {
    height: 150px;
  }
  
  .event-info {
    padding: 0.75rem;
    
    h3 {
      font-size: 1rem;
    }
    
    .event-description {
      font-size: 0.8rem;
    }
  }
}

// 小屏移动端适配
@media (max-width: 480px) {
  .time-axis {
    padding: var(--spacing-sm, 0.5rem);
    
    h2 {
      font-size: 1.1rem;
      margin-bottom: 1rem;
    }
  }
  
  .event-item {
    margin-bottom: var(--spacing-lg, 1.5rem);
    
    &.event-item-left,
    &.event-item-right {
      // 小屏移动端取消半错开效果
      margin-top: 0;
      margin-bottom: var(--spacing-lg, 1.5rem);
      
      .event-content {
        width: calc(100% - 2.5rem);
        margin-left: 2.5rem;
        padding-left: 0.75rem;
      }
    }
    
    .event-node {
      left: 15px;
      // 小屏移动端：图片高度120px，圆点与图片中央对齐
      // 日期标签约30px（字体变小）+ 图片高度的一半（120px / 2 = 60px）= 90px
      top: calc(30px + 60px);
    }
  }
  
  .node-dot {
    width: 12px;
    height: 12px;
    border-width: 2px;
  }
  
  .event-image-wrapper {
    height: 120px;
  }
  
  .event-date {
    font-size: 0.8rem;
    padding: 0.2rem 0.5rem;
  }
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

