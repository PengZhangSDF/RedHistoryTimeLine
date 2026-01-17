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
 * - 保持日期格式的一致性
 * 
 * 修改限制：
 * - 禁止修改返回值格式（YYYY年MM月DD日）
 * - 如需支持其他日期格式，添加新函数
 */
export function formatDateChinese(dateString) {
  if (!dateString) return '';
  const date = new Date(dateString);
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
 * 
 * 修改限制：
 * - 禁止修改返回值格式
 * - 如需其他格式，添加新函数
 */
export function formatCoordinates(coordinates) {
  if (!coordinates || coordinates.length !== 2) return '';
  return `${coordinates[0]}, ${coordinates[1]}`;
}

