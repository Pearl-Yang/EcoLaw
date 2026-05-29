"""检查数据内容"""
import sys
sys.path.insert(0, '.')
import json

with open('../data/crawl_results_20260404_172648.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

result = data[0]
posts = result.get('posts', [])
videos = result.get('videos', [])

print(f'帖子数: {len(posts)}, 视频数: {len(videos)}')

# 检查第一个帖子
if posts:
    p = posts[0]
    print('\n第一个帖子:')
    print(f'  平台: {p.get("platform")}')
    print(f'  帖子ID: {p.get("post_id")}')
    print(f'  标题: {p.get("title")}')
    print(f'  内容: {p.get("content")[:200]}...' if len(p.get("content", "")) > 200 else f'  内容: {p.get("content")}')

# 检查第一个视频
if videos:
    v = videos[0]
    print('\n第一个视频:')
    print(f'  平台: {v.get("platform")}')
    print(f'  视频ID: {v.get("video_id")}')
    print(f'  标题: {v.get("title")}')
    print(f'  描述: {v.get("description")[:200]}...' if len(v.get("description", "")) > 200 else f'  描述: {v.get("description")}')
    print(f'  时长: {v.get("duration")}秒')
    print(f'  播放量: {v.get("views")}')
    print(f'  点赞: {v.get("likes")}')
