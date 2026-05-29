# 绿法通 - 白色污染治理AI智慧普法平台

## 一、项目概述

绿法通是专为白色污染专项治理打造的AI智慧普法平台，覆盖政府监管、基层执行、律所服务、企业合规四端联动，实现普法与合规管理全流程数字化。

### 目标用户群体

| 用户角色 | 细分用户 |
|----------|----------|
| 政府监管端 | 县/区级司法局、生态环境局、市场监管局 |
| 基层执行端 | 乡镇司法所、村/社区居委会、法律明白人 |
| 律所服务端 | 入驻律所、主办律师 |
| 企业合规端 | 塑料制品制造、农业合作社、餐饮商超、快递物流企业 |
| 普通用户端 | 基层群众、企业员工 |

---

## 二、技术架构

### 2.1 前端技术栈

| 端 | 技术选型 | 说明 |
|----|----------|------|
| **管理后台 (Web端)** | Vue3 + Vite + JavaScript + Element Plus + Pinia + ECharts | 面向政府、律所、企业的数据管理后台 |
| **用户端 (小程序/App)** | UniApp + uView Plus + Pinia | 面向普通用户的轻量化入口 |

### 2.2 后端技术栈

| 层级 | 技术选型 | 说明 |
|------|----------|------|
| 后端业务层 | Java 17 + Spring Boot 3.2 + MyBatis-Plus + Spring Security | RESTful API服务、JWT认证 |
| AI服务层 | Python 3.11 + FastAPI + LangChain | AI普法引擎、RAG知识库 |
| 数据存储层 | MySQL 8.0 + Redis 7 + MinIO | 结构化数据、缓存、文件存储 |
| 任务队列层 | Celery + Redis | 异步任务处理 |

---

## 三、项目结构

```
EnvironmentProtection/
├── apps/                              # 应用程序
│   ├── web-admin/                     # Vue3管理后台
│   │   ├── src/
│   │   │   ├── api/                   # API接口
│   │   │   ├── assets/                # 静态资源
│   │   │   ├── components/            # 公共组件
│   │   │   ├── layouts/               # 布局组件
│   │   │   ├── router/                # 路由
│   │   │   ├── stores/                # 状态管理
│   │   │   ├── styles/                # 样式
│   │   │   ├── utils/                 # 工具函数
│   │   │   ├── views/                 # 页面视图
│   │   │   │   ├── auth/              # 认证
│   │   │   ├── App.vue
│   │   │   └── main.js
│   │   └── package.json
│   │
│   └── mini-program/                  # UniApp小程序
│       ├── src/
│       │   ���── api/                   # API接口
│       │   ├── components/            # 组件
│       │   ├── pages/                 # 页面
│       │   ├── store/                 # 状态管理
│       │   └── App.vue
│       └── package.json
│
├── services/                          # 后端服务
│   ├── api-server/                   # Java API服务
│   │   ├── src/main/java/com/lvfat/
│   │   │   ├── controller/            # 控制器
│   │   │   ├── service/               # 服务层
│   │   │   ├── repository/             # 数据访问
│   │   │   ├── entity/                 # 实体
│   │   │   └── common/                 # 公共组件
│   │   ├── pom.xml
│   │   └── Dockerfile
│   │
│   └── ai-service/                   # Python AI服务
│       ├── app/
│       │   ├── api/v1/                # API路由
│       │   └── core/                  # 核心逻辑
│       ├── requirements.txt
│       └── Dockerfile
│
│   └── social-crawler/               # 社交舆情爬虫
│       ├── src/
│       │   ├── crawlers/              # 爬虫实现
│       │   ├── analyzers/             # 数据分析
│       │   └── storage/               # 存储模块
│       ├── requirements.txt
│       └── Dockerfile
│
├── infrastructure/                    # 基础设施
│   ├── nginx/                        # Nginx配置
│   │   ├── nginx.conf
│   │   └── conf.d/
│   └── database/
│       └── mysql/
│           └── init.sql              # 数据库初始化
│
├── docker/                           # Docker编排
│   ├── docker-compose.yml            # 基础服务
│   ├── docker-compose.dev.yml        # 开发环境
│   └── docker-compose.prod.yml      # 生产环境
│
├── docs/                             # 项目文档
├── scripts/                          # 脚本工具
├── README.md
├── PROJECT_RESTRUCTURE.md           # 重构规划文档
└── SPEC.md                           # 需求规格说明书
```

