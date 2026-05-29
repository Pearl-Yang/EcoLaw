"""
HTML报告生成器 - 生成可视化统计报告
"""

import os
import json
from datetime import datetime
from typing import List, Dict, Any
from jinja2 import Template


class ReportGenerator:
    """HTML报告生成器"""

    def __init__(self, output_dir: str = "reports"):
        self.output_dir = output_dir
        os.makedirs(output_dir, exist_ok=True)

    def generate(self, crawler_results: List[Dict[str, Any]], filename: str = None) -> str:
        """生成HTML报告"""
        if not filename:
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            filename = os.path.join(self.output_dir, f"report_{timestamp}.html")

        # 计算统计数据
        stats = self._calculate_stats(crawler_results)

        # 生成报告HTML
        html_content = self._generate_html(stats, crawler_results)

        with open(filename, "w", encoding="utf-8") as f:
            f.write(html_content)

        return filename

    def _calculate_stats(self, crawler_results: List[Dict[str, Any]]) -> Dict[str, Any]:
        """计算统计数据"""
        stats = {
            "total_posts": 0,
            "total_videos": 0,
            "total_likes": 0,
            "total_comments": 0,
            "total_shares": 0,
            "total_views": 0,
            "platform_stats": {},
            "sentiment_stats": {
                "正面": 0,
                "负面": 0,
                "中性": 0
            },
            "keyword_stats": {},
            "top_posts": [],
            "top_videos": [],
            "timeline_stats": {}
        }

        all_posts = []
        all_videos = []

        for result in crawler_results:
            platform = result.get("platform", "未知")

            posts = result.get("posts", [])
            videos = result.get("videos", [])

            all_posts.extend(posts)
            all_videos.extend(videos)

            # 平台统计
            stats["platform_stats"][platform] = {
                "posts": len(posts),
                "videos": len(videos),
                "total": len(posts) + len(videos)
            }

            # 互动统计
            for post in posts:
                # 处理PostItem对象或字典
                if hasattr(post, 'likes'):
                    likes = post.likes
                    comments = post.comments
                    shares = post.shares
                    views = post.views
                    sentiment = post.sentiment
                    keywords = post.keywords
                    title = post.title
                    content = post.content
                    author = post.author
                    post_id = post.post_id
                    url = post.url
                else:
                    likes = post.get('likes', 0) if isinstance(post, dict) else 0
                    comments = post.get('comments', 0) if isinstance(post, dict) else 0
                    shares = post.get('shares', 0) if isinstance(post, dict) else 0
                    views = post.get('views', 0) if isinstance(post, dict) else 0
                    sentiment = post.get('sentiment', '中性') if isinstance(post, dict) else '中性'
                    keywords = post.get('keywords', []) if isinstance(post, dict) else []
                    title = post.get('title', '') if isinstance(post, dict) else ''
                    content = post.get('content', '') if isinstance(post, dict) else ''
                    author = post.get('author', '') if isinstance(post, dict) else ''
                    post_id = post.get('post_id', '') if isinstance(post, dict) else ''
                    url = post.get('url', '') if isinstance(post, dict) else ''

                stats["total_likes"] += likes
                stats["total_comments"] += comments
                stats["total_shares"] += shares
                stats["total_views"] += views
                stats["sentiment_stats"][sentiment] = stats["sentiment_stats"].get(sentiment, 0) + 1

                # 关键词统计
                for keyword in keywords:
                    stats["keyword_stats"][keyword] = stats["keyword_stats"].get(keyword, 0) + 1

            # 视频统计
            for video in videos:
                # 处理VideoItem对象或字典
                if hasattr(video, 'likes'):
                    likes = video.likes
                    comments = video.comments
                    shares = video.shares
                    views = video.views
                    sentiment = video.sentiment
                    keywords = video.keywords
                    title = video.title
                    author = video.author
                    video_id = video.video_id
                    url = video.url
                else:
                    likes = video.get('likes', 0) if isinstance(video, dict) else 0
                    comments = video.get('comments', 0) if isinstance(video, dict) else 0
                    shares = video.get('shares', 0) if isinstance(video, dict) else 0
                    views = video.get('views', 0) if isinstance(video, dict) else 0
                    sentiment = video.get('sentiment', '中性') if isinstance(video, dict) else '中性'
                    keywords = video.get('keywords', []) if isinstance(video, dict) else []
                    title = video.get('title', '') if isinstance(video, dict) else ''
                    author = video.get('author', '') if isinstance(video, dict) else ''
                    video_id = video.get('video_id', '') if isinstance(video, dict) else ''
                    url = video.get('url', '') if isinstance(video, dict) else ''

                stats["total_likes"] += likes
                stats["total_comments"] += comments
                stats["total_shares"] += shares
                stats["total_views"] += views
                stats["sentiment_stats"][sentiment] = stats["sentiment_stats"].get(sentiment, 0) + 1

                for keyword in keywords:
                    stats["keyword_stats"][keyword] = stats["keyword_stats"].get(keyword, 0) + 1

            # 时间线统计
            for post in posts:
                publish_time = ''
                if hasattr(post, 'publish_time'):
                    publish_time = post.publish_time
                elif isinstance(post, dict):
                    publish_time = post.get('publish_time', '')

                if publish_time:
                    date = publish_time[:10] if len(publish_time) >= 10 else publish_time
                    stats["timeline_stats"][date] = stats["timeline_stats"].get(date, 0) + 1

        stats["total_posts"] = len(all_posts)
        stats["total_videos"] = len(all_videos)

        # Top内容排序
        def get_post_score(post):
            likes = post.likes if hasattr(post, 'likes') else (post.get('likes', 0) if isinstance(post, dict) else 0)
            comments = post.comments if hasattr(post, 'comments') else (post.get('comments', 0) if isinstance(post, dict) else 0)
            shares = post.shares if hasattr(post, 'shares') else (post.get('shares', 0) if isinstance(post, dict) else 0)
            return likes + comments * 2 + shares * 3

        def get_video_score(video):
            views = video.views if hasattr(video, 'views') else (video.get('views', 0) if isinstance(video, dict) else 0)
            likes = video.likes if hasattr(video, 'likes') else (video.get('likes', 0) if isinstance(video, dict) else 0)
            return views + likes * 10

        all_posts_sorted = sorted(all_posts, key=get_post_score, reverse=True)
        all_videos_sorted = sorted(all_videos, key=get_video_score, reverse=True)

        stats["top_posts"] = all_posts_sorted[:10]
        stats["top_videos"] = all_videos_sorted[:10]

        return stats

    def _generate_html(self, stats: Dict[str, Any], crawler_results: List[Dict[str, Any]]) -> str:
        """生成HTML内容"""
        template_str = '''
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>白色污染治理舆情分析报告</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: "Microsoft YaHei", "PingFang SC", -apple-system, BlinkMacSystemFont, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }

        .container {
            max-width: 1400px;
            margin: 0 auto;
        }

        .header {
            text-align: center;
            color: white;
            padding: 40px 20px;
            margin-bottom: 30px;
        }

        .header h1 {
            font-size: 2.5em;
            margin-bottom: 15px;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.2);
        }

        .header p {
            font-size: 1.1em;
            opacity: 0.9;
        }

        .card {
            background: white;
            border-radius: 20px;
            padding: 30px;
            margin-bottom: 25px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.1);
        }

        .card h2 {
            color: #333;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 3px solid #667eea;
            display: inline-block;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
        }

        .stat-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 25px;
            border-radius: 15px;
            text-align: center;
            transition: transform 0.3s ease;
        }

        .stat-card:hover {
            transform: translateY(-5px);
        }

        .stat-card .number {
            font-size: 2.5em;
            font-weight: bold;
            margin-bottom: 5px;
        }

        .stat-card .label {
            font-size: 0.9em;
            opacity: 0.9;
        }

        .stat-card.secondary {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }

        .stat-card.success {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }

        .stat-card.warning {
            background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
        }

        .platform-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
        }

        .platform-card {
            border: 2px solid #e0e0e0;
            border-radius: 15px;
            padding: 20px;
            text-align: center;
            transition: all 0.3s ease;
        }

        .platform-card:hover {
            border-color: #667eea;
            transform: translateY(-3px);
            box-shadow: 0 5px 20px rgba(102, 126, 234, 0.2);
        }

        .platform-card .name {
            font-size: 1.3em;
            font-weight: bold;
            color: #667eea;
            margin-bottom: 15px;
        }

        .platform-card .count {
            font-size: 2em;
            font-weight: bold;
            color: #333;
        }

        .platform-card .sub {
            color: #666;
            font-size: 0.9em;
        }

        .chart-container {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
            gap: 30px;
            margin-top: 20px;
        }

        .chart-box {
            padding: 20px;
            background: #f8f9fa;
            border-radius: 15px;
        }

        .sentiment-grid {
            display: flex;
            justify-content: center;
            gap: 40px;
            margin-top: 30px;
        }

        .sentiment-item {
            text-align: center;
            padding: 20px 40px;
            border-radius: 15px;
        }

        .sentiment-item.positive {
            background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
            color: white;
        }

        .sentiment-item.negative {
            background: linear-gradient(135deg, #eb3349 0%, #f45c43 100%);
            color: white;
        }

        .sentiment-item.neutral {
            background: linear-gradient(135deg, #f2994a 0%, #f2c94c 100%);
            color: white;
        }

        .sentiment-item .percent {
            font-size: 2.5em;
            font-weight: bold;
        }

        .sentiment-item .count {
            font-size: 1.2em;
            opacity: 0.9;
        }

        .content-list {
            margin-top: 20px;
        }

        .content-item {
            display: flex;
            padding: 20px;
            border-bottom: 1px solid #eee;
            transition: background 0.2s;
        }

        .content-item:hover {
            background: #f8f9fa;
        }

        .content-item:last-child {
            border-bottom: none;
        }

        .rank {
            width: 40px;
            height: 40px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            margin-right: 20px;
            flex-shrink: 0;
        }

        .rank.top {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }

        .content-info {
            flex: 1;
        }

        .content-title {
            font-size: 1.1em;
            font-weight: bold;
            color: #333;
            margin-bottom: 8px;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }

        .content-meta {
            color: #666;
            font-size: 0.85em;
            display: flex;
            gap: 20px;
            flex-wrap: wrap;
        }

        .content-meta span {
            display: flex;
            align-items: center;
        }

        .sentiment-tag {
            display: inline-block;
            padding: 3px 10px;
            border-radius: 12px;
            font-size: 0.8em;
            margin-left: 10px;
        }

        .sentiment-tag.positive {
            background: #c6efce;
            color: #006100;
        }

        .sentiment-tag.negative {
            background: #ffc7ce;
            color: #9c0006;
        }

        .sentiment-tag.neutral {
            background: #ffeb9c;
            color: #9c5700;
        }

        .keyword-cloud {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            justify-content: center;
            margin-top: 20px;
        }

        .keyword-tag {
            padding: 8px 16px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 20px;
            font-size: 0.9em;
        }

        .footer {
            text-align: center;
            color: white;
            padding: 30px;
            margin-top: 30px;
            opacity: 0.8;
        }

        @media (max-width: 768px) {
            .header h1 {
                font-size: 1.8em;
            }

            .chart-container {
                grid-template-columns: 1fr;
            }

            .sentiment-grid {
                flex-direction: column;
                gap: 15px;
            }

            .sentiment-item {
                padding: 15px 20px;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>白色污染治理舆情分析报告</h1>
            <p>生成时间：{{ generate_time }} | 覆盖平台：{{ platform_count }}个</p>
        </div>

        <!-- 核心数据 -->
        <div class="card">
            <h2>数据概览</h2>
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="number">{{ total_posts }}</div>
                    <div class="label">帖子/笔记数</div>
                </div>
                <div class="stat-card secondary">
                    <div class="number">{{ total_videos }}</div>
                    <div class="label">视频数</div>
                </div>
                <div class="stat-card success">
                    <div class="number">{{ total_views }}</div>
                    <div class="label">总浏览/播放</div>
                </div>
                <div class="stat-card warning">
                    <div class="number">{{ total_interactions }}</div>
                    <div class="label">总互动数</div>
                </div>
            </div>
        </div>

        <!-- 平台分布 -->
        <div class="card">
            <h2>平台分布</h2>
            <div class="platform-grid">
                {% for item in platform_stats_list %}
                <div class="platform-card">
                    <div class="name">{{ item.platform }}</div>
                    <div class="count">{{ item.total }}</div>
                    <div class="sub">帖子 {{ item.posts }} | 视频 {{ item.videos }}</div>
                </div>
                {% endfor %}
            </div>
        </div>

        <!-- 图表分析 -->
        <div class="card">
            <h2>数据分析</h2>
            <div class="chart-container">
                <div class="chart-box">
                    <canvas id="platformChart"></canvas>
                </div>
                <div class="chart-box">
                    <canvas id="sentimentChart"></canvas>
                </div>
            </div>
        </div>

        <!-- 情感分析 -->
        <div class="card">
            <h2>情感倾向分析</h2>
            <div class="sentiment-grid">
                <div class="sentiment-item positive">
                    <div class="percent">{{ positive_percent }}%</div>
                    <div class="count">正面 {{ sentiment_stats.get('正面', 0) }} 条</div>
                </div>
                <div class="sentiment-item neutral">
                    <div class="percent">{{ neutral_percent }}%</div>
                    <div class="count">中性 {{ sentiment_stats.get('中性', 0) }} 条</div>
                </div>
                <div class="sentiment-item negative">
                    <div class="percent">{{ negative_percent }}%</div>
                    <div class="count">负面 {{ sentiment_stats.get('负面', 0) }} 条</div>
                </div>
            </div>
        </div>

        <!-- 热门内容 -->
        <div class="card">
            <h2>热门帖子TOP10</h2>
            <div class="content-list">
                {% for post in top_posts %}
                <div class="content-item">
                    <div class="rank {% if loop.index <= 3 %}top{% endif %}">{{ loop.index }}</div>
                    <div class="content-info">
                        <div class="content-title">
                            {{ post.title or (post.content[:50] if post.content else '') }}
                            <span class="sentiment-tag {{ post.sentiment }}">{{ post.sentiment }}</span>
                        </div>
                        <div class="content-meta">
                            <span>{{ post.platform }}</span>
                            <span>👤 {{ post.author }}</span>
                            <span>👍 {{ post.likes }}</span>
                            <span>💬 {{ post.comments }}</span>
                            <span>🔗 <a href="{{ post.url }}" target="_blank">查看原文</a></span>
                        </div>
                    </div>
                </div>
                {% endfor %}
            </div>
        </div>

        <!-- 热门视频 -->
        <div class="card">
            <h2>热门视频TOP10</h2>
            <div class="content-list">
                {% for video in top_videos %}
                <div class="content-item">
                    <div class="rank {% if loop.index <= 3 %}top{% endif %}">{{ loop.index }}</div>
                    <div class="content-info">
                        <div class="content-title">
                            {{ (video.title[:80] if video.title else '') }}
                            <span class="sentiment-tag {{ video.sentiment }}">{{ video.sentiment }}</span>
                        </div>
                        <div class="content-meta">
                            <span>{{ video.platform }}</span>
                            <span>👤 {{ video.author }}</span>
                            <span>▶️ {{ video.views }}</span>
                            <span>👍 {{ video.likes }}</span>
                            <span>💬 {{ video.comments }}</span>
                            <span>🔗 <a href="{{ video.url }}" target="_blank">查看原文</a></span>
                        </div>
                    </div>
                </div>
                {% endfor %}
            </div>
        </div>

        <!-- 关键词云 -->
        {% if keyword_stats %}
        <div class="card">
            <h2>热门关键词</h2>
            <div class="keyword-cloud">
                {% for keyword, count in keyword_stats.items()[:20] %}
                <span class="keyword-tag">{{ keyword }} ({{ count }})</span>
                {% endfor %}
            </div>
        </div>
        {% endif %}

        <div class="footer">
            <p>本报告由绿法通舆情分析系统自动生成</p>
            <p>数据仅供参考，请遵守各平台使用条款</p>
        </div>
    </div>

    <script>
        // 平台分布图表
        const platformCtx = document.getElementById('platformChart').getContext('2d');
        new Chart(platformCtx, {
            type: 'bar',
            data: {
                labels: {{ platform_names | tojson }},
                datasets: [{
                    label: '帖子数',
                    data: {{ platform_posts | tojson }},
                    backgroundColor: 'rgba(102, 126, 234, 0.8)'
                }, {
                    label: '视频数',
                    data: {{ platform_videos | tojson }},
                    backgroundColor: 'rgba(118, 75, 162, 0.8)'
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    title: {
                        display: true,
                        text: '各平台内容分布'
                    }
                }
            }
        });

        // 情感分析图表
        const sentimentCtx = document.getElementById('sentimentChart').getContext('2d');
        new Chart(sentimentCtx, {
            type: 'doughnut',
            data: {
                labels: ['正面', '中性', '负面'],
                datasets: [{
                    data: [
                        {{ sentiment_stats.get('正面', 0) }},
                        {{ sentiment_stats.get('中性', 0) }},
                        {{ sentiment_stats.get('负面', 0) }}
                    ],
                    backgroundColor: ['#11998e', '#f2994a', '#eb3349']
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    title: {
                        display: true,
                        text: '情感倾向分布'
                    }
                }
            }
        });
    </script>
</body>
</html>
        '''

        # 辅助函数：提取对象属性
        def extract_post(item):
            if hasattr(item, 'to_dict'):
                return item.to_dict()
            elif isinstance(item, dict):
                return item
            return {"title": str(item), "content": "", "sentiment": "中性", "platform": "", "author": "", "likes": 0, "comments": 0, "url": ""}

        def extract_video(item):
            if hasattr(item, 'to_dict'):
                return item.to_dict()
            elif isinstance(item, dict):
                return item
            return {"title": str(item), "description": "", "sentiment": "中性", "platform": "", "author": "", "views": 0, "likes": 0, "comments": 0, "url": ""}

        # 准备数据
        platform_stats_list = [
            {"platform": k, "posts": v["posts"], "videos": v["videos"], "total": v["total"]}
            for k, v in stats["platform_stats"].items()
        ]

        template_data = {
            "generate_time": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            "platform_count": len(stats["platform_stats"]),
            "total_posts": stats["total_posts"],
            "total_videos": stats["total_videos"],
            "total_views": self._format_number(stats["total_views"]),
            "total_interactions": self._format_number(
                stats["total_likes"] + stats["total_comments"] + stats["total_shares"]
            ),
            "platform_stats": stats["platform_stats"],
            "platform_stats_list": platform_stats_list,
            "sentiment_stats": stats["sentiment_stats"],
            "top_posts": [extract_post(p) for p in stats["top_posts"]],
            "top_videos": [extract_video(v) for v in stats["top_videos"]],
            "keyword_stats": dict(sorted(stats["keyword_stats"].items(), key=lambda x: x[1], reverse=True)),
            # 图表数据
            "platform_names": list(stats["platform_stats"].keys()),
            "platform_posts": [s["posts"] for s in stats["platform_stats"].values()],
            "platform_videos": [s["videos"] for s in stats["platform_stats"].values()],
            # 情感百分比
            "positive_percent": self._calc_percent(stats["sentiment_stats"].get("正面", 0)),
            "neutral_percent": self._calc_percent(stats["sentiment_stats"].get("中性", 0)),
            "negative_percent": self._calc_percent(stats["sentiment_stats"].get("负面", 0))
        }

        template = Template(template_str)
        return template.render(**template_data)

    def _format_number(self, num: int) -> str:
        """格式化数字"""
        if num >= 100000000:
            return f"{num / 100000000:.1f}亿"
        elif num >= 10000:
            return f"{num / 10000:.1f}万"
        else:
            return str(num)

    def _calc_percent(self, count: int) -> str:
        """计算百分比"""
        total = sum(self._sentiment_counts.values()) if hasattr(self, '_sentiment_counts') else 1
        total = max(total, 1)
        return f"{count / total * 100:.1f}"


if __name__ == "__main__":
    # 测试
    generator = ReportGenerator()
    print("报告生成器已创建")
