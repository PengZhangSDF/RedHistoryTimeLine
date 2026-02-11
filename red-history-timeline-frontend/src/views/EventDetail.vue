<!--
  事件详情页视图组件
  
  ========== 功能要求 ==========
  1. 整合详情页所需的所有组件（事件详情、多媒体、地图、人物、资料跳转）
  2. 加载事件的完整数据（事件、地点、媒体、人物、资料）
  3. 实现详情页的完整功能展示
  4. 处理数据加载状态和错误状态
  
  ========== 修改范围限制 ==========
  - 此文件由详情页功能负责人维护
  - 禁止修改组件引用关系（必须引用所有detail组件和common组件）
  - 禁止修改API调用方式（必须通过对应的API文件）
  - 可以修改页面布局和样式
  - 可以优化数据加载逻辑和性能
  - 其他开发者如需修改，需提交PR
  
  ========== 接口调用要求 ==========
  - 必须调用 api/eventApi.js 的 getEventDetail 方法
  - 必须调用 api/locationApi.js 的 getLocationDetail 方法（如果事件有locationId）
  - 必须调用 api/mediaApi.js 的 getMediaByEvent 方法
  - 人物数据通过RelatedPerson组件内部调用API
  - 所有API调用必须在setup()或mounted()中进行
  
  ========== 函数关联要求 ==========
  - 被 router/index.js 引用（路由 /detail/:id）
  - 引用 components/common/BackButton.vue（返回按钮）
  - 引用 components/detail/EventDetail.vue（事件详情主体）
  - 引用 components/detail/MediaPlayer.vue（多媒体播放器）
  - 引用 components/detail/AmapLocation.vue（地图定位）
  - 引用 components/detail/RelatedPerson.vue（相关人物）
  - 引用 components/common/LinkJump.vue（资料跳转）
  - 引用所有相关的API文件
  - 使用Vue Router获取路由参数（事件ID）
  
  ========== 数据流 ==========
  1. 路由进入 → 获取路由参数eventId
  2. loadEventData() → 并行调用多个API获取完整数据
  3. 更新响应式数据 → 传递给各个子组件
  4. 子组件根据数据渲染内容
-->
<template>
  <div class="event-detail-page detail-page">
    <!-- 返回按钮 -->
    <!-- 功能要求：提供返回首页的功能 -->
    <!-- 修改限制：禁止删除此组件 -->
    <div class="page-header">
      <BackButton />
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading">加载中...</div>
    </div>
    
    <!-- 错误状态 -->
    <div v-else-if="error" class="error-container">
      <div class="error">{{ error }}</div>
      <button @click="loadEventData" class="retry-button">重试</button>
    </div>
    
    <!-- 详情内容 -->
    <div v-else-if="event" class="detail-content">
      <!-- 事件详情主体 -->
      <!-- 功能要求：显示事件的完整信息 -->
      <!-- 修改限制：禁止删除此组件，必须传递event数据 -->
      <EventDetail :event="event" />
      
      <!-- 多媒体播放器 -->
      <!-- 功能要求：播放事件相关的图片、视频、音乐 -->
      <!-- 修改限制：禁止删除此组件，必须传递media数据 -->
      <MediaPlayer 
        :images="media.images"
        :videos="media.videos"
        :audios="media.audios"
      />
      
      <!-- 高德地图定位 -->
      <!-- 功能要求：显示事件发生地点的地图定位 -->
      <!-- 修改限制：禁止删除此组件，必须传递location数据 -->
      <AmapLocation :location="location" />
      
      <!-- 相关人物 -->
      <!-- 功能要求：显示事件相关的历史人物 -->
      <!-- 修改限制：禁止删除此组件，必须传递eventId -->
      <!-- 注意：此组件内部会调用API，无需在此处传递人物数据 -->
      <RelatedPerson :event-id="eventId" />
      
      <!-- 相关资料跳转 -->
      <!-- 功能要求：显示事件相关的资料链接 -->
      <!-- 修改限制：禁止删除此组件，必须传递relatedLinks数据 -->
      <LinkJump :links="relatedLinks" />
    </div>
    
    <!-- 事件未找到 -->
    <div v-else class="not-found">
      <p>事件未找到</p>
      <button @click="$router.push('/')" class="back-home-button">返回首页</button>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import BackButton from '@/components/common/BackButton.vue';
