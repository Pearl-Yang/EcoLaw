"""
LLM 封装 - 支持豆包、OpenAI兼容接口
"""
from typing import Optional, List, Dict
from langchain_openai import ChatOpenAI
from langchain_core.messages import HumanMessage, SystemMessage, AIMessage
import httpx

from app.core.config import settings


class LLMClient:
    """大语言模型客户端 - 支持豆包/火山引擎"""
    
    def __init__(self):
        # 豆包/火山引擎特殊处理
        if "volces.com" in settings.LLM_BASE_URL or "ark" in settings.LLM_BASE_URL:
            # 豆包API使用特殊的认证方式
            api_key = settings.LLM_API_KEY
            # 豆包API格式: AccessKeyId:SecretAccessKey
            self._api_key = api_key
            self._base_url = settings.LLM_BASE_URL
            
            self.llm = ChatOpenAI(
                api_key=api_key,
                base_url=base_url if (base_url := settings.LLM_BASE_URL) else "https://ark.cn-beijing.volces.com/api/v3",
                model=settings.LLM_MODEL,
                temperature=settings.LLM_TEMPERATURE,
                max_tokens=settings.LLM_MAX_TOKENS,
                http_client=httpx.Client(
                    timeout=120.0,
                    headers={
                        "Content-Type": "application/json",
                    }
                )
            )
        else:
            # 标准OpenAI兼容接口
            self.llm = ChatOpenAI(
                api_key=settings.LLM_API_KEY,
                base_url=settings.LLM_BASE_URL,
                model=settings.LLM_MODEL,
                temperature=settings.LLM_TEMPERATURE,
                max_tokens=settings.LLM_MAX_TOKENS
            )
    
    def chat(
        self,
        messages: List[Dict[str, str]],
        system_prompt: Optional[str] = None
    ) -> str:
        """
        对话生成
        
        Args:
            messages: 消息列表 [{"role": "user", "content": "..."}]
            system_prompt: 系统提示词
        
        Returns:
            生成的文本
        """
        langchain_messages = []
        
        if system_prompt:
            langchain_messages.append(SystemMessage(content=system_prompt))
        
        for msg in messages:
            if msg["role"] == "user":
                langchain_messages.append(HumanMessage(content=msg["content"]))
            elif msg["role"] == "assistant":
                langchain_messages.append(AIMessage(content=msg["content"]))
        
        response = self.llm.invoke(langchain_messages)
        return response.content
    
    def generate(self, prompt: str, system_prompt: Optional[str] = None) -> str:
        """
        简单生成
        
        Args:
            prompt: 用户提示
            system_prompt: 系统提示
        
        Returns:
            生成的文本
        """
        messages = [{"role": "user", "content": prompt}]
        return self.chat(messages, system_prompt)


# 全局实例
llm_client = LLMClient()