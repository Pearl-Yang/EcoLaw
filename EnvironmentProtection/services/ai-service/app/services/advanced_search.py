"""
高级搜索服务 - 实现多种搜索策略的混合搜索
支持：全文检索、向量检索、关键词匹配、重排序
"""
from typing import List, Dict, Optional, Tuple
from dataclasses import dataclass
from enum import Enum
import numpy as np
import re

from app.services.law_retrieval import law_retrieval_service
from app.services.embedding_service import embedding_service


class SearchStrategy(Enum):
    """搜索策略枚举"""
    FULLTEXT = "fulltext"           # 全文检索
    VECTOR = "vector"               # 向量检索
    KEYWORD = "keyword"             # 关键词匹配
    HYBRID = "hybrid"              # 混合搜索


@dataclass
class SearchResult:
    """搜索结果数据类"""
    id: str
    title: str
    content: str
    source: str  # law, news, comment
    relevance_score: float = 0.0
    fulltext_score: float = 0.0
    vector_score: float = 0.0
    keyword_score: float = 0.0
    metadata: Dict = None
    
    def __post_init__(self):
        if self.metadata is None:
            self.metadata = {}


class AdvancedSearchService:
    """
    高级搜索服务
    
    支持的搜索策略：
    1. 全文检索 (MySQL FULLTEXT)
    2. 向量检索 (Sentence-Transformers)
    3. 关键词匹配 (BM25算法)
    4. 混合搜索 (RRF融合)
    """
    
    def __init__(self):
        self.embedding_service = embedding_service
        
        # 搜索权重配置
        self.weights = {
            SearchStrategy.FULLTEXT: 0.35,
            SearchStrategy.VECTOR: 0.40,
            SearchStrategy.KEYWORD: 0.25
        }
        
        # RRF参数 (Reciprocal Rank Fusion)
        self.rrf_k = 60  # RRF融合参数
    
    def set_weights(self, fulltext: float = 0.35, vector: float = 0.40, keyword: float = 0.25):
        """设置各搜索策略的权重"""
        total = fulltext + vector + keyword
        if abs(total - 1.0) > 0.001:
            raise ValueError(f"权重总和必须为1.0，当前为{total}")
        self.weights[SearchStrategy.FULLTEXT] = fulltext
        self.weights[SearchStrategy.VECTOR] = vector
        self.weights[SearchStrategy.KEYWORD] = keyword
    
    def search(
        self,
        query: str,
        sources: List[str] = None,
        strategy: SearchStrategy = SearchStrategy.HYBRID,
        top_k: int = 10,
        min_score: float = 0.1,
        level: Optional[str] = None
    ) -> List[SearchResult]:
        """
        执行高级搜索
        
        Args:
            query: 用户查询
            sources: 搜索来源 ['law', 'news', 'comment']
            strategy: 搜索策略
            top_k: 返回数量
            min_score: 最低分数阈值
            level: 法规层级筛选
        
        Returns:
            List[SearchResult]: 排序后的搜索结果
        """
        if sources is None:
            sources = ["law", "news"]
        
        all_results = []
        
        # 1. 全文检索
        if strategy in [SearchStrategy.FULLTEXT, SearchStrategy.HYBRID]:
            ft_results = self._search_fulltext(query, sources, level)
            all_results.extend(ft_results)
        
        # 2. 向量检索
        if strategy in [SearchStrategy.VECTOR, SearchStrategy.HYBRID]:
            vec_results = self._search_vector(query, sources, level)
            all_results.extend(vec_results)
        
        # 3. 关键词匹配
        if strategy in [SearchStrategy.KEYWORD, SearchStrategy.HYBRID]:
            kw_results = self._search_keyword(query, sources, level)
            all_results.extend(kw_results)
        
        # 4. 融合去重和重排序
        fused_results = self._fuse_and_rank(all_results, strategy, top_k, min_score)
        
        return fused_results
    
    def _search_fulltext(
        self, 
        query: str, 
        sources: List[str],
        level: Optional[str] = None
    ) -> List[SearchResult]:
        """全文检索"""
        results = []
        
        for source in sources:
            if source == "law":
                laws = law_retrieval_service.retrieve_relevant_laws(query, top_k=20, level=level)
                for law in laws:
                    results.append(SearchResult(
                        id=law.get("id", ""),
                        title=law.get("title", ""),
                        content=law.get("content_preview", ""),
                        source="law",
                        fulltext_score=law.get("relevance", 0.5),
                        metadata={
                            "level": law.get("level", ""),
                            "category": law.get("category", ""),
                            "effective_date": law.get("effective_date", "")
                        }
                    ))
            elif source == "news":
                news_list = law_retrieval_service.retrieve_relevant_news(query, top_k=10)
                for news in news_list:
                    results.append(SearchResult(
                        id=news.get("id", ""),
                        title=news.get("title", ""),
                        content=news.get("summary", ""),
                        source="news",
                        fulltext_score=news.get("relevance", 0.5),
                        metadata={
                            "type": news.get("type", ""),
                            "source": news.get("source", ""),
                            "view_count": news.get("view_count", 0)
                        }
                    ))
        
        return results
    
    def _search_vector(
        self, 
        query: str, 
        sources: List[str],
        level: Optional[str] = None
    ) -> List[SearchResult]:
        """向量检索"""
        results = []
        
        # 获取要检索的文本
        texts_to_search = []
        source_map = {}  # 记录文本索引到来源的映射
        
        for source in sources:
            if source == "law":
                laws = law_retrieval_service.retrieve_relevant_laws(query, top_k=50, level=level)
                for law in laws:
                    text = f"{law.get('title', '')} {law.get('content_preview', '')}"
                    idx = len(texts_to_search)
                    texts_to_search.append(text)
                    source_map[idx] = ("law", law)
                    
            elif source == "news":
                news_list = law_retrieval_service.retrieve_relevant_news(query, top_k=30)
                for news in news_list:
                    text = f"{news.get('title', '')} {news.get('summary', '')}"
                    idx = len(texts_to_search)
                    texts_to_search.append(text)
                    source_map[idx] = ("news", news)
        
        if not texts_to_search:
            return results
        
        # 计算向量相似度
        try:
            similarities = self.embedding_service.batch_compute_similarity(query, texts_to_search)
            
            # 构建结果
            for idx, score in enumerate(similarities):
                if idx in source_map:
                    src, data = source_map[idx]
                    
                    if src == "law":
                        results.append(SearchResult(
                            id=data.get("id", ""),
                            title=data.get("title", ""),
                            content=data.get("content_preview", ""),
                            source="law",
                            vector_score=float(score),
                            metadata={
                                "level": data.get("level", ""),
                                "category": data.get("category", "")
                            }
                        ))
                    elif src == "news":
                        results.append(SearchResult(
                            id=data.get("id", ""),
                            title=data.get("title", ""),
                            content=data.get("summary", ""),
                            source="news",
                            vector_score=float(score),
                            metadata={
                                "type": data.get("type", ""),
                                "source": data.get("source", "")
                            }
                        ))
        except Exception as e:
            print(f"向量检索失败: {e}")
        
        return results
    
    def _search_keyword(
        self, 
        query: str, 
        sources: List[str],
        level: Optional[str] = None
    ) -> List[SearchResult]:
        """关键词匹配 (BM25-like)"""
        results = []
        
        # 提取查询关键词
        keywords = self._extract_keywords(query)
        
        for source in sources:
            if source == "law":
                laws = law_retrieval_service.retrieve_by_keyword(keywords, top_k=20)
                for law in laws:
                    # 计算关键词命中分数
                    content = f"{law.get('title', '')} {law.get('content_preview', '')}"
                    hit_score = self._calculate_keyword_score(query, content, keywords)
                    
                    results.append(SearchResult(
                        id=law.get("id", ""),
                        title=law.get("title", ""),
                        content=law.get("content_preview", ""),
                        source="law",
                        keyword_score=hit_score,
                        metadata={
                            "level": law.get("level", ""),
                            "category": law.get("category", "")
                        }
                    ))
        
        return results
    
    def _extract_keywords(self, query: str) -> List[str]:
        """提取查询关键词"""
        # 简单分词 + 停用词过滤
        stop_words = {"的", "了", "是", "在", "和", "与", "及", "或", "有", "什么", "如何", "怎样", "吗", "呢"}
        
        # 简单按空格和标点分词
        words = re.split(r'[\s,，。、！？；：""''（）《》]+', query)
        keywords = [w.strip() for w in words if len(w.strip()) >= 2 and w.strip() not in stop_words]
        
        return keywords
    
    def _calculate_keyword_score(
        self, 
        query: str, 
        content: str, 
        keywords: List[str]
    ) -> float:
        """计算关键词匹配分数"""
        if not keywords:
            return 0.0
        
        content_lower = content.lower()
        query_lower = query.lower()
        
        # 1. 查询词完整匹配
        exact_matches = query_lower in content_lower
        if exact_matches:
            return 1.0
        
        # 2. 关键词命中
        hit_count = sum(1 for kw in keywords if kw.lower() in content_lower)
        keyword_score = hit_count / len(keywords) if keywords else 0
        
        # 3. 标题命中加权
        title_bonus = 0.2 if any(kw.lower() in content[:50].lower() for kw in keywords) else 0
        
        return min(keyword_score + title_bonus, 1.0)
    
    def _fuse_and_rank(
        self,
        results: List[SearchResult],
        strategy: SearchStrategy,
        top_k: int,
        min_score: float
    ) -> List[SearchResult]:
        """融合多个搜索结果并进行排序"""
        
        if not results:
            return []
        
        # 按ID去重，保留分数最高的结果
        deduped = {}
        for r in results:
            key = f"{r.source}:{r.id}"
            if key not in deduped:
                deduped[key] = r
            else:
                # 更新各策略分数
                existing = deduped[key]
                existing.fulltext_score = max(existing.fulltext_score, r.fulltext_score)
                existing.vector_score = max(existing.vector_score, r.vector_score)
                existing.keyword_score = max(existing.keyword_score, r.keyword_score)
        
        fused_results = list(deduped.values())
        
        if strategy == SearchStrategy.HYBRID:
            # 混合搜索：计算加权分数
            for r in fused_results:
                r.relevance_score = (
                    r.fulltext_score * self.weights[SearchStrategy.FULLTEXT] +
                    r.vector_score * self.weights[SearchStrategy.VECTOR] +
                    r.keyword_score * self.weights[SearchStrategy.KEYWORD]
                )
        elif strategy == SearchStrategy.FULLTEXT:
            for r in fused_results:
                r.relevance_score = r.fulltext_score
        elif strategy == SearchStrategy.VECTOR:
            for r in fused_results:
                r.relevance_score = r.vector_score
        elif strategy == SearchStrategy.KEYWORD:
            for r in fused_results:
                r.relevance_score = r.keyword_score
        
        # 按相关度降序排序
        fused_results.sort(key=lambda x: x.relevance_score, reverse=True)
        
        # 过滤低分结果
        filtered = [r for r in fused_results if r.relevance_score >= min_score]
        
        return filtered[:top_k]
    
    def rerank_results(
        self,
        results: List[SearchResult],
        rerank_keyword: Optional[str] = None
    ) -> List[SearchResult]:
        """
        对搜索结果进行重排序
        
        Args:
            results: 原始搜索结果
            rerank_keyword: 重排序关键词（如用户点击的词）
        
        Returns:
            List[SearchResult]: 重排序后的结果
        """
        if not results:
            return []
        
        if rerank_keyword:
            # 基于点击反馈重排序
            for r in results:
                content = f"{r.title} {r.content}"
                if rerank_keyword.lower() in content.lower():
                    r.relevance_score *= 1.2  # 提升包含点击词的结果
        
        # 再次排序
        results.sort(key=lambda x: x.relevance_score, reverse=True)
        return results
    
    def get_search_suggestions(self, query: str) -> List[str]:
        """
        获取搜索建议
        
        Args:
            query: 用户输入
        
        Returns:
            List[str]: 建议列表
        """
        suggestions = []
        
        # 基于法规标题生成建议
        try:
            laws = law_retrieval_service.retrieve_relevant_laws(query, top_k=3)
            for law in laws:
                if law.get("title"):
                    suggestions.append(law["title"])
        except:
            pass
        
        # 添加常见搜索词
        common_terms = ["塑料袋", "塑料污染", "禁塑令", "可降解", "白色污染"]
        for term in common_terms:
            if term not in suggestions and term in query:
                suggestions.append(term)
        
        return suggestions[:5]


# 全局实例
advanced_search_service = AdvancedSearchService()
