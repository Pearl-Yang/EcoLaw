"""
向量嵌入服务 - 使用 sentence-transformers 生成文本向量
支持多种模型，可选豆包API进行向量化
"""
from typing import List, Optional, Dict
import numpy as np
from sentence_transformers import SentenceTransformer
from app.core.config import settings

# 预训练的轻量级中文模型
DEFAULT_EMBEDDING_MODEL = "paraphrase-multilingual-MiniLM-L12-v2"


class EmbeddingService:
    """文本向量化服务"""
    
    def __init__(self, model_name: Optional[str] = None):
        """
        初始化向量化服务
        
        Args:
            model_name: 模型名称，默认使用多语言MiniLM模型
        """
        self.model_name = model_name or DEFAULT_EMBEDDING_MODEL
        self._model = None
        self._embedding_dim = None
    
    @property
    def model(self):
        """延迟加载模型"""
        if self._model is None:
            print(f"正在加载向量化模型: {self.model_name}")
            self._model = SentenceTransformer(self.model_name)
            self._embedding_dim = self._model.get_sentence_embedding_dimension()
            print(f"模型加载完成，向量维度: {self._embedding_dim}")
        return self._model
    
    @property
    def embedding_dim(self) -> int:
        """获取向量维度"""
        if self._embedding_dim is None:
            # 触发模型加载
            _ = self.model
        return self._embedding_dim
    
    def encode(self, texts: List[str], normalize: bool = True) -> np.ndarray:
        """
        将文本列表转换为向量
        
        Args:
            texts: 文本列表
            normalize: 是否归一化向量
        
        Returns:
            numpy.ndarray: 文本向量矩阵
        """
        if isinstance(texts, str):
            texts = [texts]
        
        embeddings = self.model.encode(
            texts,
            convert_to_numpy=True,
            normalize_embeddings=normalize,
            show_progress_bar=len(texts) > 10
        )
        
        return embeddings
    
    def encode_single(self, text: str, normalize: bool = True) -> np.ndarray:
        """
        将单个文本转换为向量
        
        Args:
            text: 单个文本
            normalize: 是否归一化
        
        Returns:
            numpy.ndarray: 文本向量
        """
        return self.encode([text], normalize)[0]
    
    def compute_similarity(self, text1: str, text2: str) -> float:
        """
        计算两个文本的相似度
        
        Args:
            text1: 文本1
            text2: 文本2
        
        Returns:
            float: 相似度分数 (0-1)
        """
        emb1 = self.encode_single(text1)
        emb2 = self.encode_single(text2)
        
        # 余弦相似度
        similarity = np.dot(emb1, emb2)
        return float(similarity)
    
    def batch_compute_similarity(self, query: str, texts: List[str]) -> List[float]:
        """
        批量计算文本与查询的相似度
        
        Args:
            query: 查询文本
            texts: 文本列表
        
        Returns:
            List[float]: 相似度分数列表
        """
        query_emb = self.encode_single(query)
        text_embs = self.encode(texts)
        
        # 批量计算余弦相似度
        similarities = np.dot(text_embs, query_emb).tolist()
        return similarities
    
    def search_similar(
        self, 
        query: str, 
        texts: List[str], 
        top_k: int = 5,
        min_score: float = 0.0
    ) -> List[Dict]:
        """
        在文本列表中搜索与查询最相似的文本
        
        Args:
            query: 查询文本
            texts: 待搜索文本列表
            top_k: 返回数量
            min_score: 最低相似度阈值
        
        Returns:
            List[Dict]: 搜索结果列表，按相似度降序
        """
        if not texts:
            return []
        
        # 计算相似度
        scores = self.batch_compute_similarity(query, texts)
        
        # 构建结果列表
        results = []
        for i, (text, score) in enumerate(zip(texts, scores)):
            if score >= min_score:
                results.append({
                    "index": i,
                    "text": text,
                    "score": round(float(score), 4)
                })
        
        # 按相似度降序排序
        results.sort(key=lambda x: x["score"], reverse=True)
        
        return results[:top_k]
    
    def get_embedding_stats(self) -> Dict:
        """获取向量化服务状态"""
        return {
            "model_name": self.model_name,
            "embedding_dim": self.embedding_dim,
            "model_loaded": self._model is not None
        }


class HybridEmbeddingService(EmbeddingService):
    """
    混合向量化服务
    支持本地模型和远程API两种方式
    """
    
    def __init__(self, model_name: Optional[str] = None, use_remote: bool = False):
        super().__init__(model_name)
        self.use_remote = use_remote
    
    def encode_with_remote(self, texts: List[str]) -> np.ndarray:
        """
        使用远程API进行向量化
        可以接入豆包或其他支持embeddings的API
        
        Returns:
            numpy.ndarray: 文本向量矩阵
        """
        # TODO: 接入豆包embedding API
        # 目前使用本地模型作为fallback
        print("远程API未配置，使用本地模型")
        return self.encode(texts)
    
    def encode(self, texts: List[str], normalize: bool = True) -> np.ndarray:
        """重写encode方法，支持混合模式"""
        if self.use_remote:
            return self.encode_with_remote(texts)
        return super().encode(texts, normalize)


# 全局向量化服务实例
embedding_service = EmbeddingService()
hybrid_embedding_service = HybridEmbeddingService()
