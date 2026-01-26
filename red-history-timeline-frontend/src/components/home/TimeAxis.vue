<!--
  时间轴组件（首页核心组件）
  
  ========== 功能要求 ==========
  1. 渲染历史事件的时间轴可视化
  2. 展示事件列表，按时间顺序排列
  3. 显示事件图片和标题
  4. 支持点击事件跳转到详情页
  5. 支持时间筛选（可选功能）
  
  ========== 修改范围限制 ==========
  - 此文件由时间轴功能负责人维护
  - 禁止修改事件列表的获取逻辑（必须调用eventApi.getEventList）
  - 禁止修改点击跳转逻辑（必须跳转到 /detail/:id）
  - 可以修改时间轴的展示样式和布局
  - 如需添加新功能（如事件筛选、搜索），需团队讨论
  - 其他开发者如需修改，需提交PR
  
  ========== 接口调用要求 ==========
  - 必须调用 api/eventApi.js 的 getEventList 方法
  - 请求参数：{ startDate, endDate, locationId }（可选）
  - 响应数据格式：{ code: 200, data: Array<Event>, total: number }
  - 事件数据格式：{ id, title, date, category, description, ... }
  - 图片路径：/assets/images/{eventId}.jpg（public目录）
  
  ========== 函数关联要求 ==========
  - 被 Home.vue 引用（首页时间轴区域）
  - 引用 api/eventApi.js（获取事件数据）
  - 引用 utils/formatUtils.js（格式化日期）
  - 使用 Vue Router 进行路由跳转
  - 不直接引用其他业务组件
  
  ========== 数据流 ==========
  1. mounted() → loadEvents() → eventApi.getEventList()
  2. 获取数据 → 更新 events 数组
  3. 渲染事件列表 → 显示图片和标题
  4. 点击事件 → goToDetail(eventId) → router.push('/detail/:id')
-->
<template>
  <div class="time-axis">
    <h2>历史时间轴</h2>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else-if="events.length === 0" class="empty-state">
      <div class="empty-icon">📅</div>
      <h3>暂无历史事件数据</h3>
      <p>请稍后再试，或检查网络连接</p>
      <button class="retry-btn" @click="loadEvents">重新加载</button>
    </div>
    <div v-else class="axis-container">
      <!-- ECharts 时间轴容器 -->
      <div id="timeline-chart" ref="timelineChart" class="timeline-chart"></div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue';
import * as echarts from 'echarts';
import { getEventList } from '@/api/eventApi';
import { formatDateChinese, formatEventsForTimeline } from '@/utils/formatUtils';
import { useRouter } from 'vue-router';

