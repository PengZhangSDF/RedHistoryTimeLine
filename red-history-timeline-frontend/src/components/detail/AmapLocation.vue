<!--
  高德地图定位组件（详情页组件）
  
  ========== 功能要求 ==========
  1. 在详情页显示事件发生地点的地图定位
  2. 使用高德地图API标注事件位置
  3. 显示地点信息和坐标
  4. 地图自动定位到事件发生地
  
  ========== 修改范围限制 ==========
  - 此文件由地图功能负责人维护
  - 禁止修改地图初始化逻辑（必须使用amapUtils.initMap）
  - 禁止修改标记添加逻辑（必须使用amapUtils.addMarker）
  - 可以修改地图样式和视图
  - 如需添加新功能（如路线绘制、周边搜索），需团队讨论
  - 其他开发者如需修改，需提交PR
  
  ========== 接口调用要求 ==========
  - 不直接调用API接口
  - 接收父组件传入的location数据
  - 地点数据格式：{ id, name, province, longitude, latitude, ... }
  - 坐标格式：{ longitude: number, latitude: number } 或 coordinates: [lng, lat]
  - 如果location为null，不显示地图
  
  ========== 函数关联要求 ==========
  - 被 EventDetail.vue（页面组件）引用
  - 引用 utils/amapUtils.js（地图初始化和标记操作）
  - 不依赖其他业务组件
  - 不依赖API调用
  
  ========== 数据流 ==========
  1. 接收location prop
  2. 如果location存在且有坐标 → initMap() → 初始化地图并定位
  3. addMarker() → 在地图上添加标记
  4. 显示地点信息
-->
<template>
  <div class="amap-location" v-if="location">
    <h2>事件发生地点</h2>
    <div id="detail-map" class="detail-map"></div>
    <div class="location-info">
      <p class="location-name"><strong>{{ location.name }}</strong></p>
      <p v-if="location.province" class="location-province">{{ location.province }}</p>
      <p v-if="coordinates" class="location-coordinates">
        坐标: {{ formatCoordinates(coordinates) }}
      </p>
      <p v-if="location.description" class="location-description">{{ location.description }}</p>
    </div>
  </div>
</template>

<script>
import { onMounted, onUnmounted, watch } from 'vue';
import { initMap, addMarker } from '@/utils/amapUtils';
import { formatCoordinates } from '@/utils/formatUtils';

export default {
  name: 'AmapLocation',
  props: {
    // 地点数据对象
    // 必须包含坐标信息：{ longitude, latitude } 或 coordinates: [lng, lat]
    // 可选包含：name, province, description
    location: {
      type: Object,
      default: null
    }
  },
  setup(props) {
    let map = null;
    let AMap = null;
    let marker = null;

    /**
     * 获取坐标数组
     * 功能要求：从location对象中提取坐标
     * 支持格式：
     * - { coordinates: [lng, lat] }
     * - { longitude: lng, latitude: lat }
     */
    const getCoordinates = () => {
      if (!props.location) return null;
      
      if (props.location.coordinates && Array.isArray(props.location.coordinates)) {
        return props.location.coordinates;
      } else if (props.location.longitude && props.location.latitude) {
        return [props.location.longitude, props.location.latitude];
      }
      
      return null;
    };

    /**
     * 初始化地图并添加标记
     * 功能要求：
     * - 初始化高德地图
     * - 定位到事件发生地
     * - 添加标记
     * 
     * 修改限制：
     * - 禁止修改地图初始化方式
     * - 禁止修改标记添加方式
     */
    const initMapWithMarker = async () => {
      const coordinates = getCoordinates();
      if (!coordinates || coordinates.length !== 2) {
        console.warn('地点坐标无效，无法显示地图');
        const container = document.getElementById('detail-map');
        if (container) {
          container.innerHTML = `
            <div style="display: flex; align-items: center; justify-content: center; height: 100%; 
                        background: #f5f5f5; color: #666; font-size: 14px; text-align: center;">
              地点坐标无效，无法显示地图
            </div>
          `;
        }
        return;
      }

      // 验证坐标是否为有效数字
      if (typeof coordinates[0] !== 'number' || typeof coordinates[1] !== 'number' ||
          isNaN(coordinates[0]) || isNaN(coordinates[1])) {
        console.warn('地点坐标格式错误:', coordinates);
        const container = document.getElementById('detail-map');
        if (container) {
          container.innerHTML = `
            <div style="display: flex; align-items: center; justify-content: center; height: 100%; 
                        background: #f5f5f5; color: #666; font-size: 14px; text-align: center;">
              地点坐标格式错误
            </div>
          `;
        }
        return;
      }

      try {
        // 初始化地图
        // 功能要求：必须使用amapUtils.initMap
        // 修改限制：禁止直接调用高德API
        const mapInstance = await initMap('detail-map', {
          zoom: 15, // 详细视图
          center: coordinates // 定位到事件发生地
        });
        map = mapInstance.map;
        AMap = mapInstance.AMap;

        // 添加标记
        // 功能要求：必须使用amapUtils.addMarker
        // 修改限制：禁止直接创建AMap.Marker
        marker = addMarker(
          map,
          AMap,
          coordinates,
          props.location.name || '事件发生地'
        );
      } catch (error) {
        console.error('地图初始化失败:', error);
        const container = document.getElementById('detail-map');
        if (container) {
          container.innerHTML = `
            <div style="display: flex; align-items: center; justify-content: center; height: 100%; 
                        background: #f5f5f5; color: #e74c3c; font-size: 14px; text-align: center; padding: 20px;">
              <div>
                <p style="margin: 0 0 10px 0; font-weight: bold;">地图加载失败</p>
                <p style="margin: 0; font-size: 12px; color: #666;">${error.message || '请检查网络连接或刷新页面重试'}</p>
              </div>
            </div>
          `;
        }
      }
    };

    /**
     * 组件挂载时初始化地图
     * 功能要求：如果location存在且有坐标，初始化地图
     */
    onMounted(() => {
      if (props.location) {
        initMapWithMarker();
      }
    });

    /**
     * 监听location变化
     * 功能要求：当location prop变化时，重新初始化地图
     * 修改限制：禁止删除此监听逻辑
     */
    watch(() => props.location, (newLocation) => {
      // 清理旧地图
      if (map) {
        map.destroy();
        map = null;
        marker = null;
      }
      
      // 初始化新地图
      if (newLocation) {
        // 等待DOM更新
        setTimeout(() => {
          initMapWithMarker();
        }, 100);
      }
    }, { deep: true });

    /**
     * 组件卸载时清理资源
     * 功能要求：销毁地图实例，释放内存
     * 修改限制：禁止删除此清理逻辑
     */
    onUnmounted(() => {
      if (map) {
        map.destroy();
        map = null;
        marker = null;
      }
    });

    return {
      coordinates: getCoordinates(),
      formatCoordinates
    };
  }
};
</script>

