# Tabbar 图标说明

小程序需要以下 tabbar 图标，请放置在 `static/tabbar/` 目录下：

## 需要的图标文件

| 文件名 | 尺寸 | 说明 |
|--------|------|------|
| `home.png` | 81x81 px | 首页图标（未选中） |
| `home-active.png` | 81x81 px | 首页图标（选中） |
| `learn.png` | 81x81 px | 学习图标（未选中） |
| `learn-active.png` | 81x81 px | 学习图标（选中） |
| `task.png` | 81x81 px | 任务图标（未选中） |
| `task-active.png` | 81x81 px | 任务图标（选中） |
| `chat.png` | 81x81 px | 咨询图标（未选中） |
| `chat-active.png` | 81x81 px | 咨询图标（选中） |
| `profile.png` | 81x81 px | 我的图标（未选中） |
| `profile-active.png` | 81x81 px | 我的图标（选中） |

## 快速获取图标

可以使用阿里巴巴图标库下载：
- https://www.iconfont.cn/collections/detail?spm=a313x.7781069.0.da5a778a4&cid=4308
- 搜索 "首页"、"学习"、"任务"、"聊天"、"我的" 等关键词

## 临时方案

如果暂时没有图标，可以先使用 H5 模式运行：

```bash
cd apps/mini-program
npm install
npm run dev:h5
```

然后在浏览器中访问 http://localhost:5173 查看效果。
