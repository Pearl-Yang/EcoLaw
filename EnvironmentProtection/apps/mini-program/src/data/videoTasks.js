/**
 * 视频学习任务示例数据。接入后端后改为接口返回；封面图域名需加入小程序「downloadFile 合法域名」。
 */
export const videoTasks = [
  {
    id: 'v1',
    title: '塑料污染治理政策解读',
    coverUrl: 'https://images.unsplash.com/photo-1611273426858-450d8e3c9fce?w=640&q=80',
    emoji: '🎬',
    coverGradient: 'linear-gradient(135deg, #1a4d2e, #28a745)',
    views: 218,
    likes: 3,
    videoUrl: 'https://sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-360p.mp4'
  },
  {
    id: 'v2',
    title: '固废法企业合规要点',
    coverUrl: 'https://images.unsplash.com/photo-1532996122724-e3c354a0b15b?w=640&q=80',
    emoji: '📋',
    coverGradient: 'linear-gradient(135deg, #0077b6, #48cae4)',
    views: 800,
    likes: 12,
    videoUrl: 'https://sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-360p.mp4'
  },
  {
    id: 'v3',
    title: '农膜回收与白色污染',
    coverUrl: 'https://images.unsplash.com/photo-1464226184884-fa280b87c399?w=640&q=80',
    emoji: '🌾',
    coverGradient: 'linear-gradient(135deg, #d4a373, #e9c46a)',
    views: 89,
    likes: 2,
    videoUrl: ''
  },
  {
    id: 'v4',
    title: '环保科普：垃圾分类实操',
    coverUrl: 'https://images.unsplash.com/photo-1530587191325-3db32d826c18?w=640&q=80',
    emoji: '♻️',
    coverGradient: 'linear-gradient(135deg, #40916c, #95d5b2)',
    views: 710,
    likes: 8,
    videoUrl: ''
  },
  {
    id: 'v5',
    title: '企业排污许可入门',
    coverUrl: 'https://images.unsplash.com/photo-1581094794329-c8112a89af12?w=640&q=80',
    emoji: '🏭',
    coverGradient: 'linear-gradient(135deg, #6c5ce7, #a29bfe)',
    views: 484,
    likes: 4,
    videoUrl: ''
  },
  {
    id: 'v6',
    title: '绿色供应链与减塑',
    coverUrl: 'https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?w=640&q=80',
    emoji: '🌿',
    coverGradient: 'linear-gradient(135deg, #00b894, #55efc4)',
    views: 362,
    likes: 5,
    videoUrl: ''
  },
  {
    id: 'v7',
    title: '生态环境损害赔偿案例',
    coverUrl: 'https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=640&q=80',
    emoji: '⚖️',
    coverGradient: 'linear-gradient(135deg, #e76f51, #f4a261)',
    views: 156,
    likes: 1,
    videoUrl: ''
  }
]

export function getVideoTaskById(id) {
  return videoTasks.find((t) => t.id === id) || null
}