<style scoped>
.amap-location {
  margin: 2rem 0;
  padding: 1.5rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.amap-location:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.amap-location h2 {
  color: #e74c3c;
  margin-bottom: 1rem;
  font-size: 1.5rem;
  font-weight: 600;
  padding-bottom: 0.75rem;
  border-bottom: 2px solid #f0f0f0;
}

.detail-map {
  width: 100%;
  height: 400px;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 1.5rem;
  border: 2px solid #ddd;
  transition: all 0.3s ease;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.detail-map:hover {
  border-color: #e74c3c;
  box-shadow: 0 6px 12px rgba(231, 76, 60, 0.2);
}

/* 地图标记样式和动画 */
:deep(.amap-marker) {
  transition: all 0.3s ease;
}

:deep(.amap-marker:hover) {
  transform: scale(1.1);
  z-index: 1000;
}

:deep(.amap-marker:active) {
  transform: scale(1.2);
}

:deep(.amap-info-window) {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border: none;
  overflow: hidden;
  transition: all 0.3s ease;
}

:deep(.amap-info-content) {
  padding: 1rem;
  font-size: 0.9rem;
  line-height: 1.4;
}

.location-info {
  padding: 1.5rem;
  background: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.location-info:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.location-name {
  font-size: 1.2rem;
  color: #333;
  margin: 0 0 1rem 0;
}

.location-name strong {
  color: #e74c3c;
  font-weight: 600;
}

.location-province {
  color: #666;
  margin: 0 0 0.75rem 0;
  font-size: 0.95rem;
}

.location-coordinates {
  color: #666;
  font-size: 0.9rem;
  margin: 0 0 0.75rem 0;
  font-family: monospace;
  padding: 0.5rem;
  background: rgba(0, 0, 0, 0.05);
  border-radius: 4px;
  border-left: 3px solid #e74c3c;
}

.location-description {
  color: #555;
  line-height: 1.6;
  margin: 0;
  font-size: 0.95rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .amap-location {
    margin: 1.5rem 0;
    padding: 1.25rem;
  }
  
  .amap-location h2 {
    font-size: 1.3rem;
    padding-bottom: 0.5rem;
    margin-bottom: 0.75rem;
  }
  
  .detail-map {
    height: 300px;
    margin-bottom: 1.25rem;
  }
  
  .location-info {
    padding: 1.25rem;
  }
  
  .location-name {
    font-size: 1.1rem;
    margin-bottom: 0.75rem;
  }
  
  .location-province,
  .location-coordinates {
    margin-bottom: 0.5rem;
  }
  
  /* 手机端地图标记缩放效果增强 */
  :deep(.amap-marker:hover) {
    transform: scale(1.2);
  }
  
  :deep(.amap-info-content) {
    padding: 0.75rem;
    font-size: 0.85rem;
  }
}

/* 平板设备适配 */
@media (min-width: 769px) and (max-width: 1024px) {
  .amap-location {
    margin: 1.75rem 0;
    padding: 1.4rem;
  }
  
  .detail-map {
    height: 350px;
  }
}
</style>

