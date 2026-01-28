<!--
  全屏地图组件（首页组件）
  
  ========== 功能要求 ==========
  1. 在首页显示全屏高德地图
  2. 标记所有事件发生地点
  3. 点击标记跳转到对应事件详情页
  4. 实现地图与时间轴的联动
  
  ========== 修改范围限制 ==========
  - 此文件由地图功能负责人维护
  - 禁止修改地图初始化逻辑（必须使用amapUtils.initMap）
  - 禁止修改标记添加逻辑（必须使用amapUtils.addMarker）
  - 禁止修改点击跳转逻辑（必须触发marker-click事件）
  - 可以修改地图样式和初始视图
  - 如需添加新功能（如地图搜索、路线绘制），需团队讨论
  - 其他开发者如需修改，需提交PR
  
  ========== 接口调用要求 ==========
  - 必须调用 api/locationApi.js 的 getAllLocations 方法
  - 请求无参数
  - 响应数据格式：{ code: 200, data: Array<Location>, total: number }
  - 地点数据格式：{ id, name, province, longitude, latitude, ... }
  - 坐标格式：{ longitude: number, latitude: number } 或 coordinates: [lng, lat]
  
  ========== 函数关联要求 ==========
  - 被 Home.vue 引用（首页全屏地图区域）
  - 引用 api/locationApi.js（获取地点坐标数据）
  - 引用 utils/amapUtils.js（地图初始化和标记操作）
  - 触发 marker-click 事件，传递事件ID给父组件
  - 父组件（Home.vue）处理跳转逻辑
  
  ========== 数据流 ==========
  1. mounted() → initMap() → 初始化地图
  2. loadLocations() → locationApi.getAllLocations() → 获取所有地点
  3. 遍历地点 → addMarker() → 在地图上添加标记
  4. 点击标记 → 触发marker-click事件 → 父组件处理跳转
  
  ========== 事件说明 ==========
  - marker-click: 标记点击事件，参数为事件ID
  - 父组件监听此事件，执行路由跳转
-->
<template>
  <div id="full-screen-map" class="full-screen-map"></div>
</template>

<script>
import { onMounted, onUnmounted } from 'vue';
import { initMap, addMarker } from '@/utils/amapUtils';
import { getAllLocations } from '@/api/locationApi';

export default {
  name: 'FullScreenMap',
  emits: ['marker-click'], // 定义组件事件
  setup(props, { emit }) {
    let map = null;
    let AMap = null;
    const markers = []; // 存储所有标记，便于清理

    /**
     * 组件挂载时初始化地图
     * 功能要求：
     * - 初始化高德地图
     * - 加载地点数据并添加标记
     * 
     * 修改限制：
     * - 禁止修改地图初始化方式
     * - 可以调整地图初始视图（zoom、center）
     */
    onMounted(async () => {
      try {
        // 初始化地图
        // 功能要求：必须使用amapUtils.initMap
        // 修改限制：禁止直接调用高德API
        const mapInstance = await initMap('full-screen-map', {
          zoom: 5, // 中国全境视图
          center: [108.95, 34.27] // 中国中心点坐标
        });
        map = mapInstance.map;
        AMap = mapInstance.AMap;

        // 加载地点数据并添加标记
        await loadLocations();
      } catch (error) {
        console.error('地图初始化失败:', error);
      }
    });

    /**
     * 加载地点数据并添加标记
     * 功能要求：
     * - 调用locationApi.getAllLocations()获取所有地点
     * - 遍历地点数据，为每个地点添加地图标记
     * - 标记点击时触发marker-click事件
     * 
     * 修改限制：
     * - 禁止修改API调用方式
     * - 禁止修改标记添加方式
     * - 可以优化标记样式和交互
     */
    const loadLocations = async () => {
      try {
        const response = await getAllLocations();
        // 响应格式：{ code: 200, data: Array<Location>, total: number }
        if (response.code === 200) {
          const locations = response.data || [];
          
          locations.forEach(location => {
            // 获取坐标（支持两种格式）
            let coordinates = null;
            if (location.coordinates && Array.isArray(location.coordinates)) {
              coordinates = location.coordinates;
            } else if (location.longitude && location.latitude) {
              coordinates = [location.longitude, location.latitude];
            }
            
            if (coordinates && coordinates.length === 2) {
              // 添加标记
              // 功能要求：必须使用amapUtils.addMarker
              // 修改限制：禁止直接创建AMap.Marker
              const marker = addMarker(
                map,
                AMap,
                coordinates,
                location.name,
                () => {
                  // 点击标记的处理
                  // 功能要求：必须触发marker-click事件
                  // 修改限制：禁止直接跳转，必须通过事件通知父组件
                  if (location.relatedEvents && location.relatedEvents.length > 0) {
                    // 如果地点关联了事件，触发事件并传递第一个事件ID
                    emit('marker-click', location.relatedEvents[0]);
                  } else if (location.id) {
                    // 如果没有关联事件，可以传递地点ID（需要父组件处理）
                    // 或者通过locationId查询事件
                    emit('marker-click', location.id);
                  }
                }
              );
              markers.push(marker);
            }
          });
        }
      } catch (error) {
        console.error('加载地点数据失败:', error);
      }
    };

    /**
     * 组件卸载时清理资源
     * 功能要求：销毁地图实例，释放内存
     * 修改限制：禁止删除此清理逻辑
     */
    onUnmounted(() => {
      if (map) {
        map.destroy();
        map = null;
      }
      markers.length = 0;
    });

    /**
     * 定位地图到指定坐标
     * @param {Array<number>} coordinates - 坐标数组 [lng, lat]
     * @param {string} locationName - 地点名称
     * 
     * 功能要求：
     * - 地图中心定位到指定坐标
     * - 可选：添加动画效果
     * - 可选：放大地图层级
     */
    const locateToCoordinate = (coordinates, locationName) => {
      if (!map || !coordinates || coordinates.length !== 2) return;
      
      // 设置地图中心到指定坐标，添加动画效果
      map.setCenter(coordinates);
      map.setZoom(12);
      
      // 可选：添加弹跳动画
      map.setCenter(coordinates, true);
      
      // 可选：显示信息窗口
      const infoWindow = new AMap.InfoWindow({
        content: `<div style="padding: 10px;"><h3>${locationName}</h3></div>`,
        offset: new AMap.Pixel(0, -30)
      });
      
      infoWindow.open(map, coordinates);
    };

    // 暴露方法给父组件
    return {
      locateToCoordinate
    };
  }
};
</script>

