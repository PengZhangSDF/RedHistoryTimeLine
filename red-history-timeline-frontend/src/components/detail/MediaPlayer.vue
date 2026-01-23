<!--
  多媒体播放器组件（详情页组件）
  
  ========== 功能要求 ==========
  1. 播放事件相关的图片、视频、音乐
  2. 支持图片轮播（上一张/下一张）
  3. 支持视频播放（使用HTML5 video标签）
  4. 支持音频播放（使用HTML5 audio标签）
  5. 按类型分类显示媒体资源
  
  ========== 修改范围限制 ==========
  - 此文件由媒体功能负责人维护
  - 禁止修改props的接收方式（必须接收images、videos、audios数组）
  - 可以修改播放器样式和交互
  - 可以添加新功能（如全屏、播放列表等），需团队讨论
  - 其他开发者如需修改，需提交PR
  
  ========== 接口调用要求 ==========
  - 不直接调用API接口
  - 接收父组件传入的媒体数据
  - 媒体数据格式：
  *   images: Array<{ id, url, title, type: 'image' }>
  *   videos: Array<{ id, url, title, type: 'video' }>
  *   audios: Array<{ id, url, title, type: 'audio' }>
  - 媒体URL可以是相对路径（/assets/...）或绝对路径（http://...）
  
  ========== 函数关联要求 ==========
  - 被 EventDetail.vue（页面组件）引用
  - 不依赖其他业务组件
  - 不依赖API调用
-->
<template>
  <div class="media-player">
    <h2 class="media-player-title">多媒体资料</h2>
    
    <!-- 图片轮播 -->
    <div class="image-carousel" v-if="images && images.length > 0">
      <div class="carousel-wrapper">
        <transition name="carousel-fade" mode="out-in">
          <img 
            :key="currentImageIndex"
            :src="currentImage.url" 
            :alt="currentImage.title || '事件图片'"
            class="carousel-image"
          />
        </transition>
        <!-- 图片标题 -->
        <div class="carousel-caption" v-if="currentImage.title">
          {{ currentImage.title }}
        </div>
      </div>
      <div class="carousel-controls">
        <button @click="prevImage" :disabled="images.length <= 1" class="control-btn prev-btn">
          <span class="btn-icon">←</span> 上一张
        </button>
        <div class="carousel-info">
          <span class="image-counter">{{ currentImageIndex + 1 }} / {{ images.length }}</span>
          <button @click="toggleAutoPlay" class="auto-play-btn" :class="{ active: autoPlay }">
            {{ autoPlay ? '暂停' : '自动播放' }}
          </button>
        </div>
        <button @click="nextImage" :disabled="images.length <= 1" class="control-btn next-btn">
          下一张 <span class="btn-icon">→</span>
        </button>
      </div>
      <div class="image-list" v-if="images.length > 1">
        <img 
          v-for="(image, index) in images" 
          :key="image.id"
          :src="image.url" 
          :alt="image.title"
          :class="{ active: currentImageIndex === index }"
          @click="goToImage(index)"
          class="image-thumbnail"
          @mouseenter="pauseAutoPlay"
          @mouseleave="startAutoPlay"
        />
      </div>
    </div>
    
    <!-- 视频播放 -->
    <div class="video-player" v-if="videos && videos.length > 0">
      <h3 class="section-title">历史视频</h3>
      <div class="video-list">
        <div v-for="(video, index) in videos" :key="video.id" class="video-item" :data-index="index">
          <h4 v-if="video.title" class="media-item-title">{{ video.title }}</h4>
          <div class="video-container">
            <video :src="video.url" controls class="video-element"></video>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 音频播放 -->
    <div class="audio-player" v-if="audios && audios.length > 0">
      <h3 class="section-title">红色音乐</h3>
      <div class="audio-list">
        <div v-for="(audio, index) in audios" :key="audio.id" class="audio-item" :data-index="index">
          <h4 v-if="audio.title" class="media-item-title">{{ audio.title }}</h4>
          <div class="audio-container">
            <audio :src="audio.url" controls class="audio-element"></audio>
          </div>
        </div>
      </div>
    </div>
    
    <div v-if="!hasMedia" class="no-media">
      <div class="no-media-icon">📷</div>
      <p>暂无多媒体资料</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MediaPlayer',
  props: {
    // 图片数组
    images: {
      type: Array,
      default: () => []
    },
    // 视频数组
    videos: {
      type: Array,
      default: () => []
    },
    // 音频数组
    audios: {
      type: Array,
      default: () => []
    },
    // 自动播放间隔（毫秒）
    autoPlayInterval: {
      type: Number,
      default: 5000
    }
  },
  data() {
    return {
      currentImageIndex: 0, // 当前显示的图片索引
      autoPlay: false, // 是否自动播放
      autoPlayTimer: null // 自动播放定时器
    };
  },
  computed: {
    /**
     * 是否有媒体资源
     * 功能要求：判断是否有任何类型的媒体资源
     */
    hasMedia() {
      return (this.images && this.images.length > 0) ||
             (this.videos && this.videos.length > 0) ||
             (this.audios && this.audios.length > 0);
    },
    
    /**
     * 当前显示的图片
     * 功能要求：获取当前索引对应的图片对象
     */
    currentImage() {
      return this.images && this.images.length > 0 ? this.images[this.currentImageIndex] : {};
    }
  },
  mounted() {
    // 组件挂载后初始化
    this.initAutoPlay();
  },
  beforeUnmount() {
    // 组件卸载前清理定时器
    this.clearAutoPlayTimer();
  },
  methods: {
    /**
     * 初始化自动播放
     * 功能要求：如果有多个图片，默认开启自动播放
     */
    initAutoPlay() {
      if (this.images && this.images.length > 1) {
        this.autoPlay = true;
        this.startAutoPlay();
      }
    },
    
    /**
     * 开始自动播放
     * 功能要求：设置定时器，定时切换图片
     */
    startAutoPlay() {
      if (this.autoPlay && this.images && this.images.length > 1) {
        this.clearAutoPlayTimer();
        this.autoPlayTimer = setInterval(() => {
          this.nextImage();
        }, this.autoPlayInterval);
      }
    },
    
    /**
     * 暂停自动播放
     * 功能要求：清除定时器，停止自动播放
     */
    pauseAutoPlay() {
      this.clearAutoPlayTimer();
    },
    
    /**
     * 清除自动播放定时器
     * 功能要求：安全清除定时器
     */
    clearAutoPlayTimer() {
      if (this.autoPlayTimer) {
        clearInterval(this.autoPlayTimer);
        this.autoPlayTimer = null;
      }
    },
    
    /**
     * 切换自动播放状态
     * 功能要求：切换自动播放开关状态
     */
    toggleAutoPlay() {
      this.autoPlay = !this.autoPlay;
      if (this.autoPlay) {
        this.startAutoPlay();
      } else {
        this.pauseAutoPlay();
      }
    },
    
    /**
     * 上一张图片
     * 功能要求：切换到上一张图片（循环）
     * 修改限制：禁止修改循环逻辑
     */
    prevImage() {
      this.currentImageIndex = (this.currentImageIndex - 1 + this.images.length) % this.images.length;
    },
    
    /**
     * 下一张图片
     * 功能要求：切换到下一张图片（循环）
     * 修改限制：禁止修改循环逻辑
     */
    nextImage() {
      this.currentImageIndex = (this.currentImageIndex + 1) % this.images.length;
    },
    
    /**
     * 跳转到指定图片
     * 功能要求：根据索引跳转到指定图片
     */
    goToImage(index) {
      this.currentImageIndex = index;
      // 重置自动播放定时器
      if (this.autoPlay) {
        this.startAutoPlay();
      }
    }
  }
};
</script>

