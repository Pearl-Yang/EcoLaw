# 绿法通 - 白色污染治理AI智慧普法平台

## 一、项目概述

### 1.1 核心定位
绿法通是专为白色污染专项治理打造的AI智慧普法平台，覆盖政府监管、基层执行、律所服务、企业合规四端联动，实现普法与合规管理全流程数字化。

### 1.2 目标用户群体
| 用户角色 | 细分用户 |
|----------|----------|
| 政府监管端 | 县/区级司法局、生态环境局、市场监管局 |
| 基层执行端 | 乡镇司法所、村/社区居委会、法律明白人 |
| 律所服务端 | 入驻律所、主办律师 |
| 企业合规端 | 塑料制品制造、农业合作社、餐饮商超、快递物流企业 |
| 普通用户端 | 基层群众、企业员工 |

### 1.3 平台架构
```
「1个AI核心引擎 + 4端联动业务体系 + 1套统一数据中台」
```

---

## 二、技术架构

### 2.1 前端技术栈

| 端 | 技术选型 | 说明 |
|----|----------|------|
| **管理后台 (Web端)** | Vue3 + Vite + JavaScript + Element Plus + Pinia + ECharts | 面向政府、律所、企业的数据管理后台，PC端优先 |
| **用户端 (小程序/App)** | UniApp + uView UI + Pinia | 面向普通用户的轻量化入口，微信小程序+App双端 |

### 2.2 后端技术栈

| 层级 | 技术选型 | 说明 |
|------|----------|------|
| 网关层 | Nginx + Lua | 请求路由、负载均衡、SSL终端 |
| 后端业务层 | Java 17 + Spring Boot 3.x + Spring Security 6 + MyBatis-Plus | RESTful API服务 |
| AI服务层 | Python 3.11 + FastAPI + LangChain + Milvus | AI普法引擎、RAG知识库 |
| 数据存储层 | MySQL 8.0 + Redis 7 + MinIO | 结构化数据、缓存、文件存储 |
| 向量数据库 | Milvus 2.x | 法律条文向量检索 |
| 消息队列 | Redis Pub/Sub | 实时通知、任务推送 |

### 2.3 技术架构图

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           用户访问层                                      │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐              │
│  │   微信小程序   │    │   移动App    │    │   PC管理后台  │              │
│  │   (用户端)    │    │   (用户端)   │    │   (管理端)   │              │
│  └──────┬───────┘    └──────┬───────┘    └──────┬───────┘              │
└─────────┼───────────────────┼───────────────────┼───────────────────────┘
          │                   │                   │
          └───────────────────┼───────────────────┘
                              │ HTTPS
┌─────────────────────────────────────────────────────────────────────────┐
│                           网关层 (Nginx)                                  │
│                  负载均衡 | SSL终端 | 静态资源 | 限流                      │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │
          ┌─────────────��────────┼──────────────────────┐
          │                      │                      │
┌─────────┴─────────┐  ┌────────┴────────┐  ┌────────┴────────┐
│   后端服务集群     │  │    AI服务集群     │  │   静态资源      │
│   (Java Spring)   │  │   (Python Fast)   │  │   (CDN)         │
│   :8080          │  │   :8000          │  │                │
└─────────┬─────────┘  └────────┬────────┘  └─────────────────┘
          │                      │
          └──────────────────────┼──────────────────────┐
                                 │                      │
┌────────────────────────────────┴──────────────────────┴────────────────┐
│                           数据存储层                                      │
│  ┌────────┐  ┌────────┐  ┌────────┐  ┌────────┐  ┌────────┐          │
│  │ MySQL  │  │ Redis  │  │ Milvus │  │ MinIO  │  │  文件   │          │
│  │ 8.0   │  │  7.x   │  │ 2.x    │  │ S3    │  │  存储   │          │
│  └────────┘  └────────┘  └────────┘  └────────┘  └────────┘          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 三、项目目录结构

### 3.1 整体目录

