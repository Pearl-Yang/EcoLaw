"""
绿法通AI服务主入口
"""
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.api.v1 import law_chat, material_gen, case_analysis, exam_generate, search, external_law
from app.core.config import settings

app = FastAPI(
    title="绿法通AI服务",
    description="白色污染治理AI智慧普法引擎 - 支持高级搜索 + 外部法律API",
    version="2.1.0",
    docs_url="/docs",
    redoc_url="/redoc"
)

# CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 注册路由
app.include_router(law_chat.router, prefix="/api/v1/law-chat", tags=["法律智能问答"])
app.include_router(material_gen.router, prefix="/api/v1/material", tags=["素材生成"])
app.include_router(case_analysis.router, prefix="/api/v1/case", tags=["案例拆解"])
app.include_router(exam_generate.router, prefix="/api/v1/exam", tags=["智能组卷"])
app.include_router(search.router, prefix="/api/v1/search", tags=["高级搜索"])
app.include_router(external_law.router, prefix="/api/v1/external-law", tags=["外部法律API"])


@app.get("/health")
async def health_check():
    return {"status": "healthy", "service": "ai-service"}


@app.get("/")
async def root():
    return {"message": "欢迎使用绿法通AI服务", "docs": "/docs"}


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8000,
        reload=True,
        workers=1
    )