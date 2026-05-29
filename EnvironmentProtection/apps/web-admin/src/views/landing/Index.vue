<template>
  <div class="landing">
    <!-- 导航（首屏透明，滚动后实心 — 与登录页气质一致） -->
    <header class="nav" :class="{ 'nav--solid': navSolid }">
      <div class="nav-inner">
        <a class="logo" href="#" @click.prevent="scrollTo('hero')">
          <span class="logo-mark logo-mark--leaf">
            <span class="leaf-char">叶</span>
          </span>
          <span class="logo-text">绿法通</span>
        </a>
        <nav class="nav-links">
          <a href="#recommend" @click.prevent="scrollTo('recommend')">最新推荐</a>
          <a href="#eco" @click.prevent="scrollTo('eco')">生态知识</a>
          <a href="#platform" @click.prevent="scrollTo('platform')">平台能力</a>
          <a href="#progress" @click.prevent="scrollTo('progress')">治理进度</a>
          <a href="#articles" @click.prevent="scrollTo('articles')">最新资讯</a>
          <a href="#about" @click.prevent="scrollTo('about')">关于我们</a>
        </nav>
        <div class="nav-actions">
          <el-button text class="nav-login" @click="goLogin">登录</el-button>
          <el-button type="primary" class="nav-cta" @click="goRegister">立即注册</el-button>
        </div>
      </div>
    </header>

    <main>
      <!-- Welcome：全屏森林背景 + 左右分栏（对齐登录页） -->
      <section id="hero" class="welcome-hero">
        <div class="welcome-bg" aria-hidden="true">
          <div
            class="welcome-bg-blur"
            :style="{ backgroundImage: `url(${forestBgUrl})` }"
          />
          <div class="welcome-bg-overlay" />
        </div>
        <div class="welcome-split">
          <div class="welcome-brand">
            <div class="brand-top">
              <div class="brand-logo-round">
                <svg viewBox="0 0 48 48" fill="none" aria-hidden="true">
                  <circle cx="24" cy="24" r="22" stroke="rgba(255,255,255,0.45)" stroke-width="1.5" />
                  <path d="M24 8 C18 8 14 14 14 20 C14 26 16 30 20 33 C22 34.5 23 36 24 38 C25 36 26 34.5 28 33 C32 30 34 26 34 20 C34 14 30 8 24 8Z" fill="rgba(255,255,255,0.92)" />
                  <path d="M24 14 L24 30 M20 22 C22 20 26 20 28 22" stroke="#2d6a4f" stroke-width="1.5" stroke-linecap="round" />
                </svg>
              </div>
              <div>
                <div class="brand-name-row">
                  <span class="brand-name">绿法通</span>
                </div>
                <p class="brand-subline">白色污染治理 · AI 智慧普法平台</p>
              </div>
            </div>
            <h1 class="welcome-headline">欢迎来到绿法通</h1>
            <p class="welcome-lead">
              整合政府监管、企业合规与法律服务，任务协同、培训考核、AI 工具与数据看板一站通达。
            </p>
            <div class="service-grid" role="list">
              <button
                v-for="s in serviceTiles"
                :key="s.label"
                type="button"
                class="service-tile"
                @click="goLogin"
              >
                <span class="service-tile-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                    <path :d="s.icon" />
                  </svg>
                </span>
                <span class="service-tile-label">{{ s.label }}</span>
              </button>
            </div>
          </div>
          <div class="welcome-card-wrap">
            <div class="welcome-card">
              <div class="welcome-card-header">
                <h2 class="welcome-card-title">欢迎探索</h2>
                <p class="welcome-card-sub">请选择入口，登录后可使用与您身份匹配的全部功能</p>
              </div>
              <div class="welcome-card-actions">
                <el-button type="primary" size="large" class="welcome-btn-primary" @click="goLogin">
                  登录
                </el-button>
                <el-button size="large" class="welcome-btn-secondary" @click="goRegister">
                  立即注册
                </el-button>
              </div>
              <router-link class="welcome-forget" to="/forget">忘记密码？</router-link>
              <div class="welcome-demo-tip">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="12" cy="12" r="10"/><path d="M12 16v-4M12 8h.01"/></svg>
                <span>演示 <strong>admin</strong> / <strong>admin123</strong></span>
              </div>
            </div>
          </div>
        </div>
        <button type="button" class="welcome-scroll" @click="scrollTo('recommend')">
          <span class="welcome-scroll-line" />
          <span>探索更多</span>
        </button>
      </section>

      <!-- 数据统计条 -->
      <section class="stats-bar">
        <div class="stats-inner">
          <div v-for="(s, i) in statsBar" :key="s.label" class="stats-item">
            <div class="stats-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <template v-if="i === 0">
                  <path d="M12 2a10 10 0 1 0 10 10"/>
                  <path d="M12 2v10l5-5"/>
                  <path d="M17 2c0 0-2 3-2 6s2 5 5 5"/>
                </template>
                <template v-else-if="i === 1">
                  <rect x="3" y="8" width="18" height="13" rx="2"/>
                  <path d="M3 12h18"/>
                  <path d="M9 21V15h6v6"/>
                </template>
                <template v-else-if="i === 2">
                  <rect x="3" y="3" width="18" height="18" rx="2"/>
                  <path d="M8 7h8M8 11h8M8 15h4"/>
                </template>
                <template v-else-if="i === 3">
                  <rect x="4" y="4" width="16" height="16" rx="4"/>
                  <path d="M9 9h.01M15 9h.01M9 15h.01M15 15h.01"/>
                  <path d="M8 12h8"/>
                </template>
                <template v-else>
                  <path d="M12 2L2 7l10 5 10-5-10-5z"/>
                  <path d="M2 17l10 5 10-5"/>
                  <path d="M2 12l10 5 10-5"/>
                </template>
              </svg>
            </div>
            <div class="stats-info">
              <strong class="stats-num">{{ s.num }}</strong>
              <span class="stats-label">{{ s.label }}</span>
            </div>
          </div>
        </div>
      </section>

      <!-- 最新推荐：大图 + 栅格（资讯/普法内容位） -->
      <section id="recommend" class="section recommend">
        <div class="recommend-inner">
          <div class="section-head recommend-head">
            <h2 class="section-title">最新推荐</h2>
            <p class="section-sub">精选普法素材与平台动态，登录后可收藏与推送</p>
          </div>
          <div class="recommend-layout">
            <article class="recommend-feature" @click="goLogin">
              <div class="recommend-player">
                <img :src="featuredRecommend.img" :alt="featuredRecommend.title" />
                <div class="recommend-play">
                  <svg width="56" height="56" viewBox="0 0 56 56" fill="none" aria-hidden="true">
                    <circle cx="28" cy="28" r="28" fill="rgba(0,0,0,0.45)" />
                    <path d="M22 18L38 28L22 38V18Z" fill="white" />
                  </svg>
                </div>
              </div>
              <h3 class="recommend-feature-title">{{ featuredRecommend.title }}</h3>
              <p class="recommend-feature-desc">{{ featuredRecommend.desc }}</p>
            </article>
            <div class="recommend-grid">
              <article
                v-for="item in recommendList"
                :key="item.title"
                class="recommend-item"
                @click="goLogin"
              >
                <div class="rec-thumb">
                  <img :src="item.img" :alt="item.title" />
                  <div class="rec-stats">
                    <span class="rec-stat">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor"><path d="M8 5v14l11-7z"/></svg>
                      {{ item.views }}
                    </span>
                    <span class="rec-stat">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg>
                      {{ item.likes }}
                    </span>
                  </div>
                </div>
                <h4 class="rec-title">{{ item.title }}</h4>
              </article>
            </div>
          </div>
        </div>
      </section>

      <!-- 生态知识 -->
      <section id="eco" class="section eco">
        <div class="section-head">
          <p class="section-eyebrow">ECO KNOWLEDGE</p>
          <h2 class="section-title">生态环保知识</h2>
          <p class="section-sub">了解白色污染治理与绿色低碳的前沿动态</p>
        </div>
        <div class="eco-grid">
          <article v-for="a in ecoArticles" :key="a.title" class="eco-card" @click="goLogin">
            <div class="eco-img-wrap">
              <img :src="a.img" :alt="a.title" class="eco-img" />
              <span class="eco-tag">{{ a.tag }}</span>
            </div>
            <div class="eco-body">
              <h3 class="eco-title">{{ a.title }}</h3>
              <p class="eco-excerpt">{{ a.excerpt }}</p>
              <div class="eco-meta">
                <span class="eco-date">{{ a.date }}</span>
                <span class="eco-read">
                  <svg width="12" height="12" viewBox="0 0 12 12" fill="none"><circle cx="6" cy="6" r="5" stroke="currentColor" stroke-width="1"/><path d="M4 6h4M6 4l2 2-2 2" stroke="currentColor" stroke-width="1" stroke-linecap="round"/></svg>
                  {{ a.read }} 阅读
                </span>
              </div>
            </div>
          </article>
        </div>
        <div class="section-more">
          <el-button plain @click="goLogin">查看更多知识 →</el-button>
        </div>
      </section>

      <!-- 平台能力 -->
      <section id="platform" class="section platform">
        <div class="platform-bg">
          <img src="https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?w=1400&q=80" alt="自然" class="platform-bg-img" />
          <div class="platform-overlay" />
        </div>
        <div class="section-head light">
          <p class="section-eyebrow">PLATFORM CAPABILITIES</p>
          <h2 class="section-title">平台核心能力</h2>
          <p class="section-sub">覆盖政府、企业、律所全场景的一站式解决方案</p>
        </div>
        <div class="plat-grid">
          <div v-for="p in platforms" :key="p.title" class="plat-card" @click="goLogin">
            <div class="plat-icon-wrap" :style="{ background: p.bg }">
              <svg class="plat-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <template v-if="p.icon === 'gov'">
                  <rect x="3" y="3" width="7" height="7" rx="1"/>
                  <rect x="14" y="3" width="7" height="7" rx="1"/>
                  <rect x="3" y="14" width="7" height="7" rx="1"/>
                  <rect x="14" y="14" width="7" height="7" rx="1"/>
                </template>
                <template v-else-if="p.icon === 'ent'">
                  <rect x="2" y="7" width="20" height="14" rx="2"/>
                  <path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/>
                  <path d="M12 12v4M10 14h4"/>
                </template>
                <template v-else-if="p.icon === 'law'">
                  <circle cx="12" cy="6" r="2"/>
                  <path d="M6 20V10a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v10"/>
                  <path d="M6 14h12"/>
                </template>
                <template v-else>
                  <rect x="4" y="4" width="16" height="16" rx="4"/>
                  <path d="M9 9h.01M15 9h.01M9 15h.01M15 15h.01"/>
                  <path d="M8 12h8"/>
                </template>
              </svg>
            </div>
            <div class="plat-body">
              <h3 class="plat-title">{{ p.title }}</h3>
              <p class="plat-desc">{{ p.desc }}</p>
              <ul class="plat-tags">
                <li v-for="tag in p.tags" :key="tag">{{ tag }}</li>
              </ul>
            </div>
          </div>
        </div>
      </section>

      <!-- 治理进度 -->
      <section id="progress" class="section progress">
        <div class="section-head">
          <p class="section-eyebrow">GOVERNANCE PROGRESS</p>
          <h2 class="section-title">全国治理进度一览</h2>
          <p class="section-sub">实时数据看板，透明展示各地白色污染治理成效</p>
        </div>
        <div class="progress-layout">
          <div class="progress-left">
            <div class="progress-map">
              <div class="map-placeholder">
                <img src="https://images.unsplash.com/photo-1477959858617-67f85cf4f1df?w=800&q=80" alt="城市" class="map-img" />
                <div class="map-overlay-text">
                  <div class="map-title">全国塑料污染治理</div>
                  <div class="map-sub">数据每日更新 · 接入真实系统后可展示</div>
                </div>
              </div>
            </div>
          </div>
          <div class="progress-right">
            <div v-for="pg in progressData" :key="pg.label" class="pg-item">
              <div class="pg-header">
                <span class="pg-label">{{ pg.label }}</span>
                <span class="pg-val" :class="pg.cls">{{ pg.val }}</span>
              </div>
              <div class="pg-bar-bg">
                <div class="pg-bar-fill" :style="{ width: pg.pct, background: pg.color }" />
              </div>
            </div>
            <div class="pg-enter">
              <el-button type="primary" @click="goLogin">进入数据看板 →</el-button>
            </div>
          </div>
        </div>
      </section>

      <!-- 企业机构 -->
      <section id="enterprise" class="section enterprise">
        <div class="section-head">
          <p class="section-eyebrow">ENTERPRISE &amp; INSTITUTIONS</p>
          <h2 class="section-title">入驻企业与机构</h2>
          <p class="section-sub">政府、企业、律所三方协同，共建绿色合规生态</p>
        </div>
        <div class="ent-logos">
          <div v-for="e in enterprises" :key="e.name" class="ent-card" @click="goLogin">
            <div class="ent-icon" :style="{ background: e.bg }">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <template v-if="e.type === '政府'">
                  <rect x="3" y="6" width="18" height="15" rx="2"/>
                  <path d="M7 6V4a1 1 0 0 1 1-1h8a1 1 0 0 1 1 1v2"/>
                  <path d="M8 11h8M8 15h5"/>
                </template>
                <template v-else-if="e.type === '律所'">
                  <path d="M3 21h18M5 21V7l7-4 7 4v14"/>
                  <path d="M9 21v-6h6v6M9 9h.01M15 9h.01"/>
                </template>
                <template v-else>
                  <rect x="3" y="8" width="18" height="13" rx="2"/>
                  <path d="M3 12h18M9 21V15h6v6"/>
                </template>
              </svg>
            </div>
            <div class="ent-info">
              <strong class="ent-name">{{ e.name }}</strong>
              <span class="ent-type">{{ e.type }}</span>
              <div class="ent-status">
                <span class="ent-dot" :class="e.status" />
                {{ e.statusText }}
              </div>
            </div>
          </div>
        </div>
        <div class="section-more">
          <el-button plain @click="goLogin">查看更多入驻单位 →</el-button>
        </div>
      </section>

      <!-- 最新资讯 -->
      <section id="articles" class="section articles">
        <div class="articles-inner">
          <div class="articles-head">
            <div>
              <p class="section-eyebrow">LATEST NEWS</p>
              <h2 class="section-title" style="text-align:left;margin:0">最新资讯推送</h2>
            </div>
            <el-button text @click="goLogin" class="more-link">更多资讯 →</el-button>
          </div>
          <div class="articles-list">
            <article v-for="art in articles" :key="art.title" class="art-item" @click="goLogin">
              <div class="art-thumb">
                <img :src="art.img" :alt="art.title" />
              </div>
              <div class="art-content">
                <div class="art-meta">
                  <span class="art-tag">{{ art.tag }}</span>
                  <span class="art-date">{{ art.date }}</span>
                </div>
                <h3 class="art-title">{{ art.title }}</h3>
                <p class="art-excerpt">{{ art.excerpt }}</p>
                <div class="art-footer">
                  <span class="art-author">
                    <el-avatar :size="20" style="background:#52b788;font-size:10px">{{ art.author[0] }}</el-avatar>
                    {{ art.author }}
                  </span>
                  <span class="art-views">
                    <svg width="12" height="12" viewBox="0 0 12 12" fill="none"><path d="M1 6s2-4 5-4 5 4 5 4-2 4-5 4-5-4-5-4Z" stroke="currentColor" stroke-width="1"/><circle cx="6" cy="6" r="1.5" stroke="currentColor" stroke-width="1"/></svg>
                    {{ art.views }}
                  </span>
                </div>
              </div>
            </article>
          </div>
        </div>
      </section>

      <!-- 用户反馈 -->
      <section class="section reviews">
        <div class="reviews-inner">
          <div v-for="r in reviews" :key="r.name" class="review-card" @click="goLogin">
            <div class="review-quote">
              <svg width="32" height="24" viewBox="0 0 32 24" fill="none"><path d="M0 24V14.4C0 6.4 4.267 1.6 12.8 0l1.6 2.4C10.133 3.467 7.733 6.4 7.2 10.4H12.8V24H0ZM19.2 24V14.4C19.2 6.4 23.467 1.6 32 0l1.6 2.4C29.333 3.467 26.933 6.4 26.4 10.4H32V24H19.2Z" fill="#52b788" fill-opacity="0.15"/></svg>
              <p class="review-text">{{ r.text }}</p>
            </div>
            <div class="review-author">
              <el-avatar :size="40" style="background:linear-gradient(135deg,#52b788,#2d6a4f);color:#fff;font-weight:700">{{ r.name[0] }}</el-avatar>
              <div class="review-info">
                <strong>{{ r.name }}</strong>
                <span>{{ r.role }}</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- CTA -->
      <section class="section cta">
        <div class="cta-inner">
          <div class="cta-leaf left">
            <svg width="120" height="200" viewBox="0 0 120 200" fill="none"><path d="M60 10C40 10 20 40 20 80C20 130 40 170 60 190C80 170 100 130 100 80C100 40 80 10 60 10Z" fill="#52b788" fill-opacity="0.15"/><path d="M60 30C45 30 30 55 30 90C30 130 45 160 60 175C75 160 90 130 90 90C90 55 75 30 60 30Z" fill="#52b788" fill-opacity="0.1"/></svg>
          </div>
          <div class="cta-content">
            <h2 class="cta-title">开启绿色合规之旅</h2>
            <p class="cta-desc">免费注册，即刻体验政府监管、企业合规、AI 普法与数据可视化的全链路功能。</p>
            <el-button type="primary" size="large" class="cta-btn" @click="goLogin">
              立即体验绿法通
            </el-button>
          </div>
          <div class="cta-leaf right">
            <svg width="120" height="200" viewBox="0 0 120 200" fill="none"><path d="M60 10C40 10 20 40 20 80C20 130 40 170 60 190C80 170 100 130 100 80C100 40 80 10 60 10Z" fill="#52b788" fill-opacity="0.15"/><path d="M60 30C45 30 30 55 30 90C30 130 45 160 60 175C75 160 90 130 90 90C90 55 75 30 60 30Z" fill="#52b788" fill-opacity="0.1"/></svg>
          </div>
        </div>
      </section>
    </main>

    <!-- 页脚 -->
    <footer id="about" class="footer">
      <div class="footer-inner">
        <div class="footer-brand">
          <div class="footer-logo">
            <svg viewBox="0 0 32 32" fill="none" width="36" height="36"><circle cx="16" cy="16" r="14" fill="url(#flg)"/><path d="M16 8C12 8 10 12 10 16C10 20 12 24 16 24C17.5 24 19 23.5 20 22.5" stroke="white" stroke-width="2" stroke-linecap="round"/><defs><linearGradient id="flg" x1="10" y1="10" x2="22" y2="22"><stop stop-color="#52b788"/><stop offset="1" stop-color="#2d6a4f"/></linearGradient></defs></svg>
            <strong>绿法通</strong>
          </div>
          <p class="footer-desc">白色污染治理 · AI 智慧普法平台<br /><span>演示环境 · 数据均为示例</span></p>
        </div>
        <div class="footer-col">
          <div class="footer-h">快捷入口</div>
          <a @click.prevent="goLogin">登录</a>
          <router-link to="/register">注册账号</router-link>
          <router-link to="/forget">忘记密码</router-link>
        </div>
        <div class="footer-col">
          <div class="footer-h">平台</div>
          <a href="#eco" @click.prevent="scrollTo('eco')">生态知识</a>
          <a href="#platform" @click.prevent="scrollTo('platform')">平台能力</a>
          <a href="#progress" @click.prevent="scrollTo('progress')">治理进度</a>
        </div>
        <div class="footer-col">
          <div class="footer-h">联系</div>
          <span>环保普法 · 绿色未来</span>
        </div>
      </div>
      <div class="footer-copy">© {{ year }} 绿法通 · Environment Protection Demo · 共建绿色家园</div>
    </footer>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const year = computed(() => new Date().getFullYear())