---

## 四、快速启动

### 4.1 环境要求

- Node.js 18+
- Java 17+
- Python 3.11+
- Docker & Docker Compose

### 4.2 启动基础服务

```bash
cd docker
docker-compose -f docker-compose.yml up -d
```

### 4.3 启动后端服务

**API 服务 (Java):**

```bash
cd services/api-server
./mvnw spring-boot:run
# 或使用 Docker
docker-compose -f docker-compose.dev.yml up -d api-server
```

**AI 服务 (Python):**

```bash
cd services/ai-service
pip install -r requirements.txt
uvicorn main:app --reload
# 或使用 Docker
docker-compose -f docker-compose.dev.yml up -d ai-service
```

**社交舆情爬虫 (Python):**

```bash
cd social-crawler
pip install -r requirements.txt
python -m src.main
```

### 4.4 启动前端

**管理后台 (Vue3):**

```bash
cd apps/web-admin
npm install
npm run dev
```

**小程序端 (UniApp):**

```bash
cd apps/mini-program
npm install
npm run dev:mp-weixin  # 微信小程序
npm run dev:app        # App
```

---

## 五、核心功能模块

### 5.1 AI智能普法素材生成

- 法条通俗化解读
- 全形态宣传素材一键生成
- 以案释法智能拆解
- 智能组卷

### 5.2 基层普法任务管理

- 标准化普法任务方案库
- 任务派发与执行管理
- 过程性文件管理
- 工作报告自动生成
- 成果量化可视化上报

### 5.3 企业合规培训管理

- 培训任务创建与派发
- 在线学习系统
- 在线考试系统
- 培训数据统计与台账管理
- ESG报告数据导出

### 5.4 律所服务端

- 律所入驻与信息管理
- 典型案例上传与展示
- 企业客户增值服务管理
- 法律咨询与案源对接

### 5.5 普法与执法联动

- 违法行为线索举报
- 线索分流与处置
- 普法与执法闭环

---

## 六、API 文档

- Swagger UI: <http://localhost:8080/api/swagger-ui.html>
- OpenAPI: <http://localhost:8080/api/v3/api-docs>
- AI服务文档: <http://localhost:8000/docs>

---

## 七、部署

### 开发环境

```bash
cd docker
docker-compose -f docker-compose.dev.yml up -d
```

### 生产环境

```bash
# 设置环境变量
export REGISTRY=your-registry
export TAG=v1.0.0
export LLM_API_KEY=your-api-key

# 构建并启动
docker-compose -f docker-compose.prod.yml up -d
```

---

## 八、技术选型详情

| 类型 | 选型 | 版本 |
|------|------|------|
| 前端框架 | Vue 3 | 3.4.21 |
| 开发语言 | JavaScript (ES6+) | - |
| 构建工具 | Vite | 5.1.4 |
| UI组件库 | Element Plus | 2.5.6 |
| 图表库 | ECharts | 5.5.0 |
| 状态管理 | Pinia | 2.1.7 |
| 小程序框架 | UniApp | 3.0.0-alpha.4 (20240507) |
| 后端框架 | Spring Boot | 3.2.3 |
| ORM | MyBatis-Plus | 3.5.5 |
| 身份认证 | JWT (jjwt) | 0.12.5 |
| API文档 | springdoc-openapi | 2.3.0 |
| AI框架 | FastAPI + LangChain | 0.1.12 |
| 异步任务 | Celery | 5.3.6 |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis | 7.x |
| 文件存储 | MinIO | 8.5.7 |
| 向量嵌入 | sentence-transformers | 2.4.0 |

---

## 九、注意事项

1. **数据安全**：所有敏感数据需加密存储，传输使用HTTPS
2. **等保2.0**：系统需满足三级等保要求
3. **日志审计**：所有操作需留存日志，留存期≥6个月
4. **容灾备份**：每日全量备份，异地存储

---

## 十、许可证

本项目仅供学习交流使用。
