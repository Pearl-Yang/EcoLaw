@REM 绿法通API服务启动脚本
@echo off
setlocal

set "PROJECT_DIR=%~dp0"
cd /d "%PROJECT_DIR%"

echo ==========================================
echo  绿法通API服务启动脚本
echo ==========================================

REM 检查Java环境
java -version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到Java环境，请先安装JDK 17+
    pause
    exit /b 1
)

REM 检查Maven
where mvn >nul 2>&1
if errorlevel 1 (
    echo [警告] 未检测到Maven，将使用mvnw
    set "MVN=mvnw.cmd"
) else (
    set "MVN=mvn"
)

REM 设置Spring Profile
set "SPRING_PROFILES_ACTIVE=dev"

REM 启动服务
echo.
echo 正在启动服务...
echo Profile: %SPRING_PROFILES_ACTIVE%
echo.

%MVN% spring-boot:run -Dspring-boot.run.profiles=%SPRING_PROFILES_ACTIVE%

if errorlevel 1 (
    echo.
    echo [错误] 服务启动失败
    pause
    exit /b 1
)

pause
