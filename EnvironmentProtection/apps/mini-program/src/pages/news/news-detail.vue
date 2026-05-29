<template>
  <view class="page">
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-state">
      <text class="loading-text">加载中...</text>
    </view>

    <template v-else-if="news">
      <!-- 新闻内容 -->
      <view class="news-header">
        <view class="news-tags">
          <text class="news-tag" :class="'tag-' + news.type">{{ getTypeName(news.type) }}</text>
          <text v-if="news.isHot" class="hot-badge">🔥 热门</text>
        </view>
        <text class="news-title">{{ news.title }}</text>
        <view class="news-meta">
          <text class="meta-source">{{ news.source || '官方发布' }}</text>
          <text class="meta-author" v-if="news.author">{{ news.author }}</text>
          <text class="meta-time">{{ formatDate(news.publishTime) }}</text>
        </view>
      </view>

      <!-- 封面图 -->
      <image
        v-if="news.coverUrl"
        class="news-cover"
        :src="news.coverUrl"
        mode="widthFix"
      />

      <!-- 文章内容 -->
      <view class="news-content">
        <text class="content-text">{{ news.content }}</text>
      </view>

      <!-- 标签 -->
      <view class="news-tags-row" v-if="news.tags">
        <text
          v-for="tag in parseTags(news.tags)"
          :key="tag"
          class="tag-item"
        >
          {{ tag }}
        </text>
      </view>

      <!-- 互动栏 -->
      <view class="action-bar">
        <view class="action-item" @click="toggleLike">
          <text class="action-icon">{{ isLiked ? '❤️' : '🤍' }}</text>
          <text class="action-text">{{ news.likeCount || 0 }}</text>
        </view>
        <view class="action-item">
          <text class="action-icon">💬</text>
          <text class="action-text">{{ news.commentCount || 0 }}</text>
        </view>
        <view class="action-item">
          <text class="action-icon">👁</text>
          <text class="action-text">{{ news.viewCount || 0 }}</text>
        </view>
        <view class="action-item" @click="shareNews">
          <text class="action-icon">📤</text>
          <text class="action-text">分享</text>
        </view>
      </view>

      <!-- 评论区 -->
      <view class="comment-section">
        <view class="section-header">
          <text class="section-title">评论 ({{ comments.length }})</text>
        </view>

        <!-- 空评论 -->
        <view v-if="comments.length === 0" class="empty-comments">
          <text class="empty-icon">💬</text>
          <text class="empty-text">暂无评论，快来抢沙发吧~</text>
        </view>

        <!-- 评论列表 -->
        <view
          v-for="comment in comments"
          :key="comment.id"
          class="comment-item"
        >
          <view class="comment-user">
            <image
              class="user-avatar"
              :src="comment.userAvatar || '/static/default-avatar.png'"
              mode="aspectFill"
            />
            <view class="user-info">
              <text class="user-name">{{ comment.userNickname || '匿名用户' }}</text>
              <text class="comment-time">{{ formatCommentTime(comment.createTime) }}</text>
            </view>
          </view>
          <text class="comment-content">{{ comment.content }}</text>
          <view class="comment-actions">
            <view class="action-btn" @click="toggleCommentLike(comment)">
              <text class="btn-icon">{{ comment.isLiked ? '❤️' : '🤍' }}</text>
              <text class="btn-text">{{ comment.likeCount || 0 }}</text>
            </view>
            <view class="action-btn" @click="showReplyInput(comment)">
              <text class="btn-icon">↩️</text>
              <text class="btn-text">回复</text>
            </view>
          </view>

          <!-- 回复列表 -->
          <view v-if="comment.replyCount > 0" class="replies">
            <view
              v-for="reply in comment.replies"
              :key="reply.id"
              class="reply-item"
              @click="showReplyInput(comment, reply)"
            >
              <text class="reply-user">{{ reply.userNickname || '匿名用户' }}</text>
              <text v-if="reply.replyTo" class="reply-to"> 回复 </text>
              <text class="reply-to-user">{{ reply.replyTo }}</text>
              <text class="reply-colon">：</text>
              <text class="reply-content">{{ reply.content }}</text>
            </view>
            <view
              v-if="comment.replyCount > 2 && !comment.showAllReplies"
              class="load-replies"
              @click="loadMoreReplies(comment)"
            >
              <text>展开 {{ comment.replyCount }} 条回复 ›</text>
            </view>
          </view>
        </view>
      </view>
    </template>

    <!-- 底部输入框 -->
    <view class="input-bar">
      <view class="input-wrapper" @click="showReplyInput(null)">
        <text class="input-placeholder">写评论...</text>
      </view>
    </view>

    <!-- 评论弹窗 -->
    <view v-if="showInput" class="input-popup" @click="hideInput">
      <view class="input-popup-content" @click.stop>
        <textarea
          v-model="commentContent"
          class="comment-input"
          :placeholder="replyTo ? `回复 @${replyTo.userNickname}` : '请输入评论'"
          :auto-focus="true"
          :maxlength="500"
        />
        <view class="input-actions">
          <text class="char-count">{{ commentContent.length }}/500</text>
          <view class="send-btn" @click="submitComment">
            <text class="send-text">发送</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { api } from '../../api/index.js'
