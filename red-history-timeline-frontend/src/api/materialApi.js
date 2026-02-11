/**
 * 事件相关资料 API 接口
 *
 * ========== 功能要求 ==========
 * 1. 封装根据事件ID获取相关资料的后端接口调用
 * 2. 返回给详情页用于渲染“相关资料”模块
 *
 * ========== 接口调用要求 ==========
 * - 所有方法必须调用 utils/request.js
 * - 请求路径：/api/materials/event/{eventId}
 * - 响应格式：{ code: 200, msg: 'success', data: Array<Material>, total: number }
 * - 资料数据格式：{ id, eventId, title, url, type }
 */
import request from '@/utils/request';

/**
 * 根据事件ID获取关联资料列表
 * @param {string} eventId - 事件ID
 * @returns {Promise<Object>} 返回 { code, data, total }
 */
export function getMaterialsByEvent(eventId) {
  return request.get(`/materials/event/${eventId}`);
}