<style scoped>
/* 主容器样式 */
.media-player {
  margin: 2rem 0;
  padding: 2rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.media-player::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, #e74c3c, #3498db);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.media-player:hover::before {
  transform: scaleX(1);
}

.media-player:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

/* 标题样式 */
.media-player-title {
  color: #e74c3c;
  margin-bottom: 2rem;
  font-size: 1.8rem;
  font-weight: 700;
  text-align: center;
  padding-bottom: 1rem;
  border-bottom: 2px solid #f0f0f0;
  position: relative;
  transition: all 0.3s ease;
}

.media-player-title::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 50%;
  transform: translateX(-50%);
  width: 100px;
  height: 2px;
  background: #e74c3c;
  transform: scaleX(0);
  transform-origin: center;
  transition: transform 0.3s ease;
}

.media-player:hover .media-player-title::after {
  transform: scaleX(1) translateX(-50%);
}

/* 图片轮播样式 */
.image-carousel {
  margin-bottom: 2rem;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.image-carousel:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.carousel-wrapper {
  position: relative;
  width: 100%;
  height: 500px;
  overflow: hidden;
  border-radius: 12px;
  background: #000;
}

/* 轮播过渡动画 */
.carousel-fade-enter-active,
.carousel-fade-leave-active {
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.carousel-fade-enter-from {
  opacity: 0;
  transform: scale(1.1);
}

.carousel-fade-leave-to {
  opacity: 0;
  transform: scale(0.9);
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
  transition: all 0.3s ease;
}

/* 图片标题 */
.carousel-caption {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  padding: 1.5rem;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
  color: white;
  font-size: 1.1rem;
  font-weight: 600;
  text-align: center;
  transform: translateY(100%);
  transition: transform 0.3s ease;
}

.carousel-wrapper:hover .carousel-caption {
  transform: translateY(0);
}

/* 轮播控制样式 */
.carousel-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  margin-top: 1rem;
  padding: 1rem;
  background: rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  position: relative;
  overflow: hidden;
}

.carousel-controls::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(231, 76, 60, 0.1), transparent);
  transition: left 0.6s ease;
}

