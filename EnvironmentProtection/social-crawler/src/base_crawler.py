"""
爬虫基类 - 定义通用爬虫接口和工具方法
"""

import requests
import time
import random
from abc import ABC, abstractmethod
from typing import List, Dict, Any, Optional
from dataclasses import dataclass, field
from datetime import datetime
from fake_useragent import UserAgent


@dataclass
class CrawlerConfig:
    """爬虫配置"""
    request_delay_min: float = 1.0  # 最小请求间隔（秒）
    request_delay_max: float = 3.0  # 最大请求间隔（秒）
    max_retries: int = 3            # 最大重试次数
    timeout: int = 30                # 请求超时时间（秒）
    proxy_enabled: bool = False      # 是否使用代理


@dataclass
class PostItem:
    """帖子/评论数据结构"""
    platform: str = ""               # 平台名称
    post_id: str = ""                # 帖子ID
    title: str = ""                  # 标题
    content: str = ""                # 内容
    author: str = ""                 # 作者
    author_id: str = ""              # 作者ID
    publish_time: str = ""           # 发布时间
    likes: int = 0                   # 点赞数
    comments: int = 0                # 评论数
    shares: int = 0                  # 转发/分享数
    views: int = 0                   # 浏览/播放数
    url: str = ""                    # 原文链接
    tags: List[str] = field(default_factory=list)  # 标签
    sentiment: str = ""              # 情感倾向
    keywords: List[str] = field(default_factory=list)  # 匹配关键词
    raw_data: Dict[str, Any] = field(default_factory=dict)  # 原始数据

    def to_dict(self) -> Dict[str, Any]:
        """转换为字典"""
        return {
            "平台": self.platform,
            "帖子ID": self.post_id,
            "标题": self.title,
            "内容": self.content,
            "作者": self.author,
            "作者ID": self.author_id,
            "发布时间": self.publish_time,
            "点赞数": self.likes,
            "评论数": self.comments,
            "转发数": self.shares,
            "浏览量": self.views,
            "链接": self.url,
            "标签": ",".join(self.tags),
            "情感倾向": self.sentiment,
            "匹配关键词": ",".join(self.keywords)
        }


@dataclass
class VideoItem:
    """视频数据结构"""
    platform: str = ""               # 平台名称
    video_id: str = ""               # 视频ID
    title: str = ""                  # 标题
    description: str = ""           # 描述
    author: str = ""                 # 作者/UP主
    author_id: str = ""             # 作者ID
    publish_time: str = ""          # 发布时间
    duration: int = 0               # 时长（秒）
    likes: int = 0                   # 点赞数
    comments: int = 0                # 评论数
    shares: int = 0                  # 转发数
    views: int = 0                   # 播放量
    coins: int = 0                   # 投币数
    favorites: int = 0               # 收藏数
    danmaku: int = 0                 # 弹幕数
    url: str = ""                    # 视频链接
    cover_url: str = ""              # 封面链接
    tags: List[str] = field(default_factory=list)  # 标签
    category: str = ""               # 分类
    sentiment: str = ""              # 情感倾向
    keywords: List[str] = field(default_factory=list)  # 匹配关键词
    raw_data: Dict[str, Any] = field(default_factory=dict)  # 原始数据

    def to_dict(self) -> Dict[str, Any]:
        """转换为字典"""
        return {
            "平台": self.platform,
            "视频ID": self.video_id,
            "标题": self.title,
            "描述": self.description,
            "作者": self.author,
            "作者ID": self.author_id,
            "发布时间": self.publish_time,
            "时长(秒)": self.duration,
            "点赞数": self.likes,
            "评论数": self.comments,
            "转发数": self.shares,
            "播放量": self.views,
            "投币数": self.coins,
            "收藏数": self.favorites,
            "弹幕数": self.danmaku,
            "链接": self.url,
            "标签": ",".join(self.tags),
            "分类": self.category,
            "情感倾向": self.sentiment,
            "匹配关键词": ",".join(self.keywords)
        }


