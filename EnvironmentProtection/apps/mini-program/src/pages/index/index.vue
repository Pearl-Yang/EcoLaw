<template>
  <view class="page">
    <!-- 顶部区域：品牌 + 消息图标 -->
    <view class="top-bar">
      <view class="brand-area">
        <view class="brand-name-row">
          <text class="brand-name">绿法通</text>
          <text v-if="currentRoleLabel" class="brand-role-pill">{{ roleBadgeText }}</text>
        </view>
        <text class="brand-sub">{{ brandSlogan }}</text>
      </view>
      <view class="top-actions">
        <view class="action-btn action-btn--notice" @click="goMessages">
          <LineIcon name="bell" :size="36" color="#b45309" />
          <view v-if="unreadCount > 0" class="badge">{{ unreadCount > 99 ? '99+' : unreadCount }}</view>
        </view>
        <view class="action-btn action-btn--map" @click="goMap">
          <LineIcon name="map" :size="36" color="#7c3aed" />
        </view>
      </view>
    </view>

    <!-- 搜索框 -->
    <view class="search-bar" @click="goLawSearch">
      <LineIcon name="search" :size="32" color="#6b7280" class="search-ico" />
      <text class="search-placeholder">搜索法规/问题/案例...</text>
    </view>

    <!-- Banner轮播 -->
    <view class="banner-section">
      <swiper class="banner-swiper" :indicator-dots="true" :autoplay="true" :interval="4000" :circular="true" indicator-color="rgba(107,114,128,0.35)" indicator-active-color="#22c55e">
        <swiper-item v-for="(item, index) in homeBanners" :key="index" @click="onBannerClick(item)">
          <view class="banner-item" :style="{ background: item.bg }">
            <text class="banner-title">{{ item.title }}</text>
            <text class="banner-sub">{{ item.sub }}</text>
          </view>
        </swiper-item>
      </swiper>
    </view>

    <view class="role-tags" v-if="currentRoleLabel">
      <text class="role-tag active">{{ currentRoleLabel }}</text>
    </view>

    <!-- 核心功能入口 -->
    <view class="card section-card">
      <text class="section-label">核心功能</text>
      <view class="grid-5">
        <view
          v-for="item in coreEntries"
          :key="item.id"
          class="grid-5-item"
          @click="goPage(item.path)"
        >
          <view class="g5-icon">
            <LineIcon :name="item.icon" :size="44" :color="item.iconColor" />
          </view>
          <text class="g5-title">{{ item.title }}</text>
        </view>
      </view>
    </view>

    <!-- 任务提醒卡片 -->
    <view class="task-remind" @click="switchTab('/pages/task/task')">
      <view class="remind-left">
        <view class="remind-icon-wrap">
          <LineIcon name="bell" :size="40" color="#ca8a04" />
        </view>
        <view class="remind-text">
          <text class="remind-title">{{ taskRemindTitle }}</text>
          <text class="remind-sub">{{ taskRemindSub }}</text>
        </view>
      </view>
      <text class="remind-arrow">›</text>
    </view>

    <view class="role-row">
      <view
        v-for="card in roleStripCards"
        :key="card.key"
        class="role-card"
        :class="card.cardClass"
        @click="goPage(card.path)"
      >
        <text class="role-t">{{ card.title }}</text>
        <text class="role-d">{{ card.desc }}</text>
      </view>
    </view>

    <view class="dual-row">
      <view class="dual-card" @click="goPage(dualPrimary.path)">
        <text class="dual-title">{{ dualPrimary.title }}</text>
        <text class="dual-desc">{{ dualPrimary.desc }}</text>
      </view>
      <view class="dual-card dual-chart" @click="goPage(dualSecondary.path)">
        <text class="dual-title">{{ dualSecondary.title }}</text>
        <view v-if="dualSecondary.showBars" class="mini-bars">
          <view
            v-for="(h, i) in barHeights"
            :key="i"
            class="mini-bar"
            :style="{ height: h + 'rpx' }"
          />
        </view>
        <text class="dual-more">{{ dualSecondary.more }}</text>
      </view>
    </view>

    <!-- 普法学习推荐 -->
    <view class="card section-card">
      <view class="section-head">
        <text class="section-title">{{ learnSectionTitle }}</text>
        <text class="section-more" @click="switchTab('/pages/learn/learn')">更多 ›</text>
      </view>
      <view class="learn-tabs">
        <view 
          v-for="tab in learnTabs" 
          :key="tab.key" 
          class="learn-tab" 
          :class="{ active: currentLearnTab === tab.key }"
          @click="currentLearnTab = tab.key"
        >
          <LineIcon :name="tab.icon" :size="28" :color="currentLearnTab === tab.key ? '#15803d' : '#6b7280'" />
          <text class="tab-name">{{ tab.name }}</text>
        </view>
      </view>
    </view>

    <!-- 进行中的任务 -->
    <view class="card section-card">
      <view class="section-head">
        <text class="section-title">进行中的任务</text>
        <text class="section-more" @click="switchTab('/pages/task/task')">更多 ›</text>
      </view>
      <view v-if="loadingTasksValue" class="loading-state">
        <text class="loading-text">加载中...</text>
      </view>
      <view v-else-if="tasks.length === 0" class="empty-state">
        <text class="empty-text">暂无进行中的任务</text>
      </view>
      <view
        v-else
        v-for="task in tasks"
        :key="task.id"
        class="task-item"
        @click="goTask(task.id)"
      >
        <view class="task-main">
          <text class="task-title">{{ task.title || task.name }}</text>
          <text class="task-desc">{{ task.description || task.desc || '点击查看详情' }}</text>
          <view class="task-meta">
            <text class="task-deadline">截止 {{ task.deadline || formatDate(task.endTime) }}</text>
            <text class="task-points">+{{ task.points || task.score || 0 }} 积分</text>
          </view>
        </view>
        <view class="task-progress">
          <view class="progress-track">
            <view
              class="progress-fill"
              :style="{ width: (task.progress || task.percent || 0) + '%' }"
            />
          </view>
          <text class="progress-num">{{ task.progress || task.percent || 0 }}%</text>
        </view>
      </view>
    </view>

    <!-- 最新资讯 -->
    <view class="card section-card">
      <view class="section-head">
        <text class="section-title">最新资讯</text>
        <text class="section-more" @click="goNewsList">更多 ›</text>
      </view>
      <view v-if="loadingNewsValue" class="loading-state">
        <text class="loading-text">加载中...</text>
      </view>
      <view v-else-if="newsList.length === 0" class="empty-state">
        <text class="empty-text">暂无资讯</text>
      </view>
      <view 
        v-else
        v-for="item in newsList" 
        :key="item.id" 
        class="news-item"
        @click="goNewsDetail(item.id)"
      >
        <image v-if="item.cover" class="news-cover" :src="item.cover" mode="aspectFill" />
        <view class="news-content">
          <text class="news-title">{{ item.title }}</text>
          <text class="news-date">{{ formatDate(item.publishTime || item.createdAt) }}</text>
        </view>
      </view>
    </view>

    <!-- 热门法规 -->
    <view class="card section-card">
      <view class="section-head">
        <text class="section-title">热门法规</text>
        <text class="section-more" @click="goLawSearch">更多 ›</text>
      </view>
      <view v-if="loadingLawsValue" class="loading-state">
        <text class="loading-text">加载中...</text>
      </view>
      <view v-else-if="laws.length === 0" class="empty-state">
        <text class="empty-text">暂无热门法规</text>
      </view>
      <view
        v-else
        v-for="law in laws"
        :key="law.id"
        class="law-row"
        @click="goLaw(law.id)"
      >
        <view class="law-badge" :class="getLawBadgeClass(law.level)">
          {{ getLawBadgeText(law.level) }}
        </view>
        <view class="law-body">
          <text class="law-title">{{ law.title || law.name }}</text>
          <text class="law-date">发布 {{ law.publishDate || formatDate(law.createdAt) }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { userStore } from '../../store/user.js'
import { api } from '../../api/index.js'
import LineIcon from '../../components/LineIcon.vue'

const TAB_PREFIXES = ['/pages/index/index', '/pages/learn/learn', '/pages/task/task', '/pages/ai-chat/chat', '/pages/profile/profile']

export default {
  components: { LineIcon },
  data() {
    return {
      unreadCount: 0,
      pendingTaskCount: 0,
      currentLearnTab: 'video',
      barHeights: [32, 48, 40, 56, 36],
      learnTabs: [
        { key: 'video', name: '视频课程', icon: 'video' },
        { key: 'article', name: '图文推送', icon: 'newspaper' },
        { key: 'case', name: '案例分享', icon: 'clipboard-list' }
      ],
      tasks: [],
      laws: [],
      newsList: [],
      loadingValue: false,
      loadingTasksValue: false,
      loadingLawsValue: false,
      loadingNewsValue: false
    }
  },
  computed: {
    isEnterpriseEmployee() {
      return userStore.userInfo?.role === 'enterprise_employee'
    },
    roleBadgeText() {
      return this.isEnterpriseEmployee ? '岗位端' : '基层端'
    },
    brandSlogan() {
      return this.isEnterpriseEmployee
        ? '岗位合规 · 减塑守法 · 企业内部学法'
        : '塑料污染治理 · 村社普法 · 任务与线索'
    },
    coreEntries() {
      if (this.isEnterpriseEmployee) {
        return [
          { id: 'e1', title: '禁塑政策', icon: 'clipboard-list', iconColor: '#22c55e', path: '/pages/law-search/law-search' },
          { id: 'e2', title: '必修学习', icon: 'graduation-cap', iconColor: '#a855f7', path: '/pages/learn/learn' },
          { id: 'e3', title: '合规问答', icon: 'message-circle', iconColor: '#ca8a04', path: '/pages/ai-chat/chat' },
          { id: 'e4', title: '任务待办', icon: 'check-square', iconColor: '#22c55e', path: '/pages/task/task' },
          { id: 'e5', title: '风险线索', icon: 'map-pin', iconColor: '#7c3aed', path: '/pages/report/report' }
        ]
      }
      return [
        { id: 1, title: 'AI 素材', icon: 'file-text', iconColor: '#22c55e', path: '/pages/ai-chat/chat' },
        { id: 2, title: '法条解读', icon: 'book-open', iconColor: '#a855f7', path: '/pages/ai-chat/chat' },
        { id: 3, title: '环保科普', icon: 'leaf', iconColor: '#4ade80', path: '/pages/learn/learn' },
        { id: 4, title: '线索举报', icon: 'map-pin', iconColor: '#ca8a04', path: '/pages/report/report' },
        { id: 5, title: '法律咨询', icon: 'message-circle', iconColor: '#22c55e', path: '/pages/ai-chat/chat' }
      ]
    },
    roleStripCards() {
      if (this.isEnterpriseEmployee) {
        return [
          { key: 'e-a', title: '制度速览', desc: '法规与内部要求', path: '/pages/law-search/law-search', cardClass: 'role-a' },
          { key: 'e-b', title: '我的学时', desc: '培训与考试', path: '/pages/learn/learn', cardClass: 'role-b' },
          { key: 'e-c', title: '合规台账', desc: '档案与留痕', path: '/pages/profile/profile', cardClass: 'role-c' }
        ]
      }
      return [
        { key: 'g-a', title: '普法速递', desc: '任务与活动', path: '/pages/task/task', cardClass: 'role-a' },
        { key: 'g-b', title: '环保科普', desc: '课程与学习', path: '/pages/learn/learn', cardClass: 'role-b' },
        { key: 'g-c', title: '明白人专区', desc: '积分与执行', path: '/pages/task/task', cardClass: 'role-c' }
      ]
    },
    dualPrimary() {
      return this.isEnterpriseEmployee
        ? { title: '岗位合规清单', desc: '对照禁塑与固废要求，完成日常自查与记录', path: '/pages/task/task' }
        : { title: '法律明白人专区', desc: '任务执行、过程留痕与积分激励', path: '/pages/task/task' }
    },
    dualSecondary() {
      return this.isEnterpriseEmployee
        ? { title: '学时与排行', desc: '完成必修、查看团队学习进度', more: '排行 ›', path: '/pages/ranking/ranking', showBars: true }
        : { title: '学习数据', desc: '', more: '排行 ›', path: '/pages/ranking/ranking', showBars: true }
    },
    currentRoleLabel() {
      const r = userStore.userInfo?.role
      if (r === 'enterprise_employee') return '当前身份：企业员工'
      if (r === 'grass_roots') return '当前身份：基层用户'
      return ''
    },
    homeBanners() {
      if (this.isEnterpriseEmployee) {
        return [
          { title: '守法经营 · 岗位合规', sub: '禁塑政策、内部学时与 AI 合规问答', bg: 'linear-gradient(135deg, #ffffff 0%, #e0e7ff 45%, #d1fae5 100%)' },
          { title: '固废法与塑料污染治理要点', sub: '仓储、门店、产线通用清单', bg: 'linear-gradient(135deg, #fafafa 0%, #fef9c3 40%, #ecfccb 100%)' },
          { title: '完成必修 · 降低合规风险', sub: '培训记录留痕与抽查准备', bg: 'linear-gradient(135deg, #ffffff 0%, #f3e8ff 35%, #d1fae5 100%)' }
        ]
      }
      return [
        { title: '减塑守法 · 共建清洁城乡', sub: 'AI智慧普法 · 法规检索 · 任务与举报', bg: 'linear-gradient(135deg, #ffffff 0%, #f0fdf4 50%, #bbf7d0 100%)' },
        { title: '《固废法》新规解读', sub: '基层宣讲与线索反映指引', bg: 'linear-gradient(135deg, #fafafa 0%, #ecfdf5 45%, #86efac 100%)' },
        { title: '农膜回收 · 绿色春耕', sub: '农业生产者责任延伸制度宣传', bg: 'linear-gradient(135deg, #ffffff 0%, #fefce8 30%, #d1fae5 100%)' }
      ]
    },
    taskRemindTitle() {
      const n = this.pendingTaskCount
      return this.isEnterpriseEmployee ? `您有 ${n} 项岗位合规待办` : `您有 ${n} 个待完成任务`
    },
    taskRemindSub() {
      return this.isEnterpriseEmployee ? '对照清单完成自查与签到' : '参与普法活动与现场留痕'
    },
    learnSectionTitle() {
      return this.isEnterpriseEmployee ? '必修与选修（岗位端）' : '普法学习推荐'
    }
  },
  onPullDownRefresh() {
    this.loadData().finally(() => {
      uni.stopPullDownRefresh()
    })
  },
  async mounted() {
    userStore.init()
    await this.loadData()
  },
  methods: {
    async loadData() {
      this.loadingValue = true
      try {
        await Promise.all([
          this.loadTasks(),
          this.loadLaws(),
          this.loadNews(),
          this.loadPendingTaskCount(),
          this.loadTrends(),
          this.loadUnreadCount()
        ])
      } catch (error) {
        console.error('加载数据失败:', error)
      } finally {
        this.loadingValue = false
      }
    },
    async loadUnreadCount() {
      try {
        const userId = userStore.userInfo?.id
        if (userId) {
          const res = await api.notification.unreadCount(userId)
          this.unreadCount = res || 0
        }
      } catch (error) {
        this.unreadCount = 0
      }
    },
    async loadPendingTaskCount() {
      try {
        const res = await api.dashboard.pendingCount()
        this.pendingTaskCount = res?.count ?? res ?? 0
      } catch (error) {
        this.pendingTaskCount = 3
      }
    },
    async loadTrends() {
      try {
        const res = await api.dashboard.trends({ range: '7d' })
        if (res?.reachData?.length) {
          const max = Math.max(...res.reachData)
          this.barHeights = res.reachData.map((v) => Math.round((v / max) * 56))
        }
      } catch (error) {
        console.error('加载趋势数据失败:', error)
      }
    },
    async loadTasks() {
      this.loadingTasksValue = true
      try {
        const res = await api.task.list({ page: 1, pageSize: 3, status: 1 })
        if (res) {
          if (Array.isArray(res)) this.tasks = res
          else if (res.list) this.tasks = res.list
          else this.tasks = []
        }
      } catch (error) {
        console.error('加载任务失败:', error)
        this.tasks = this.getMockTasks()
      } finally {
        this.loadingTasksValue = false
      }
    },
    async loadLaws() {
      this.loadingLawsValue = true
      try {
        const res = await api.law.list({ page: 1, pageSize: 5, hot: true })
        if (res) {
          if (Array.isArray(res)) this.laws = res
          else if (res.list) this.laws = res.list
          else this.laws = []
        }
      } catch (error) {
        console.error('加载法规失败:', error)
        this.laws = this.getMockLaws()
      } finally {
        this.loadingLawsValue = false
      }
    },
    async loadNews() {
      this.loadingNewsValue = true
      try {
        const res = await api.news.list({ page: 1, pageSize: 3 })
        if (res) {
          if (Array.isArray(res)) this.newsList = res
          else if (res.list) this.newsList = res.list
          else this.newsList = []
        }
      } catch (error) {
        console.error('加载资讯失败:', error)
        this.newsList = this.getMockNews()
      } finally {
        this.loadingNewsValue = false
      }
    },
    onBannerClick(item) {
      uni.showToast({ title: item.title, icon: 'none' })
    },
    goMessages() {
      uni.navigateTo({ url: '/pages/notification/notification' })
    },
    goMap() {
      uni.showToast({ title: '地图功能待开发', icon: 'none' })
    },
    getMockTasks() {
      return [
        { id: '1', title: '校园白色污染调研', description: '问卷发放与现场走访记录上传', deadline: '2026-04-12', points: 50, progress: 60 },
        { id: '2', title: '市集减塑宣传', description: '发放普法折页并拍照留痕', deadline: '2026-04-18', points: 30, progress: 25 }
      ]
    },
    getMockLaws() {
      return [
        { id: '1', title: '中华人民共和国固体废物污染环境防治法', level: 'national', publishDate: '2020-04-29' },
        { id: '2', title: '塑料污染治理专项行动方案', level: 'provincial', publishDate: '2024-01-15' }
      ]
    },
    getMockNews() {
      return [
        { id: '1', title: '某地开展白色污染治理专项执法行动', cover: '', publishTime: '2026-04-04' },
        { id: '2', title: '企业合规培训通知：4月培训计划已发布', cover: '', publishTime: '2026-04-03' },
        { id: '3', title: '最新政策法规解读：农膜回收新规', cover: '', publishTime: '2026-04-02' }
      ]
    },
    getLawBadgeClass(level) {
      if (!level) return 'national'
      const l = level.toLowerCase()
      return l.includes('省') || l === 'provincial' || l === 'province' ? 'provincial' : 'national'
    },
    getLawBadgeText(level) {
      if (!level) return '国家级'
      const l = level.toLowerCase()
      return l.includes('省') || l === 'provincial' || l === 'province' ? '省级' : '国家级'
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      try {
        const date = new Date(dateStr)
        return `${date.getMonth() + 1}-${date.getDate()}`
      } catch {
        return dateStr
      }
    },
    goPage(path) {
      const base = path.split('?')[0]
      const isTab = TAB_PREFIXES.some((p) => base === p)
      if (isTab) uni.switchTab({ url: base })
      else uni.navigateTo({ url: path })
    },
    switchTab(path) {
      uni.switchTab({ url: path })
    },
    goLawSearch() {
      uni.navigateTo({ url: '/pages/law-search/law-search' })
    },
    goTask(id) {
      uni.navigateTo({ url: `/pages/task/task?id=${id}` })
    },
    goLaw(id) {
      uni.navigateTo({ url: `/pages/law-search/law-search?id=${id}` })
    },
    goNewsList() {
      uni.navigateTo({ url: '/pages/news/news-list' })
    },
    goNewsDetail(id) {
      uni.navigateTo({ url: `/pages/news/news-detail?id=${id}` })
    }
  }
}
</script>

<style lang="scss" scoped>
$page-bg: #f5f6f8;
$text-primary: #1f2937;
$text-secondary: #4b5563;
$primary: #22c55e;
$primary-dark: #15803d;
$mint: #ecfdf5;

.page {
  min-height: 100vh;
  background: $page-bg;
  padding: 0 24rpx 48rpx;
  padding-bottom: calc(48rpx + env(safe-area-inset-bottom));
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0 20rpx;
}

.brand-area {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.brand-name-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  flex-wrap: wrap;
}

.brand-name {
  font-size: 40rpx;
  font-weight: 700;
  color: $text-primary;
  letter-spacing: 2rpx;
}

.brand-role-pill {
  font-size: 20rpx;
  color: $primary-dark;
  background: $mint;
  padding: 4rpx 14rpx;
  border-radius: 999rpx;
  font-weight: 600;
  border: 1rpx solid rgba(34, 197, 94, 0.25);
}

.brand-sub {
  font-size: 24rpx;
  color: $text-secondary;
}

.top-actions {
  display: flex;
  gap: 20rpx;
}

.action-btn {
  position: relative;
  width: 72rpx;
  height: 72rpx;
  background: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2rpx solid #e5e7eb;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.action-btn--notice {
  background: linear-gradient(145deg, #fffbeb, #ffffff);
  border-color: rgba(234, 179, 8, 0.35);
}

.action-btn--map {
  background: linear-gradient(145deg, #faf5ff, #ffffff);
  border-color: rgba(168, 85, 247, 0.3);
}

.badge {
  position: absolute;
  top: -6rpx;
  right: -6rpx;
  min-width: 32rpx;
  height: 32rpx;
  padding: 0 8rpx;
  background: #ef4444;
  color: #fff;
  font-size: 20rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 16rpx;
  background: #fff;
  border-radius: 999rpx;
  padding: 22rpx 28rpx;
  margin-bottom: 24rpx;
  border: 2rpx solid #e5e7eb;
  box-shadow: 0 2rpx 14rpx rgba(0, 0, 0, 0.04);
}

.search-ico {
  flex-shrink: 0;
}

.search-placeholder {
  color: #999;
  font-size: 28rpx;
}

.banner-section {
  margin-bottom: 24rpx;
}

.banner-swiper {
  height: 280rpx;
  border-radius: 20rpx;
  overflow: hidden;
}

.banner-item {
  height: 100%;
  border-radius: 20rpx;
  padding: 36rpx 32rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.banner-title {
  font-size: 36rpx;
  font-weight: 700;
  color: $text-primary;
  margin-bottom: 12rpx;
}

.banner-sub {
  font-size: 24rpx;
  color: $text-secondary;
  line-height: 1.5;
}

.role-tags {
  display: flex;
  gap: 16rpx;
  flex-wrap: wrap;
  margin-bottom: 24rpx;
}

.role-tag {
  padding: 12rpx 24rpx;
  background: #fff;
  border-radius: 999rpx;
  font-size: 24rpx;
  color: $text-secondary;
  border: 2rpx solid #e5e7eb;
}

.role-tag.active {
  background: linear-gradient(135deg, #ffffff 0%, $mint 100%);
  color: $primary-dark;
  border-color: rgba(34, 197, 94, 0.45);
  font-weight: 600;
}

.card {
  background: #fff;
  border-radius: 20rpx;
  padding: 28rpx;
  margin-bottom: 24rpx;
  border: 1rpx solid #eef0f3;
  box-shadow: 0 2rpx 16rpx rgba(0, 0, 0, 0.04);
}

.section-label {
  display: block;
  font-size: 26rpx;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 24rpx;
}

.grid-5 {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  row-gap: 28rpx;
}

.grid-5-item {
  width: 18%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
}

.g5-icon {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
  border: 2rpx solid #e5e7eb;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.03);
}

.g5-title {
  font-size: 22rpx;
  color: $text-primary;
  text-align: center;
  line-height: 1.3;
}

.task-remind {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28rpx 32rpx;
  background: #fff;
  border-radius: 20rpx;
  margin-bottom: 24rpx;
  border: 1rpx solid #e5e7eb;
  border-left: 8rpx solid $primary;
  box-shadow: 0 2rpx 16rpx rgba(0, 0, 0, 0.04);
}

.remind-left {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.remind-icon-wrap {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background: linear-gradient(145deg, #fffbeb, #fef3c7);
  border: 2rpx solid rgba(234, 179, 8, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
}

.remind-text {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.remind-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
}

.remind-sub {
  font-size: 24rpx;
  color: $text-secondary;
}

.remind-arrow {
  font-size: 40rpx;
  color: #9ca3af;
}

.role-row {
  display: flex;
  gap: 16rpx;
  margin-bottom: 24rpx;
}

.role-card {
  flex: 1;
  border-radius: 16rpx;
  padding: 24rpx 16rpx;
  min-height: 120rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8rpx;
}

.role-a {
  background: linear-gradient(160deg, #ffffff 0%, #f0fdf4 55%, #d1fae5 100%);
  color: $text-primary;
  border: 1rpx solid rgba(34, 197, 94, 0.25);
}

.role-b {
  background: linear-gradient(160deg, #ffffff 0%, #fefce8 50%, #fef9c3 100%);
  color: $text-primary;
  border: 1rpx solid rgba(234, 179, 8, 0.3);
}

.role-c {
  background: linear-gradient(160deg, #ffffff 0%, #faf5ff 45%, #f3e8ff 100%);
  color: $text-primary;
  border: 1rpx solid rgba(168, 85, 247, 0.28);
}

.role-t {
  font-size: 26rpx;
  font-weight: 700;
}

.role-d {
  font-size: 22rpx;
  color: $text-secondary;
}

.dual-row {
  display: flex;
  gap: 16rpx;
  margin-bottom: 24rpx;
}

.dual-card {
  flex: 1;
  background: #fff;
  border-radius: 20rpx;
  padding: 24rpx;
  border: 1rpx solid #eef0f3;
  box-shadow: 0 2rpx 14rpx rgba(0, 0, 0, 0.04);
  position: relative;
  min-height: 180rpx;
}

.dual-title {
  font-size: 28rpx;
  font-weight: 700;
  color: $text-primary;
  display: block;
  margin-bottom: 12rpx;
}

.dual-desc {
  font-size: 24rpx;
  color: $text-secondary;
  line-height: 1.45;
}

.dual-chart .dual-more {
  position: absolute;
  top: 24rpx;
  right: 24rpx;
  font-size: 24rpx;
  color: $primary;
}

.mini-bars {
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  height: 100rpx;
  margin-top: 16rpx;
  padding-right: 80rpx;
}

.mini-bar {
  width: 20rpx;
  border-radius: 8rpx 8rpx 0 0;
  background: linear-gradient(180deg, $primary, #95d5b2);
}

.learn-tabs {
  display: flex;
  gap: 16rpx;
}

.learn-tab {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10rpx;
  padding: 20rpx;
  background: #f9fafb;
  border-radius: 16rpx;
  border: 2rpx solid #e5e7eb;
  transition: all 0.2s;
}

.learn-tab.active {
  background: linear-gradient(180deg, #ffffff 0%, $mint 100%);
  border-color: rgba(34, 197, 94, 0.5);
}

.tab-name {
  font-size: 26rpx;
  font-weight: 600;
  color: $text-secondary;
}

.learn-tab.active .tab-name {
  color: $primary-dark;
}

.section-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 700;
  color: $text-primary;
}

.section-more {
  font-size: 24rpx;
  color: $primary;
}

.task-item {
  padding: 24rpx;
  background: $mint;
  border-radius: 16rpx;
  margin-bottom: 16rpx;
}

.task-item:last-child {
  margin-bottom: 0;
}

.task-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #222;
  display: block;
  margin-bottom: 8rpx;
}

.task-desc {
  font-size: 24rpx;
  color: #555;
  display: block;
  margin-bottom: 12rpx;
}

.task-meta {
  display: flex;
  justify-content: space-between;
  font-size: 22rpx;
  color: #888;
  margin-bottom: 16rpx;
}

.task-points {
  color: $primary;
  font-weight: 600;
}

.task-progress {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.progress-track {
  flex: 1;
  height: 10rpx;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 6rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, $primary, #52b788);
  border-radius: 6rpx;
}

.progress-num {
  font-size: 22rpx;
  color: $primary;
  width: 72rpx;
  text-align: right;
}

.news-item {
  display: flex;
  gap: 20rpx;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #eef5f0;
}

.news-item:last-child {
  border-bottom: none;
}

.news-cover {
  width: 180rpx;
  height: 120rpx;
  border-radius: 12rpx;
  background: $mint;
  flex-shrink: 0;
}

.news-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.news-title {
  font-size: 28rpx;
  color: #333;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.news-date {
  font-size: 22rpx;
  color: #999;
}

.law-row {
  display: flex;
  gap: 16rpx;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #eef5f0;
}

.law-row:last-child {
  border-bottom: none;
}

.law-badge {
  font-size: 20rpx;
  padding: 6rpx 14rpx;
  border-radius: 8rpx;
  height: fit-content;
  flex-shrink: 0;
}

.law-badge.national {
  background: #fff8e1;
  color: #856404;
}

.law-badge.provincial {
  background: #e3f2fd;
  color: #1565c0;
}

.law-body {
  flex: 1;
  min-width: 0;
}

.law-title {
  font-size: 26rpx;
  color: #333;
  line-height: 1.4;
  display: block;
  margin-bottom: 8rpx;
}

.law-date {
  font-size: 22rpx;
  color: #999;
}

.loading-state,
.empty-state {
  padding: 40rpx 0;
  text-align: center;
}

.loading-text {
  font-size: 26rpx;
  color: #999;
}

.empty-text {
  font-size: 26rpx;
  color: #999;
}
</style>