.carousel-controls:hover::before {
  left: 100%;
}

.control-btn {
  padding: 0.75rem 1.5rem;
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 8px rgba(231, 76, 60, 0.3);
  position: relative;
  overflow: hidden;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.control-btn::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: width 0.6s ease, height 0.6s ease;
  z-index: -1;
}

.control-btn:hover:not(:disabled)::before {
  width: 300px;
  height: 300px;
}

.control-btn:hover:not(:disabled) {
  background: #c0392b;
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(231, 76, 60, 0.4);
}

.control-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

.btn-icon {
  font-size: 1.2rem;
  font-weight: bold;
  transition: transform 0.3s ease;
}

.control-btn:hover:not(:disabled) .btn-icon {
  transform: scale(1.2);
}

.prev-btn:hover:not(:disabled) .btn-icon {
  transform: translateX(-3px) scale(1.2);
}

.next-btn:hover:not(:disabled) .btn-icon {
  transform: translateX(3px) scale(1.2);
}

/* 轮播信息区域 */
.carousel-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  flex: 1;
}

.image-counter {
  font-weight: 700;
  color: #666;
  font-size: 1rem;
  padding: 0.5rem 1rem;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.carousel-controls:hover .image-counter {
  background: rgba(255, 255, 255, 1);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

/* 自动播放按钮 */
.auto-play-btn {
  padding: 0.5rem 1rem;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(52, 152, 219, 0.3);
}

.auto-play-btn:hover {
  background: #2980b9;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(52, 152, 219, 0.4);
}

.auto-play-btn.active {
  background: #e74c3c;
  box-shadow: 0 2px 4px rgba(231, 76, 60, 0.3);
}

.auto-play-btn.active:hover {
  background: #c0392b;
  box-shadow: 0 4px 8px rgba(231, 76, 60, 0.4);
}

/* 图片缩略图列表 */
.image-list {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
  overflow-x: auto;
  padding: 1rem 0;
  scrollbar-width: thin;
  scrollbar-color: #e74c3c transparent;
  position: relative;
}

.image-list::after {
  content: '';
  position: absolute;
  right: 0;
  top: 0;
  height: 100%;
  width: 50px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.8));
  pointer-events: none;
  z-index: 1;
}

.image-list::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 50px;
  background: linear-gradient(-90deg, transparent, rgba(255, 255, 255, 0.8));
  pointer-events: none;
  z-index: 1;
}

.image-list::-webkit-scrollbar {
  height: 6px;
  margin-top: 0.5rem;
}

.image-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.1);
}

.image-list::-webkit-scrollbar-thumb {
  background: #e74c3c;
  border-radius: 3px;
  transition: all 0.3s ease;
}

.image-list::-webkit-scrollbar-thumb:hover {
  background: #c0392b;
  transform: scaleY(1.2);
}

.image-thumbnail {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2px solid transparent;
  opacity: 0.7;
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 0;
}

.image-thumbnail::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, transparent 30%, rgba(255, 255, 255, 0.3) 50%, transparent 70%);
  transform: translateX(-100%);
  transition: transform 0.6s ease;
  z-index: 1;
  pointer-events: none;
}

.image-thumbnail:hover::before {
  transform: translateX(100%);
}

.image-thumbnail:hover {
  opacity: 1;
  border-color: #e74c3c;
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
  z-index: 10;
}

.image-thumbnail.active {
  opacity: 1;
  border-color: #e74c3c;
  box-shadow: 0 4px 12px rgba(231, 76, 60, 0.4);
  transform: scale(1.05);
  z-index: 5;
}

.image-thumbnail.active::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(231, 76, 60, 0.1);
  z-index: 1;
  pointer-events: none;
}

/* 章节标题样式 */
.section-title {
  color: #333;
  margin-bottom: 1.5rem;
  font-size: 1.3rem;
  font-weight: 700;
  position: relative;
  padding-left: 1rem;
  transition: all 0.3s ease;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 70%;
  background: #3498db;
  border-radius: 2px;
  transition: all 0.3s ease;
}

.section-title:hover::before {
  height: 100%;
  background: #2980b9;
}

