"""运行AI分析"""
import sys
sys.path.insert(0, '.')
import json
from ai_analyzer import AIAnalyzer, SummaryExporter

# 使用最新的JSON文件
with open('../data/crawl_results_20260404_172648.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

result = data[0]
posts = result.get('posts', [])
videos = result.get('videos', [])

print(f'帖子数: {len(posts)}, 视频数: {len(videos)}')

# 检查数据类型
if posts:
    print(f'第一个帖子类型: {type(posts[0])}')
    if isinstance(posts[0], dict):
        print(f'第一个帖子标题: {posts[0].get("title", "无")[:50]}')
    else:
        print(f'第一个帖子: {str(posts[0])[:100]}')

# 创建分析器
analyzer = AIAnalyzer()

# 分析前10个帖子和视频
print('\n正在分析帖子...')
post_summaries = analyzer.summarize_posts(posts[:10])

print('\n正在分析视频...')
video_summaries = analyzer.summarize_videos(videos[:10])

# 合并并导出
all_summaries = post_summaries + video_summaries
exporter = SummaryExporter()
filename = exporter.export_summary_to_excel(all_summaries, '../data/content_summaries_new.xlsx')
print(f'\n摘要已导出: {filename}')

# 显示示例
print('\n帖子摘要示例:')
for s in post_summaries[:3]:
    print(f'  标题: {s.original_title[:40]}...')
    print(f'  摘要: {s.summary}')
    print(f'  关键点: {s.key_points}')
    print(f'  舆论: {s.public_opinion}')
    print()

print('视频摘要示例:')
for s in video_summaries[:3]:
    print(f'  标题: {s.original_title[:40]}...')
    print(f'  摘要: {s.summary}')
    print(f'  关键点: {s.key_points}')
    print(f'  舆论: {s.public_opinion}')
    print()
