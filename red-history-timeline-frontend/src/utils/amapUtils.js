/**
 * 高德地图工具类
 * 
 * ========== 功能要求 ==========
 * 1. 封装高德地图的初始化方法（initMap）
 * 2. 封装地图标记添加方法（addMarker）
 * 3. 封装地址解析方法（geocodeAddress，地址→坐标）
 * 4. 统一管理地图相关操作，避免重复代码
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由地图功能负责人维护
 * - 禁止修改initMap、addMarker的核心逻辑
 * - 如需添加新功能（如绘制路线、区域等），需团队讨论
 * - 其他开发者如需使用地图功能，必须通过此工具类
 * - 禁止在其他组件中直接调用高德API
 * 
 * ========== 接口调用要求 ==========
 * - 调用高德地图JavaScript API
 * - 使用 @amap/amap-jsapi-loader 加载API
 * - API Key通过环境变量或配置文件获取
 * - 所有地图操作必须通过此工具类，禁止直接调用AMap对象
 * 
 * ========== 函数关联要求 ==========
 * - 被 FullScreenMap.vue 引用（首页地图）
 * - 被 AmapLocation.vue 引用（详情页地图）
 * - 引用高德地图API（通过AMapLoader）
 * - 不引用业务API（保持工具类独立性）
 * 
 * ========== 使用示例 ==========
 * import { initMap, addMarker } from '@/utils/amapUtils'
 * 
 * // 初始化地图
 * const { map, AMap } = await initMap('map-container', { zoom: 10 })
 * 
 * // 添加标记
 * addMarker(map, AMap, [116.397428, 39.90923], '地点名称', () => {
 *   console.log('标记被点击')
 * })
 */
import AMapLoader from '@amap/amap-jsapi-loader';

// 高德地图配置（从环境变量或配置文件获取）
const amapConfig = {
  key: import.meta.env.VITE_AMAP_KEY || 'YOUR_AMAP_API_KEY',
  version: '2.0',
  plugins: ['AMap.Marker', 'AMap.InfoWindow', 'AMap.Geocoder']
};

/**
 * 初始化高德地图
 * @param {string} containerId - 地图容器的DOM ID
 * @param {Object} options - 地图配置选项 { zoom, center, ... }
 * @returns {Promise<{map, AMap}>} 返回地图实例和AMap对象
 * 
 * 功能要求：
 * - 异步加载高德地图API
 * - 创建地图实例
 * - 返回map和AMap对象供后续使用
 * 
 * 修改限制：
 * - 禁止修改AMapLoader.load的配置
 * - 禁止修改返回值的结构
 */
export async function initMap(containerId, options = {}) {
  const AMap = await AMapLoader.load({
    key: amapConfig.key,
    version: amapConfig.version,
    plugins: amapConfig.plugins
  });
  
  const map = new AMap.Map(containerId, {
    zoom: 10,
    center: [116.397428, 39.90923], // 默认中心点（北京）
    ...options
  });
  
  return { map, AMap };
}

/**
 * 添加地图标记
 * @param {Object} map - 地图实例
 * @param {Object} AMap - AMap对象
 * @param {Array<number>} position - 坐标 [lng, lat]
 * @param {string} title - 标记标题（鼠标悬停显示）
 * @param {Function} onClick - 点击事件回调函数
 * @returns {Object} 返回标记对象
 * 
 * 功能要求：
 * - 在地图上添加标记点
 * - 支持点击事件
 * - 返回标记对象，便于后续操作（如删除）
 * 
 * 修改限制：
 * - 禁止修改标记的创建方式
 * - 如需修改标记样式，需团队讨论
 */
export function addMarker(map, AMap, position, title, onClick) {
  const marker = new AMap.Marker({
    position: position,
    title: title
  });
  
  if (onClick) {
    marker.on('click', onClick);
  }
  
  map.add(marker);
  return marker;
}

/**
 * 根据地址获取坐标（地理编码）
 * @param {Object} AMap - AMap对象
 * @param {string} address - 地址字符串
 * @returns {Promise<Array<number>>} 返回坐标 [lng, lat]
 * 
 * 功能要求：
 * - 调用高德地理编码API
 * - 将地址转换为坐标
 * - 返回坐标数组
 * 
 * 修改限制：
 * - 禁止修改API调用方式
 * - 如需添加错误重试机制，需团队讨论
 */
export function geocodeAddress(AMap, address) {
  return new Promise((resolve, reject) => {
    const geocoder = new AMap.Geocoder();
    geocoder.getLocation(address, (status, result) => {
      if (status === 'complete' && result.geocodes.length) {
        const location = result.geocodes[0].location;
        resolve([location.lng, location.lat]);
      } else {
        reject(new Error('地址解析失败'));
      }
    });
  });
}