/** 与登录页一致的森林背景（雾林氛围） */
const forestBgUrl =
  'https://images.unsplash.com/photo-1448375240586-882707db888b?w=1920&q=80'

const navSolid = ref(false)
function onScrollNav() {
  navSolid.value = window.scrollY > 48
}
onMounted(() => {
  window.addEventListener('scroll', onScrollNav, { passive: true })
  onScrollNav()
})
onUnmounted(() => window.removeEventListener('scroll', onScrollNav))

const serviceTiles = [
  {
    label: '政府监管',
    icon: 'M3 21h18M3 7v1a3 3 0 0 0 6 0V7m0 1a3 3 0 0 0 6 0V7m0 1a3 3 0 0 0 6 0V7H3l2-4h14l2 4M5 21V10.85M19 21V10.85M9 21v-4a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v4'
  },
  {
    label: '律所服务',
    icon: 'M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5'
  },
  {
    label: '企业合规',
    icon: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z M9 22V12h6v10'
  },
  {
    label: '平台管理',
    icon: 'M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z'
  }
]

const featuredRecommend = {
  title: '塑料污染治理年度要点解读',
  desc: '政策脉络、执法重点与企业合规清单，登录后查看完整讲义与案例',
  img: 'https://images.unsplash.com/photo-1589829545856-d10d557cf95f?w=960&q=80'
}