```
EnvironmentProtection/                          # 项目根目录
├── apps/                                       # 应用程序目录
│   ├── web-admin/                             # 管理后台 (Vue3)
│   │   ├── src/
│   │   │   ├── api/                          # API接口定义
│   │   │   ├── assets/                       # 静态资源
│   │   │   ├── components/                   # 公共组件
│   │   │   ├── composables/                  # 组合式函数
│   │   │   ├── layouts/                      # 布局组件
│   │   │   ├── router/                       # 路由配置
│   │   │   ├── stores/                       # Pinia状态管理
│   │   │   ├── utils/                         # 工具函数
│   │   │   ├── views/                        # 页面视图
│   │   │   │   ├── auth/                     # 认证相关
│   │   │   │   ├── government/               # 政府端页面
│   │   │   │   ├── enterprise/               # 企业端页面
│   │   │   │   ├── law-firm/                 # 律所端页面
│   │   │   │   └── platform/                 # 平台端页面
│   │   │   ├── App.vue
│   │   │   ├── main.js
│   │   │   └── permission.js               # 权限控制
│   │   ├── index.html
│   │   ├── package.json
│   │   ├── vite.config.js
│   │   └── .env.example
│   │
│   └── mini-program/                          # 小程序端 (UniApp)
│       ├── src/
│       │   ├── api/                          # API接口
│       │   ├── components/                   # 组件
│       │   ├── pages/                        # 页面
│       │   │   ├── index/                    # 首页
│       │   │   ├── learn/                    # 学习中心
│       │   │   ├── task/                     # 任务中心
│       │   │   ├── chat/                      # AI咨询
│       │   │   ├── law/                       # 法规查询
│       │   │   ├── report/                   # 线索举报
│       │   │   ├── ranking/                   # 排行榜
│       │   │   └── profile/                   # 个人中心
│       │   ├── static/                        # 静态资源
│       │   ├── store/                         # 状态管理
│       │   ├── styles/                        # 样式
│       │   ├── utils/                         # 工具
│       │   ├── App.vue
│       │   ├── main.js
│       │   ├── manifest.json
│       │   └── pages.json
│       ├── package.json
│       ├── vite.config.js
│       └── .env.example
│
├── services/                                   # 后端服务目录
│   ├── api-gateway/                           # API网关 (可选)
│   │   ├── src/main/java/
│   │   ├── pom.xml
│   │   └── Dockerfile
│   │
│   ├── api-server/                            # 业务API服务 (Java)
│   │   ├── src/main/java/com/lvfat/
│   │   │   ├── controller/                   # 控制器
│   │   │   ├── service/                       # 服务层
│   │   │   ├── repository/                    # 数据访问层
│   │   │   ├── entity/                        # 实体
│   │   │   ├── dto/                           # 数据传输对象
│   │   │   ├── vo/                            # 视图对象
│   │   │   ├── config/                        # 配置
│   │   │   ├── security/                      # 安全认证
│   │   │   ├── common/                        # 公共组件
│   │   │   └── LvFatApplication.java
│   │   ├── src/main/resources/
│   │   │   ├── mapper/                        # MyBatis XML
│   │   │   ├── application.yml
│   │   │   ├── application-dev.yml
│   │   │   └── application-prod.yml
│   │   ├── src/test/java/
│   │   ├── pom.xml
│   │   └── Dockerfile
│   │
│   └── ai-service/                            # AI服务 (Python)
│       ├── app/
│       │   ├── api/
│       │   │   └── v1/
│       │   │       ├── law_chat.py           # 法律智能问答
│       │   │       ���── material_gen.py       # 素材生成
│       │   │       ├── case_analysis.py      # 案例拆解
│       │   │       └── exam_generate.py      # 智能组卷
│       │   ├── core/
│       │   │   ├── config.py                  # 配置管理
│       │   │   ├── llm.py                     # LLM封装
│       │   │   ├── rag.py                     # RAG实现
│       │   │   ├── vectorstore.py             # 向量存储
│       │   │   └── prompt.py                  # Prompt模板
│       │   ├── models/                        # 数据模型
│       │   ├── services/                      # 业务服务
│       │   └── utils/                         # 工具函数
│       ├── data/
│       │   ├── laws/                          # 法规库
│       │   ├── cases/                         # 案例库
│       │   └── materials/                     # 素材库
│       ├── requirements.txt
│       ├── main.py
│       └── Dockerfile
│
├── infrastructure/                             # 基础设施配置
│   ├── nginx/
│   │   ├── nginx.conf
│   │   └── conf.d/
│   │       ├── api.conf                      # API代理配置
│   │       └── ai.conf                       # AI服务代理
│   │
│   ├── database/
│   │   ├── mysql/
│   │   │   └── init.sql                      # 数据库初始化
│   │   └── redis/
│   │       └── redis.conf
│   │
│   └── minio/
│       └── config.json                       # 对象存储配置
│
├── docker/                                    # Docker编排
│   ├── docker-compose.yml                    # 基础服务
│   ├── docker-compose.dev.yml                # 开发环境
│   └── docker-compose.prod.yml               # 生产环境
│
├── docs/                                      # 项目文档
│   ├── api/                                   # API文档
│   ├── database/                              # 数据库设计
│   ├── deploy/                                # 部署文档
│   └── manual/                                # 用户手册
│
├── scripts/                                   # 脚本工具
│   ├── init-db.sh                            # 数据库初始化
│   ├── build.sh                              # 构建脚本
│   └── deploy.sh                             # 部署脚本
│
├── README.md                                  # 项目说明
├── SPEC.md                                   # 需求规格说明书
└── .gitignore
```

