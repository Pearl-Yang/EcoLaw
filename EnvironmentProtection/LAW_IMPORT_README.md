# 生态环境法典检索系统

将《中华人民共和国生态环境法典》Word 文档导入数据库，支持全文搜索和中文分词检索。

## 功能特性

- **全文检索**: 基于 MySQL FULLTEXT 支持中文分词搜索
- **向量检索**: 基于 Milvus 的语义相似度搜索
- **混合检索**: 综合全文和向量搜索结果，RRF 算法融合
- **API 接口**: RESTful API 支持前端调用
- **前端界面**: 微信小程序风格的法规检索页面

## 目录结构

```
EnvironmentProtection/
├── ai-service/
│   ├── app/
│   │   ├── api/v1/
│   │   │   └── search.py          # 法规检索 API
│   │   ├── services/
│   │   │   ├── database_service.py # MySQL 数据库服务
│   │   │   └── search_service.py   # 搜索服务
│   │   └── core/
│   │       ├── config.py          # 配置
│   │       └── vectorstore.py     # 向量存储
│   ├── tools/
│   │   ├── parse_law_doc.py       # 文档解析工具
│   │   └── import_law.py           # 数据导入工具
│   └── data/                      # 数据目录
├── docker/
│   └── mysql/
│       └── law_code.sql           # 法典数据表
└── frontend/
    └── pages/
        └── law-search/            # 检索页面
```

## 数据库表结构

### law_code - 法典主表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| title | VARCHAR(500) | 法典标题 |
| law_number | VARCHAR(100) | 法规编号 |
| category | VARCHAR(50) | 分类 |
| content | LONGTEXT | 法典完整内容 |
| chapter_count | INT | 章节数量 |

### law_chapter - 章节表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| law_id | BIGINT | 所属法典ID |
| parent_id | BIGINT | 父章节ID |
| title | VARCHAR(500) | 章节标题 |
| content | LONGTEXT | 章节内容 |
| level | TINYINT | 层级深度 |

## 搜索 API

### POST /api/v1/law/law

搜索法规法典

**请求体:**
```json
{
  "keyword": "环境保护",
  "search_type": "HYBRID",
  "top_k": 10,
  "category": null
}
```

**search_type 选项:**
- `FULLTEXT`: 全文检索（基于 MySQL FULLTEXT）
- `VECTOR`: 向量检索（基于 Milvus）
- `HYBRID`: 混合检索（综合全文和向量结果）

**响应:**
```json
{
  "total": 5,
  "keyword": "环境保护",
  "search_type": "HYBRID",
  "duration_ms": 125,
  "results": [
    {
      "id": 1,
      "law_id": 1,
      "title": "第一章 总则",
      "chapter_num": "第一章",
      "content": "为了保护和改善环境...",
      "level": 1,
      "relevance_score": 0.85
    }
  ]
}
```

### GET /api/v1/law/{law_id}

获取法典详情

### GET /api/v1/law/chapter/{chapter_id}

获取章节内容

### GET /api/v1/law/suggest

获取搜索建议

## 使用步骤

### 1. 启动数据库服务

```bash
cd docker
docker-compose up -d mysql redis milvus
```

### 2. 创建数据库表

```bash
mysql -h localhost -u root -proot123456 env_protection < docker/mysql/law_code.sql
```

### 3. 安装依赖

```bash
cd ai-service
pip install -r requirements.txt
```

### 4. 导入法典数据

```bash
cd ai-service
python -m tools.import_law
```

### 5. 启动 AI 服务

```bash
cd ai-service
uvicorn main:app --reload --port 8000
```

### 6. 访问前端

在微信开发者工具中打开 `frontend` 项目，访问法规检索页面。

## 中文分词配置

MySQL 8.0 使用 `ngram` 分词器进行中文全文搜索。

### 创建全文索引

```sql
ALTER TABLE law_code
ADD FULLTEXT INDEX ft_title_content(title, content) WITH PARSER ngram;
```

### 搜索语法

```sql
-- 自然语言模式
SELECT * FROM law_chapter
WHERE MATCH(title, content) AGAINST('环境保护' IN NATURAL LANGUAGE MODE);

-- 布尔模式
SELECT * FROM law_chapter
WHERE MATCH(title, content) AGAINST('+环境保护 -污染' IN BOOLEAN MODE);
```

## 环境变量

```env
# MySQL
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_USER=root
MYSQL_PASSWORD=root123456
MYSQL_DATABASE=env_protection

# Milvus
MILVUS_HOST=localhost
MILVUS_PORT=19530

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
```

## 技术栈

- **后端**: FastAPI + Python
- **数据库**: MySQL 8.0 (FULLTEXT + ngram)
- **向量库**: Milvus 2.3
- **嵌入模型**: sentence-transformers
- **前端**: uni-app (Vue 3)
