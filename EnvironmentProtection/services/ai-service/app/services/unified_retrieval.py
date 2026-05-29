"""
统一法律检索服务 - 整合内部检索和外部API
"""
from typing import List, Dict, Optional
from dataclasses import dataclass

from app.services.law_retrieval import law_retrieval_service
from app.services.embedding_service import embedding_service
from app.client.external_law_client import external_law_client
from app.core.config import settings


@dataclass
class UnifiedSearchResult:
    """统一检索结果"""
    id: str
    title: str
    content: str
    source: str  # internal / external
    relevance_score: float
    law_level: str = ""
    category: str = ""
    law_id: str = ""
    url: str = ""
    metadata: Dict = None
    
    def __post_init__(self):
        if self.metadata is None:
            self.metadata = {}


class UnifiedLawRetrievalService:
    """
    统一法律检索服务
    
    整合多种检索来源：
    1. 内部MySQL全文检索
    2. 内部向量检索
    3. 外部法律语义API
    """
    
    def __init__(self):
        self.external_enabled = settings.EXTERNAL_LAW_API_ENABLED
        self.external_client = external_law_client
        self.embedding_service = embedding_service
    
    def search(
        self,
        query: str,
        top_k: int = 5,
        include_external: bool = True,
        external_only: bool = False
    ) -> List[Dict]:
        """
        统一检索
        
        Args:
            query: 查询文本
            top_k: 返回数量
            include_external: 是否包含外部检索结果
            external_only: 是否仅使用外部检索
        
        Returns:
            检索结果列表
        """
        all_results = []
        
        # 1. 内部全文检索（如果不禁用）
        if not external_only:
            try:
                internal_results = law_retrieval_service.retrieve_relevant_laws(query, top_k=top_k)
                for law in internal_results:
                    all_results.append({
                        "id": law.get("id", ""),
                        "title": law.get("title", ""),
                        "content": law.get("content_preview", ""),
                        "source": "internal",
                        "relevance_score": law.get("relevance", 0.5),
                        "law_level": law.get("level", ""),
                        "category": law.get("category", "")
                    })
            except Exception as e:
                print(f"内部检索失败: {e}")
        
        # 2. 外部法律API检索（如果启用）
        if include_external and self.external_enabled:
            try:
                external_results = self.external_client.vector_search(query, top_k=top_k)
                for law in external_results:
                    all_results.append({
                        "id": law.get("law_id", ""),
                        "title": law.get("title", ""),
                        "content": law.get("content", "")[:500],
                        "source": "external",
                        "relevance_score": law.get("score", 0.5),
                        "law_level": law.get("level", ""),
                        "category": law.get("category", ""),
                        "url": law.get("url", "")
                    })
            except Exception as e:
                print(f"外部检索失败: {e}")
        
        # 3. 去重和排序
        results = self._deduplicate_and_rank(all_results, top_k)
        
        return results
    
    def _deduplicate_and_rank(
        self,
        results: List[Dict],
        top_k: int
    ) -> List[Dict]:
        """去重并排序"""
        # 按ID去重，保留分数最高的
        seen = {}
        for r in results:
            key = r.get("id", "") or r.get("title", "")
            if key not in seen or r["relevance_score"] > seen[key]["relevance_score"]:
                seen[key] = r
        
        # 按相关度排序
        sorted_results = sorted(seen.values(), 
                              key=lambda x: x["relevance_score"], 
                              reverse=True)
        
        return sorted_results[:top_k]
    
    def semantic_search(
        self,
        query: str,
        law_ids: List[str] = None,
        top_k: int = 5
    ) -> Dict:
        """
        语义检索（使用外部API）
        
        Args:
            query: 查询文本
            law_ids: 指定检索的法规ID
            top_k: 返回数量
        
        Returns:
            语义检索结果
        """
        if not self.external_enabled:
            return {"error": "外部API未启用", "data": []}
        
        try:
            result = self.external_client.semantic_query(query, law_ids)
            return result
        except Exception as e:
            return {"error": str(e), "data": []}
    
    def get_external_quota(self) -> Dict:
        """获取外部API配额"""
        if not self.external_enabled:
            return {"enabled": False}
        
        try:
            quota = self.external_client.get_quota()
            return quota
        except Exception as e:
            return {"enabled": True, "error": str(e)}
    
    def check_external_health(self) -> bool:
        """检查外部API健康状态"""
        if not self.external_enabled:
            return False
        return self.external_client.health_check()


# 全局实例
unified_law_service = UnifiedLawRetrievalService()
