<!--
  相关人物组件（详情页组件）
  
  ========== 功能要求 ==========
  1. 展示事件相关的历史人物
  2. 显示人物姓名、角色、简介
  3. 点击人物显示人物详情（弹窗或跳转）
  
  ========== 修改范围限制 ==========
  - 此文件由人物功能负责人维护
  - 禁止修改API调用方式（必须调用personApi.getPersonsByEvent）
  - 禁止修改props的接收方式（必须接收eventId）
  - 可以修改展示样式和布局
  - 可以优化人物详情展示方式（弹窗/跳转/展开）
  - 其他开发者如需修改，需提交PR
  
  ========== 接口调用要求 ==========
  - 必须调用 api/personApi.js 的 getPersonsByEvent 方法
  - 请求参数：eventId（事件ID）
  - 响应数据格式：{ code: 200, data: Array<Person>, total: number }
  - 人物数据格式：{ id, name, role, description, birthDate, deathDate, ... }
  
  ========== 函数关联要求 ==========
  - 被 EventDetail.vue（页面组件）引用
  - 引用 api/personApi.js（获取人物数据）
  - 可以调用 personApi.getPersonDetail 获取详细人物信息
  - 不依赖其他业务组件
  
  ========== 数据流 ==========
  1. mounted() → loadPersons() → personApi.getPersonsByEvent(eventId)
  2. 获取数据 → 更新 persons 数组
  3. 渲染人物列表
  4. 点击人物 → showPersonDetail(personId) → 显示人物详情
-->
<template>
  <div class="related-person">
    <h2>相关人物</h2>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else-if="persons && persons.length > 0" class="person-list">
      <div 
        v-for="person in persons" 
        :key="person.id"
        class="person-item"
        @click="showPersonDetail(person.id)"
      >
        <h3>{{ person.name }}</h3>
        <p class="person-role">{{ person.role }}</p>
        <p class="person-desc">{{ person.description }}</p>
      </div>
    </div>
    <p v-else class="no-person">暂无相关人物</p>
  </div>
</template>

<script>
import { getPersonsByEvent, getPersonDetail } from '@/api/personApi';

export default {
  name: 'RelatedPerson',
  props: {
    // 事件ID
    // 功能要求：必须传入事件ID，用于查询关联人物
    // 修改限制：禁止修改prop名称和类型
    eventId: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      persons: [],
      loading: false,
      error: null
    };
  },
  mounted() {
    // 功能要求：组件挂载时自动加载人物列表
    // 禁止修改此调用逻辑
    this.loadPersons();
  },
  watch: {
    // 功能要求：当eventId变化时，重新加载人物列表
    // 修改限制：禁止删除此监听逻辑
    eventId() {
      this.loadPersons();
    }
  },
  methods: {
    /**
     * 加载人物列表
     * 功能要求：
     * - 调用personApi.getPersonsByEvent(eventId)获取人物数据
     * - 处理加载状态和错误状态
     * - 更新persons数组
     * 
     * 修改限制：
     * - 禁止修改API调用方式
     * - 可以优化错误处理和加载状态显示
     */
    async loadPersons() {
      if (!this.eventId) return;
      
      this.loading = true;
      this.error = null;
      try {
        const response = await getPersonsByEvent(this.eventId);
        // 响应格式：{ code: 200, data: Array<Person>, total: number }
        if (response.code === 200) {
          this.persons = response.data || [];
        } else {
          this.error = response.msg || '加载失败';
        }
      } catch (error) {
        console.error('加载人物数据失败:', error);
        this.error = '加载人物失败，请稍后重试';
      } finally {
        this.loading = false;
      }
    },
    
    /**
     * 显示人物详情
     * 功能要求：点击人物时显示人物详细信息
     * 
     * 修改限制：
     * - 可以修改展示方式（弹窗/跳转/展开）
     * - 如需跳转到人物详情页，需先创建路由和页面
     * - 当前实现为简单alert，可以优化为弹窗组件
     */
    async showPersonDetail(personId) {
      try {
        // 获取人物详情
        const response = await getPersonDetail(personId);
        if (response.code === 200) {
          const person = response.data;
          // TODO: 可以优化为弹窗组件或跳转到人物详情页
          alert(`人物详情：\n姓名：${person.name}\n角色：${person.role}\n简介：${person.description}`);
        }
      } catch (error) {
        console.error('获取人物详情失败:', error);
        alert('获取人物详情失败');
      }
    }
  }
};
</script>

<style scoped>
.related-person {
  margin: 2rem 0;
  padding: 1.5rem;
  background: white;
  border-radius: 8px;
}

.related-person h2 {
  color: #e74c3c;
  margin-bottom: 1rem;
}

.loading,
.error,
.no-person {
  text-align: center;
  padding: 2rem;
  color: #666;
}

.error {
  color: #e74c3c;
}

.person-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

.person-item {
  padding: 1.5rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  background: white;
}

.person-item:hover {
  background: #f5f5f5;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #e74c3c;
}

.person-item h3 {
  margin: 0 0 0.5rem 0;
  color: #333;
  font-size: 1.2rem;
}

.person-role {
  color: #e74c3c;
  font-weight: bold;
  margin: 0.5rem 0;
  font-size: 0.9rem;
}

.person-desc {
  color: #666;
  font-size: 0.9rem;
  line-height: 1.6;
  margin: 0.5rem 0 0 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>

