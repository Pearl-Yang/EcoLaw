# 智慧普法与生态环境法律规定法典化舆情爬虫

本项目用于爬取社交媒体平台上关于智慧普法、生态环境法律规定法典化等主题的群众看法、帖子和视频内容。

## 支持的平台

- 哔哩哔哩 (Bilibili)
- 微博 (Weibo)
- 知乎 (Zhihu)
- 小红书 (Xiaohongshu)
- 抖音 (Douyin)

## 调研主题

本项目聚焦于以下两个调研方向：

1. **智慧普法**：收集公众对智慧普法、智能普法、数字化普法、智慧司法等理念和应用的看法与讨论
2. **生态环境法律规定法典化**：收集公众对生态环境法典、环境法律体系、环保立法等法典化工作的意见与反馈

## 安装

```bash
pip install -r requirements.txt
```

## 使用方法

### 运行完整爬虫（所有平台）

```bash
python src/main.py
```

### 运行单个平台爬虫

```bash
python src/main.py --platform bilibili
python src/main.py --platform weibo
python src/main.py --platform zhihu
python src/main.py --platform xiaohongshu
python src/main.py --platform douyin
```

### 自定义搜索关键词

```bash
python src/main.py --keywords "限塑令 可降解塑料 白色污染"
```

### 查看帮助

```bash
python src/main.py --help
```

## 输出文件

运行后会生成以下文件：

- `data/{platform}_{timestamp}.json` - 原始数据（JSON格式）
- `data/{platform}_{timestamp}.xlsx` - Excel表格
- `reports/report_{timestamp}.html` - HTML可视化报告

## 搜索关键词

默认关键词列表涵盖两个调研方向：

**智慧普法方向：**
智慧普法、智能普法、数字普法、普法信息化、智慧司法
普法科技、数字化普法、智慧法治、普法平台

**生态环境法律规定法典化方向：**
生态环境法典、环境法典、环保法典
生态环境法律、环境法律体系、环保法律法规
生态环境法治、环境法治建设、生态环境保护法
环保立法、环境法典化、生态环境法律规定

## 注意事项

1. 请遵守各平台的使用条款和爬虫协议
2. 请勿过于频繁地请求，以免对服务器造成负担
3. 爬取的数据仅供学习和研究使用
4. 部分平台可能需要登录或验证码才能获取完整数据

## 项目结构

social-crawler/
├── src/
│   ├── main.py                 # 主程序入口
│   ├── base_crawler.py         # 爬虫基类
│   ├── bilibili_crawler.py     # 哔哩哔哩爬虫
│   ├── weibo_crawler.py        # 微博爬虫 (预留)
│   ├── zhihu_crawler.py        # 知乎爬虫 (预留)
│   ├── xiaohongshu_crawler.py  # 小红书爬虫 (预留)
│   ├── douyin_crawler.py       # 抖音爬虫 (预留)
│   ├── exporter.py             # Excel导出器
│   ├── report_generator.py     # HTML报告生成器
│   └── ai_analyzer.py          # AI内容分析器
├── data/                       # 数据存储目录
├── reports/                    # 报告输出目录
├── requirements.txt
└── README.md
