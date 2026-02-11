<!--
  首页视图组件
  
  ========== 功能要求 ==========
  1. 整合首页所需的所有组件（时间轴、事件卡片、全屏地图）
  2. 实现首页的布局和交互
  3. 处理地图标记点击事件，跳转到详情页
  4. 实现时间轴和地图的联动效果
  
  ========== 修改范围限制 ==========
  - 此文件由首页功能负责人维护
  - 禁止修改组件引用关系（必须引用TimeAxis、EventCard、FullScreenMap）
  - 禁止修改路由跳转逻辑（必须跳转到 /detail/:id）
  - 可以修改页面布局和样式
  - 可以优化数据加载和性能
  - 其他开发者如需修改，需提交PR
  
  ========== 接口调用要求 ==========
  - 必须调用 api/eventApi.js 的 getEventList 方法
  - 请求参数：无（获取所有事件）
  - 响应数据格式：{ code: 200, data: Array<Event>, total: number }
  - 事件数据传递给TimeAxis和EventCard组件
  
  ========== 函数关联要求 ==========
  - 被 router/index.js 引用（路由 /）
  - 引用 components/home/TimeAxis.vue（时间轴组件）
  - 引用 components/home/EventCard.vue（事件卡片组件）
  - 引用 components/home/FullScreenMap.vue（全屏地图组件）
  - 引用 api/eventApi.js（获取事件数据）
  - 监听FullScreenMap的marker-click事件
  - 使用Vue Router进行路由跳转
  
  ========== 数据流 ==========
  1. mounted() → loadEvents() → eventApi.getEventList()
  2. 获取数据 → 更新events数组 → 传递给TimeAxis和EventCard
  3. FullScreenMap加载地点数据 → 显示地图标记
  4. 点击地图标记 → handleMarkerClick(eventId) → router.push('/detail/:id')
  5. 点击时间轴/卡片 → 跳转到详情页
-->
<template>
  <div class="home-page">
    <header class="page-header">
      <h1>抗日战争时间轴</h1>
      <p class="subtitle">重温历史，铭记初心</p>
    </header>
    
    <div class="home-content">
      <!-- 左侧：时间轴和事件卡片 -->
      <div class="left-panel">
        <!-- 时间轴组件 -->
        <!-- 功能要求：显示历史事件时间轴 -->
        <!-- 修改限制：禁止删除此组件引用 -->
        <TimeAxis ref="timeAxisRef" @event-click="handleEventClick" />
        
        <!-- 事件卡片列表 -->
        <!-- 功能要求：以卡片形式展示事件 -->
        <!-- 修改限制：禁止修改v-for循环和事件传递 -->
        <div class="event-cards-section">
          <h2>历史事件</h2>
          <div class="event-cards">
            <EventCard 
              v-for="event in events" 
              :key="event.id"
              :event="event"
            />
          </div>
        </div>
      </div>
      
      <!-- 右侧：全屏地图 -->
      <div class="right-panel">
        <!-- 全屏地图组件 -->
        <!-- 功能要求：显示所有事件地点的地图标记 -->
        <!-- 修改限制：禁止删除此组件引用 -->
        <!-- 事件处理：监听marker-click事件，跳转到详情页 -->
        <FullScreenMap
          ref="mapRef"
          @marker-click="handleMarkerClick"
          @marker-image-click="handleMarkerImageClick"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import TimeAxis from '@/components/home/TimeAxis.vue';
import EventCard from '@/components/home/EventCard.vue';
import FullScreenMap from '@/components/home/FullScreenMap.vue';
import { getEventList } from '@/api/eventApi';
import { useRouter } from 'vue-router';