import EventDetail from '@/components/detail/EventDetail.vue';
import MediaPlayer from '@/components/detail/MediaPlayer.vue';
import AmapLocation from '@/components/detail/AmapLocation.vue';
import RelatedPerson from '@/components/detail/RelatedPerson.vue';
import LinkJump from '@/components/common/LinkJump.vue';
import { getEventDetail } from '@/api/eventApi';
import { getLocationDetail } from '@/api/locationApi';
import { getMediaByEvent } from '@/api/mediaApi';
import { getMaterialsByEvent } from '@/api/materialApi';

export default {
  name: 'EventDetailPage',
  components: {
    // 功能要求：必须注册所有详情页组件
    // 修改限制：禁止删除组件注册
    BackButton,
    EventDetail,
    MediaPlayer,
    AmapLocation,
    RelatedPerson,
    LinkJump
  },
  setup() {
    const route = useRoute();
    // 功能要求：从路由参数获取事件ID
    // 修改限制：禁止修改参数获取方式
    const eventId = route.params.id;
    
    // 响应式数据
    const event = ref(null);
    const location = ref(null);
    const media = ref({ images: [], videos: [], audios: [] });
    const relatedLinks = ref([]);
    const loading = ref(true);
    const error = ref(null);

    /**
     * 加载事件完整数据
     * 功能要求：
     * - 并行加载事件详情、地点信息、媒体资源
     * - 处理加载状态和错误状态
     * - 更新所有响应式数据
     * 
     * 修改限制：
     * - 禁止修改API调用方式
     * - 可以优化加载逻辑（如添加缓存）
     * - 可以优化错误处理
     */
    const loadEventData = async () => {
      if (!eventId) {
        error.value = '事件ID无效';
        loading.value = false;
        return;
      }

      loading.value = true;
      error.value = null;

      try {
        // 并行加载所有数据
        // 功能要求：必须调用这些API
        // 修改限制：禁止修改API调用方式
        const [eventResponse, mediaResponse, materialsResponse] = await Promise.all([
          getEventDetail(eventId),
          getMediaByEvent(eventId),
          getMaterialsByEvent(eventId)
        ]);

        // 处理事件数据
        if (eventResponse.code === 200) {
          event.value = eventResponse.data;
          
          // 如果有locationId，加载地点信息
          if (event.value.locationId) {
            try {
              const locationResponse = await getLocationDetail(event.value.locationId);
              if (locationResponse.code === 200) {
                location.value = locationResponse.data;
              }
            } catch (err) {
              console.warn('加载地点信息失败:', err);
              // 地点加载失败不影响页面显示
            }
          }
          
          // 处理相关资料：
          // 1) 优先使用后端 materials 表返回的数据
          // 2) 如果没有，则退回事件对象中的 relatedMaterials 字段（兼容旧数据）
          if (materialsResponse && materialsResponse.code === 200) {
            const materials = materialsResponse.data || [];
            relatedLinks.value = materials.map((m) => ({
              id: m.id,
              title: m.title,
              url: m.url,
              type: m.type || '其他'
            }));
          } else {
            relatedLinks.value = event.value.relatedMaterials || [];
          }
        } else {
          error.value = eventResponse.msg || '加载事件失败';
        }

        // 处理媒体数据
        if (mediaResponse.code === 200) {
          const allMedia = mediaResponse.data || [];
          // 按类型分类
          media.value = {
            images: allMedia.filter(m => m.type === 'image'),
            videos: allMedia.filter(m => m.type === 'video'),
            audios: allMedia.filter(m => m.type === 'audio')
          };
        }

      } catch (err) {
        console.error('加载事件数据失败:', err);
        error.value = '加载数据失败，请稍后重试';
      } finally {
        loading.value = false;
      }
    };

    /**
     * 组件挂载时加载数据
     * 功能要求：页面加载时自动获取事件数据
     * 修改限制：禁止删除此调用
     */
    onMounted(() => {
      loadEventData();
    });

    return {
      eventId,
      event,
      location,
      media,
      relatedLinks,
      loading,
      error,
      loadEventData
    };
  }
};
</script>

<style scoped>
.event-detail-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.page-header {
  padding: 1rem 2rem;
  background: white;
  border-bottom: 1px solid #ddd;
}

.loading-container,
.error-container,
.not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  padding: 2rem;
}

.loading {
  font-size: 1.2rem;
  color: #666;
}

.error {
  font-size: 1.1rem;
  color: #e74c3c;
  margin-bottom: 1rem;
}

.retry-button,
.back-home-button {
  padding: 0.5rem 1.5rem;
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.retry-button:hover,
.back-home-button:hover {
  background: #c0392b;
}

.detail-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.not-found p {
  font-size: 1.2rem;
  color: #666;
  margin-bottom: 1rem;
}
</style>

