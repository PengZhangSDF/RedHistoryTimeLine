/**
 * 高德地图API密钥配置
 * 
 * ========== 功能要求 ==========
 * 1. 统一管理高德地图API Key
 * 2. 避免在代码中硬编码密钥
 * 3. 便于在不同环境使用不同的Key
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由项目负责人维护
 * - 禁止在代码中直接写API Key
 * - 如需更换Key，只修改此文件
 * 
 * ========== 接口调用要求 ==========
 * - 使用方式：import amapKey from '/config/amapKey.js'
 * - 在amapUtils.js中引用此配置
 * - 生产环境应使用环境变量，此文件仅用于开发环境
 * 
 * ========== 函数关联要求 ==========
 * - 被 amapUtils.js 引用
 * - 被 FullScreenMap.vue 间接引用（通过amapUtils）
 * - 被 AmapLocation.vue 间接引用（通过amapUtils）
 */
export default {
  key: '8b32b5bec8feb5ffee48cb4cf2941ce7', // 请替换为实际的高德地图API Key
  version: '2.0',
  plugins: ['AMap.Marker', 'AMap.InfoWindow', 'AMap.Geocoder']
};