import { userStore } from '../../store/user.js'

const loading = ref(true)
const news = ref(null)
const isLiked = ref(false)
const comments = ref([])
const showInput = ref(false)
const commentContent = ref('')
const replyTo = ref(null)

onMounted(async () => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const id = currentPage.options?.id

  if (id) {
    await Promise.all([
      loadNews(id),
      loadComments(id),
      checkLikeStatus(id)
    ])
  }
  loading.value = false
})

async function loadNews(id) {
  try {
    const res = await api.news.get(id)
    if (res) {
      news.value = res
    }
  } catch (error) {
    console.error('加载新闻详情失败:', error)
  }
}

async function loadComments(targetId) {
  try {
    const res = await api.comment.list('news', targetId)
    if (res && Array.isArray(res)) {
      comments.value = res.map(c => ({ ...c, replies: [], showAllReplies: false }))
    }
  } catch (error) {
    console.error('加载评论失败:', error)
  }
}

async function checkLikeStatus(id) {
  const userId = userStore.userInfo?.id
  if (!userId) return
  try {
    const res = await api.news.isLiked(id, userId)
    isLiked.value = res
  } catch (error) {
    console.error('检查点赞状态失败:', error)
  }
}

async function toggleLike() {
  const userId = userStore.userInfo?.id
  if (!userId) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    return
  }
  try {
    const res = await api.news.like(news.value.id, userId)
    isLiked.value = !isLiked.value
    news.value.likeCount = (news.value.likeCount || 0) + (isLiked.value ? 1 : -1)
    uni.showToast({ title: res || '操作成功', icon: 'none' })
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

async function toggleCommentLike(comment) {
  const userId = userStore.userInfo?.id
  if (!userId) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    return
  }
  try {
    await api.comment.like(comment.id, userId)
    comment.isLiked = !comment.isLiked
    comment.likeCount = (comment.likeCount || 0) + (comment.isLiked ? 1 : -1)
  } catch (error) {
    console.error('评论点赞失败:', error)
  }
}

function showReplyInput(parentComment, reply = null) {
  const userId = userStore.userInfo?.id
  if (!userId) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    return
  }
  replyTo.value = reply ? { ...reply, parentComment } : (parentComment ? { parentComment } : null)
  showInput.value = true
}

function hideInput() {
  showInput.value = false
  commentContent.value = ''
  replyTo.value = null
}

async function submitComment() {
  if (!commentContent.value.trim()) {
    uni.showToast({ title: '请输入评论内容', icon: 'none' })
    return
  }
  const userId = userStore.userInfo?.id
  const userInfo = userStore.userInfo
  try {
    const data = {
      content: commentContent.value.trim(),
      targetType: 'news',
      targetId: news.value.id,
      userId: userId,
      userNickname: userInfo?.nickname || userInfo?.username || '匿名用户',
      userAvatar: userInfo?.avatar
    }
    if (replyTo.value?.parentComment) {
      data.parentId = replyTo.value.parentComment.id
      data.rootId = replyTo.value.parentComment.rootId || replyTo.value.parentComment.id
    }
    await api.comment.create(data)
    hideInput()
    uni.showToast({ title: '评论成功', icon: 'success' })
    await loadComments(news.value.id)
    news.value.commentCount = (news.value.commentCount || 0) + 1
  } catch (error) {
    console.error('发表评论失败:', error)
  }
}

async function loadMoreReplies(comment) {
  try {
    const res = await api.comment.getReplies(comment.id)
    if (res && Array.isArray(res)) {
      comment.replies = res
      comment.showAllReplies = true
    }
  } catch (error) {
    console.error('加载回复失败:', error)
  }
}

function shareNews() {
  uni.showShareMenu({
    withShareTicket: true,
    menus: ['shareAppMessage', 'shareTimeline']
  })
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  try {
    const date = new Date(dateStr)
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  } catch {
    return dateStr
  }
}

function formatCommentTime(dateStr) {
  if (!dateStr) return ''
  try {
    const date = new Date(dateStr)
    const now = new Date()
    const diff = now - date
    if (diff < 60000) return '刚刚'
    if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
    if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
    if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
    return formatDate(dateStr)
  } catch {
    return dateStr
  }
}

function parseTags(tags) {
  try {
    if (typeof tags === 'string') {
      return JSON.parse(tags)
    }
    return tags || []
  } catch {
    return []
  }
}

