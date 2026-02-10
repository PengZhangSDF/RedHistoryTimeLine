<!--
  相关资料跳转组件（通用组件）
  
  ========== 功能要求 ==========
  1. 展示事件相关的资料链接列表
  2. 点击链接在新窗口打开外部资源
  3. 可复用组件，用于详情页展示相关资料
  
  ========== 修改范围限制 ==========
  - 此文件由UI组件负责人维护
  - 禁止修改链接打开方式（必须新窗口打开）
  - 可以修改样式和布局
  - 如需添加其他功能（如链接预览），需团队讨论
  - 其他开发者可以引用此组件，但禁止修改核心逻辑
  
  ========== 接口调用要求 ==========
  - 不调用任何API接口
  - 接收父组件传入的links数据
  - links格式：Array<{ id, title, url, type }>
  
  ========== 函数关联要求 ==========
  - 被 EventDetail.vue 引用（详情页相关资料）
  - 接收父组件传入的props
  - 不依赖其他业务组件
  - 不依赖API调用
-->
<template>
  <div class="link-jump">
    <h3>相关资料</h3>
    <ul v-if="links && links.length > 0" class="links-list">
      <li v-for="link in links" :key="link.id" class="link-item">
        <a :href="link.url" target="_blank" rel="noopener noreferrer" class="link-wrapper">
          <span class="link-icon" :class="getLinkIconClass(link.type)">
            {{ getLinkIcon(link.type) }}
          </span>
          <span class="link-title">{{ link.title }}</span>
          <span class="link-type" :class="getLinkTypeClass(link.type)">{{ link.type }}</span>
          <span class="link-external">↗</span>
        </a>
      </li>
    </ul>
    <div v-else class="no-links">
      <div class="no-links-icon">📚</div>
      <p>暂无相关资料</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LinkJump',
  props: {
    // 资料链接列表
    // 格式：[{ id: 'mat-001', title: '资料标题', url: 'https://...', type: '历史文献' }]
    links: {
      type: Array,
      default: () => []
    }
  },
  methods: {
    /**
     * 根据链接类型获取图标
     * @param {string} type - 链接类型
     * @returns {string} 图标
     */
    getLinkIcon(type) {
      const iconMap = {
        '历史文献': '📜',
        '图片资料': '🖼️',
        '视频资料': '🎬',
        '音频资料': '🎵',
        '官方网站': '🌐',
        '百科条目': '📚',
        '学术论文': '📄',
        '新闻报道': '📰',
        '其他': '🔗'
      };
      return iconMap[type] || '🔗';
    },
    
    /**
     * 根据链接类型获取图标样式类
     * @param {string} type - 链接类型
     * @returns {string} 样式类
     */
    getLinkIconClass(type) {
      const classMap = {
        '历史文献': 'icon-document',
        '图片资料': 'icon-image',
        '视频资料': 'icon-video',
        '音频资料': 'icon-audio',
        '官方网站': 'icon-website',
        '百科条目': 'icon-encyclopedia',
        '学术论文': 'icon-paper',
        '新闻报道': 'icon-news',
        '其他': 'icon-other'
      };
      return classMap[type] || 'icon-other';
    },
    
    /**
     * 根据链接类型获取类型样式类
     * @param {string} type - 链接类型
     * @returns {string} 样式类
     */
    getLinkTypeClass(type) {
      const classMap = {
        '历史文献': 'type-document',
        '图片资料': 'type-image',
        '视频资料': 'type-video',
        '音频资料': 'type-audio',
        '官方网站': 'type-website',
        '百科条目': 'type-encyclopedia',
        '学术论文': 'type-paper',
        '新闻报道': 'type-news',
        '其他': 'type-other'
      };
      return classMap[type] || 'type-other';
    }
  }
};
</script>

<style scoped>
.link-jump {
  margin: 2rem 0;
  padding: 1.5rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.link-jump:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.link-jump h3 {
  color: #e74c3c;
  margin-bottom: 1.5rem;
  font-size: 1.3rem;
  padding-bottom: 0.75rem;
  border-bottom: 2px solid #f0f0f0;
  position: relative;
}

.link-jump h3::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 80px;
  height: 2px;
  background: #e74c3c;
}

.links-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.link-item {
  transition: all 0.3s ease;
}

.link-wrapper {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.25rem;
  background: #f9f9f9;
  border-radius: 8px;
  color: #333;
  text-decoration: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  border: 1px solid transparent;
}

.link-wrapper::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(231, 76, 60, 0.1), transparent);
  transition: left 0.6s ease;
}

.link-wrapper:hover::before {
  left: 100%;
}

.link-wrapper:hover {
  background: white;
  transform: translateX(10px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
  border-color: #e74c3c;
}

.link-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
  transition: transform 0.3s ease;
}

.link-wrapper:hover .link-icon {
  transform: scale(1.2) rotate(5deg);
}

.link-title {
  flex: 1;
  font-weight: 500;
  line-height: 1.4;
  transition: color 0.3s ease;
}

.link-wrapper:hover .link-title {
  color: #e74c3c;
}

.link-type {
  padding: 0.35rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.link-external {
  font-size: 1.1rem;
  color: #666;
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.link-wrapper:hover .link-external {
  color: #e74c3c;
  transform: translateX(5px) rotate(45deg);
}

/* 链接类型样式 */
.type-document {
  background: #3498db;
  color: white;
}

.type-image {
  background: #27ae60;
  color: white;
}

.type-video {
  background: #e67e22;
  color: white;
}

.type-audio {
  background: #9b59b6;
  color: white;
}

.type-website {
  background: #1abc9c;
  color: white;
}

.type-encyclopedia {
  background: #f39c12;
  color: white;
}

.type-paper {
  background: #d35400;
  color: white;
}

.type-news {
  background: #e74c3c;
  color: white;
}

.type-other {
  background: #95a5a6;
  color: white;
}

/* 无链接提示样式 */
.no-links {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem;
  background: #f9f9f9;
  border-radius: 8px;
  text-align: center;
  transition: all 0.3s ease;
}

.no-links:hover {
  background: #f5f5f5;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.no-links-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  transition: transform 0.3s ease;
}

.no-links:hover .no-links-icon {
  transform: scale(1.2) rotate(5deg);
}

.no-links p {
  margin: 0;
  color: #999;
  font-size: 1.1rem;
  transition: color 0.3s ease;
}

.no-links:hover p {
  color: #666;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .link-jump {
    margin: 1.5rem 0;
    padding: 1.25rem;
  }
  
  .link-jump h3 {
    font-size: 1.1rem;
    margin-bottom: 1.25rem;
    padding-bottom: 0.5rem;
  }
  
  .link-wrapper {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;
    padding: 1rem;
  }
  
  .link-icon {
    font-size: 1.3rem;
  }
  
  .link-type {
    font-size: 0.75rem;
    padding: 0.25rem 0.5rem;
  }
  
  .no-links {
    padding: 2rem;
  }
  
  .no-links-icon {
    font-size: 2.5rem;
    margin-bottom: 0.75rem;
  }
  
  .no-links p {
    font-size: 1rem;
  }
}

/* 动画效果 */
@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.link-item {
  animation: slideInLeft 0.3s ease forwards;
}

.link-item:nth-child(1) {
  animation-delay: 0.1s;
}

.link-item:nth-child(2) {
  animation-delay: 0.2s;
}

.link-item:nth-child(3) {
  animation-delay: 0.3s;
}

.link-item:nth-child(4) {
  animation-delay: 0.4s;
}

.link-item:nth-child(5) {
  animation-delay: 0.5s;
}
</style>