const recommendList = [
  {
    title: '《固废法》企业义务清单',
    views: '2.1k',
    likes: 12,
    img: 'https://images.unsplash.com/photo-1604187351574-c75ca79f5807?w=400&q=80'
  },
  {
    title: '农膜回收体系实践案例',
    views: '1.8k',
    likes: 8,
    img: 'https://images.unsplash.com/photo-1625246333195-78d9c38ad449?w=400&q=80'
  },
  {
    title: '2025 网络安全与信息化服务周',
    views: '980',
    likes: 5,
    img: 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=400&q=80'
  },
  {
    title: '环保合规培训开班通知',
    views: '1.5k',
    likes: 9,
    img: 'https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=400&q=80'
  },
  {
    title: '绿色供应链与减塑路径',
    views: '760',
    likes: 4,
    img: 'https://images.unsplash.com/photo-1532996122724-e3c354a0b15b?w=400&q=80'
  },
  {
    title: 'AI 普法：智能问答演示',
    views: '2.4k',
    likes: 18,
    img: 'https://images.unsplash.com/photo-1551434678-e076c223a692?w=400&q=80'
  }
]

const statsBar = [
  { num: '15+', label: '业务模块' },
  { num: '200+', label: '入驻企业' },
  { num: '1,200+', label: '处理任务' },
  { num: '50+', label: 'AI 法规条目' },
  { num: '30+', label: '合作律所' }
]