function getTypeName(type) {
  const map = {
    hotspot: '热点',
    policy: '政策解读',
    news: '新闻',
    activity: '活动'
  }
  return map[type] || '资讯'
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f0f7f2;
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
}

.loading-state {
  padding: 200rpx 0;
  text-align: center;
}

.loading-text {
  font-size: 28rpx;
  color: #999;
}

.news-header {
  padding: 32rpx 24rpx 24rpx;
  background: #fff;
}

.news-tags {
  display: flex;
  gap: 12rpx;
  margin-bottom: 16rpx;
}

.news-tag {
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
  background: #e8f5eb;
  color: #28a745;
}

.tag-hotspot {
  background: #fff3e0;
  color: #e65100;
}

.tag-policy {
  background: #e3f2fd;
  color: #1565c0;
}

.tag-activity {
  background: #f3e5f5;
  color: #7b1fa2;
}

.hot-badge {
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
  background: linear-gradient(135deg, #ff6b6b, #ee5a5a);
  color: #fff;
}

.news-title {
  font-size: 38rpx;
  font-weight: 700;
  color: #222;
  line-height: 1.4;
  margin-bottom: 20rpx;
}

.news-meta {
  display: flex;
  gap: 20rpx;
  font-size: 24rpx;
  color: #999;
}

.news-cover {
  width: 100%;
  display: block;
}

.news-content {
  padding: 32rpx 24rpx;
  background: #fff;
}

.content-text {
  font-size: 32rpx;
  color: #333;
  line-height: 1.8;
  white-space: pre-wrap;
}

.news-tags-row {
  display: flex;
  gap: 12rpx;
  padding: 0 24rpx 24rpx;
  background: #fff;
  flex-wrap: wrap;
}

.tag-item {
  padding: 8rpx 20rpx;
  background: #f0f7f2;
  border-radius: 999rpx;
  font-size: 24rpx;
  color: #666;
}

.action-bar {
  display: flex;
  justify-content: space-around;
  padding: 24rpx;
  background: #fff;
  border-top: 1rpx solid #eee;
  margin-bottom: 16rpx;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.action-icon {
  font-size: 32rpx;
}

.action-text {
  font-size: 26rpx;
  color: #666;
}

.comment-section {
  background: #fff;
  padding: 24rpx;
}

.section-header {
  margin-bottom: 24rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #222;
}

.empty-comments {
  text-align: center;
  padding: 48rpx 0;
}

.empty-icon {
  font-size: 64rpx;
  display: block;
  margin-bottom: 16rpx;
}

.empty-text {
  font-size: 26rpx;
  color: #999;
}

.comment-item {
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f0f7f2;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-user {
  display: flex;
  gap: 16rpx;
  margin-bottom: 16rpx;
}

.user-avatar {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background: #e8f5eb;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 4rpx;
}

.comment-time {
  font-size: 22rpx;
  color: #999;
}

.comment-content {
  font-size: 28rpx;
  color: #333;
  line-height: 1.6;
  display: block;
  margin-bottom: 16rpx;
}

.comment-actions {
  display: flex;
  gap: 32rpx;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6rpx;
}

.btn-icon {
  font-size: 26rpx;
}

.btn-text {
  font-size: 24rpx;
  color: #999;
}

.replies {
  margin-top: 16rpx;
  padding: 16rpx;
  background: #f9f9f9;
  border-radius: 12rpx;
}

.reply-item {
  padding: 12rpx 0;
  font-size: 26rpx;
  line-height: 1.5;
}

.reply-user {
  color: #28a745;
  font-weight: 500;
}

.reply-to,
.reply-to-user {
  color: #999;
}

.reply-content {
  color: #333;
}

.load-replies {
  padding: 12rpx 0;
  text-align: center;
  font-size: 24rpx;
  color: #28a745;
}

.input-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16rpx 24rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  background: #fff;
  border-top: 1rpx solid #eee;
  z-index: 100;
}

.input-wrapper {
  padding: 20rpx 24rpx;
  background: #f5f5f5;
  border-radius: 999rpx;
}

.input-placeholder {
  font-size: 28rpx;
  color: #999;
}

.input-popup {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 200;
  display: flex;
  align-items: flex-end;
}

.input-popup-content {
  width: 100%;
  padding: 24rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  background: #fff;
  border-radius: 24rpx 24rpx 0 0;
}

.comment-input {
  width: 100%;
  min-height: 200rpx;
  padding: 24rpx;
  background: #f5f5f5;
  border-radius: 16rpx;
  font-size: 28rpx;
  color: #333;
  line-height: 1.6;
  box-sizing: border-box;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20rpx;
}

.char-count {
  font-size: 24rpx;
  color: #999;
}

.send-btn {
  padding: 16rpx 48rpx;
  background: linear-gradient(135deg, #28a745, #1a4d2e);
  border-radius: 999rpx;
}

.send-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #fff;
}
</style>
