/**
 * 数据格式化工具函数
 * 
 * ========== 功能要求 ==========
 * 1. 提供日期格式化方法（formatDateChinese）
 * 2. 提供坐标格式化方法（formatCoordinates）
 * 3. 统一数据格式转换逻辑，避免在组件中重复编写
 * 
 * ========== 修改范围限制 ==========
 * - 此文件由工具类负责人维护
 * - 可以添加新的格式化函数，但需保持函数命名规范
 * - 禁止修改现有函数的参数和返回值格式
 * - 如需修改格式化逻辑，需团队讨论
 * - 其他开发者可以添加新的格式化函数，但需提交PR
 * 
 * ========== 接口调用要求 ==========
 * - 不调用任何API
 * - 纯函数，无副作用
 * - 只处理数据格式转换
 * 
 * ========== 函数关联要求 ==========
 * - 被所有需要格式化数据的组件引用
 * - 不依赖其他工具类
 * - 不依赖业务逻辑
 * 
 * ========== 使用示例 ==========
 * import { formatDateChinese, formatCoordinates } from '@/utils/formatUtils'
 * 
 * const dateStr = formatDateChinese('1931-09-18') // "1931年9月18日"
 * const coordStr = formatCoordinates([116.397428, 39.90923]) // "116.397428, 39.90923"
 */

/**
 * 格式化日期为中文格式
 * @param {string} dateString - 日期字符串 (YYYY-MM-DD)
 * @returns {string} 格式化后的日期 (YYYY年MM月DD日)
 * 
 * 功能要求：
 * - 将 "1931-09-18" 格式转换为 "1931年9月18日"
 * - 处理空值情况
 * - 处理非法日期情况
 * - 保持日期格式的一致性
 * 
 * 修改限制：
 * - 禁止修改返回值格式（YYYY年MM月DD日）
 * - 如需支持其他日期格式，添加新函数
 */
export function formatDateChinese(dateString) {
  if (!dateString || typeof dateString !== 'string') return '';
  
  const date = new Date(dateString);
  // 检查是否为有效日期
  if (isNaN(date.getTime())) return '';
  
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  return `${year}年${month}月${day}日`;
}

/**
 * 格式化坐标数组为字符串
 * @param {Array<number>} coordinates - 坐标数组 [lng, lat]
 * @returns {string} 格式化后的坐标字符串
 * 
 * 功能要求：
 * - 将坐标数组转换为可读的字符串格式
 * - 处理空值情况
 * - 处理非法坐标情况
 * 
 * 修改限制：
 * - 禁止修改返回值格式
 * - 如需其他格式，添加新函数
 */
export function formatCoordinates(coordinates) {
  if (!coordinates || !Array.isArray(coordinates) || coordinates.length !== 2) return '';
  if (typeof coordinates[0] !== 'number' || typeof coordinates[1] !== 'number') return '';
  return `${coordinates[0]}, ${coordinates[1]}`;
}

/**
 * 格式化事件数据为ECharts时间轴所需格式
 * @param {Array<Object>} events - 事件数据数组
 * @returns {Object} 格式化后的时间轴数据
 * 
 * 功能要求：
 * - 将事件数组转换为ECharts时间轴所需的data格式
 * - 按时间顺序排序
 * - 处理空值情况
 * - 处理非法事件数据情况
 * 
 * 使用示例：
 * const timelineData = formatEventsForTimeline(events)
 */
export function formatEventsForTimeline(events) {
  if (!events || !Array.isArray(events)) return [];
  
  // 按时间排序并过滤有效事件
  const sortedEvents = [...events]
    .filter(event => event && event.id && event.title && event.date)
    .sort((a, b) => {
      const dateA = new Date(a.date);
      const dateB = new Date(b.date);
      if (isNaN(dateA.getTime())) return 1;
      if (isNaN(dateB.getTime())) return -1;
      return dateA - dateB;
    });
  
  // 格式化为ECharts时间轴所需数据格式
  return sortedEvents.map(event => ({
    id: event.id,
    name: event.title,
    value: formatDateChinese(event.date),
    date: event.date,
    category: event.category || '',
    description: event.description || '',
    location: event.location || '',
    coordinates: event.coordinates || null
  }));
}

/**
 * 格式化日期范围
 * @param {string} startDate - 开始日期 (YYYY-MM-DD)
 * @param {string} endDate - 结束日期 (YYYY-MM-DD)
 * @returns {string} 格式化后的日期范围
 * 
 * 功能要求：
 * - 将日期范围转换为中文格式
 * - 处理单个日期情况
 * - 处理空值情况
 * - 处理非法日期情况
 */
export function formatDateRange(startDate, endDate) {
  if (!startDate && !endDate) return '';
  if (!startDate) return formatDateChinese(endDate);
  if (!endDate) return formatDateChinese(startDate);
  return `${formatDateChinese(startDate)} - ${formatDateChinese(endDate)}`;
}