const ecoArticles = [
  {
    tag: '法规解读',
    title: '《一次性塑料制品禁止使用规定》重点解读',
    excerpt: '2024年起，全国地级以上城市建成区外卖禁用不可降解塑料袋，详细解读新规核心要点与执法要点。',
    img: 'https://images.unsplash.com/photo-1604187351574-c75ca79f5807?w=600&q=80',
    date: '2024-03-15',
    read: '2.3k'
  },
  {
    tag: '环保科普',
    title: '塑料污染治理的全球经验与国内实践',
    excerpt: '从欧盟塑料公约到国内限塑令，分析不同治理路径的成效与挑战，探讨多元主体协同机制。',
    img: 'https://images.unsplash.com/photo-1532996122724-e3c354a0b15b?w=600&q=80',
    date: '2024-03-10',
    read: '1.8k'
  },
  {
    tag: '案例分享',
    title: '某省农膜回收体系建设经验分享',
    excerpt: '以某省为试点，构建"政府+企业+农户"三位一体农膜回收体系，覆盖率提升至87%。',
    img: 'https://images.unsplash.com/photo-1625246333195-78d9c38ad449?w=600&q=80',
    date: '2024-03-05',
    read: '1.5k'
  }
]

const platforms = [
  { icon: 'gov', title: '政府监管端', desc: '任务下发、进度跟踪、执法协同、数据分析，全流程在线闭环管理。', tags: ['任务管理', '企业管理', '数据分析', '报表导出'], bg: 'rgba(82,183,136,0.12)' },
  { icon: 'ent', title: '企业合规端', desc: '员工培训、合规台账、内部审核、在线考试，轻松满足监管要求。', tags: ['培训管理', '员工管理', '合规台账', '在线考试'], bg: 'rgba(45,106,79,0.12)' },
  { icon: 'law', title: '律所服务端', desc: '案例管理、客户协同、咨询对接、知识库检索，提升法律服务效率。', tags: ['案例管理', '客户管理', '咨询对接'], bg: 'rgba(82,183,136,0.08)' },
  { icon: 'ai', title: 'AI 智能工具', desc: '基于大模型的法条解读、普法素材生成与智能组卷，降低合规门槛。', tags: ['法条解读', '素材生成', '智能组卷', '合规问答'], bg: 'rgba(45,106,79,0.08)' }
]

