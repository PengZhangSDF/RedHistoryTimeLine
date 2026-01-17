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
    <h2>多媒体资料</h2>
    
    <!-- 图片轮播 -->
    <div class="image-carousel" v-if="images && images.length > 0">
      <div class="carousel-wrapper">
        <img 
          v-for="(image, index) in images" 
          :key="image.id"
          :src="image.url" 
          :alt="image.title || '事件图片'"
          v-show="currentImageIndex === index"
          class="carousel-image"
        />
      </div>
      <div class="carousel-controls">
        <button @click="prevImage" :disabled="images.length <= 1">上一张</button>
        <span class="image-counter">{{ currentImageIndex + 1 }} / {{ images.length }}</span>
        <button @click="nextImage" :disabled="images.length <= 1">下一张</button>
      </div>
      <div class="image-list" v-if="images.length > 1">
        <img 
          v-for="(image, index) in images" 
          :key="image.id"
          :src="image.url" 
          :alt="image.title"
          :class="{ active: currentImageIndex === index }"
          @click="currentImageIndex = index"
          class="image-thumbnail"
        />
      </div>
    </div>
    
    <!-- 视频播放 -->
    <div class="video-player" v-if="videos && videos.length > 0">
      <h3>历史视频</h3>
      <div class="video-list">
        <div v-for="video in videos" :key="video.id" class="video-item">
          <h4 v-if="video.title">{{ video.title }}</h4>
          <video :src="video.url" controls class="video-element"></video>
        </div>
      </div>
    </div>
    
    <!-- 音频播放 -->
    <div class="audio-player" v-if="audios && audios.length > 0">
      <h3>红色音乐</h3>
      <div class="audio-list">
        <div v-for="audio in audios" :key="audio.id" class="audio-item">
          <h4 v-if="audio.title">{{ audio.title }}</h4>
          <audio :src="audio.url" controls class="audio-element"></audio>
        </div>
      </div>
    </div>
    
    <p v-if="!hasMedia" class="no-media">暂无多媒体资料</p>
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
    }
  },
  data() {
    return {
      currentImageIndex: 0 // 当前显示的图片索引
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
    }
  },
  methods: {
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
    }
  }
};
</script>

<style scoped>
.media-player {
  margin: 2rem 0;
  padding: 1.5rem;
  background: white;
  border-radius: 8px;
}

.media-player h2 {
  color: #e74c3c;
  margin-bottom: 1.5rem;
}

/* 图片轮播样式 */
.image-carousel {
  margin-bottom: 2rem;
}

.carousel-wrapper {
  position: relative;
  width: 100%;
  height: 500px;
  overflow: hidden;
  border-radius: 8px;
  background: #000;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
}

.carousel-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 1rem;
}

.carousel-controls button {
  padding: 0.5rem 1.5rem;
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.carousel-controls button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.image-counter {
  font-weight: bold;
  color: #666;
}

.image-list {
  display: flex;
  gap: 0.5rem;
  margin-top: 1rem;
  overflow-x: auto;
  padding: 0.5rem 0;
}

.image-thumbnail {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
  border: 2px solid transparent;
  opacity: 0.6;
  transition: all 0.3s;
}

.image-thumbnail:hover {
  opacity: 1;
  border-color: #e74c3c;
}

.image-thumbnail.active {
  opacity: 1;
  border-color: #e74c3c;
}

/* 视频播放样式 */
.video-player,
.audio-player {
  margin-top: 2rem;
}

.video-player h3,
.audio-player h3 {
  color: #333;
  margin-bottom: 1rem;
}

.video-list,
.audio-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.video-item,
.audio-item {
  padding: 1rem;
  background: #f5f5f5;
  border-radius: 4px;
}

.video-item h4,
.audio-item h4 {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.video-element {
  width: 100%;
  max-height: 500px;
  border-radius: 4px;
}

.audio-element {
  width: 100%;
}

.no-media {
  text-align: center;
  color: #999;
  font-style: italic;
  padding: 2rem;
}
</style>

