/**
 * 人物相关API接口
 * 
 * ========== 功能要求 ==========
 * 1. 封装所有与人物相关的后端接口调用
 * 2. 提供人物详情、关联事件等接口方法
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由人物功能负责人维护
 * - 禁止修改现有函数的参数和返回值格式
 * - 如需添加新接口，需先确认后端接口已实现
 * - 其他开发者如需调用人物接口，必须通过此文件
 * 
 * ========== 接口调用要求 ==========
 * - 所有方法必须调用 utils/request.js
 * - 请求路径：/api/persons/{id} 或 /api/persons/event/{eventId}
 * - 响应格式：{ code: 200, msg: 'success', data: {...}, total: 0 }
 * 
 * ========== 函数关联要求 ==========
 * - 被 RelatedPerson.vue 引用（获取人物详情和关联人物）
 * - 引用 utils/request.js
 * - 不直接引用后端Controller
 * 
 * ========== 后端接口对应关系 ==========
 * - getPersonDetail(id) → GET /api/persons/{id} (PersonController.getPersonById)
 * - getPersonsByEvent(eventId) → GET /api/persons/event/{eventId} (PersonController.getPersonsByEvent)
 */
import request from '@/utils/request';

/**
 * 获取人物详情
 * @param {string} id - 人物ID
 * @returns {Promise<Object>} 返回 { code: 200, data: Person }
 */
export function getPersonDetail(id) {
  return request.get(`/persons/${id}`);
}

/**
 * 根据事件ID获取关联人物列表
 * @param {string} eventId - 事件ID
 * @returns {Promise<Object>} 返回 { code: 200, data: Array<Person>, total: number }
 */
export function getPersonsByEvent(eventId) {
  return request.get(`/persons/event/${eventId}`);
}