const progressData = [
  { label: '禁塑政策覆盖率', val: '87%', pct: '87%', color: '#52b788', cls: 'high' },
  { label: '企业合规台账完成率', val: '92%', pct: '92%', color: '#40916c', cls: 'high' },
  { label: '培训任务完成率', val: '78%', pct: '78%', color: '#74c69d', cls: 'mid' },
  { label: '农膜回收利用率', val: '65%', pct: '65%', color: '#95d5b2', cls: 'mid' }
]

const enterprises = [
  { name: '绿源环保科技', type: '企业', bg: 'rgba(82,183,136,0.12)', status: 'active', statusText: '合规中' },
  { name: '清河县生态环境局', type: '政府', bg: 'rgba(45,106,79,0.12)', status: 'active', statusText: '平台运营' },
  { name: '星辰律师事务所', type: '律所', bg: 'rgba(82,183,136,0.08)', status: 'active', statusText: '服务中' },
  { name: '绿禾农业合作社', type: '企业', bg: 'rgba(95,213,178,0.12)', status: 'active', statusText: '已完成备案' },
  { name: '青山县市场监管局', type: '政府', bg: 'rgba(45,106,79,0.1)', status: 'active', statusText: '平台运营' },
  { name: '明德环保科技', type: '企业', bg: 'rgba(82,183,136,0.1)', status: 'pending', statusText: '审核中' }
]

const articles = [
  {
    tag: '平台更新',
    title: '绿法通 v2.1 发布：新增企业自评估模块',
    excerpt: '支持企业在线完成合规自评估，AI 自动生成评估报告，并推送整改建议。',
    img: 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=400&q=80',
    date: '2024-03-18',
    author: '绿法通团队',
    views: '986'
  },
  {
    tag: '培训通知',
    title: '2024年第一期企业环保合规培训班开放报名',
    excerpt: '涵盖最新法规解读、合规台账填写规范与案例实操，名额有限，报满即止。',
    img: 'https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=400&q=80',
    date: '2024-03-16',
    author: '培训组',
    views: '1.2k'
  },
  {
    tag: '法规动态',
    title: '国家发改委发布塑料污染治理 2024 年工作要点',
    excerpt: '明确年度目标、重点任务与保障措施，要求各地加快建立长效机制。',
    img: 'https://images.unsplash.com/photo-1589829545856-d10d557cf95f?w=400&q=80',
    date: '2024-03-12',
    author: '政策研究组',
    views: '2.1k'
  }
]

const reviews = [
  { text: '台账管理和任务下发全流程线上化，执法效率明显提升，报表导出也省了不少功夫。', name: '张主任', role: '某市生态环境局监管科' },
  { text: '员工培训、在线考试和合规台账一站式搞定，环保检查时台账资料调取特别方便。', name: '李经理', role: '某化工企业 EHS 主管' },
  { text: '咨询对接和案例库功能对团队协同很有帮助，普法素材也可以直接用 AI 生成。', name: '王律师', role: '某环保领域律师事务所' }
]

function scrollTo(id) {
  const el = document.getElementById(id)
  el?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

function goLogin() {
  router.push('/login')
}

function goRegister() {
  router.push('/register')
}
</script>

<style lang="scss" scoped>
.landing {
  min-height: 100vh;
  color: #1a2e22;
  background: #f6faf8;
}

/* ---- 导航：首屏透明 + 白字，滚动后实心（对齐登录页） ---- */
.nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  transition: background 0.35s ease, border-color 0.35s ease, box-shadow 0.35s ease;
  background: transparent;
  border-bottom: 1px solid transparent;
}

.nav--solid {
  background: rgba(255, 255, 255, 0.94);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-bottom-color: rgba(82, 183, 136, 0.12);
  box-shadow: 0 4px 24px rgba(15, 36, 25, 0.06);
}

.nav-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 32px;
  height: 64px;
  display: flex;
  align-items: center;
  gap: 32px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  color: inherit;
  flex-shrink: 0;
}

.logo-mark {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  svg { width: 100%; height: 100%; }
}

