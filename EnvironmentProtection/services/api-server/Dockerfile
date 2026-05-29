# 绿法通API服务Dockerfile
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# 复制pom.xml和源代码
COPY pom.xml .
COPY src ./src

# 设置Maven镜像加速
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories

# 构建项目
RUN mvn clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jre

WORKDIR /app

# 复制构建产物
COPY --from=builder /app/target/*.jar app.jar

# 复制配置文件
COPY src/main/resources/application.yml /app/config/application.yml

# 环境变量
ENV JAVA_OPTS="-Xms512m -Xmx1024m -Dspring.profiles.active=prod"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