### 3.2 目录说明

| 目录 | 说明 | 保留/新建 |
|------|------|----------|
| `apps/web-admin` | Vue3管理后台 - 政府/企业/律所/平台端 | 新建 |
| `apps/mini-program` | UniApp小程序端 - 普通用户入口 | 迁移自原`frontend` |
| `services/api-server` | Java后端API服务 | 新建(原backend空目录) |
| `services/ai-service` | Python AI服务 | 迁移自原`ai-service` |
| `infrastructure` | Nginx/数据库/存储配置 | 新建整合 |
| `docker` | Docker编排 | 保留 |
| `docs` | 文档目录 | 保留 |
| `scripts` | 脚本工具 | 新建 |
| `admin-portal` | 旧管理后台(静态HTML) | **删除** |
| `pages-admin` | 空目录 | **删除** |
| `static` | 空目录 | **删除** |
| `backend` | 空目录 | **删除** |
| `frontend` | UniApp项目 | 迁移至`apps/mini-program` |
| `gateway` | Nginx配置 | 迁移至`infrastructure/nginx` |

---

## 四、功能模块规划

### 4.1 管理后台模块 (web-admin)

| 模块 | 功能 | 适用角色 |
|------|------|----------|
| **认证模块** | 登录/注册/找回密码/双因素认证 | 全角色 |
| **政府端** | 任务派发/进度督导/数据看板/考核管理/报表导出 | 政府监管员 |
| **企业端** | 培训管理/员工管理/台账导出/合规预警 | 企业管理员 |
| **律所端** | 案例管理/客户服务/咨询对接/数据统计 | 律所管理员 |
| **平台端** | 组织管理/用户管理/内容审核/系统配置 | 超级管理员 |
| **AI工具** | 素材生成/法条解读/智能组卷 | 全角色 |
| **数据看板** | 可视化统计//对比分析 | 全角色 |

### 4.2 小程序端模块 (mini-program)

> **说明**：小程序端为所有角色提供统一的移动端入口，不同角色登录后根据其身份展示对应的功能模块。复杂的数据分析和可视化报表建议在网页端管理后台使用。

| 模块 | 功能 | 适用角色 |
|------|------|----------|
| **首页** | 普法内容推荐/任务入口/快速咨询 | 全部角色 |
| **学习中心** | 课程学习/在线考试/学习记录 | 普通用户/企业员工 |
| **任务中心** | 普法任务/执行进度/过程上传 | 法律明白人/基层执行人员 |
| **企业培训** | 培训任务/学习进度/考核结果 | 企业管理员/企业员工 |
| **AI咨询** | 智能问答/法条查询/专业咨询 | 全部角色 |
| **线索举报** | 违法举报/进度查询/反馈评价 | 全部角色 |
| **个人中心** | 学习档案/积分排名/我的任务/设置 | 全部角色 |