export default {
  name: 'Home',
  components: {
    // 功能要求：必须注册这三个组件
    // 修改限制：禁止删除组件注册
    TimeAxis,
    EventCard,
    FullScreenMap
  },
  setup() {
    const router = useRouter();
    const events = ref([]);
    const timeAxisRef = ref(null);
    const mapRef = ref(null);

    /**
     * 加载事件列表
     * 功能要求：
     * - 调用eventApi.getEventList()获取所有事件
     * - 更新events数组，供EventCard组件使用
     * 
     * 修改限制：
     * - 禁止修改API调用方式
     * - 可以优化加载状态和错误处理
     */
    const loadEvents = async () => {
      try {
        const response = await getEventList();
        // 响应格式：{ code: 200, data: Array<Event>, total: number }
        if (response.code === 200) {
          events.value = response.data || [];
        }
      } catch (error) {
        console.error('加载事件失败:', error);
        // TODO: 可以添加错误提示UI
      }
    };

    /**
     * 处理地图标记点击事件
     * 功能要求：
     * - 接收FullScreenMap组件触发的marker-click事件
     * - 点击地图标记后，时间轴自动滚动到对应事件位置
     * - 不跳转到详情页，只滚动时间轴
     * 
     * 修改限制：
     * - 禁止修改跳转路径格式（必须 /detail/:id）
     * - 禁止修改跳转方式（必须使用router.push）
     * - 参数可能是事件ID、地点ID或地点对象，需要处理
     */
    const handleMarkerClick = (data) => {
      // 点击地图标记后，时间轴自动滚动到对应位置
      if (!data) return;
      
      let locationId = null;
      let locationName = null;
      let eventId = null;
      
      // 解析数据，获取地点ID、地点名称和事件ID
      if (typeof data === 'string') {
        if (data.startsWith('E')) {
          // 如果是事件ID，需要先找到对应的地点ID
          // 这里暂时只处理地点ID的情况
          return;
        } else {
          // 地点ID
          locationId = data;
        }
      } else if (typeof data === 'object') {
        // 如果是对象，提取地点信息和事件ID
        locationId = data.locationId;
        locationName = data.locationName;
        eventId = data.eventId;
      }
      
      // 关键修复：点击地图钉点不再“筛选/锁定”时间轴（不替换事件列表），只做定位滚动
      if (!timeAxisRef.value) return;

      // 优先按事件ID滚动（最精确）
      if (eventId && typeof timeAxisRef.value.scrollToEvent === 'function') {
        timeAxisRef.value.scrollToEvent(eventId);
        return;
      }

      // 没有事件ID时，按地点ID在完整事件列表中查找并滚动到该地点的第一个事件
      if (locationId && typeof timeAxisRef.value.scrollToLocation === 'function') {
        timeAxisRef.value.scrollToLocation(locationId, locationName || null);
      }
    };

    /**
     * 点击“标记上方图片”进入详情页（钉点点击联动时间轴不变）
     */
    const handleMarkerImageClick = (payload) => {
      const eventId = payload && typeof payload === 'object' ? payload.eventId : null;
      if (!eventId) return;
      router.push(`/detail/${eventId}`);
    };

    /**
     * 处理时间轴事件点击事件
     * 功能要求：
     * - 接收TimeAxis组件触发的event-click事件
     * - 调用地图组件的locateToCoordinate方法定位到对应地点
     * - 实现时间轴与地图的联动
     * - 处理地图初始化失败和无匹配地点的情况
     */
    const handleEventClick = (event) => {
      if (!event) return;
      
      // 检查地图组件是否初始化成功
      if (!mapRef.value) {
        console.warn('地图组件未初始化成功，无法定位地点');
        return;
      }
      
      // 获取事件的坐标信息
      let coordinates = null;
      if (event.coordinates && Array.isArray(event.coordinates)) {
        coordinates = event.coordinates;
      } else if (event.location && event.location.longitude && event.location.latitude) {
        coordinates = [event.location.longitude, event.location.latitude];
      }
      
      // 调用地图组件的定位方法
      if (coordinates) {
        try {
          mapRef.value.locateToCoordinate(coordinates, event.location || event.name);
        } catch (err) {
          console.error('地图定位失败:', err);
          // 可以添加更友好的错误提示
        }
      } else {
        console.warn('该事件无地点信息，无法定位');
        // 可以添加提示信息
      }
    };

    // 组件挂载时加载事件列表
    onMounted(() => {
      loadEvents();
    });

    return {
      events,
      timeAxisRef,
      mapRef,
      loadEvents,
      handleMarkerClick,
      handleMarkerImageClick,
      handleEventClick
    };
  }
};
</script>

<style scoped>
.home-page {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.page-header {
  /* 调小标题栏高度与内边距 */
  padding: 0.8rem 1.5rem;
  background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
  color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.page-header h1 {
  margin: 0;
  font-size: 1.6rem;
}

.subtitle {
  margin: 0;
  opacity: 0.9;
  font-size: 0.95rem;
  text-align: right;
}

.home-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.left-panel {
  width: 40%;
  overflow-y: auto;
  padding: 1rem;
  background: #f5f5f5;
}

.event-cards-section {
  margin-top: 2rem;
}

.event-cards-section h2 {
  color: #e74c3c;
  margin-bottom: 1rem;
}

.event-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1rem;
}

.right-panel {
  width: 60%;
  position: relative;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .home-content {
    flex-direction: column;
  }
  
  .left-panel,
  .right-panel {
    width: 100%;
  }
  
  .right-panel {
    height: 50vh;
  }
}
</style>

