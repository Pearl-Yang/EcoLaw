"""
豆包/火山引擎 API 客户端
豆包API文档: https://www.volcengine.com/docs/82379/1263482
"""
import base64
import hashlib
import hmac
import time
import os
from typing import Optional, List, Dict
import httpx
from openai import OpenAI

from app.core.config import settings


class DoubaoClient:
    """豆包/火山引擎 API 客户端"""

    def __init__(self):
        # 设置火山引擎认证环境变量
        api_key = settings.LLM_API_KEY

        # 解析API Key (格式: AccessKeyId:SecretAccessKey)
        if ":" in api_key:
            access_key_id, secret_access_key = api_key.split(":", 1)
            os.environ['VOLC_ACCESSKEY'] = access_key_id
            os.environ['VOLC_SECRETKEY'] = secret_access_key
            self.access_key_id = access_key_id
            self.secret_access_key = secret_access_key
        else:
            # 如果是纯API Key，设置VOLC_ACCESSKEY
            os.environ['VOLC_ACCESSKEY'] = api_key
            self.access_key_id = api_key
            self.secret_access_key = ""

        self.base_url = settings.LLM_BASE_URL
        self.model = settings.LLM_MODEL
        self.temperature = settings.LLM_TEMPERATURE
        self.max_tokens = settings.LLM_MAX_TOKENS

        # 创建HTTP客户端
        self.client = httpx.Client(
            timeout=120.0,
            headers={"Content-Type": "application/json"}
        )

    def _generate_signature(self, timestamp: int) -> str:
        """生成签名"""
        string_to_sign = f"{self.access_key_id}\n{timestamp}\n"
        signature = hmac.new(
            self.secret_access_key.encode('utf-8'),
            string_to_sign.encode('utf-8'),
            hashlib.sha256
        ).hexdigest()
        return signature

    def _generate_auth_header(self, method: str = "POST", path: str = "/chat/completions") -> Dict[str, str]:
        """生成火山引擎API签名认证头"""
        timestamp = int(time.time())
        signature = self._generate_signature(timestamp)

        return {
            "Authorization": f"Bearer {self.access_key_id}:{signature}",
            "X-Date": str(timestamp)
        }
    
    def chat(self, messages: List[Dict[str, str]], system_prompt: Optional[str] = None) -> str:
        """
        对话生成
        
        Args:
            messages: 消息列表 [{"role": "user", "content": "..."}]
            system_prompt: 系统提示词
        
        Returns:
            生成的文本
        """
        # 构建消息
        chat_messages = []
        
        if system_prompt:
            chat_messages.append({"role": "system", "content": system_prompt})
        
        for msg in messages:
            chat_messages.append({
                "role": msg.get("role", "user"),
                "content": msg.get("content", "")
            })
        
        # 请求体
        payload = {
            "model": self.model,
            "messages": chat_messages,
            "temperature": self.temperature,
            "max_tokens": self.max_tokens
        }
        
        # 发送请求
        url = f"{self.base_url}/chat/completions"

        try:
            auth_headers = self._generate_auth_header()
            headers = {
                "Content-Type": "application/json"
            }
            headers.update(auth_headers)

            response = self.client.post(
                url,
                json=payload,
                headers=headers
            )
            response.raise_for_status()

            result = response.json()
            return result["choices"][0]["message"]["content"]
        except httpx.HTTPStatusError as e:
            return f"API请求失败: {e.response.status_code} - {e.response.text}"
        except Exception as e:
            return f"请求异常: {str(e)}"
    
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
    
    def close(self):
        """关闭客户端"""
        self.client.close()


# 全局实例
doubao_client = DoubaoClient()


class LLMClient:
    """统一的大语言模型客户端 - 兼容豆包和OpenAI接口"""
    
    def __init__(self):
        # 检测是否使用豆包/火山引擎
        if "volces.com" in settings.LLM_BASE_URL or "ark" in settings.LLM_BASE_URL:
            self._client = doubao_client
            self._use_doubao = True
        else:
            # 标准OpenAI兼容接口
            self._client = OpenAI(
                api_key=settings.LLM_API_KEY,
                base_url=settings.LLM_BASE_URL,
                timeout=120.0
            )
            self._use_doubao = False
    
    def chat(self, messages: List[Dict[str, str]], system_prompt: Optional[str] = None) -> str:
        """对话生成"""
        if self._use_doubao:
            return self._client.chat(messages, system_prompt)
        else:
            # OpenAI兼容接口
            chat_messages = []
            if system_prompt:
                chat_messages.append({"role": "system", "content": system_prompt})
            chat_messages.extend(messages)
            
            response = self._client.chat.completions.create(
                model=settings.LLM_MODEL,
                messages=chat_messages,
                temperature=settings.LLM_TEMPERATURE,
                max_tokens=settings.LLM_MAX_TOKENS
            )
            return response.choices[0].message.content
    
    def generate(self, prompt: str, system_prompt: Optional[str] = None) -> str:
        """简单生成"""
        messages = [{"role": "user", "content": prompt}]
        return self.chat(messages, system_prompt)


# 全局实例
llm_client = LLMClient()
