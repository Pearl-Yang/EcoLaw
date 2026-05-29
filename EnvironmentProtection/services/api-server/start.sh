#!/bin/bash
# 绿法通API服务启动脚本

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_DIR"

echo "=========================================="
echo "  绿法通API服务启动脚本"
echo "=========================================="

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "[错误] 未检测到Java环境，请先安装JDK 17+"
    exit 1
fi

echo "Java版本:"
java -version

# 检查Maven
if command -v mvn &> /dev/null; then
    MVN="mvn"
elif [ -f "./mvnw" ]; then
    MVN="./mvnw"
    chmod +x ./mvnw
else
    echo "[错误] 未检测到Maven"
    exit 1
fi

# 设置Spring Profile
SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-dev}"
export SPRING_PROFILES_ACTIVE

echo ""
echo "正在启动服务..."
echo "Profile: $SPRING_PROFILES_ACTIVE"
echo ""

$MVN spring-boot:run -Dspring-boot.run.profiles=$SPRING_PROFILES_ACTIVE