class BaseCrawler(ABC):
    """爬虫基类"""

    PLATFORM_NAME: str = ""

    def __init__(self, config: Optional[CrawlerConfig] = None):
        self.config = config or CrawlerConfig()
        self.session = requests.Session()
        self.ua = UserAgent()
        self._setup_session()

    def _setup_session(self):
        """设置会话"""
        self.session.headers.update({
            "User-Agent": self.ua.random,
            "Accept": "application/json, text/html, */*",
            "Accept-Language": "zh-CN,zh;q=0.9,en;q=0.8",
            "Accept-Encoding": "gzip, deflate, br"
        })

    def _get_headers(self) -> Dict[str, str]:
        """获取请求头（可被子类重写添加特定头）"""
        return {
            "User-Agent": self.ua.random
        }

    def _random_delay(self):
        """随机延迟"""
        delay = random.uniform(
            self.config.request_delay_min,
            self.config.request_delay_max
        )
        time.sleep(delay)

    def _retry_request(self, method: str, url: str, **kwargs) -> Optional[requests.Response]:
        """带重试的请求"""
        for attempt in range(self.config.max_retries):
            try:
                self._random_delay()
                kwargs.setdefault("timeout", self.config.timeout)
                kwargs.setdefault("headers", self._get_headers())

                if method.upper() == "GET":
                    response = self.session.get(url, **kwargs)
                else:
                    response = self.session.post(url, **kwargs)

                response.raise_for_status()
                return response

            except requests.RequestException as e:
                print(f"请求失败 (尝试 {attempt + 1}/{self.config.max_retries}): {e}")
                if attempt < self.config.max_retries - 1:
                    time.sleep(2 ** attempt)  # 指数退避
                else:
                    print(f"达到最大重试次数，跳过此请求")
                    return None

        return None

    def get(self, url: str, **kwargs) -> Optional[requests.Response]:
        """GET请求"""
        return self._retry_request("GET", url, **kwargs)

    def post(self, url: str, **kwargs) -> Optional[requests.Response]:
        """POST请求"""
        return self._retry_request("POST", url, **kwargs)

    def _check_keyword_match(self, text: str, keywords: List[str]) -> List[str]:
        """检查文本中包含的关键词"""
        matched = []
        text_lower = text.lower()
        for keyword in keywords:
            if keyword.lower() in text_lower:
                matched.append(keyword)
        return matched

    @abstractmethod
    def search_posts(self, keyword: str, limit: int = 50) -> List[PostItem]:
        """搜索帖子/评论"""
        pass

    @abstractmethod
    def search_videos(self, keyword: str, limit: int = 30) -> List[VideoItem]:
        """搜索视频"""
        pass

    def crawl(self, keywords: List[str], post_limit: int = 50, video_limit: int = 30) -> Dict[str, Any]:
        """执行爬取任务"""
        all_posts = []
        all_videos = []

        for keyword in keywords:
            print(f"\n{'='*50}")
            print(f"正在爬取关键词: {keyword}")
            print(f"{'='*50}")

            # 爬取帖子
            try:
                posts = self.search_posts(keyword, post_limit)
                all_posts.extend(posts)
                print(f"  帖子: 获取到 {len(posts)} 条")
            except Exception as e:
                print(f"  帖子爬取失败: {e}")

            # 爬取视频
            try:
                videos = self.search_videos(keyword, video_limit)
                all_videos.extend(videos)
                print(f"  视频: 获取到 {len(videos)} 条")
            except Exception as e:
                print(f"  视频爬取失败: {e}")

        # 去重
        seen_posts = set()
        unique_posts = []
        for post in all_posts:
            if post.post_id not in seen_posts:
                seen_posts.add(post.post_id)
                unique_posts.append(post)

        seen_videos = set()
        unique_videos = []
        for video in all_videos:
            if video.video_id not in seen_videos:
                seen_videos.add(video.video_id)
                unique_videos.append(video)

        return {
            "platform": self.PLATFORM_NAME,
            "crawl_time": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            "keywords": keywords,
            "posts": [p.to_dict() for p in unique_posts],
            "videos": [v.to_dict() for v in unique_videos],
            "post_count": len(unique_posts),
            "video_count": len(unique_videos)
        }


class SentimentAnalyzer:
    """简单的情感分析器（基于关键词）"""

    POSITIVE_WORDS = [
        "支持", "点赞", "好", "棒", "赞", "优秀", "厉害", "环保", "保护",
        "重要", "必要", "应该", "正确", "积极", "有利", "健康", "可持续"
    ]

    NEGATIVE_WORDS = [
        "反对", "吐槽", "差", "垃圾", "坑", "骗", "假", "虚", "假环保",
        "没用", "无用", "浪费", "麻烦", "不便", "困难", "难", "贵"
    ]

    @classmethod
    def analyze(cls, text: str) -> str:
        """分析情感倾向"""
        text_lower = text.lower()

        positive_count = sum(1 for word in cls.POSITIVE_WORDS if word in text_lower)
        negative_count = sum(1 for word in cls.NEGATIVE_WORDS if word in text_lower)

        if positive_count > negative_count:
            return "正面"
        elif negative_count > positive_count:
            return "负面"
        else:
            return "中性"


def format_number(num: int) -> str:
    """格式化数字显示"""
    if num >= 100000000:
        return f"{num / 100000000:.1f}亿"
    elif num >= 10000:
        return f"{num / 10000:.1f}万"
    else:
        return str(num)


def parse_time_to_string(time_str: Any) -> str:
    """将时间解析为字符串"""
    if isinstance(time_str, datetime):
        return time_str.strftime("%Y-%m-%d %H:%M:%S")
    elif isinstance(time_str, (int, float)):
        return datetime.fromtimestamp(time_str).strftime("%Y-%m-%d %H:%M:%S")
    else:
        return str(time_str)
