"""
配置管理
"""
from pydantic_settings import BaseSettings
from typing import List
import os


class Settings(BaseSettings):
    # 应用配置
    APP_NAME: str = "绿法通AI服务"
    DEBUG: bool = True
    
    # 服务端口
    HOST: str = "0.0.0.0"
    PORT: int = 8000
    
    # LLM配置 (OpenAI兼容)
    LLM_API_KEY: str = os.getenv("LLM_API_KEY", "")
    LLM_BASE_URL: str = os.getenv("LLM_BASE_URL", "https://api.openai.com/v1")
    LLM_MODEL: str = os.getenv("LLM_MODEL", "gpt-4o")
    LLM_TEMPERATURE: float = 0.7
    LLM_MAX_TOKENS: int = 2000
    
    # MySQL数据库配置 (用于RAG检索)
    MYSQL_HOST: str = os.getenv("MYSQL_HOST", "localhost")
    MYSQL_PORT: int = int(os.getenv("MYSQL_PORT", "3306"))
    MYSQL_USER: str = os.getenv("MYSQL_USER", "root")
    MYSQL_PASSWORD: str = os.getenv("MYSQL_PASSWORD", "123456")
    MYSQL_DATABASE: str = os.getenv("MYSQL_DATABASE", "lvfat")
    
    # 向量嵌入配置
    EMBEDDING_MODEL: str = os.getenv("EMBEDDING_MODEL", "paraphrase-multilingual-MiniLM-L12-v2")
    EMBEDDING_DIM: int = 384  # MiniLM模型的向量维度
    
    # 高级搜索配置
    SEARCH_WEIGHT_FULLTEXT: float = 0.35
    SEARCH_WEIGHT_VECTOR: float = 0.40
    SEARCH_WEIGHT_KEYWORD: float = 0.25
    SEARCH_TOP_K: int = int(os.getenv("SEARCH_TOP_K", "10"))
    SEARCH_MIN_SCORE: float = 0.1
    
    # 外部法律语义查询API配置
    EXTERNAL_LAW_API_ENABLED: bool = os.getenv("EXTERNAL_LAW_API_ENABLED", "false").lower() == "true"
    EXTERNAL_LAW_API_URL: str = os.getenv("EXTERNAL_LAW_API_URL", "https://law-api.example.com")
    EXTERNAL_APP_ID: str = os.getenv("EXTERNAL_APP_ID", "Peh-4088-ad611aBzd3f16fb9b")
    EXTERNAL_APP_SECRET: str = os.getenv("EXTERNAL_APP_SECRET", "")
    EXTERNAL_VECTOR_LIMIT: int = 300  # 向量查询配额
    EXTERNAL_TEXT_LIMIT: int = 50     # 正文请求配额
    
    # Redis配置
    REDIS_HOST: str = os.getenv("REDIS_HOST", "localhost")
    REDIS_PORT: int = 6379
    REDIS_DB: int = 0
    
    # 文件存储配置
    DATA_DIR: str = os.path.join(os.path.dirname(os.path.dirname(__file__)), "data")
    
    # RAG检索配置
    TOP_K: int = int(os.getenv("TOP_K", "5"))
    MIN_SIMILARITY: float = float(os.getenv("MIN_SIMILARITY", "0.3"))
    
    class Config:
        env_file = ".env"
        case_sensitive = True


settings = Settings()