---

## 五、技术选型详情

### 5.1 管理后台 (web-admin)

| 类型 | 选型 | 版本 |
|------|------|------|
| 开发语言 | JavaScript (ES6+) | - |
| 框架 | Vue 3 | 3.4+ |
| 构建工具 | Vite | 5.x |
| UI组件库 | Element Plus | 2.5+ |
| 状态管理 | Pinia | 2.x |
| 路由 | Vue Router | 4.x |
| HTTP客户端 | Axios | 1.6+ |
| 图表 | ECharts | 5.x |
| 表格 | Element Plus Table | 内置 |
| 表单验证 | async-validator | 内置 |
| 国际化 | vue-i18n | 9.x |
| CSS预处理器 | SCSS | 内置 |

### 5.2 小程序端 (mini-program)

| 类型 | 选型 | 版本 |
|------|------|------|
| 框架 | UniApp | 3.x |
| 构建工具 | Vite | 5.x |
| UI组件库 | uView Plus | 3.x |
| 状态管理 | Pinia | 2.x |
| HTTP客户端 | uView-request | 内置 |
| 云存储 | 微信云开发 | - |
| 地图 | 腾讯地图 | - |

### 5.3 后端服务 (api-server)

| 类型 | 选型 | 版本 |
|------|------|------|
| 语言 | Java | 17 |
| 框架 | Spring Boot | 3.2+ |
| 安全框架 | Spring Security | 6.x |
| ORM | MyBatis-Plus | 3.5+ |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis | 7.x |
| 文件存储 | MinIO | latest |
| 任务调度 | Quartz | 2.3+ |
| 文档 | Swagger/OpenAPI | 3.x |

### 5.4 AI服务 (ai-service)

| 类型 | 选型 | 版本 |
|------|------|------|
| 语言 | Python | 3.11 |
| 框架 | FastAPI | 0.109+ |
| LLM框架 | LangChain | 0.1+ |
| 向量数据库 | Milvus | 2.3+ |
| 嵌入模型 | text2vec-base-chinese | - |
| 异步任务 | Celery + Redis | - |

---

## 六、实施计划

### 6.1 第一阶段：项目重构 (1-2周)
- [ ] 清理旧目录结构
- [ ] 创建新的项目骨架
- [ ] 配置开发环境
- [ ] 搭建基础设施服务

### 6.2 第二阶段：核心功能开发 (4-6周)
- [ ] 用户认证系统
- [ ] 组织架构与权限管理
- [ ] 基层普法任务管理
- [ ] 企业合规培训管理

### 6.3 第三阶段：AI能力集成 (2-3周)
- [ ] AI服务部署
- [ ] RAG知识库构建
- [ ] 素材生成功能
- [ ] 智能问答功能

### 6.4 第四阶段：完善与优化 (2-3周)
- [ ] 律所服务端功能
- [ ] 政府端监管看板
- [ ] 性能优化
- [ ] 安全加固

---

## 七、部署架构

### 7.1 开发环境
```yaml
# docker/docker-compose.dev.yml
services:
  mysql:
    image: mysql:8.0
    ports:
      - "3306:3306"
  
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
  
  minio:
    image: minio/minio
    ports:
      - "9000:9000"
  
  milvus:
    image: milvusdb/milvus:v2.3
    ports:
      - "19530:19530"
```

### 7.2 生产环境
- K8s/Helm部署
- Nginx Ingress
- 自动弹性伸缩
- 监控告警 (Prometheus + Grafana)

---

## 八、注意事项

1. **数据安全**：所有敏感数据需加密存储，传输使用HTTPS
2. **等保2.0**：系统需满足三级等保要求
3. **日志审计**：所有操作需留存日志，留存期≥6个月
4. **容灾备份**：每日全量备份，异地存储
5. **代码质量**：遵循企业Java开发规范和Vue最佳实践