.logo-mark--leaf {
  border-radius: 10px;
  background: linear-gradient(145deg, rgba(82, 183, 136, 0.95), #2d6a4f);
  box-shadow: 0 4px 14px rgba(45, 106, 79, 0.35);
}

.leaf-char {
  font-size: 18px;
  font-weight: 800;
  color: #fff;
  line-height: 1;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 2px;
  transition: color 0.25s;
}

.nav:not(.nav--solid) .logo-text {
  color: #fff;
  text-shadow: 0 1px 8px rgba(0, 0, 0, 0.25);
}

.nav--solid .logo-text {
  color: #1a4d2e;
}

.nav-links {
  display: none;
  flex: 1;
  justify-content: center;
  gap: 22px;
  a {
    font-size: 14px;
    font-weight: 500;
    text-decoration: none;
    transition: color 0.2s, opacity 0.2s;
  }
}
@media (min-width: 900px) { .nav-links { display: flex; } }

.nav:not(.nav--solid) .nav-links a {
  color: rgba(255, 255, 255, 0.88);
  &:hover { color: #fff; }
}

.nav--solid .nav-links a {
  color: #4b5563;
  &:hover { color: #2d6a4f; }
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: auto;
}

.nav-login {
  font-weight: 600 !important;
}

.nav:not(.nav--solid) .nav-login {
  color: #fff !important;
}

.nav--solid .nav-login {
  color: #2d6a4f !important;
}

.nav-cta {
  font-weight: 600;
  border: none !important;
  background: linear-gradient(135deg, #52b788, #2d6a4f) !important;
}

.nav:not(.nav--solid) .nav-cta {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
}

/* ---- Welcome 首屏（登录页同款分栏） ---- */
.welcome-hero {
  position: relative;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 96px 32px 80px;
  overflow: hidden;
}

.welcome-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.welcome-bg-blur {
  position: absolute;
  inset: -24px;
  background-size: cover;
  background-position: center;
  filter: blur(10px);
  transform: scale(1.06);
}

.welcome-bg-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    115deg,
    rgba(12, 38, 28, 0.88) 0%,
    rgba(25, 72, 52, 0.78) 42%,
    rgba(20, 55, 42, 0.82) 100%
  );
}

.welcome-split {
  position: relative;
  z-index: 2;
  max-width: 1180px;
  width: 100%;
  margin: 0 auto;
  display: grid;
  gap: 40px;
  align-items: center;
}

@media (min-width: 960px) {
  .welcome-split {
    grid-template-columns: 1fr minmax(320px, 420px);
    gap: 56px;
  }
}

.welcome-brand {
  color: #fff;
}

.brand-top {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 28px;
}

.brand-logo-round {
  width: 56px;
  height: 56px;
  flex-shrink: 0;
  filter: drop-shadow(0 6px 20px rgba(0, 0, 0, 0.25));
  svg {
    width: 100%;
    height: 100%;
  }
}

.brand-name-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.brand-name {
  font-size: 22px;
  font-weight: 800;
  letter-spacing: 3px;
  text-shadow: 0 2px 16px rgba(0, 0, 0, 0.3);
}

.brand-subline {
  margin: 4px 0 0;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.78);
  letter-spacing: 0.5px;
}

.welcome-headline {
  font-size: clamp(2rem, 4.2vw, 2.85rem);
  font-weight: 800;
  line-height: 1.2;
  margin: 0 0 14px;
  letter-spacing: -0.02em;
  text-shadow: 0 2px 24px rgba(0, 0, 0, 0.35);
}

.welcome-lead {
  font-size: 15px;
  line-height: 1.75;
  color: rgba(255, 255, 255, 0.82);
  max-width: 520px;
  margin: 0 0 28px;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  max-width: 440px;
}

.service-tile {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(10px);
  color: rgba(255, 255, 255, 0.95);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  text-align: left;
  transition: background 0.25s, border-color 0.25s, transform 0.2s;
  &:hover {
    background: rgba(82, 183, 136, 0.22);
    border-color: rgba(82, 183, 136, 0.55);
    transform: translateY(-2px);
  }
}

.service-tile-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: rgba(82, 183, 136, 0.28);
  border: 1px solid rgba(82, 183, 136, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: #fff;
  svg {
    width: 18px;
    height: 18px;
  }
}

.service-tile-label {
  letter-spacing: 0.02em;
}

.welcome-card-wrap {
  display: flex;
  justify-content: center;
}

.welcome-card {
  width: 100%;
  max-width: 420px;
  padding: 36px 32px 28px;
  border-radius: 24px;
  background: #fff;
  box-shadow:
    0 24px 56px rgba(15, 36, 25, 0.18),
    0 0 0 1px rgba(82, 183, 136, 0.08);
}

.welcome-card-header {
  text-align: center;
  margin-bottom: 24px;
}

.welcome-card-title {
  margin: 0 0 8px;
  font-size: 24px;
  font-weight: 800;
  color: #1a6b45;
  letter-spacing: 0.02em;
}

.welcome-card-sub {
  margin: 0;
  font-size: 13px;
  line-height: 1.65;
  color: #6b7280;
}

.welcome-card-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.welcome-btn-primary {
  width: 100%;
  height: 48px !important;
  font-weight: 700 !important;
  font-size: 16px !important;
  border-radius: 14px !important;
  background: linear-gradient(135deg, #3daf6a, #28823f) !important;
  border: none !important;
}

.welcome-btn-secondary {
  width: 100%;
  height: 48px !important;
  font-weight: 600 !important;
  border-radius: 14px !important;
  border: 1.5px solid rgba(40, 130, 63, 0.35) !important;
  color: #28823f !important;
  background: rgba(82, 183, 136, 0.06) !important;
}

.welcome-forget {
  display: block;
  text-align: center;
  margin-top: 14px;
  font-size: 13px;
  color: #28823f;
  text-decoration: none;
  &:hover {
    text-decoration: underline;
  }
}

.welcome-demo-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 18px;
  padding: 12px 14px;
  border-radius: 12px;
  background: rgba(82, 183, 136, 0.1);
  border: 1px solid rgba(82, 183, 136, 0.22);
  font-size: 12px;
  color: #4b5563;
  svg {
    width: 16px;
    height: 16px;
    flex-shrink: 0;
    color: #28823f;
  }
  strong {
    color: #1a4d2e;
  }
}

.welcome-scroll {
  position: absolute;
  bottom: 28px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 3;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  background: none;
  border: none;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.55);
  font-size: 11px;
  letter-spacing: 2px;
  &:hover {
    color: rgba(255, 255, 255, 0.85);
  }
}

.welcome-scroll-line {
  width: 1px;
  height: 36px;
  background: linear-gradient(to bottom, rgba(255, 255, 255, 0.55), transparent);
  animation: scrollLine 2.2s ease-in-out infinite;
}

@keyframes scrollLine {
  0%,
  100% {
    transform: scaleY(0.4);
    opacity: 0.5;
  }
  50% {
    transform: scaleY(1);
    opacity: 1;
  }
}

/* ---- 最新推荐 ---- */
.recommend {
  padding-top: 64px;
  padding-bottom: 64px;
  background: #fff;
}

.recommend-inner {
  max-width: 1200px;
  margin: 0 auto;
}

.recommend-head {
  text-align: left;
  margin-bottom: 36px;
  .section-title {
    margin-bottom: 8px;
  }
  .section-sub {
    margin: 0;
    max-width: 560px;
  }
}

.recommend-layout {
  display: grid;
  gap: 28px;
  align-items: start;
}

@media (min-width: 900px) {
  .recommend-layout {
    grid-template-columns: 1.15fr 1fr;
    gap: 32px;
  }
}

.recommend-feature {
  cursor: pointer;
  border-radius: 20px;
  overflow: hidden;
  transition: transform 0.25s, box-shadow 0.25s;
  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 16px 40px rgba(45, 106, 79, 0.12);
  }
}

.recommend-player {
  position: relative;
  aspect-ratio: 16 / 9;
  background: #0f2419;
  border-radius: 16px;
  overflow: hidden;
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.recommend-play {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.12);
  transition: background 0.2s;
  .recommend-feature:hover & {
    background: rgba(0, 0, 0, 0.22);
  }
}

.recommend-feature-title {
  margin: 16px 4px 6px;
  font-size: 18px;
  font-weight: 800;
  color: #0f2419;
  line-height: 1.35;
}

.recommend-feature-desc {
  margin: 0 4px;
  font-size: 13px;
  line-height: 1.6;
  color: #6b7280;
}

.recommend-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

