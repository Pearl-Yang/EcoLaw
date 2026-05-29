<script>
export default {
  name: 'LineIcon',
  props: {
    name: { type: String, required: true },
    size: { type: [Number, String], default: 44 },
    color: { type: String, default: '#374151' }
  }
}
</script>

<template>
  <view class="line-icon" :style="{ width: size + 'rpx', height: size + 'rpx', color }">
    <svg :width="size + 'rpx'" :height="size + 'rpx'" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <!-- bell -->
      <g v-if="name === 'bell'">
        <path
          d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"
          stroke="currentColor"
          stroke-width="1.5"
          stroke-linecap="round"
          stroke-linejoin="round"
        />
        <path d="M13.73 21a2 2 0 0 1-3.46 0" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      </g>
      <!-- map -->
      <g v-else-if="name === 'map'">
        <polygon points="1 6 1 22 8 18 16 22 23 18 23 2 16 6 8 2 1 6" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linejoin="round" />
        <line x1="8" y1="2" x2="8" y2="18" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
        <line x1="16" y1="6" x2="16" y2="22" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      </g>
      <!-- search -->
      <g v-else-if="name === 'search'">
        <circle cx="11" cy="11" r="8" stroke="currentColor" stroke-width="1.5" />
        <line x1="21" y1="21" x2="16.65" y2="16.65" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      </g>
      <!-- file-text -->
      <g v-else-if="name === 'file-text'">
        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
        <polyline points="14 2 14 8 20 8" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
        <line x1="16" y1="13" x2="8" y2="13" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
        <line x1="16" y1="17" x2="8" y2="17" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
        <line x1="10" y1="9" x2="8" y2="9" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      </g>
      <!-- book-open -->
      <g v-else-if="name === 'book-open'">
        <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
        <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
      </g>
      <!-- leaf -->
      <g v-else-if="name === 'leaf'">
        <path d="M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.48 19 2c1 2 2 4.18 2 8 0 5.5-4.78 10-10 10Z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
        <path d="M2 21c0-3 1.85-5.36 5.08-6C9.5 14.52 12 13 13 12" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      </g>
      <!-- map-pin -->
      <g v-else-if="name === 'map-pin'">
        <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
        <circle cx="12" cy="10" r="3" stroke="currentColor" stroke-width="1.5" />
      </g>
      <!-- message-circle -->
      <g v-else-if="name === 'message-circle'">
        <path
          d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"
          stroke="currentColor"
          stroke-width="1.5"
          stroke-linejoin="round"
        />
      </g>
      <!-- video -->
      <g v-else-if="name === 'video'">
        <rect x="2" y="5" width="15" height="14" rx="2" stroke="currentColor" stroke-width="1.5" />
        <polygon points="23 7 16 12 23 17 23 7" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
      </g>
      <!-- newspaper -->
      <g v-else-if="name === 'newspaper'">
        <path d="M4 22h16a2 2 0 0 0 2-2V4a2 2 0 0 0-2-2H8a2 2 0 0 0-2 2v16a2 2 0 0 1-2 2Zm0 0a2 2 0 0 1-2-2v-9c0-1.1.9-2 2-2h2" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
        <path d="M18 14h-8M15 18h-5M10 6h8v4h-8V6Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
      </g>
      <!-- clipboard-list -->
      <g v-else-if="name === 'clipboard-list'">
        <path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
        <rect x="8" y="2" width="8" height="4" rx="1" stroke="currentColor" stroke-width="1.5" />
        <path d="M12 11h4M12 16h4M8 11h.01M8 16h.01" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      </g>
      <!-- check-square -->
      <g v-else-if="name === 'check-square'">
        <path d="M9 11l3 3L22 4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
        <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
      </g>
      <!-- graduation-cap -->
      <g v-else-if="name === 'graduation-cap'">
        <path d="M22 10v6M2 10l10-5 10 5-10 5z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
        <path d="M6 12v5c3 3 9 3 12 0v-5" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
      </g>
      <!-- star -->
      <g v-else-if="name === 'star'">
        <polygon
          points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"
          stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"
        />
      </g>
      <!-- star-filled (use fill) -->
      <g v-else-if="name === 'star-filled'">
        <polygon
          points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"
          fill="currentColor" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"
        />
      </g>
      <!-- settings -->
      <g v-else-if="name === 'settings'">
        <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.5" />
        <path
          d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"
          stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"
        />
      </g>
      <!-- award / trophy -->
      <g v-else-if="name === 'award'">
        <circle cx="12" cy="8" r="7" stroke="currentColor" stroke-width="1.5" />
        <polyline points="8.21 13.89 7 23 12 20 17 23 15.79 13.88" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
      </g>
      <!-- bar-chart-2 -->
      <g v-else-if="name === 'bar-chart-2'">
        <line x1="18" y1="20" x2="18" y2="10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
        <line x1="12" y1="20" x2="12" y2="4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
        <line x1="6" y1="20" x2="6" y2="14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      </g>
      <!-- users -->
      <g v-else-if="name === 'users'">
        <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
        <circle cx="9" cy="7" r="4" stroke="currentColor" stroke-width="1.5" />
        <path d="M23 21v-2a4 4 0 0 0-3-3.87" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
        <path d="M16 3.13a4 4 0 0 1 0 7.75" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      </g>
      <!-- upload -->
      <g v-else-if="name === 'upload'">
        <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
        <polyline points="17 8 12 3 7 8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
        <line x1="12" y1="3" x2="12" y2="15" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      </g>
      <!-- clock -->
      <g v-else-if="name === 'clock'">
        <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="1.5" />
        <polyline points="12 6 12 12 16 14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
      </g>
      <!-- heart -->
      <g v-else-if="name === 'heart'">
        <path
          d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"
          stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"
        />
      </g>
      <!-- camera -->
      <g v-else-if="name === 'camera'">
        <path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z" stroke="currentColor" stroke-width="1.5" />
        <circle cx="12" cy="13" r="4" stroke="currentColor" stroke-width="1.5" />
      </g>
      <!-- warning -->
      <g v-else-if="name === 'warning'">
        <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
        <line x1="12" y1="9" x2="12" y2="13" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
        <line x1="12" y1="17" x2="12.01" y2="17" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      </g>
      <!-- info -->
      <g v-else-if="name === 'info'">
        <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="1.5" />
        <line x1="12" y1="16" x2="12" y2="12" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
        <line x1="12" y1="8" x2="12.01" y2="8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      </g>
      <!-- globe -->
      <g v-else-if="name === 'globe'">
        <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="1.5" />
        <line x1="2" y1="12" x2="22" y2="12" stroke="currentColor" stroke-width="1.5" />
        <path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z" stroke="currentColor" stroke-width="1.5" />
      </g>
      <!-- shield -->
      <g v-else-if="name === 'shield'">
        <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
      </g>
      <!-- shield-check -->
      <g v-else-if="name === 'shield-check'">
        <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
        <polyline points="9 12 11 14 15 10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
      </g>
      <!-- edit / pencil -->
      <g v-else-if="name === 'edit'">
        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
      </g>
      <!-- log-out -->
      <g v-else-if="name === 'log-out'">
        <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
        <polyline points="16 17 21 12 16 7" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
        <line x1="21" y1="12" x2="9" y2="12" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      </g>
      <!-- user -->
      <g v-else-if="name === 'user'">
        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
        <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="1.5" />
      </g>
      <!-- chevron-right -->
      <g v-else-if="name === 'chevron-right'">
        <polyline points="9 18 15 12 9 6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
      </g>
      <!-- chevron-down -->
      <g v-else-if="name === 'chevron-down'">
        <polyline points="6 9 12 15 18 9" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
      </g>
      <!-- plus -->
      <g v-else-if="name === 'plus'">
        <line x1="12" y1="5" x2="12" y2="19" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
        <line x1="5" y1="12" x2="19" y2="12" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      </g>
      <!-- check-circle -->
      <g v-else-if="name === 'check-circle'">
        <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
        <polyline points="22 4 12 14.01 9 11.01" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
      </g>
      <!-- x-circle -->
      <g v-else-if="name === 'x-circle'">
        <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="1.5" />
        <line x1="15" y1="9" x2="9" y2="15" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
        <line x1="9" y1="9" x2="15" y2="15" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      </g>
      <!-- download -->
      <g v-else-if="name === 'download'">
        <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
        <polyline points="7 10 12 15 17 10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
        <line x1="12" y1="15" x2="12" y2="3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      </g>
      <!-- external-link -->
      <g v-else-if="name === 'external-link'">
        <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
        <polyline points="15 3 21 3 21 9" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
        <line x1="10" y1="14" x2="21" y2="3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      </g>
      <!-- mail -->
      <g v-else-if="name === 'mail'">
        <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
        <polyline points="22 6 12 13 2 6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
      </g>
      <!-- trash-2 -->
      <g v-else-if="name === 'trash-2'">
        <polyline points="3 6 5 6 21 6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
        <line x1="10" y1="11" x2="10" y2="17" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
        <line x1="14" y1="11" x2="14" y2="17" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      </g>
      <!-- home -->
      <g v-else-if="name === 'home'">
        <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
        <polyline points="9 22 9 12 15 12 15 22" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
      </g>
      <!-- pin -->
      <g v-else-if="name === 'pin'">
        <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
        <circle cx="12" cy="10" r="3" stroke="currentColor" stroke-width="1.5" />
      </g>
      <!-- paperclip -->
      <g v-else-if="name === 'paperclip'">
        <path d="M21.44 11.05l-9.19 9.19a6 6 0 0 1-8.49-8.49l9.19-9.19a4 4 0 0 1 5.66 5.66l-9.2 9.19a2 2 0 0 1-2.83-2.83l8.49-8.48" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
      </g>
      <!-- arrow-right -->
      <g v-else-if="name === 'arrow-right'">
        <line x1="5" y1="12" x2="19" y2="12" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
        <polyline points="12 5 19 12 12 19" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
      </g>
      <!-- play -->
      <g v-else-if="name === 'play'">
        <polygon points="5 3 19 12 5 21 5 3" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" fill="currentColor" opacity="0.8" />
      </g>
      <!-- thumb-up -->
      <g v-else-if="name === 'thumb-up'">
        <path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
      </g>
      <!-- bookmark -->
      <g v-else-if="name === 'bookmark'">
        <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
      </g>
    </svg>
  </view>
</template>

<style scoped>
.line-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
</style>
