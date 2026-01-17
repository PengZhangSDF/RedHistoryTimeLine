/**
 * 地点相关API接口
 * 
 * ========== 功能要求 ==========
 * 1. 封装所有与地点相关的后端接口调用
 * 2. 提供地点坐标、关联事件等接口方法
 * 3. 用于地图标记和定位功能
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由地图功能负责人维护
 * - 禁止修改现有函数的参数和返回值格式
 * - 如需添加新接口，需先确认后端接口已实现
 * - 其他开发者如需调用地点接口，必须通过此文件
 * 
 * ========== 接口调用要求 ==========
 * - 所有方法必须调用 utils/request.js
 * - 请求路径：/api/locations 或 /api/locations/{id}
 * - 响应格式：{ code: 200, msg: 'success', data: {...}, total: 0 }
 * - 地点数据必须包含坐标字段：{ longitude, latitude } 或 coordinates: [lng, lat]
 * 
 * ========== 函数关联要求 ==========
 * - 被 FullScreenMap.vue 引用（获取所有地点坐标，用于地图标记）
 * - 被 AmapLocation.vue 引用（获取事件地点坐标，用于定位）
 * - 被 EventDetail.vue 间接引用（通过AmapLocation组件）
 * - 引用 utils/request.js
 * 
 * ========== 后端接口对应关系 ==========
 * - getAllLocations() → GET /api/locations (LocationController.getAllLocations)
 * - getLocationDetail(id) → GET /api/locations/{id} (LocationController.getLocationById)
 * - getLocationsByEvent(eventId) → GET /api/locations/event/{eventId} (LocationController.getLocationsByEvent)
 */
import request from '@/utils/request';

/**
 * 获取所有地点列表（包含坐标）
 * @returns {Promise<Object>} 返回 { code: 200, data: Array<Location>, total: number }
 * 
 * 功能要求：
 * - 获取所有地点数据，用于首页地图标记
 * - 每个地点必须包含坐标信息
 */
export function getAllLocations() {
  return request.get('/locations');
}

/**
 * 获取地点详情（包含高德坐标）
 * @param {string} id - 地点ID
 * @returns {Promise<Object>} 返回 { code: 200, data: Location }
 * 
 * 功能要求：
 * - 获取地点完整信息，包括坐标
 * - 用于详情页地图定位
 */
export function getLocationDetail(id) {
  return request.get(`/locations/${id}`);
}

/**
 * 根据事件ID获取关联地点列表
 * @param {string} eventId - 事件ID
 * @returns {Promise<Object>} 返回 { code: 200, data: Array<Location>, total: number }
 */
export function getLocationsByEvent(eventId) {
  return request.get(`/locations/event/${eventId}`);
}

