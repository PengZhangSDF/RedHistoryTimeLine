/**
 * 媒体资源相关API接口
 * 
 * ========== 功能要求 ==========
 * 1. 封装所有与媒体资源相关的后端接口调用
 * 2. 提供图片、视频、音乐等媒体资源的链接
 * 3. 用于详情页的多媒体播放功能
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由媒体功能负责人维护
 * - 禁止修改现有函数的参数和返回值格式
 * - 如需添加新接口，需先确认后端接口已实现
 * - 其他开发者如需调用媒体接口，必须通过此文件
 * 
 * ========== 接口调用要求 ==========
 * - 所有方法必须调用 utils/request.js
 * - 请求路径：/api/media/event/{eventId}
 * - 响应格式：{ code: 200, msg: 'success', data: Array<Media>, total: number }
 * - 媒体数据格式：{ id, type: 'image'|'video'|'audio', url, title, eventId }
 * 
 * ========== 函数关联要求 ==========
 * - 被 MediaPlayer.vue 引用（获取媒体资源列表）
 * - 被 EventDetail.vue 间接引用（通过MediaPlayer组件）
 * - 引用 utils/request.js
 * 
 * ========== 后端接口对应关系 ==========
 * - getMediaByEvent(eventId, type) → GET /api/media/event/{eventId}?type=image (MediaController.getMediaByEvent)
 */
import request from '@/utils/request';

/**
 * 根据事件ID获取关联媒体资源
 * @param {string} eventId - 事件ID
 * @param {string} type - 媒体类型（可选：'image'|'video'|'audio'，不传则返回所有类型）
 * @returns {Promise<Object>} 返回 { code: 200, data: Array<Media>, total: number }
 * 
 * 功能要求：
 * - 获取事件关联的所有媒体资源
 * - 支持按类型筛选（图片/视频/音频）
 * - 返回媒体资源的URL和标题
 */
export function getMediaByEvent(eventId, type = null) {
  const params = type ? { type } : {};
  return request.get(`/media/event/${eventId}`, { params });
}

