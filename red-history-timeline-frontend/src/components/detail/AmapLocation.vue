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
}

.amap-location h2 {
  color: #e74c3c;
  margin-bottom: 1rem;
}

.detail-map {
  width: 100%;
  height: 400px;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 1rem;
  border: 2px solid #ddd;
}

.location-info {
  padding: 1rem;
  background: #f5f5f5;
  border-radius: 4px;
}

.location-name {
  font-size: 1.2rem;
  color: #333;
  margin: 0.5rem 0;
}

.location-name strong {
  color: #e74c3c;
}

.location-province {
  color: #666;
  margin: 0.5rem 0;
}

.location-coordinates {
  color: #666;
  font-size: 0.9rem;
  margin: 0.5rem 0;
  font-family: monospace;
}

.location-description {
  color: #555;
  line-height: 1.6;
  margin: 0.5rem 0;
}
</style>