/* 媒体项标题 */
.media-item-title {
  margin: 0 0 1rem 0;
  color: #333;
  font-size: 1.1rem;
  font-weight: 600;
  transition: all 0.3s ease;
}

/* 视频播放样式 */
.video-player,
.audio-player {
  margin-top: 2.5rem;
  padding: 2rem;
  background: #f9f9f9;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.video-player::before,
.audio-player::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, #3498db, #e74c3c);
  transition: left 0.6s ease;
}

.video-player:hover::before,
.audio-player:hover::before {
  left: 100%;
}

.video-player:hover,
.audio-player:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.video-list,
.audio-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.video-item,
.audio-item {
  padding: 1.5rem;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.video-item::before,
.audio-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: #3498db;
  transform: scaleY(0);
  transform-origin: top;
  transition: transform 0.6s ease;
}

.video-item:hover::before,
.audio-item:hover::before {
  transform: scaleY(1);
}

.video-item:hover,
.audio-item:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-4px);
}

.video-item:hover .media-item-title,
.audio-item:hover .media-item-title {
  color: #3498db;
}

/* 视频容器 */
.video-container {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.video-container:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
  transform: scale(1.02);
}

.video-element {
  width: 100%;
  max-height: 600px;
  border-radius: 8px;
  background: #000;
  transition: all 0.3s ease;
}

/* 音频容器 */
.audio-container {
  position: relative;
  width: 100%;
  max-width: 500px;
  transition: all 0.3s ease;
}

.audio-item:hover .audio-container {
  transform: scale(1.02);
}

.audio-element {
  width: 100%;
  height: 40px;
  transition: all 0.3s ease;
}

.audio-element:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 无媒体提示 */
.no-media {
  text-align: center;
  color: #999;
  font-style: italic;
  padding: 4rem;
  background: #f9f9f9;
  border-radius: 12px;
  margin: 2rem 0;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.no-media::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, #e74c3c, #3498db);
}

.no-media:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
  background: #f5f5f5;
}

.no-media-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  transition: all 0.3s ease;
}

.no-media:hover .no-media-icon {
  transform: scale(1.2) rotate(5deg);
}

.no-media p {
  margin: 0;
  font-size: 1.1rem;
  transition: all 0.3s ease;
}

.no-media:hover p {
  color: #666;
}

/* 响应式设计 */
@media (max-width: 768px) {
  /* 主容器 */
  .media-player {
    padding: 1.5rem;
    margin: 1rem 0;
  }
  
  .media-player-title {
    font-size: 1.5rem;
    margin-bottom: 1.5rem;
    padding-bottom: 0.75rem;
  }
  
  /* 轮播组件 */
  .carousel-wrapper {
    height: 300px;
  }
  
  .carousel-controls {
    flex-direction: column;
    gap: 1rem;
    padding: 1rem;
  }
  
  .control-btn {
    padding: 0.5rem 1rem;
    font-size: 0.9rem;
    width: 100%;
    max-width: 200px;
  }
  
  .carousel-info {
    flex-direction: row;
    gap: 1rem;
    width: 100%;
    justify-content: center;
  }
  
  /* 图片缩略图 */
  .image-thumbnail {
    width: 80px;
    height: 80px;
  }
  
  .image-list {
    gap: 0.75rem;
    padding: 0.75rem 0;
  }
  
  /* 播放器组件 */
  .video-player,
  .audio-player {
    padding: 1.5rem;
    margin: 1.5rem 0;
  }
  
  .video-element {
    max-height: 400px;
  }
  
  .audio-container {
    max-width: 100%;
  }
  
  .audio-element {
    width: 100%;
  }
  
  /* 媒体项 */
  .video-item,
  .audio-item {
    padding: 1.25rem;
  }
  
  .media-item-title {
    font-size: 1rem;
    margin-bottom: 0.75rem;
  }
  
  /* 无媒体提示 */
  .no-media {
    padding: 3rem;
    margin: 1.5rem 0;
  }
  
  .no-media-icon {
    font-size: 2.5rem;
    margin-bottom: 0.75rem;
  }
  
  .no-media p {
    font-size: 1rem;
  }
}

/* 平板设备响应式设计 */
@media (min-width: 769px) and (max-width: 1024px) {
  .carousel-wrapper {
    height: 400px;
  }
  
  .video-element {
    max-height: 500px;
  }
  
  .audio-container {
    max-width: 100%;
  }
}

/* 大屏幕设备响应式设计 */
@media (min-width: 1025px) {
  .carousel-wrapper {
    height: 550px;
  }
  
  .video-element {
    max-height: 650px;
  }
  
  .video-player,
  .audio-player {
    max-width: 1200px;
    margin-left: auto;
    margin-right: auto;
  }
}
</style>