@media (min-width: 520px) and (max-width: 899px) {
  .recommend-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

.recommend-item {
  cursor: pointer;
  border-radius: 14px;
  overflow: hidden;
  background: #f8fdf9;
  border: 1px solid rgba(82, 183, 136, 0.1);
  transition: box-shadow 0.2s, border-color 0.2s;
  &:hover {
    box-shadow: 0 8px 24px rgba(45, 106, 79, 0.1);
    border-color: rgba(82, 183, 136, 0.28);
  }
}

.rec-thumb {
  position: relative;
  aspect-ratio: 16 / 10;
  overflow: hidden;
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.rec-stats {
  position: absolute;
  left: 8px;
  bottom: 8px;
  display: flex;
  gap: 12px;
  font-size: 11px;
  font-weight: 600;
  color: #fff;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.6);
}

.rec-stat {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  svg {
    opacity: 0.95;
  }
}

.rec-title {
  margin: 0;
  padding: 10px 12px 12px;
  font-size: 13px;
  font-weight: 700;
  color: #1a2e22;
  line-height: 1.45;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* ---- 统计条 ---- */
.stats-bar {
  background: #fff;
  border-bottom: 1px solid rgba(82,183,136,0.12);
  box-shadow: 0 4px 20px rgba(45,106,79,0.06);
}

.stats-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 32px;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-around;
  gap: 0;
}

.stats-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 24px 20px;
  border-right: 1px solid rgba(82,183,136,0.08);
  &:last-child { border-right: none; }
}

.stats-icon {
  width: 40px;
  height: 40px;
  flex-shrink: 0;
  color: #52b788;
  svg { width: 100%; height: 100%; }
}

.stats-info {
  display: flex;
  flex-direction: column;
}

.stats-num {
  font-size: 22px;
  font-weight: 800;
  color: #1a4d2e;
  line-height: 1.2;
}

.stats-label {
  font-size: 12px;
  color: #9ca3af;
  font-weight: 500;
}

/* ---- 通用章节 ---- */
.section {
  padding: 72px 32px;
  max-width: 1200px;
  margin: 0 auto;
}

.section-head {
  text-align: center;
  margin-bottom: 48px;
}

.section-eyebrow {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 3px;
  color: #52b788;
  margin: 0 0 8px;
  text-transform: uppercase;
}

.section-title {
  font-size: clamp(1.5rem, 3vw, 2.2rem);
  font-weight: 800;
  color: #0f2419;
  margin: 0 0 10px;
  line-height: 1.2;
}

.section-sub {
  font-size: 15px;
  color: #6b7280;
  margin: 0;
  max-width: 520px;
  margin: 0 auto;
  line-height: 1.6;
}

.section-more {
  text-align: center;
  margin-top: 36px;
}

/* ---- 生态知识 ---- */
.eco-grid {
  display: grid;
  gap: 24px;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
}

.eco-card {
  background: #fff;
  border-radius: 20px;
  overflow: hidden;
  border: 1px solid rgba(82,183,136,0.1);
  box-shadow: 0 4px 20px rgba(45,106,79,0.05);
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 40px rgba(45,106,79,0.12);
  }
}

.eco-img-wrap {
  position: relative;
  height: 200px;
  overflow: hidden;
  .eco-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.4s;
  }
  &:hover .eco-img { transform: scale(1.04); }
}

.eco-tag {
  position: absolute;
  top: 12px;
  left: 12px;
  background: rgba(45,106,79,0.88);
  color: #fff;
  font-size: 11px;
  font-weight: 700;
  padding: 4px 10px;
  border-radius: 999px;
  letter-spacing: 0.5px;
}

.eco-body {
  padding: 20px;
}

.eco-title {
  font-size: 16px;
  font-weight: 700;
  color: #0f2419;
  margin: 0 0 8px;
  line-height: 1.4;
}

.eco-excerpt {
  font-size: 13px;
  line-height: 1.65;
  color: #6b7280;
  margin: 0 0 14px;
}

.eco-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #9ca3af;
}

