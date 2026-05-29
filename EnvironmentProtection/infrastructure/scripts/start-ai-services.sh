#!/bin/bash
# =============================================
# 绿法通智能搜索服务启动脚本
# =============================================

echo "========================================"
echo "  绿法通智能搜索服务启动脚本"
echo "========================================"

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 检查端口占用
check_port() {
    if lsof -Pi :$1 -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo -e "${YELLOW}⚠️  端口 $1 已被占用${NC}"
        return 1
    else
        echo -e "${GREEN}✅ 端口 $1 可用${NC}"
        return 0
    fi
}

# 启动 MySQL（如果是本地 Docker）
start_mysql() {
    echo -e "\n${YELLOW}[1/4] 检查 MySQL 服务...${NC}"
    if command -v docker &> /dev/null; then
        if docker ps | grep -q mysql; then
            echo -e "${GREEN}✅ MySQL 容器正在运行${NC}"
        else
            echo "启动 MySQL 容器..."
            docker run -d --name lvfat-mysql \
                -p 3306:3306 \
                -e MYSQL_ROOT_PASSWORD=123456 \
                -e MYSQL_DATABASE=lvfat \
                -v $(pwd)/../mysql:/docker-entrypoint-initdb.d \
                mysql:8.0 \
                --character-set-server=utf8mb4 \
                --collation-server=utf8mb4_unicode_ci \
                --ft_min_word_len=1
        fi
    else
        echo -e "${YELLOW}⚠️  请确保本地 MySQL 服务已启动（端口 3306）${NC}"
    fi
}

# 初始化数据库
init_database() {
    echo -e "\n${YELLOW}[2/4] 初始化数据库（导入 FULLTEXT 索引）...${NC}"
    if [ -f "./mysql/fix_fulltext_indexes.sql" ]; then
        mysql -h localhost -u root -p123456 lvfat < ./mysql/fix_fulltext_indexes.sql
        echo -e "${GREEN}✅ 数据库 FULLTEXT 索引已更新${NC}"
    else
        echo -e "${YELLOW}⚠️  未找到索引修复脚本，跳过${NC}"
    fi
}

# 启动 Python AI 服务
start_python_ai() {
    echo -e "\n${YELLOW}[3/4] 启动 Python AI 服务...${NC}"
    cd ../services/ai-service
    
    # 检查依赖
    if ! pip show pydantic-settings > /dev/null 2>&1; then
        echo "安装 Python 依赖..."
        pip install -r requirements.txt
    fi
    
    # 后台启动
    nohup python -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload > ../logs/python-ai.log 2>&1 &
    echo $! > ../logs/python-ai.pid
    
    echo -e "${GREEN}✅ Python AI 服务已启动（PID: $(cat ../logs/python-ai.pid)）${NC}"
    echo -e "   访问地址: http://localhost:8000"
    echo -e "   API 文档: http://localhost:8000/docs"
}

# 启动 Java 后端
start_java_backend() {
    echo -e "\n${YELLOW}[4/4] 启动 Java 后端服务...${NC}"
    cd ../services/api-server
    
    # 使用 Maven 启动
    nohup mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8080" > ../logs/java-backend.log 2>&1 &
    echo $! > ../logs/java-backend.pid
    
    echo -e "${GREEN}✅ Java 后端服务已启动（PID: $(cat ../logs/java-backend.pid)）${NC}"
    echo -e "   访问地址: http://localhost:8080"
}

# 主函数
main() {
    # 创建日志目录
    mkdir -p ../logs
    
    # 启动各项服务
    start_mysql
    init_database
    start_python_ai
    start_java_backend
    
    echo -e "\n========================================"
    echo -e "  ${GREEN}所有服务已启动！${NC}"
    echo "========================================"
    echo ""
    echo "服务状态："
    echo "  - MySQL:      localhost:3306"
    echo "  - Python AI:  localhost:8000"
    echo "  - Java 后端:  localhost:8080"
    echo ""
    echo "停止所有服务: ./stop-all.sh"
    echo ""
}

# 执行
main