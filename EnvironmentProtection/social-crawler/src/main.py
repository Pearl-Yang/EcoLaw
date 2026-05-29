"""
社交媒体舆情爬虫 - 主程序入口
用于爬取各社交平台的智慧普法和生态环境法律规定法典化相关帖子和视频
"""

import os
import sys
import json
import argparse
import time
import io
from datetime import datetime
from typing import List, Dict, Any

# 修复Windows控制台编码问题
if sys.platform == 'win32':
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8', errors='replace')
    sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8', errors='replace')

# 添加src目录到路径
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from base_crawler import CrawlerConfig
from bilibili_crawler import BilibiliCrawler
from exporter import ExcelExporter, export_to_json
from report_generator import ReportGenerator


# 默认搜索关键词
DEFAULT_KEYWORDS = [
    # 智慧普法相关
    "智慧普法",
    "智能普法",
    "数字普法",
    "普法信息化",
    "智慧司法",
    "普法科技",
    "数字化普法",
    "智慧法治",
    "普法平台",
    "普法数字化",

    # 生态环境法律规定法典化相关
    "生态环境法典",
    "环境法典",
    "环保法典",
    "生态环境法律",
    "环境法律体系",
    "环保法律法规",
    "生态环境法治",
    "环境法治建设",
    "生态环境保护法",
    "环保立法",
    "环境法典化",
    "生态环境法律规定"
]

# 平台名称映射
PLATFORM_MAP = {
    "bilibili": "bilibili",
    "b": "bilibili",
    "all": "all"
}

# 爬虫类映射
CRAWLERS = {
    "bilibili": BilibiliCrawler
}


def print_banner():
    """打印横幅"""
    banner = """
    ╔══════════════════════════════════════════════════════════╗
    ║                                                          ║
    ║     智慧普法与生态环境法律规定法典化舆情爬虫 v1.0      ║
    ║     Social Media Sentiment Crawler                      ║
    ║                                                          ║
    ║     支持平台：B站 (Bilibili)                              ║
    ║                                                          ║
    ╚══════════════════════════════════════════════════════════╝
    """
    print(banner)


def get_crawler_class(platform: str):
    """获取爬虫类"""
    normalized = PLATFORM_MAP.get(platform.lower(), platform.lower())
    return CRAWLERS.get(normalized)


def crawl_platform(platform: str, keywords: List[str], post_limit: int, video_limit: int) -> Dict[str, Any]:
    """爬取单个平台"""
    crawler_class = get_crawler_class(platform)

    if not crawler_class:
        print(f"  [错误] 未知的平台: {platform}")
        return None

    print(f"\n{'─'*50}")
    print(f"正在爬取平台: {platform}")
    print(f"{'─'*50}")

    # 创建爬虫实例
    config = CrawlerConfig(
        request_delay_min=1.5,
        request_delay_max=3.0,
        max_retries=3,
        timeout=30
    )
    crawler = crawler_class(config)

    try:
        result = crawler.crawl(keywords, post_limit, video_limit)
        print(f"\n  ✅ {platform} 爬取完成!")
        print(f"     帖子: {result.get('post_count', 0)} 条")
        print(f"     视频: {result.get('video_count', 0)} 条")
        return result

    except Exception as e:
        print(f"\n  ❌ {platform} 爬取出错: {e}")
        return None


def save_results(crawler_results: List[Dict[str, Any]], output_dir: str):
    """保存爬取结果"""
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")

    # 保存原始JSON数据
    json_file = os.path.join(output_dir, f"crawl_results_{timestamp}.json")
    with open(json_file, "w", encoding="utf-8") as f:
        json.dump(crawler_results, f, ensure_ascii=False, indent=2, default=str)
    print(f"\n📄 JSON数据已保存: {json_file}")

    # 导出Excel
    exporter = ExcelExporter(output_dir)
    excel_file = exporter.export_all(crawler_results)
    print(f"📊 Excel报告已保存: {excel_file}")

    # 生成HTML报告
    if crawler_results:
        report_generator = ReportGenerator(output_dir)
        html_file = report_generator.generate(crawler_results)
        print(f"🌐 HTML报告已保存: {html_file}")

    return {
        "json": json_file,
        "excel": excel_file if crawler_results else None,
        "html": html_file if crawler_results else None
    }


