"""
社交媒体舆情爬虫包
"""

from .base_crawler import BaseCrawler, CrawlerConfig, PostItem, VideoItem, SentimentAnalyzer
from .bilibili_crawler import BilibiliCrawler
from .weibo_crawler import WeiboCrawler
from .zhihu_crawler import ZhihuCrawler
from .xiaohongshu_crawler import XiaohongshuCrawler
from .douyin_crawler import DouyinCrawler
from .exporter import ExcelExporter, export_to_json
from .report_generator import ReportGenerator

__all__ = [
    "BaseCrawler",
    "CrawlerConfig",
    "PostItem",
    "VideoItem",
    "SentimentAnalyzer",
    "BilibiliCrawler",
    "WeiboCrawler",
    "ZhihuCrawler",
    "XiaohongshuCrawler",
    "DouyinCrawler",
    "ExcelExporter",
    "ReportGenerator",
    "export_to_json"
]

__version__ = "1.0.0"