<style scoped>
.full-screen-map {
  width: 100%;
  height: 100vh;
  position: relative;
  transition: all 0.3s ease;
  animation: mapFadeIn 0.6s ease-out;
}

/* 地图标记样式（通过CSS变量控制，可在JS中动态修改） */
:deep(.amap-marker) {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform-origin: center bottom;
}

:deep(.amap-marker:hover) {
  transform: scale(1.1);
  z-index: 1000;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.2));
}

/* 地图标记点击缩放动画 */
:deep(.amap-marker:active) {
  transform: scale(0.95);
  transition: all 0.1s ease;
}

/* 地图标记选中状态 */
:deep(.amap-marker.active) {
  transform: scale(1.2);
  filter: drop-shadow(0 6px 12px rgba(231, 76, 60, 0.4));
}

:deep(.amap-info-window) {
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  border: none;
  overflow: hidden;
  transition: all 0.3s ease;
  transform-origin: center bottom;
}

:deep(.amap-info-window:hover) {
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.25);
}

:deep(.amap-info-content) {
  padding: 1.25rem;
  font-size: 0.95rem;
  line-height: 1.5;
  background: #ffffff;
  border-radius: 12px;
}

/* 信息窗口标题样式 */
:deep(.amap-info-content h3) {
  margin: 0 0 0.5rem 0;
  color: #e74c3c;
  font-size: 1.1rem;
  font-weight: 600;
  line-height: 1.3;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .full-screen-map {
    height: 70vh;
  }
  
  :deep(.amap-info-content) {
    padding: 1rem;
    font-size: 0.9rem;
  }
  
  :deep(.amap-info-content h3) {
    font-size: 1rem;
  }
  
  /* 手机端地图标记缩放效果增强 */
  :deep(.amap-marker:hover) {
    transform: scale(1.2);
  }
  
  /* 手机端信息窗口适配 */
  :deep(.amap-info-window) {
    max-width: 90vw;
    min-width: 250px;
  }
}

/* 平板设备适配 */
@media (min-width: 769px) and (max-width: 1024px) {
  .full-screen-map {
    height: 80vh;
  }
  
  :deep(.amap-info-content) {
    padding: 1.1rem;
    font-size: 0.92rem;
  }
}

/* 小屏幕笔记本适配 */
@media (min-width: 1025px) and (max-width: 1440px) {
  .full-screen-map {
    height: 90vh;
  }
}

/* 大屏设备适配 */
@media (min-width: 1441px) {
  .full-screen-map {
    height: 95vh;
  }
  
  :deep(.amap-info-content) {
    padding: 1.5rem;
    font-size: 1rem;
  }
  
  :deep(.amap-info-content h3) {
    font-size: 1.2rem;
  }
}

/* 动画效果 */
@keyframes mapFadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 地图加载动画 */
.full-screen-map::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 40px;
  height: 40px;
  margin: -20px 0 0 -20px;
  border: 3px solid rgba(231, 76, 60, 0.3);
  border-radius: 50%;
  border-top-color: #e74c3c;
  animation: mapLoading 1s ease-in-out infinite;
  z-index: 9999;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.3s ease;
}

.full-screen-map.loading::before {
  opacity: 1;
}

@keyframes mapLoading {
  to {
    transform: rotate(360deg);
  }
}
</style>