def main():
    """主函数"""
    parser = argparse.ArgumentParser(
        description="智慧普法与生态环境法律规定法典化舆情爬虫",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
示例:
  python main.py                           # 爬取B站
  python main.py --keywords 智慧普法 生态环境法典  # 自定义关键词
  python main.py --limit 100               # 设置爬取数量
        """
    )

    parser.add_argument(
        "--platform", "-p",
        type=str,
        default="bilibili",
        help="指定平台 (bilibili)"
    )

    parser.add_argument(
        "--keywords", "-k",
        type=str,
        nargs="+",
        default=None,
        help="搜索关键词 (多个关键词用空格分隔)"
    )

    parser.add_argument(
        "--limit", "-l",
        type=int,
        default=50,
        help="每个平台爬取数量上限 (默认50)"
    )

    parser.add_argument(
        "--post-limit",
        type=int,
        default=None,
        help="帖子/评论数量上限"
    )

    parser.add_argument(
        "--video-limit",
        type=int,
        default=None,
        help="视频数量上限"
    )

    parser.add_argument(
        "--output", "-o",
        type=str,
        default="data",
        help="输出目录 (默认: data)"
    )

    parser.add_argument(
        "--no-export",
        action="store_true",
        help="不导出Excel和HTML报告"
    )

    args = parser.parse_args()

    # 打印横幅
    print_banner()

    # 确定关键词
    keywords = args.keywords if args.keywords else DEFAULT_KEYWORDS
    print(f"🔍 搜索关键词: {', '.join(keywords)}")

    # 确定平台列表
    if args.platform.lower() == "all":
        platforms = list(CRAWLERS.keys())
    else:
        platforms = [p.strip() for p in args.platform.split(",")]

    print(f"📱 目标平台: {', '.join(platforms)}")
    print(f"📊 爬取数量限制: {args.limit}")
    print()

    # 创建输出目录
    script_dir = os.path.dirname(os.path.abspath(__file__))
    output_dir = os.path.join(script_dir, "..", args.output)
    os.makedirs(output_dir, exist_ok=True)

    # 确定数量限制
    post_limit = args.post_limit if args.post_limit else args.limit
    video_limit = args.video_limit if args.video_limit else args.limit

    # 开始爬取
    start_time = time.time()
    crawler_results = []

    for platform in platforms:
        result = crawl_platform(platform, keywords, post_limit, video_limit)
        if result:
            crawler_results.append(result)

        # 平台间延迟
        if platform != platforms[-1]:
            print(f"\n⏳ 等待3秒后继续下一个平台...")
            time.sleep(3)

    # 统计结果
    total_posts = sum(r.get("post_count", 0) for r in crawler_results)
    total_videos = sum(r.get("video_count", 0) for r in crawler_results)

    print(f"\n{'═'*50}")
    print(f"✅ 爬取完成!")
    print(f"   总帖子数: {total_posts}")
    print(f"   总视频数: {total_videos}")
    print(f"   耗时: {time.time() - start_time:.1f}秒")
    print(f"{'═'*50}")

    # 保存结果
    if crawler_results and not args.no_export:
        print(f"\n💾 正在保存结果...")
        save_results(crawler_results, output_dir)

    print("\n🎉 全部完成!")
    return 0


if __name__ == "__main__":
    try:
        sys.exit(main())
    except KeyboardInterrupt:
        print("\n\n⚠️ 用户中断爬取")
        sys.exit(1)
    except Exception as e:
        print(f"\n\n❌ 发生错误: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)
