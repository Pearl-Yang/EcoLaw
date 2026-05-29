<script>
export default {
  name: 'SvgIcon',
  props: {
    name: { type: String, required: true },
    size: { type: [Number, String], default: 44 },
    color: { type: String, default: '#33857a' }
  },
  data() {
    return {
      icons: {
        phone: `<path d="M3.654 1.328a.645.645 0 0 0-1.01.01L2 4.725a13.19 13.19 0 0 0 3.581 5.044l.992-.665a.645.645 0 0 1 1.01.01l.001 2.666c2.168-.386 4.493-1.266 6.79-2.633l1.108-1.088a13.19 13.19 0 0 0 5.044 3.581l-.665.992a.645.645 0 0 1-.01 1.01l-3.397 2.667a.645.645 0 0 0 .01 1.01l3.397 2.667a.645.645 0 0 1-.01 1.01l-2.667-3.397a13.19 13.19 0 0 0-5.044-3.581l-.992.665a.645.645 0 0 1-1.01-.01l-.001-2.666a13.19 13.19 0 0 0-2.633-6.79l-1.108 1.088a13.19 13.19 0 0 0-3.581-5.044l.665-.992a.645.645 0 0 1 1.01-.01L1.645 5.03a.645.645 0 0 0 .01-1.01L1.645 1.34a.645.645 0 0 0-1.01.01L.001 2.67l2.667-.001-.002-.002.02-.339z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" fill="none"/>`,
        lock: `<rect x="5" y="11" width="14" height="10" rx="2" stroke="currentColor" stroke-width="1.5" fill="none"/><path d="M8 11V7a4 4 0 1 1 8 0v4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" fill="none"/>`,
        unlock: `<rect x="5" y="11" width="14" height="10" rx="2" stroke="currentColor" stroke-width="1.5" fill="none"/><path d="M8 11V7a4 4 0 0 1 7.83-1" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" fill="none"/>`,
        code: `<path d="M9.17 6l2 2L17 4l-2-2-5.83 4z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" fill="none"/><path d="M14.83 18l-2 2L7 14l2-2 5.83 4z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" fill="none"/><path d="M17 14l-2-2" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" fill="none"/>`,
        eye: `<path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" fill="none"/><circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.5" fill="none"/>`,
        eyeOff: `<path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" fill="none"/><line x1="1" y1="1" x2="23" y2="23" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>`,
        user: `<path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" fill="none"/><circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="1.5" fill="none"/>`,
        wechat: `<path d="M8.5 13.5a.5.5 0 0 1 .5-.5h.5a1 1 0 0 1 .5.9l-1 1.6a.5.5 0 0 1-.9-.4v-.6h.4a.5.5 0 0 1 .5.5v.5a.5.5 0 0 1-.5.5h-1a2.5 2.5 0 0 1-5 0 2.5 2.5 0 0 1 2-2.5h1a.5.5 0 0 1 .5-.5z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" fill="none"/><path d="M15 12.5a3 3 0 0 1 .5-1.5h.5a1 1 0 0 1 .5.9l-1 1.6a.5.5 0 0 1-.9-.4V12h.5a.5.5 0 0 1 .5.5v.5a.5.5 0 0 1-.5.5h-1a2.5 2.5 0 0 1-5 0 2.5 2.5 0 0 1 2-2.5h1a.5.5 0 0 1 .5-.5z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" fill="none"/><path d="M12 6.5a4.5 4.5 0 1 1-9 0 4.5 4.5 0 0 1 9 0z" stroke="currentColor" stroke-width="1.5" fill="none"/><path d="M12 18.5A6.5 6.5 0 1 1 18.5 12" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" fill="none"/>`,
        'arrow-left': `<line x1="19" y1="12" x2="5" y2="12" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/><polyline points="12 19 5 12 12 5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" fill="none"/>`,
        bell: `<path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linecap="round" stroke-linejoin="round"/><path d="M13.73 21a2 2 0 0 1-3.46 0" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linecap="round" stroke-linejoin="round"/>`,
        map: `<polygon points="1 6 1 22 8 18 16 22 23 18 23 2 16 6 8 2 1 6" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linejoin="round"/><line x1="8" y1="2" x2="8" y2="18" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/><line x1="16" y1="6" x2="16" y2="22" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>`,
        search: `<circle cx="11" cy="11" r="8" stroke="currentColor" stroke-width="1.5" fill="none"/><line x1="21" y1="21" x2="16.65" y2="16.65" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>`,
        'file-text': `<path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linejoin="round"/><polyline points="14 2 14 8 20 8" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linejoin="round"/><line x1="16" y1="13" x2="8" y2="13" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/><line x1="16" y1="17" x2="8" y2="17" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/><line x1="10" y1="9" x2="8" y2="9" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>`,
        'book-open': `<path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linejoin="round"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linejoin="round"/>`,
        leaf: `<path d="M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.48 19 2c1 2 2 4.18 2 8 0 5.5-4.78 10-10 10Z" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linejoin="round"/><path d="M2 21c0-3 1.85-5.36 5.08-6C9.5 14.52 12 13 13 12" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linecap="round"/>`,
        'map-pin': `<path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linejoin="round"/><circle cx="12" cy="10" r="3" stroke="currentColor" stroke-width="1.5" fill="none"/>`,
        'message-circle': `<path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linejoin="round"/>`,
        video: `<rect x="2" y="5" width="15" height="14" rx="2" stroke="currentColor" stroke-width="1.5" fill="none"/><polygon points="23 7 16 12 23 17 23 7" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linejoin="round"/>`,
        newspaper: `<path d="M4 22h16a2 2 0 0 0 2-2V4a2 2 0 0 0-2-2H8a2 2 0 0 0-2 2v16a2 2 0 0 1-2 2Zm0 0a2 2 0 0 1-2-2v-9c0-1.1.9-2 2-2h2" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linejoin="round"/><path d="M18 14h-8M15 18h-5M10 6h8v4h-8V6Z" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linecap="round" stroke-linejoin="round"/>`,
        'clipboard-list': `<path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linejoin="round"/><rect x="8" y="2" width="8" height="4" rx="1" stroke="currentColor" stroke-width="1.5" fill="none"/><path d="M12 11h4M12 16h4M8 11h.01M8 16h.01" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>`,
        'check-square': `<path d="M9 11l3 3L22 4" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linecap="round" stroke-linejoin="round"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linejoin="round"/>`,
        'graduation-cap': `<path d="M22 10v6M2 10l10-5 10 5-10 5z" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linejoin="round"/><path d="M6 12v5c3 3 9 3 12 0v-5" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linejoin="round"/>`
      }
    }
  },
  computed: {
    iconPath() {
      return this.icons[this.name] || ''
    }
  }
}
</script>

<template>
  <view class="svg-icon" :style="{ width: size + 'rpx', height: size + 'rpx', color: color }">
    <svg
      :width="size + 'rpx'"
      :height="size + 'rpx'"
      viewBox="0 0 24 24"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      :style="{ color: color }"
    >
      <g v-html="iconPath" />
    </svg>
  </view>
</template>

<style scoped>
.svg-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
</style>