export default {
  name: 'TimeAxis',
  setup(props, { emit }) {
    const router = useRouter();
    const events = ref([]);
    const loading = ref(false);
    const error = ref(null);
    const timelineChart = ref(null);
    let chartInstance = null;

    /**
     * 加载事件列表
     * 功能要求：
     * - 调用 eventApi.getEventList() 获取事件数据
     * - 处理加载状态和错误状态
     * - 更新 events 数组
     * 
     * 修改限制：
     * - 禁止修改API调用方式
     * - 可以优化错误处理和加载状态显示
     */
    const loadEvents = async () => {
      loading.value = true;
      error.value = null;
      try {
        const response = await getEventList();
        // 响应格式：{ code: 200, data: Array<Event>, total: number }
        if (response.code === 200) {
          events.value = response.data || [];
        } else {
          error.value = response.msg || '加载失败';
        }
      } catch (error) {
        console.error('加载事件失败:', error);
        error.value = '加载事件失败，请稍后重试';
      } finally {
        loading.value = false;
      }
    };

    /**
     * 初始化ECharts时间轴
     */
    const initTimelineChart = () => {
      if (!timelineChart.value) return;

      try {
        // 初始化图表实例
        chartInstance = echarts.init(timelineChart.value);

        // 格式化事件数据为ECharts所需格式
        const timelineData = formatEventsForTimeline(events.value);

        // ECharts配置项
        const option = {
          baseOption: {
            timeline: {
              type: 'slider',
              axisType: 'time',
              autoPlay: false,
              playInterval: 3000,
              orient: 'horizontal',
              center: ['50%', 'bottom'],
              width: '90%',
              data: timelineData.map(item => item.date),
              label: {
                formatter: (value) => {
                  return formatDateChinese(value);
                }
              },
              emphasis: {
                label: {
                  color: '#e74c3c'
                }
              },
              itemStyle: {
                color: '#e74c3c'
              },
              lineStyle: {
                color: '#ddd'
              },
              checkpointStyle: {
                borderColor: '#e74c3c',
                borderWidth: 2
              }
            },
            title: {
              text: '红色历史时间轴',
              left: 'center',
              textStyle: {
                color: '#e74c3c',
                fontSize: 18,
                fontWeight: 'bold'
              }
            },
            tooltip: {
              trigger: 'axis',
              axisPointer: {
                type: 'cross'
              },
              formatter: (params) => {
                try {
                  const event = timelineData[params[0].dataIndex];
                  return `
                    <div class="tooltip-content">
                      <h3>${event.name}</h3>
                      <p><strong>日期：</strong>${event.value}</p>
                      <p><strong>类别：</strong>${event.category}</p>
                      <p><strong>地点：</strong>${event.location || '未知'}</p>
                      <p><strong>描述：</strong>${event.description || '暂无描述'}</p>
                    </div>
                  `;
                } catch (err) {
                  return '<div>事件信息加载失败</div>';
                }
              }
            },
            grid: {
              left: '3%',
              right: '4%',
              bottom: '15%',
              containLabel: true
            },
            xAxis: {
              type: 'time',
              boundaryGap: false,
              axisLine: {
                lineStyle: {
                  color: '#e74c3c'
                }
              }
            },
            yAxis: {
              type: 'category',
              data: ['历史事件'],
              axisLabel: {
                color: '#333'
              }
            },
            series: [
              {
                name: '历史事件',
                type: 'scatter',
                symbolSize: 20,
                data: timelineData.map((item, index) => [
                  item.date,
                  0,
                  index
                ]),
                itemStyle: {
                  color: '#e74c3c'
                },
                emphasis: {
                  itemStyle: {
                    color: '#c0392b',
                    borderWidth: 2,
                    borderColor: '#fff'
                  }
                },
                label: {
                  show: true,
                  formatter: (params) => {
                    try {
                      const event = timelineData[params.data[2]];
                      return event.name;
                    } catch (err) {
                      return '';
                    }
                  },
                  position: 'top',
                  color: '#333',
                  fontSize: 12,
                  fontWeight: 'bold'
                }
              }
            ]
          },
          options: timelineData.map(item => ({
            title: {
              text: item.name,
              subtext: item.value,
              textStyle: {
                color: '#e74c3c',
                fontSize: 16
              },
              subtextStyle: {
                color: '#666',
                fontSize: 12
              }
            },
            series: [
              {
                name: '历史事件',
                type: 'scatter',
                symbolSize: 30,
                data: [
                  [item.date, 0]
                ],
                itemStyle: {
                  color: '#e74c3c'
                },
                label: {
                  show: true,
                  formatter: item.name,
                  position: 'top'
                }
              }
            ]
          }))
        };

        // 设置图表配置
        chartInstance.setOption(option);

        // 绑定点击事件
        chartInstance.on('click', (params) => {
          try {
            if (params.componentType === 'series') {
              const event = timelineData[params.data[2]];
              if (!event) {
                console.warn('点击事件无匹配数据');
                return;
              }
              
              goToDetail(event.id);
              // 触发地图定位事件
              emit('event-click', event);
            }
          } catch (err) {
            console.error('处理事件点击失败:', err);
            // 显示错误提示
            alert('定位到事件地点失败，请稍后再试');
          }
        });

        // 窗口大小变化时自适应
        window.addEventListener('resize', handleResize);
      } catch (err) {
        console.error('初始化时间轴失败:', err);
        error.value = '时间轴初始化失败，请稍后再试';
      }
    };

    /**
     * 跳转到事件详情页
     * 功能要求：点击事件跳转到详情页
     * 
     * 修改限制：
     * - 禁止修改跳转路径格式（必须 /detail/:id）
     * - 禁止修改跳转方式（必须使用router.push）
     */
    const goToDetail = (eventId) => {
      router.push(`/detail/${eventId}`);
    };

    /**
     * 处理窗口大小变化
     */
    const handleResize = () => {
      chartInstance && chartInstance.resize();
    };

    /**
     * 获取事件图片路径
     * 功能要求：根据事件ID生成图片路径
     * 图片路径规则：/assets/images/{eventId}.jpg（public目录）
     * 
     * 修改限制：
     * - 禁止修改路径规则
     * - 如需支持其他图片格式，可以扩展
     */
    const getEventImage = (eventId) => {
      // public目录下的文件在Vite中可以直接通过绝对路径访问
      return `/assets/images/${eventId}.jpg`;
    };

    // 监听事件数据变化，重新渲染图表
    watch(events, (newEvents) => {
      if (newEvents.length > 0 && chartInstance) {
        initTimelineChart();
      }
    }, { deep: true });

    // 组件挂载时加载数据并初始化图表
    onMounted(() => {
      loadEvents();
      // 延迟初始化图表，确保DOM已经渲染
      setTimeout(() => {
        initTimelineChart();
      }, 100);
    });

    // 组件卸载前销毁图表实例和事件监听
    onBeforeUnmount(() => {
      if (chartInstance) {
        chartInstance.dispose();
        chartInstance = null;
      }
      window.removeEventListener('resize', handleResize);
    });

    return {
      events,
      loading,
      error,
      timelineChart,
      loadEvents,
      formatDate: formatDateChinese,
      getEventImage,
      goToDetail
    };
  }
};
</script>

<style scoped>
.time-axis {
  padding: 2rem;
  background: white;
  border-radius: 8px;
  margin-bottom: 2rem;
}

.time-axis h2 {
  margin-bottom: 1.5rem;
  color: #e74c3c;
  text-align: center;
}

.loading,
.error {
  text-align: center;
  padding: 2rem;
  color: #666;
  font-size: 1.1rem;
}

.empty-state {
  text-align: center;
  padding: 3rem 2rem;
  background: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  margin: 1rem 0;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.empty-state h3 {
  color: #333;
  margin-bottom: 0.5rem;
}

.empty-state p {
  color: #666;
  margin-bottom: 2rem;
}

.retry-btn {
  background: #e74c3c;
  color: white;
  border: none;
  padding: 0.8rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: background 0.3s;
}

.retry-btn:hover {
  background: #c0392b;
}

.axis-container {
  width: 100%;
  height: 100%;
}

.timeline-chart {
  width: 100%;
  height: 400px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  background: #fafafa;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .timeline-chart {
    height: 300px;
  }
  
  .time-axis {
    padding: 1rem;
  }
}

/* 自定义tooltip样式 */
:deep(.tooltip-content) {
  max-width: 400px;
}

:deep(.tooltip-content h3) {
  margin: 0 0 10px 0;
  color: #e74c3c;
  font-size: 16px;
}

:deep(.tooltip-content p) {
  margin: 5px 0;
  font-size: 14px;
  line-height: 1.5;
}

:deep(.tooltip-content strong) {
  color: #333;
}
</style>

