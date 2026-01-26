/**
 * 事件相关API接口
 * 
 * ========== 功能要求 ==========
 * 1. 封装所有与事件相关的后端接口调用
 * 2. 提供事件列表、详情、筛选等接口方法
 * 3. 统一处理事件数据的请求和响应
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由事件功能负责人维护
 * - 禁止修改现有函数的参数和返回值格式
 * - 如需添加新接口，需先确认后端接口已实现
 * - 其他开发者如需调用事件接口，必须通过此文件
 * - 禁止在组件中直接调用request，必须通过此API文件
 * 
 * ========== 接口调用要求 ==========
 * - 所有方法必须调用 utils/request.js
 * - 请求路径：/api/events 或 /api/events/{id}
 * - 响应格式：{ code: 200, msg: 'success', data: {...}, total: 0 }
 * - 错误处理：由request.js统一处理，此文件只负责接口定义
 * 
 * ========== 函数关联要求 ==========
 * - 被 TimeAxis.vue 引用（获取事件列表）
 * - 被 EventCard.vue 间接引用（通过父组件）
 * - 被 EventDetail.vue 引用（获取事件详情）
 * - 被 Home.vue 引用（获取事件列表）
 * - 引用 utils/request.js
 * - 不直接引用后端Controller（通过HTTP请求）
 * 
 * ========== 后端接口对应关系 ==========
 * - getEventList() → GET /api/events (EventController.getAllEvents)
 * - getEventDetail(id) → GET /api/events/{id} (EventController.getEventById)
 * - getEventsByLocation(locationId) → GET /api/events/location/{locationId} (EventController.getEventsByLocation)
 * 
 * ========== 使用示例 ==========
 * import { getEventList, getEventDetail } from '@/api/eventApi'
 * 
 * // 获取事件列表
 * const response = await getEventList({ startDate: '1931-09-18', endDate: '1945-09-02' })
 * const events = response.data
 * 
 * // 获取事件详情
 * const response = await getEventDetail('event-001')
 * const event = response.data
 */
import request from '@/utils/request';

/**
 * 获取事件列表
 * @param {Object} params - 查询参数
 *   - startDate: 开始日期 (可选，格式：YYYY-MM-DD)
 *   - endDate: 结束日期 (可选，格式：YYYY-MM-DD)
 *   - locationId: 地点ID (可选)
 * @returns {Promise<Object>} 返回 { code: 200, data: Array<Event>, total: number }
 * 
 * 功能要求：
 * - 支持按时间范围筛选
 * - 支持按地点筛选
 * - 返回事件列表和总数
 * 
 * 修改限制：
 * - 禁止修改参数格式
 * - 禁止修改返回值结构
 */
export async function getEventList(params = {}) {
  try {
    const response = await request.get('/events', { params });
    return response;
  } catch (error) {
    console.error('获取事件列表失败:', error);
    // 返回友好的错误提示
    return {
      code: 500,
      msg: '获取事件列表失败，请稍后重试',
      data: [],
      total: 0
    };
  }
}

/**
 * 获取事件详情
 * @param {string} id - 事件ID
 * @returns {Promise<Object>} 返回 { code: 200, data: Event }
 * 
 * 功能要求：
 * - 根据事件ID获取完整事件信息
 * - 包含事件描述、背景、影响等所有字段
 * 
 * 修改限制：
 * - 禁止修改参数格式
 * - 禁止修改返回值结构
 */
export async function getEventDetail(id) {
  try {
    const response = await request.get(`/events/${id}`);
    return response;
  } catch (error) {
    console.error(`获取事件详情失败 (ID: ${id}):`, error);
    // 返回友好的错误提示
    return {
      code: 500,
      msg: '获取事件详情失败，请稍后重试',
      data: null
    };
  }
}

/**
 * 根据地点ID获取事件列表
 * @param {string} locationId - 地点ID
 * @returns {Promise<Object>} 返回 { code: 200, data: Array<Event>, total: number }
 * 
 * 功能要求：
 * - 获取指定地点发生的所有事件
 * - 按时间排序
 * 
 * 修改限制：
 * - 禁止修改参数格式
 * - 禁止修改返回值结构
 */
export async function getEventsByLocation(locationId) {
  try {
    const response = await request.get(`/events/location/${locationId}`);
    return response;
  } catch (error) {
    console.error(`根据地点获取事件列表失败 (LocationID: ${locationId}):`, error);
    // 返回友好的错误提示
    return {
      code: 500,
      msg: '获取地点事件列表失败，请稍后重试',
      data: [],
      total: 0
    };
  }
}

