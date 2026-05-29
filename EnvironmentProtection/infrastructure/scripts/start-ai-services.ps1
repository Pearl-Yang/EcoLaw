# =============================================
# 绿法通智能搜索服务启动脚本 (PowerShell)
# =============================================

$ErrorActionPreference = "Continue"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  绿法通智能搜索服务启动脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# 项目根目录
$PROJECT_ROOT = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)

# 检查端口函数
function Test-Port {
    param($Port)
    $result = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue
    return $null -ne $result
}

# 检查并启动 MySQL
Write-Host ""
Write-Host "[1/4] 检查 MySQL 服务..." -ForegroundColor Yellow
if (Test-Port -Port 3306) {
    Write-Host "  [OK] MySQL 端口 3306 已在运行" -ForegroundColor Green
} else {
    Write-Host "  [WARNING] MySQL 未运行，请确保本地 MySQL 已启动" -ForegroundColor Yellow
}

# 执行 FULLTEXT 索引修复
Write-Host ""
Write-Host "[2/4] 检查 FULLTEXT 索引..." -ForegroundColor Yellow
$mysqlFixScript = Join-Path $PROJECT_ROOT "infrastructure\database\mysql\fix_fulltext_indexes.sql"
if (Test-Path $mysqlFixScript) {
    Write-Host "  [INFO] 索引修复脚本已就绪" -ForegroundColor Cyan
    Write-Host "  提示: 请手动执行以下命令修复索引:" -ForegroundColor Yellow
    Write-Host "    mysql -u root -p123456 lvfat < infrastructure\database\mysql\fix_fulltext_indexes.sql" -ForegroundColor White
} else {
    Write-Host "  [INFO] 使用 init.sql 自动创建索引" -ForegroundColor Cyan
}

# 检查并启动 Python AI 服务
Write-Host ""
Write-Host "[3/4] 检查 Python AI 服务..." -ForegroundColor Yellow
if (Test-Port -Port 8000) {
    Write-Host "  [OK] Python AI 服务已在端口 8000 运行" -ForegroundColor Green
} else {
    Write-Host "  [INFO] 正在启动 Python AI 服务..." -ForegroundColor Cyan
    $aiServicePath = Join-Path $PROJECT_ROOT "services\ai-service"
    try {
        $process = Start-Process -FilePath "python" -ArgumentList "-m uvicorn main:app --host 0.0.0.0 --port 8000 --reload" -WorkingDirectory $aiServicePath -PassThru -NoNewWindow
        Write-Host "  [OK] Python AI 服务已启动 (PID: $($process.Id))" -ForegroundColor Green
        Start-Sleep -Seconds 3
        
        # 检查健康状态
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:8000/health" -UseBasicParsing -TimeoutSec 5 -ErrorAction Stop
            if ($response.StatusCode -eq 200) {
                Write-Host "  [OK] 健康检查通过" -ForegroundColor Green
            }
        } catch {
            Write-Host "  [INFO] 服务启动中，请稍后访问 http://localhost:8000" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "  [ERROR] 启动失败: $_" -ForegroundColor Red
    }
}

# 检查并启动 Java 后端
Write-Host ""
Write-Host "[4/4] 检查 Java 后端服务..." -ForegroundColor Yellow
if (Test-Port -Port 8080) {
    Write-Host "  [OK] Java 后端服务已在端口 8080 运行" -ForegroundColor Green
} else {
    Write-Host "  [INFO] 正在启动 Java 后端..." -ForegroundColor Cyan
    Write-Host "  提示: 请手动启动 Java 后端服务" -ForegroundColor Yellow
    Write-Host "    cd services\api-server" -ForegroundColor White
    Write-Host "    mvn spring-boot:run" -ForegroundColor White
}

# 显示服务状态汇总
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  服务状态汇总" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$status = @()

# MySQL
if (Test-Port -Port 3306) {
    Write-Host "  [OK] MySQL       localhost:3306" -ForegroundColor Green
    $status += "MySQL"
} else {
    Write-Host "  [--] MySQL       localhost:3306 (未运行)" -ForegroundColor Gray
}

# Python AI
if (Test-Port -Port 8000) {
    Write-Host "  [OK] Python AI  http://localhost:8000" -ForegroundColor Green
    $status += "PythonAI"
} else {
    Write-Host "  [--] Python AI  http://localhost:8000 (未运行)" -ForegroundColor Gray
}

# Java
if (Test-Port -Port 8080) {
    Write-Host "  [OK] Java 后端   http://localhost:8080/api" -ForegroundColor Green
    $status += "Java"
} else {
    Write-Host "  [--] Java 后端   http://localhost:8080/api (未运行)" -ForegroundColor Gray
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  快速测试链接" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "  Python AI API 文档: http://localhost:8000/docs" -ForegroundColor White
Write-Host "  Swagger UI:          http://localhost:8080/api/swagger-ui.html" -ForegroundColor White
Write-Host "  前端 (需启动):       http://localhost:5173" -ForegroundColor White
Write-Host ""

# 如果所有服务都运行，进行完整测试
if ($status.Count -ge 2) {
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "  快速功能测试" -ForegroundColor Cyan
    Write-Host "========================================" -ForegroundColor Cyan
    
    # 测试 Python AI 服务
    try {
        Write-Host ""
        Write-Host "测试 RAG 法律检索..." -ForegroundColor Yellow
        
        $body = @{
            question = "企业违规倾倒废塑料怎么处理？"
            role = "common"
            top_k = 3
            enable_rag = $true
        } | ConvertTo-Json
        
        $response = Invoke-RestMethod -Uri "http://localhost:8000/api/v1/law-chat/chat" `
            -Method Post `
            -ContentType "application/json" `
            -Body $body `
            -TimeoutSec 30 `
            -ErrorAction Stop
        
        Write-Host ""
        Write-Host "  [OK] 测试成功！" -ForegroundColor Green
        Write-Host "  返回内容: $($response | ConvertTo-Json -Depth 3)" -ForegroundColor White
    } catch {
        Write-Host "  [INFO] 测试请求失败（可能服务还在启动）: $_" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  启动完成！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "提示: 如需停止所有服务，请关闭对应进程窗口或使用任务管理器" -ForegroundColor Gray
Write-Host ""