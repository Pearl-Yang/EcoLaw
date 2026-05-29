"""
外部法律语义查询接口客户端
服务: 法律精准查询接口 - 标准版 - 试用版 (law-star.com)

配置信息:
- appid: zTX-8571-189D7BD9b9c4364
- app_secret: 8E2756d1054336E7d3da2D9e6a90C401
- 开通时间: 2026-04-19
- 到期时间: 2026-05-19
- 向量查询: 300次
- 正文请求: 50篇
"""
import hashlib
import time
import requests
from typing import List, Dict, Optional
from app.core.config import settings


class ExternalLawSearchClient:
    """
    外部法律语义查询API客户端
    
    支持功能:
    - 向量语义检索
    - 正文全文检索
    - 语义相似度匹配
    """
    
    def __init__(self, appid: str = None, app_secret: str = None):
        """
        初始化外部API客户端
        
        Args:
            appid: 应用ID
            app_secret: 应用密钥
        """
        # 从配置或直接传入
        self.appid = appid or settings.EXTERNAL_APP_ID
        self.app_secret = app_secret or settings.EXTERNAL_APP_SECRET
        
        # API基础地址（需要用户提供实际地址）
        self.base_url = settings.EXTERNAL_LAW_API_URL
        
        # 配额限制
        self.vector_limit = settings.EXTERNAL_VECTOR_LIMIT  # 向量查询次数
        self.text_limit = settings.EXTERNAL_TEXT_LIMIT     # 正文请求次数
        
        # 请求头
        self.headers = {
            "Content-Type": "application/json",
            "appid": self.appid
        }
    
    def _generate_sign(self, timestamp: int) -> str:
        """
        生成签名
        
        Args:
            timestamp: 时间戳
        
        Returns:
            签名字符串
        """
        sign_str = f"{self.appid}{self.app_secret}{timestamp}"
        return hashlib.md5(sign_str.encode()).hexdigest()
    
    def vector_search(self, query: str, top_k: int = 5) -> List[Dict]:
        """
        向量语义检索
        
        Args:
            query: 查询文本
            top_k: 返回数量
        
        Returns:
            检索结果列表
        """
        timestamp = int(time.time())
        sign = self._generate_sign(timestamp)
        
        payload = {
            "appid": self.appid,
            "timestamp": timestamp,
            "sign": sign,
            "query": query,
            "top_k": top_k
        }
        
        try:
            response = requests.post(
                f"{self.base_url}/vector/search",
                json=payload,
                headers=self.headers,
                timeout=30
            )
            response.raise_for_status()
            return response.json().get("data", [])
        except requests.RequestException as e:
            print(f"向量检索失败: {e}")
            return []
    
    def text_search(self, query: str, filters: Dict = None) -> List[Dict]:
        """
        正文全文检索
        
        Args:
            query: 查询文本
            filters: 筛选条件
        
        Returns:
            检索结果列表
        """
        timestamp = int(time.time())
        sign = self._generate_sign(timestamp)
        
        payload = {
            "appid": self.appid,
            "timestamp": timestamp,
            "sign": sign,
            "query": query,
            "filters": filters or {}
        }
        
        try:
            response = requests.post(
                f"{self.base_url}/text/search",
                json=payload,
                headers=self.headers,
                timeout=30
            )
            response.raise_for_status()
            return response.json().get("data", [])
        except requests.RequestException as e:
            print(f"正文检索失败: {e}")
            return []
    
    def semantic_query(self, query: str, law_ids: List[str] = None) -> Dict:
        """
        语义查询 - 综合检索
        
        Args:
            query: 查询文本
            law_ids: 指定检索的法规ID列表
        
        Returns:
            包含检索结果和相关信息的字典
        """
        timestamp = int(time.time())
        sign = self._generate_sign(timestamp)
        
        payload = {
            "appid": self.appid,
            "timestamp": timestamp,
            "sign": sign,
            "query": query,
            "law_ids": law_ids,
            "include_similar": True,
            "min_score": 0.5
        }
        
        try:
            response = requests.post(
                f"{self.base_url}/semantic/query",
                json=payload,
                headers=self.headers,
                timeout=60
            )
            response.raise_for_status()
            return response.json()
        except requests.RequestException as e:
            print(f"语义查询失败: {e}")
            return {"error": str(e), "data": []}
    
    def get_quota(self) -> Dict:
        """
        获取API配额使用情况
        
        Returns:
            配额信息
        """
        timestamp = int(time.time())
        sign = self._generate_sign(timestamp)
        
        payload = {
            "appid": self.appid,
            "timestamp": timestamp,
            "sign": sign
        }
        
        try:
            response = requests.post(
                f"{self.base_url}/quota",
                json=payload,
                headers=self.headers,
                timeout=10
            )
            response.raise_for_status()
            return response.json()
        except requests.RequestException as e:
            return {"error": str(e)}
    
    def health_check(self) -> bool:
        """
        检查服务健康状态
        
        Returns:
            是否正常
        """
        try:
            response = requests.get(
                f"{self.base_url}/health",
                timeout=5
            )
            return response.status_code == 200
        except:
            return False


# 全局实例（需要配置后使用）
external_law_client = ExternalLawSearchClient()