.eco-read {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* ---- 平台能力 ---- */
.platform {
  position: relative;
  padding: 72px 0;
  max-width: 100%;
  overflow: hidden;
}

.platform-bg {
  position: absolute;
  inset: 0;
  .platform-bg-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  .platform-overlay {
    position: absolute;
    inset: 0;
    background: rgba(15,36,25,0.85);
  }
}

.platform > .section-head {
  position: relative;
  z-index: 2;
  padding: 0 32px;
  &.light .section-title { color: #fff; }
  &.light .section-sub { color: rgba(255,255,255,0.7); }
  &.light .section-eyebrow { color: #74c69d; }
}

.plat-grid {
  position: relative;
  z-index: 2;
  display: grid;
  gap: 20px;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  padding: 0 32px;
  max-width: 1200px;
  margin: 0 auto;
}

.plat-card {
  background: rgba(255,255,255,0.95);
  backdrop-filter: blur(12px);
  border-radius: 20px;
  padding: 24px;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 16px 48px rgba(0,0,0,0.2);
  }
}

.plat-icon-wrap {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  .plat-icon {
    width: 30px;
    height: 30px;
    color: #2d6a4f;
  }
}

.plat-title {
  font-size: 17px;
  font-weight: 700;
  color: #0f2419;
  margin: 0 0 8px;
}

.plat-desc {
  font-size: 13px;
  line-height: 1.65;
  color: #6b7280;
  margin: 0 0 14px;
}

.plat-tags {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  li {
    background: rgba(82,183,136,0.1);
    color: #2d6a4f;
    font-size: 11px;
    font-weight: 600;
    padding: 3px 10px;
    border-radius: 999px;
  }
}

/* ---- 治理进度 ---- */
.progress-layout {
  display: grid;
  gap: 32px;
  align-items: center;
}

@media (min-width: 768px) {
  .progress-layout {
    grid-template-columns: 1fr 1fr;
  }
}

.progress-map {
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(45,106,79,0.1);
}

.map-placeholder {
  position: relative;
  .map-img {
    width: 100%;
    height: 320px;
    object-fit: cover;
  }
  .map-overlay-text {
    position: absolute;
    inset: 0;
    background: rgba(15,36,25,0.55);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 8px;
    .map-title {
      color: #fff;
      font-size: 22px;
      font-weight: 800;
    }
    .map-sub {
      color: rgba(255,255,255,0.7);
      font-size: 13px;
    }
  }
}

.progress-right {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.pg-item {
  .pg-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 8px;
  }
  .pg-label {
    font-size: 13px;
    color: #374151;
    font-weight: 500;
  }
  .pg-val {
    font-size: 13px;
    font-weight: 700;
    &.high { color: #2d6a4f; }
    &.mid { color: #74c69d; }
  }
}

.pg-bar-bg {
  height: 8px;
  background: rgba(82,183,136,0.12);
  border-radius: 999px;
  overflow: hidden;
  .pg-bar-fill {
    height: 100%;
    border-radius: 999px;
    transition: width 1s ease;
  }
}

.pg-enter {
  margin-top: 8px;
}

/* ---- 企业机构 ---- */
.ent-logos {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
}

.ent-card {
  display: flex;
  align-items: center;
  gap: 14px;
  background: #fff;
  border-radius: 16px;
  padding: 16px 20px;
  border: 1px solid rgba(82,183,136,0.1);
  cursor: pointer;
  transition: box-shadow 0.2s, border-color 0.2s;
  &:hover {
    box-shadow: 0 6px 24px rgba(82,183,136,0.1);
    border-color: rgba(82,183,136,0.3);
  }
}

.ent-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  color: #2d6a4f;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  svg { width: 24px; height: 24px; }
}

.ent-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.ent-name {
  font-size: 15px;
  font-weight: 700;
  color: #0f2419;
}

.ent-type {
  font-size: 12px;
  color: #9ca3af;
}

.ent-status {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  font-weight: 600;
  .ent-dot {
    width: 7px;
    height: 7px;
    border-radius: 50%;
    &.active { background: #52b788; }
    &.pending { background: #f59e0b; }
  }
  .ent-dot + * { color: #2d6a4f; }
}

/* ---- 资讯 ---- */
.articles {
  padding: 72px 0;
  max-width: 100%;
  background: #fff;
}

.articles-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 32px;
}

.articles-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 32px;
}

.more-link {
  color: #2d6a4f !important;
  font-weight: 600;
  font-size: 14px;
}

.articles-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.art-item {
  display: flex;
  gap: 20px;
  background: #f8fdf9;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid rgba(82,183,136,0.08);
  cursor: pointer;
  transition: box-shadow 0.2s, border-color 0.2s;
  &:hover {
    box-shadow: 0 6px 24px rgba(82,183,136,0.1);
    border-color: rgba(82,183,136,0.25);
  }
}

.art-thumb {
  width: 200px;
  flex-shrink: 0;
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.art-content {
  padding: 18px 20px 18px 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.art-meta {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 8px;
}

.art-tag {
  background: rgba(82,183,136,0.12);
  color: #2d6a4f;
  font-size: 11px;
  font-weight: 700;
  padding: 3px 10px;
  border-radius: 999px;
}

.art-date {
  font-size: 12px;
  color: #9ca3af;
}

.art-title {
  font-size: 16px;
  font-weight: 700;
  color: #0f2419;
  margin: 0 0 6px;
  line-height: 1.4;
}

.art-excerpt {
  font-size: 13px;
  color: #6b7280;
  margin: 0 0 12px;
  line-height: 1.6;
}

.art-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.art-author {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #6b7280;
  font-weight: 500;
}

.art-views {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #9ca3af;
}

/* ---- 用户反馈 ---- */
.reviews {
  background: rgba(82,183,136,0.04);
  max-width: 100%;
  padding: 72px 32px;
}

.reviews-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  gap: 20px;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
}

.review-card {
  background: #fff;
  border-radius: 20px;
  padding: 28px;
  border: 1px solid rgba(82,183,136,0.1);
  box-shadow: 0 4px 20px rgba(45,106,79,0.05);
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 10px 36px rgba(45,106,79,0.1);
  }
}

.review-quote {
  margin-bottom: 20px;
}

.review-text {
  font-size: 14px;
  line-height: 1.75;
  color: #374151;
  margin: 0;
}

.review-author {
  display: flex;
  align-items: center;
  gap: 12px;
}

.review-info {
  display: flex;
  flex-direction: column;
  strong {
    font-size: 14px;
    color: #0f2419;
  }
  span {
    font-size: 12px;
    color: #9ca3af;
  }
}

/* ---- CTA ---- */
.cta {
  max-width: 100%;
  padding: 80px 32px;
  background: linear-gradient(135deg, rgba(45,106,79,0.06), rgba(82,183,136,0.06));
  .cta-inner {
    max-width: 1200px;
    margin: 0 auto;
    position: relative;
    text-align: center;
  }
}

.cta-leaf {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  opacity: 0.6;
  &.left { left: -40px; }
  &.right { right: -40px; }
}

.cta-content {
  position: relative;
  z-index: 2;
}

.cta-title {
  font-size: clamp(1.6rem, 3vw, 2.4rem);
  font-weight: 800;
  color: #0f2419;
  margin: 0 0 12px;
}

.cta-desc {
  font-size: 15px;
  color: #6b7280;
  max-width: 480px;
  margin: 0 auto 28px;
  line-height: 1.7;
}

.cta-btn {
  background: linear-gradient(135deg, #52b788, #2d6a4f) !important;
  border: none !important;
  font-weight: 700;
  padding: 14px 40px !important;
  font-size: 16px !important;
}

/* ---- 页脚 ---- */
.footer {
  background: #0f2419;
  color: rgba(255,255,255,0.75);
  padding: 56px 32px 24px;
}

.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  gap: 32px;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  padding-bottom: 32px;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}

.footer-brand {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  strong {
    font-size: 18px;
    font-weight: 700;
    color: #fff;
    letter-spacing: 2px;
  }
}

.footer-desc {
  margin: 0;
  font-size: 13px;
  line-height: 1.6;
  span { opacity: 0.6; }
}

.footer-col {
  display: flex;
  flex-direction: column;
  gap: 10px;
  font-size: 13px;
  a, :deep(a) {
    color: rgba(255,255,255,0.7);
    text-decoration: none;
    transition: color 0.2s;
    cursor: pointer;
    &:hover { color: #fff; }
  }
}

.footer-h {
  font-weight: 700;
  color: #fff;
  margin-bottom: 4px;
  font-size: 13px;
}

.footer-copy {
  max-width: 1200px;
  margin: 24px auto 0;
  font-size: 12px;
  opacity: 0.45;
  text-align: center;
}

/* ---- 响应式 ---- */
@media (max-width: 640px) {
  .art-thumb { width: 120px; }
  .ent-logos { grid-template-columns: 1fr; }
  .stats-inner { flex-direction: column; gap: 0; }
  .stats-item { border-right: none; border-bottom: 1px solid rgba(82,183,136,0.08); &:last-child { border-bottom: none; } }
  .cta-leaf { display: none; }
  .articles-head { flex-direction: column; align-items: flex-start; gap: 12px; }
}
</style>
