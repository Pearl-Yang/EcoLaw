@echo off
chcp 65001 >nul
REM =============================================
REM  绿法通智能搜索服务启动脚本 (Windows)
REM =============================================

echo ============================================
echo   绿法通智能搜索服务启动脚本
echo ============================================

set "SCRIPT_DIR=%~dp0"
set "PROJECT_ROOT=%SCRIPT_DIR%..\.."

REM 颜色定义
set "GREEN=[92m"
set "YELLOW=[93m"
set "RED=[91m"
set "NC=[0m"

REM 检查端口
echo.
echo [1/4] 检查端口状态...
netstat -an | find "3306" | find "LISTENING" >nul
if errorlevel 1 (
    echo   MySQL 端口 3306 未监听，请先启动 MySQL
) else (
    echo   MySQL 端口 3306 已就绪
)

REM 检查 Python AI 服务
netstat -an | find "8000" | find "LISTENING" >nul
if errorlevel 1 (
    echo   Python AI 服务未运行，尝试启动...
    cd /d "%PROJECT_ROOT%\services\ai-service"
    start /min cmd /c "python -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload"
    timeout /t 5 /nobreak >nul
) else (
    echo   Python AI 服务已在端口 8000 运行
)

REM 检查 Java 后端
netstat -an | find "8080" | find "LISTENING" >nul
if errorlevel 1 (
    echo   Java 后端服务未运行，尝试启动...
    cd /d "%PROJECT_ROOT%\services\api-server"
    start /min cmd /c "mvn spring-boot:run"
    timeout /t 10 /nobreak >nul
) else (
    echo   Java 后端服务已在端口 8080 运行
)

echo.
echo ============================================
echo   服务状态检查
echo ============================================
echo.

REM 检查 Python AI 健康状态
curl -s http://localhost:8000/health >nul 2>&1
if errorlevel 1 (
    echo   [ERROR] Python AI 服务未响应 (http://localhost:8000)
) else (
    echo   [OK] Python AI 服务运行正常
)

REM 检查 Java 后端健康状态
curl -s http://localhost:8080/api/health >nul 2>&1
if errorlevel 1 (
    echo   [WARNING] Java 后端可能未完全启动
) else (
    echo   [OK] Java 后端服务运行正常
)

echo.
echo ============================================
echo   服务访问地址
echo ============================================
echo.
echo   - MySQL:        localhost:3306
echo   - Python AI:    http://localhost:8000
echo   - API 文档:     http://localhost:8000/docs
echo   - Java 后端:    http://localhost:8080/api
echo   - Swagger UI:  http://localhost:8080/api/swagger-ui.html
echo.
echo   前端请访问: http://localhost:5173 (或配置的端口)
echo.
echo ============================================
echo   如需停止服务，请使用 stop-ai-services.bat
echo ============================================
pause