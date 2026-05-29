"""
高级搜索 API - 提供统一的搜索入口
"""
from fastapi import APIRouter, HTTPException, Query
from pydantic import BaseModel, Field
from typing import Optional, List, Dict

from app.services.advanced_search import advanced_search_service, SearchStrategy

router = APIRouter()


class AdvancedSearchRequest(BaseModel):
    """高级搜索请求"""
    query: str = Field(..., description="搜索查询")
    sources: List[str] = Field(default=["law", "news"], description="搜索来源: law, news, comment")
    strategy: str = Field(default="hybrid", description="搜索策略: fulltext, vector, keyword, hybrid")
    top_k: int = Field(default=10, description="返回数量")
    min_score: float = Field(default=0.1, description="最低分数阈值")
    level: Optional[str] = Field(None, description="法规层级: national, provincial, city")


class SearchResultResponse(BaseModel):
    """搜索结果响应"""
    id: str
    title: str
    content: str
    source: str
    relevance_score: float
    metadata: Dict


class AdvancedSearchResponse(BaseModel):
    """高级搜索响应"""
    query: str
    total: int
    results: List[SearchResultResponse]
    suggestions: List[str]
    search_time_ms: int
    strategy: str


@router.post("/search", response_model=AdvancedSearchResponse)
async def advanced_search(request: AdvancedSearchRequest):
    """
    高级搜索API
    
    支持多种搜索策略：
    - fulltext: MySQL全文检索
    - vector: 向量语义检索
    - keyword: 关键词匹配
    - hybrid: 混合搜索（推荐）
    
    特点：
    - 多源检索：法规、新闻、评论
    - 智能排序：基于多策略融合
    - 语义理解：支持同义词、上下文
    """
    import time
    start_time = time.time()
    
    try:
        # 映射策略
        strategy_map = {
            "fulltext": SearchStrategy.FULLTEXT,
            "vector": SearchStrategy.VECTOR,
            "keyword": SearchStrategy.KEYWORD,
            "hybrid": SearchStrategy.HYBRID
        }
        strategy = strategy_map.get(request.strategy, SearchStrategy.HYBRID)
        
        # 执行搜索
        results = advanced_search_service.search(
            query=request.query,
            sources=request.sources,
            strategy=strategy,
            top_k=request.top_k,
            min_score=request.min_score,
            level=request.level
        )
        
        # 获取搜索建议
        suggestions = advanced_search_service.get_search_suggestions(request.query)
        
        search_time_ms = int((time.time() - start_time) * 1000)
        
        return AdvancedSearchResponse(
            query=request.query,
            total=len(results),
            results=[
                SearchResultResponse(
                    id=r.id,
                    title=r.title,
                    content=r.content[:200] + "..." if len(r.content) > 200 else r.content,
                    source=r.source,
                    relevance_score=r.relevance_score,
                    metadata=r.metadata
                )
                for r in results
            ],
            suggestions=suggestions,
            search_time_ms=search_time_ms,
            strategy=request.strategy
        )
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"搜索失败: {str(e)}")


@router.get("/suggest")
async def search_suggestions(q: str = Query(..., description="搜索前缀")):
    """
    搜索建议/自动补全
    
    返回与输入相关的搜索建议
    """
    try:
        suggestions = advanced_search_service.get_search_suggestions(q)
        return {"suggestions": suggestions}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"获取建议失败: {str(e)}")


@router.post("/rerank")
async def rerank_search(
    results: List[Dict],
    rerank_keyword: Optional[str] = None
):
    """
    搜索结果重排序
    
    根据用户反馈（如点击、停留时间）重新排序结果
    """
    try:
        from app.services.advanced_search import SearchResult
        
        # 转换为SearchResult对象
        search_results = []
        for r in results:
            search_results.append(SearchResult(
                id=r.get("id", ""),
                title=r.get("title", ""),
                content=r.get("content", ""),
                source=r.get("source", ""),
                relevance_score=r.get("relevance_score", 0.5),
                metadata=r.get("metadata", {})
            ))
        
        # 重排序
        reranked = advanced_search_service.rerank_results(search_results, rerank_keyword)
        
        # 转换回字典
        return {
            "results": [
                {
                    "id": r.id,
                    "title": r.title,
                    "content": r.content,
                    "source": r.source,
                    "relevance_score": r.relevance_score
                }
                for r in reranked
            ]
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"重排序失败: {str(e)}")


@router.get("/stats")
async def search_stats():
    """
    获取搜索服务状态
    """
    try:
        embedding_stats = advanced_search_service.embedding_service.get_embedding_stats()
        
        return {
            "embedding": embedding_stats,
            "weights": {
                "fulltext": advanced_search_service.weights[SearchStrategy.FULLTEXT],
                "vector": advanced_search_service.weights[SearchStrategy.VECTOR],
                "keyword": advanced_search_service.weights[SearchStrategy.KEYWORD]
            }
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"获取状态失败: {str(e)}")
