"""
哔哩哔哩爬虫 - 搜索帖子/评论和视频
"""

import json
import re
from typing import List, Optional
from datetime import datetime
from base_crawler import (
    BaseCrawler, CrawlerConfig, PostItem, VideoItem, SentimentAnalyzer,
    format_number, parse_time_to_string
)


class BilibiliCrawler(BaseCrawler):
    """哔哩哔哩爬虫"""

    PLATFORM_NAME = "哔哩哔哩"

    def __init__(self, config: Optional[CrawlerConfig] = None):
        super().__init__(config)
        self.search_url = "https://api.bilibili.com/x/web-interface/search/type"
        self.video_url = "https://api.bilibili.com/x/web-interface/view"
        self.comment_url = "https://api.bilibili.com/x/v2/reply"
        self.hot_url = "https://api.bilibili.com/x/web-interface/ranking/v2"

    def _get_headers(self) -> dict:
        """添加B站特定头"""
        headers = super()._get_headers()
        headers.update({
            "Referer": "https://www.bilibili.com",
            "Origin": "https://www.bilibili.com"
        })
        return headers

    def _extract_video_id(self, bvid: str, avid: Optional[int] = None) -> str:
        """提取视频ID"""
        return bvid or str(avid) if avid else ""

    def _parse_duration(self, duration_str: str) -> int:
        """解析时长字符串为秒数"""
        if not duration_str:
            return 0
        parts = duration_str.split(":")
        if len(parts) == 2:
            return int(parts[0]) * 60 + int(parts[1])
        elif len(parts) == 3:
            return int(parts[0]) * 3600 + int(parts[1]) * 60 + int(parts[2])
        return 0

    def _extract_text_from_html(self, html_content: str) -> str:
        """从HTML中提取纯文本"""
        if not html_content:
            return ""
        # 移除script和style标签
        html_content = re.sub(r'<script[^>]*>.*?</script>', '', html_content, flags=re.DOTALL)
        html_content = re.sub(r'<style[^>]*>.*?</style>', '', html_content, flags=re.DOTALL)
        # 替换标签为空格
        html_content = re.sub(r'<[^>]+>', ' ', html_content)
        # 移除多余空格
        html_content = re.sub(r'\s+', ' ', html_content).strip()
        return html_content

    def search_posts(self, keyword: str, limit: int = 50) -> List[PostItem]:
        """搜索帖子/评论（B站专栏和评论）"""
        posts = []

        # 搜索专栏文章
        params = {
            "search_type": "article",
            "keyword": keyword,
            "page": 1,
            "pagesize": min(limit, 20)
        }

        try:
            response = self.get(self.search_url, params=params)
            if response and response.status_code == 200:
                data = response.json()
                if data.get("code") == 0 and data.get("data", {}).get("result"):
                    for item in data["data"]["result"]:
                        matched_keywords = self._check_keyword_match(
                            item.get("title", "") + item.get("desc", ""),
                            [keyword]
                        )

                        post = PostItem(
                            platform=self.PLATFORM_NAME,
                            post_id=str(item.get("id", "")),
                            title=item.get("title", ""),
                            content=item.get("desc", ""),
                            author=item.get("author", ""),
                            author_id=str(item.get("mid", "")),
                            publish_time=parse_time_to_string(item.get("pubdate", "")),
                            likes=item.get("like", 0),
                            comments=item.get("reply", 0),
                            views=item.get("view", 0),
                            url=item.get("arcurl", ""),
                            tags=item.get("tag", "").split(",") if item.get("tag") else [],
                            sentiment=SentimentAnalyzer.analyze(item.get("title", "") + item.get("desc", "")),
                            keywords=matched_keywords
                        )
                        posts.append(post)
        except Exception as e:
            print(f"    B站专栏搜索出错: {e}")

        return posts

    def search_videos(self, keyword: str, limit: int = 30) -> List[VideoItem]:
        """搜索视频"""
        videos = []

        params = {
            "search_type": "video",
            "keyword": keyword,
            "page": 1,
            "pagesize": min(limit, 20)
        }

        try:
            response = self.get(self.search_url, params=params)
            if response and response.status_code == 200:
                data = response.json()
                if data.get("code") == 0 and data.get("data", {}).get("result"):
                    for item in data["data"]["result"]:
                        matched_keywords = self._check_keyword_match(
                            item.get("title", "") + item.get("description", ""),
                            [keyword]
                        )

                        # 清理标题中的HTML标签
                        title = self._extract_text_from_html(item.get("title", ""))

                        video = VideoItem(
                            platform=self.PLATFORM_NAME,
                            video_id=item.get("bvid", ""),
                            title=title,
                            description=item.get("description", ""),
                            author=item.get("author", ""),
                            author_id=str(item.get("mid", "")),
                            publish_time=parse_time_to_string(item.get("pubdate", "")),
                            duration=self._parse_duration(item.get("duration", "")),
                            likes=item.get("like", 0),
                            comments=item.get("review", 0),
                            shares=item.get("share", 0),
                            views=item.get("play", 0),
                            url=f"https://www.bilibili.com/video/{item.get('bvid', '')}",
                            cover_url=item.get("pic", ""),
                            tags=item.get("tag", "").split(",") if item.get("tag") else [],
                            sentiment=SentimentAnalyzer.analyze(title + item.get("description", "")),
                            keywords=matched_keywords
                        )
                        videos.append(video)

        except Exception as e:
            print(f"    B站视频搜索出错: {e}")

        # 如果搜索结果不足，可以尝试获取热门视频
        if len(videos) < 5:
            try:
                hot_videos = self._get_trending_videos(keyword, limit - len(videos))
                videos.extend(hot_videos)
            except Exception as e:
                print(f"    B站热门视频获取出错: {e}")

        return videos

    def _get_trending_videos(self, keyword: str, limit: int) -> List[VideoItem]:
        """获取排行榜视频"""
        videos = []

        try:
            response = self.get(self.hot_url)
            if response and response.status_code == 200:
                data = response.json()
                if data.get("code") == 0:
                    for item in data.get("data", {}).get("list", []):
                        title = self._extract_text_from_html(item.get("title", ""))
                        matched_keywords = self._check_keyword_match(
                            title + item.get("desc", ""),
                            [keyword]
                        )

                        if matched_keywords or keyword in title:
                            video = VideoItem(
                                platform=self.PLATFORM_NAME,
                                video_id=item.get("bvid", ""),
                                title=title,
                                description=item.get("desc", ""),
                                author=item.get("owner", {}).get("name", ""),
                                author_id=str(item.get("owner", {}).get("mid", "")),
                                publish_time="",
                                likes=item.get("stat", {}).get("like", 0),
                                comments=item.get("stat", {}).get("reply", 0),
                                views=item.get("stat", {}).get("view", 0),
                                url=f"https://www.bilibili.com/video/{item.get('bvid', '')}",
                                cover_url=item.get("pic", ""),
                                tags=[],
                                sentiment=SentimentAnalyzer.analyze(title),
                                keywords=matched_keywords
                            )
                            videos.append(video)

                            if len(videos) >= limit:
                                break

        except Exception as e:
            print(f"    获取热门视频出错: {e}")

        return videos


if __name__ == "__main__":
    # 测试
    crawler = BilibiliCrawler()
    results = crawler.crawl(["限塑令", "白色污染"], post_limit=10, video_limit=10)
    print(f"帖子数量: {results['post_count']}")
    print(f"视频数量: {results['video_count']}")
