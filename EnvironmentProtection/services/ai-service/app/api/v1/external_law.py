"""
外部法律API路由
"""
from fastapi import APIRouter, HTTPException, Query
from pydantic import BaseModel, Field
from typing import Optional, List, Dict

from app.client.external_law_client import external_law_client
from app.services.unified_retrieval import unified_law_service

router = APIRouter()


class ExternalSearchRequest(BaseModel):
    """外部API搜索请求"""
    query: str = Field(..., description="查询文本")
    top_k: int = Field(default=5, description="返回数量")
    search_type: str = Field(default="vector", description="搜索类型: vector/text/semantic")


class ExternalSearchResponse(BaseModel):
    """外部API搜索响应"""
    results: List[Dict]
    quota: Dict
    source: str


@router.post("/search", response_model=ExternalSearchResponse)
async def external_search(request: ExternalSearchRequest):
    """
    使用外部法律API进行检索
    
    支持三种搜索类型:
    - vector: 向量语义检索
    - text: 正文全文检索
    - semantic: 综合语义检索
    """
    results = []
    
    try:
        if request.search_type == "vector":
            results = external_law_client.vector_search(request.query, request.top_k)
        elif request.search_type == "text":
            results = external_law_client.text_search(request.query)
        elif request.search_type == "semantic":
            result = external_law_client.semantic_query(request.query)
            results = result.get("data", [])
        
        # 获取配额信息
        quota = unified_law_service.get_external_quota()
        
        return ExternalSearchResponse(
            results=results,
            quota=quota,
            source="external_api"
        )
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"外部API调用失败: {str(e)}")


@router.get("/quota")
async def get_quota():
    """
    获取外部API配额使用情况
    """
    quota = unified_law_service.get_external_quota()
    return quota


@router.get("/health")
async def check_health():
    """
    检查外部API健康状态
    """
    healthy = unified_law_service.check_external_health()
    return {
        "healthy": healthy,
        "external_api_enabled": unified_law_service.external_enabled
    }


@router.post("/unified-search")
async def unified_search(
    query: str = Query(..., description="查询文本"),
    top_k: int = Query(5, description="返回数量"),
    include_external: bool = Query(True, description="包含外部检索"),
    external_only: bool = Query(False, description="仅使用外部检索")
):
    """
    统一检索 - 整合内部和外部检索
    
    自动融合多种检索结果，返回最优排序
    """
    try:
        results = unified_law_service.search(
            query=query,
            top_k=top_k,
            include_external=include_external,
            external_only=external_only
        )
        
        # 统计来源
        internal_count = sum(1 for r in results if r.get("source") == "internal")
        external_count = sum(1 for r in results if r.get("source") == "external")
        
        return {
            "total": len(results),
            "results": results,
            "stats": {
                "internal": internal_count,
                "external": external_count
            }
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"统一检索失败: {str(e)}")
