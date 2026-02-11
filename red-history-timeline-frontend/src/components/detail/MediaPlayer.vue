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
            @load="handleImageLoad"
            @error="handleImageError"
          />
        </transition>
        <!-- 图片加载状态 -->
        <div v-if="imageLoading" class="image-loading">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>
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
            <!-- 如果是 B 站链接，用 iframe 嵌入播放器；否则用原生 video -->
            <template v-if="isBilibili(video.url)">
              <iframe
                class="video-element bilibili-iframe"
                :src="toBilibiliEmbed(video.url)"
                frameborder="0"
                allowfullscreen
                scrolling="no"
                sandbox="allow-scripts allow-same-origin allow-presentation"
              ></iframe>
            </template>
            <template v-else>
              <video 
                :src="video.url" 
                controls 
                class="video-element"
                @play="handleVideoPlay"
                @pause="handleVideoPause"
              ></video>
            </template>
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
            <audio 
              :src="audio.url" 
              controls 
              class="audio-element"
              @play="handleAudioPlay"
              @pause="handleAudioPause"
            ></audio>
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
      autoPlayTimer: null, // 自动播放定时器
      playingVideo: null, // 当前播放的视频元素
      playingAudio: null, // 当前播放的音频元素
      imageLoading: true // 图片加载状态
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
     * 判断是否为 B 站视频链接
     */
    isBilibili(url) {
      return typeof url === 'string' && url.includes('bilibili.com/video/');
    },

    /**
     * 将普通 B 站链接转换为可嵌入的播放器地址
     * 例如：https://www.bilibili.com/video/BVxxxx → https://player.bilibili.com/player.html?bvid=BVxxxx&page=1
     */
    toBilibiliEmbed(url) {
      if (!this.isBilibili(url)) return url;
      const match = url.match(/\/video\/(BV[0-9A-Za-z]+)/);
      const bvid = match ? match[1] : '';
      if (!bvid) return url;
      return `https://player.bilibili.com/player.html?bvid=${bvid}&page=1&high_quality=1&danmaku=0`;
    },
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
    },
    
    /**
     * 处理视频播放
     * 功能要求：确保同一时间只有一个视频在播放
     * @param {Event} event - 播放事件
     */
    handleVideoPlay(event) {
      const videoElement = event.target;
      // 暂停其他正在播放的视频
      if (this.playingVideo && this.playingVideo !== videoElement) {
        this.playingVideo.pause();
      }
      // 暂停正在播放的音频
      if (this.playingAudio) {
        this.playingAudio.pause();
      }
      // 更新当前播放的视频
      this.playingVideo = videoElement;
    },
    
    /**
     * 处理视频暂停
     * 功能要求：更新当前播放的视频状态
     * @param {Event} event - 暂停事件
     */
    handleVideoPause(event) {
      const videoElement = event.target;
      if (this.playingVideo === videoElement) {
        this.playingVideo = null;
      }
    },
    
    /**
     * 处理音频播放
     * 功能要求：确保同一时间只有一个音频在播放
     * @param {Event} event - 播放事件
     */
    handleAudioPlay(event) {
      const audioElement = event.target;
      // 暂停其他正在播放的音频
      if (this.playingAudio && this.playingAudio !== audioElement) {
        this.playingAudio.pause();
      }
      // 暂停正在播放的视频
      if (this.playingVideo) {
        this.playingVideo.pause();
      }
      // 更新当前播放的音频
      this.playingAudio = audioElement;
    },
    
    /**
     * 处理音频暂停
     * 功能要求：更新当前播放的音频状态
     * @param {Event} event - 暂停事件
     */
    handleAudioPause(event) {
      const audioElement = event.target;
      if (this.playingAudio === audioElement) {
        this.playingAudio = null;
      }
    },
    
    /**
     * 处理图片加载完成
     * 功能要求：图片加载完成后隐藏加载状态
     */
    handleImageLoad() {
      this.imageLoading = false;
    },
    
    /**
     * 处理图片加载失败
     * 功能要求：图片加载失败后隐藏加载状态
     */
    handleImageError() {
      this.imageLoading = false;
    },
    
    /**
     * 跳转到指定图片时重置加载状态
     * 功能要求：确保每次切换图片时都显示加载状态
     */
    goToImage(index) {
      this.imageLoading = true;
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
  margin: var(--spacing-xl, 2rem) 0;
  padding: var(--spacing-xl, 2rem);
  background: var(--bg-white, #FFFFFF);
  border-radius: var(--radius-lg, 12px);
  box-shadow: var(--shadow-md, 0 4px 8px rgba(0, 0, 0, 0.12));
  border: 1px solid var(--border-light, #eeeeee);
  transition: all var(--transition-normal, 0.3s ease);
  position: relative;
  overflow: hidden;
  animation: fadeIn 0.6s ease-out;
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
  transition: transform var(--transition-slow, 0.5s ease);
}

.media-player:hover::before {
  transform: scaleX(1);
}

.media-player:hover {
  box-shadow: var(--shadow-lg, 0 8px 16px rgba(0, 0, 0, 0.15));
  transform: translateY(-2px);
}

/* 标题样式 */
.media-player-title {
  color: var(--text-red, #e74c3c);
  margin-bottom: var(--spacing-xl, 2rem);
  font-size: var(--font-2xl, 1.5rem);
  font-weight: 700;
  text-align: center;
  padding-bottom: var(--spacing-md, 1rem);
  border-bottom: 2px solid var(--border-light, #eeeeee);
  position: relative;
  transition: all var(--transition-normal, 0.3s ease);
}

.media-player-title::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 50%;
  transform: translateX(-50%);
  width: 100px;
  height: 2px;
  background: var(--text-red, #e74c3c);
  transform: scaleX(0);
  transform-origin: center;
  transition: transform var(--transition-normal, 0.3s ease);
}

.media-player:hover .media-player-title::after {
  transform: scaleX(1) translateX(-50%);
}

/* 图片轮播样式 */
.image-carousel {
  margin-bottom: var(--spacing-xl, 2rem);
  border-radius: var(--radius-lg, 12px);
  overflow: hidden;
  box-shadow: var(--shadow-md, 0 4px 8px rgba(0, 0, 0, 0.12));
  transition: all var(--transition-normal, 0.3s ease);
  animation: fadeIn 0.6s ease-out 0.2s both;
}

.image-carousel:hover {
  box-shadow: var(--shadow-lg, 0 8px 16px rgba(0, 0, 0, 0.15));
}

.carousel-wrapper {
  position: relative;
  width: 100%;
  /* 高度扩大一倍，突出多媒体视觉区域 */
  height: 1000px;
  overflow: hidden;
  border-radius: var(--radius-lg, 12px);
  background: #000;
}

/* 轮播过渡动画 */
.carousel-fade-enter-active,
.carousel-fade-leave-active {
  transition: all var(--transition-slow, 0.5s ease);
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
  transition: all var(--transition-normal, 0.3s ease);
}

/* 图片加载状态 */
.image-loading {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 10;
  color: white;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 5px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s ease-in-out infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.image-loading p {
  margin: 0;
  font-size: 1.1rem;
}

/* 图片标题 */
.carousel-caption {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  padding: var(--spacing-lg, 1.5rem);
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
  color: var(--text-white, #FFFFFF);
  font-size: var(--font-lg, 1.125rem);
  font-weight: 600;
  text-align: center;
  transform: translateY(100%);
  transition: transform var(--transition-normal, 0.3s ease);
}

.carousel-wrapper:hover .carousel-caption {
  transform: translateY(0);
}

/* 轮播控制样式 */
.carousel-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--spacing-md, 1rem);
  margin-top: var(--spacing-md, 1rem);
  padding: var(--spacing-md, 1rem);
  background: var(--bg-light, #f8f9fa);
  border-radius: var(--radius-md, 8px);
  position: relative;
  overflow: hidden;
  border: 1px solid var(--border-light, #eeeeee);
}

.carousel-controls::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(231, 76, 60, 0.1), transparent);
  transition: left var(--transition-slow, 0.5s ease);
}

.carousel-controls:hover::before {
  left: 100%;
}

.control-btn {
  padding: var(--spacing-sm, 0.5rem) var(--spacing-lg, 1.5rem);
  background: var(--text-red, #e74c3c);
  color: var(--text-white, #FFFFFF);
  border: none;
  border-radius: var(--radius-md, 8px);
  cursor: pointer;
  font-size: var(--font-base, 1rem);
  font-weight: 500;
  transition: all var(--transition-normal, 0.3s ease);
  box-shadow: var(--shadow-red, 0 4px 12px rgba(231, 76, 60, 0.3));
  position: relative;
  overflow: hidden;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: var(--spacing-sm, 0.5rem);
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
  transition: width var(--transition-slow, 0.5s ease), height var(--transition-slow, 0.5s ease);
  z-index: -1;
}

.control-btn:hover:not(:disabled)::before {
  width: 300px;
  height: 300px;
}

.control-btn:hover:not(:disabled) {
  background: var(--text-red-dark, #c0392b);
  transform: translateY(-3px);
  box-shadow: var(--shadow-red-hover, 0 6px 20px rgba(231, 76, 60, 0.4));
}

.control-btn:disabled {
  background: var(--text-light, #999999);
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

.btn-icon {
  font-size: var(--font-lg, 1.125rem);
  font-weight: bold;
  transition: transform var(--transition-normal, 0.3s ease);
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
  gap: var(--spacing-sm, 0.5rem);
  flex: 1;
}

.image-counter {
  font-weight: 700;
  color: var(--text-gray, #666666);
  font-size: var(--font-base, 1rem);
  padding: var(--spacing-sm, 0.5rem) var(--spacing-md, 1rem);
  background: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  box-shadow: var(--shadow-sm, 0 2px 4px rgba(0, 0, 0, 0.1));
  transition: all var(--transition-normal, 0.3s ease);
  border: 1px solid var(--border-light, #eeeeee);
}

.carousel-controls:hover .image-counter {
  background: rgba(255, 255, 255, 1);
  box-shadow: var(--shadow-md, 0 4px 8px rgba(0, 0, 0, 0.12));
  transform: scale(1.05);
}

/* 自动播放按钮 */
.auto-play-btn {
  padding: var(--spacing-sm, 0.5rem) var(--spacing-md, 1rem);
  background: #3498db;
  color: var(--text-white, #FFFFFF);
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: var(--font-sm, 0.875rem);
  font-weight: 500;
  transition: all var(--transition-normal, 0.3s ease);
  box-shadow: 0 2px 4px rgba(52, 152, 219, 0.3);
}

.auto-play-btn:hover {
  background: #2980b9;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(52, 152, 219, 0.4);
}

.auto-play-btn.active {
  background: var(--text-red, #e74c3c);
  box-shadow: var(--shadow-red, 0 4px 12px rgba(231, 76, 60, 0.3));
}

.auto-play-btn.active:hover {
  background: var(--text-red-dark, #c0392b);
  box-shadow: var(--shadow-red-hover, 0 6px 20px rgba(231, 76, 60, 0.4));
}

/* 图片缩略图列表 */
.image-list {
  display: flex;
  gap: var(--spacing-md, 1rem);
  margin-top: var(--spacing-lg, 1.5rem);
  overflow-x: auto;
  padding: var(--spacing-md, 1rem) 0;
  scrollbar-width: thin;
  scrollbar-color: var(--text-red, #e74c3c) transparent;
  position: relative;
  animation: fadeIn 0.6s ease-out 0.4s both;
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
  margin-top: var(--spacing-sm, 0.5rem);
}

.image-list::-webkit-scrollbar-track {
  background: var(--bg-light, #f8f9fa);
  border-radius: var(--radius-sm, 4px);
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.1);
}

.image-list::-webkit-scrollbar-thumb {
  background: var(--text-red, #e74c3c);
  border-radius: var(--radius-sm, 4px);
  transition: all var(--transition-normal, 0.3s ease);
}

.image-list::-webkit-scrollbar-thumb:hover {
  background: var(--text-red-dark, #c0392b);
  transform: scaleY(1.2);
}

.image-thumbnail {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: var(--radius-md, 8px);
  cursor: pointer;
  transition: all var(--transition-normal, 0.3s ease);
  border: 2px solid transparent;
  opacity: 0.7;
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
  box-shadow: var(--shadow-sm, 0 2px 4px rgba(0, 0, 0, 0.1));
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
  transition: transform var(--transition-slow, 0.5s ease);
  z-index: 1;
  pointer-events: none;
}

.image-thumbnail:hover::before {
  transform: translateX(100%);
}

.image-thumbnail:hover {
  opacity: 1;
  border-color: var(--text-red, #e74c3c);
  transform: scale(1.1);
  box-shadow: var(--shadow-md, 0 4px 8px rgba(0, 0, 0, 0.12));
  z-index: 10;
}

.image-thumbnail.active {
  opacity: 1;
  border-color: var(--text-red, #e74c3c);
  box-shadow: var(--shadow-red, 0 4px 12px rgba(231, 76, 60, 0.3));
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
  color: var(--text-dark, #333333);
  margin-bottom: var(--spacing-lg, 1.5rem);
  font-size: var(--font-xl, 1.25rem);
  font-weight: 700;
  position: relative;
  padding-left: var(--spacing-md, 1rem);
  transition: all var(--transition-normal, 0.3s ease);
  animation: slideInLeft 0.6s ease-out 0.2s both;
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
  border-radius: var(--radius-sm, 4px);
  transition: all var(--transition-normal, 0.3s ease);
}

.section-title:hover::before {
  height: 100%;
  background: #2980b9;
  transform: translateY(-50%) scaleX(1.2);
}

.section-title:hover {
  color: #3498db;
  transform: translateX(5px);
}

/* 媒体项标题 */
.media-item-title {
  margin: 0 0 var(--spacing-md, 1rem) 0;
  color: var(--text-dark, #333333);
  font-size: var(--font-lg, 1.125rem);
  font-weight: 600;
  transition: all var(--transition-normal, 0.3s ease);
}

/* 视频播放样式 */
.video-player,
.audio-player {
  margin-top: var(--spacing-xxl, 3rem);
  padding: var(--spacing-xl, 2rem);
  background: var(--bg-light, #f8f9fa);
  border-radius: var(--radius-lg, 12px);
  box-shadow: var(--shadow-md, 0 4px 8px rgba(0, 0, 0, 0.12));
  transition: all var(--transition-normal, 0.3s ease);
  position: relative;
  overflow: hidden;
  border: 1px solid var(--border-light, #eeeeee);
  animation: fadeIn 0.6s ease-out 0.3s both;
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
  transition: left var(--transition-slow, 0.5s ease);
}

.video-player:hover::before,
.audio-player:hover::before {
  left: 100%;
}

.video-player:hover,
.audio-player:hover {
  box-shadow: var(--shadow-lg, 0 8px 16px rgba(0, 0, 0, 0.15));
  transform: translateY(-2px);
}

.video-list,
.audio-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg, 1.5rem);
}

.video-item,
.audio-item {
  padding: var(--spacing-lg, 1.5rem);
  background: var(--bg-white, #FFFFFF);
  border-radius: var(--radius-md, 8px);
  box-shadow: var(--shadow-sm, 0 2px 4px rgba(0, 0, 0, 0.1));
  transition: all var(--transition-normal, 0.3s ease);
  position: relative;
  overflow: hidden;
  animation: fadeIn 0.6s ease-out 0.4s both;
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
  transition: transform var(--transition-slow, 0.5s ease);
}

.video-item:hover::before,
.audio-item:hover::before {
  transform: scaleY(1);
}

.video-item:hover,
.audio-item:hover {
  box-shadow: var(--shadow-md, 0 4px 8px rgba(0, 0, 0, 0.12));
  transform: translateY(-4px);
}

.video-item:hover .media-item-title,
.audio-item:hover .media-item-title {
  color: #3498db;
}

/* 视频容器 */
.video-container {
  position: relative;
  border-radius: var(--radius-md, 8px);
  overflow: hidden;
  box-shadow: var(--shadow-md, 0 4px 8px rgba(0, 0, 0, 0.12));
  transition: all var(--transition-normal, 0.3s ease);
  /* 桌面端固定高度，让视频真正放大显示 */
  height: 600px;
}

.video-container:hover {
  box-shadow: var(--shadow-lg, 0 8px 16px rgba(0, 0, 0, 0.15));
  transform: scale(1.02);
}

.video-element {
  width: 100%;
  /* 让视频填满容器高度，真正变大 */
  height: 100%;
  max-height: none;
  border-radius: var(--radius-md, 8px);
  background: #000;
  transition: all var(--transition-normal, 0.3s ease);
}

/* B站 iframe 播放器占满容器 */
.bilibili-iframe {
  border: none;
  width: 100%;
  height: 100%;
}

/* 音频容器 */
.audio-container {
  position: relative;
  width: 100%;
  max-width: 500px;
  transition: all var(--transition-normal, 0.3s ease);
}

.audio-item:hover .audio-container {
  transform: scale(1.02);
}

.audio-element {
  width: 100%;
  height: 40px;
  transition: all var(--transition-normal, 0.3s ease);
}

.audio-element:hover {
  box-shadow: var(--shadow-sm, 0 2px 4px rgba(0, 0, 0, 0.1));
}

/* 无媒体提示 */
.no-media {
  text-align: center;
  color: var(--text-light, #999999);
  font-style: italic;
  padding: var(--spacing-xxl, 3rem);
  background: var(--bg-light, #f8f9fa);
  border-radius: var(--radius-lg, 12px);
  margin: var(--spacing-xl, 2rem) 0;
  box-shadow: var(--shadow-md, 0 4px 8px rgba(0, 0, 0, 0.12));
  transition: all var(--transition-normal, 0.3s ease);
  position: relative;
  overflow: hidden;
  border: 1px solid var(--border-light, #eeeeee);
  animation: fadeIn 0.6s ease-out;
}

.no-media::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, var(--text-red, #e74c3c), #3498db);
}

.no-media:hover {
  box-shadow: var(--shadow-lg, 0 8px 16px rgba(0, 0, 0, 0.15));
  transform: translateY(-2px);
  background: var(--bg-gray, #F5F5F5);
}

.no-media-icon {
  font-size: 3rem;
  margin-bottom: var(--spacing-md, 1rem);
  transition: all var(--transition-normal, 0.3s ease);
}

.no-media:hover .no-media-icon {
  transform: scale(1.2) rotate(5deg);
}

.no-media p {
  margin: 0;
  font-size: var(--font-lg, 1.125rem);
  transition: all var(--transition-normal, 0.3s ease);
}

.no-media:hover p {
  color: var(--text-gray, #666666);
}

/* 响应式设计 */
@media (max-width: 768px) {
  /* 主容器 */
  .media-player {
    padding: var(--spacing-lg, 1.5rem);
    margin: var(--spacing-md, 1rem) 0;
  }
  
  .media-player-title {
    font-size: var(--font-xl, 1.25rem);
    margin-bottom: var(--spacing-lg, 1.5rem);
    padding-bottom: var(--spacing-sm, 0.5rem);
  }
  
  /* 轮播组件 */
  .carousel-wrapper {
    height: 600px;
  }
  
  .carousel-controls {
    flex-direction: column;
    gap: var(--spacing-md, 1rem);
    padding: var(--spacing-md, 1rem);
  }
  
  .control-btn {
    padding: var(--spacing-sm, 0.5rem) var(--spacing-md, 1rem);
    font-size: var(--font-sm, 0.875rem);
    width: 100%;
    max-width: 200px;
  }
  
  .carousel-info {
    flex-direction: row;
    gap: var(--spacing-md, 1rem);
    width: 100%;
    justify-content: center;
  }
  
  /* 图片缩略图 */
  .image-thumbnail {
    width: 80px;
    height: 80px;
  }
  
  .image-list {
    gap: var(--spacing-sm, 0.5rem);
    padding: var(--spacing-sm, 0.5rem) 0;
  }
  
  /* 播放器组件 */
  .video-player,
  .audio-player {
    padding: var(--spacing-lg, 1.5rem);
    margin: var(--spacing-lg, 1.5rem) 0;
  }
  
  .video-container {
    height: 300px;
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
    padding: var(--spacing-lg, 1.5rem);
  }
  
  .media-item-title {
    font-size: var(--font-base, 1rem);
    margin-bottom: var(--spacing-sm, 0.5rem);
  }
  
  /* 无媒体提示 */
  .no-media {
    padding: var(--spacing-xl, 2rem);
    margin: var(--spacing-lg, 1.5rem) 0;
  }
  
  .no-media-icon {
    font-size: 2.5rem;
    margin-bottom: var(--spacing-sm, 0.5rem);
  }
  
  .no-media p {
    font-size: var(--font-base, 1rem);
  }
}

/* 平板设备响应式设计 */
@media (min-width: 769px) and (max-width: 1024px) {
  .carousel-wrapper {
    height: 800px;
  }
  
  .video-container {
    height: 450px;
  }
  
  .audio-container {
    max-width: 100%;
  }
}

/* 大屏幕设备响应式设计 */
@media (min-width: 1025px) {
  .carousel-wrapper {
    height: 1100px;
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

