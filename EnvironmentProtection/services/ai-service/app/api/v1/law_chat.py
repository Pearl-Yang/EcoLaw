"""
法律智能问答 API - RAG增强版
"""
from fastapi import APIRouter, HTTPException
from pydantic import BaseModel, Field
from typing import Optional, List, Dict
from app.api.v1.rag_chat import rag_law_chat
from app.services.law_retrieval import law_retrieval_service
from app.core.doubao_client import llm_client  # 使用豆包客户端

router = APIRouter()


class LawChatRequest(BaseModel):
    """RAG增强的法律问答请求"""
    question: str = Field(..., description="用户问题")
    role: str = Field(default="common", description="用户角色")
    conversation_id: Optional[str] = Field(None, description="会话ID")
    law_id: Optional[str] = Field(None, description="关联的法规ID")
    top_k: int = Field(default=5, description="检索结果数量")
    enable_rag: bool = Field(default=True, description="是否启用RAG")


class LawChatResponse(BaseModel):
    """RAG增强的法律问答响应"""
    answer: str = Field(..., description="AI回答")
    related_laws: List[dict] = Field(default=[], description="相关法规")
    suggestions: List[str] = Field(default=[], description="追问建议")
    used_rag: bool = Field(default=False, description="是否使用了RAG")


@router.post("/chat", response_model=LawChatResponse)
async def rag_law_chat_endpoint(request: LawChatRequest):
    """
    RAG增强的法律智能问答
    
    - 使用MySQL全文检索查找相关法规
    - 基于检索结果生成更准确的回答
    - 提供法规引用和追问建议
    """
    try:
        result = rag_law_chat(
            question=request.question,
            role=request.role,
            law_id=request.law_id,
            top_k=request.top_k,
            enable_rag=request.enable_rag
        )
        return LawChatResponse(**result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"AI服务错误: {str(e)}")


# 保留原有的简单问答接口作为备用
class SimpleLawChatRequest(BaseModel):
    """简单法律问答请求"""
    question: str = Field(..., description="用户问题")
    role: str = Field(default="common", description="用户角色")
    law_id: Optional[str] = Field(None, description="关联的法规ID")
    context: Optional[str] = Field(None, description="上下文内容")


class SimpleLawChatResponse(BaseModel):
    """简单法律问答响应"""
    answer: str = Field(..., description="AI回答")
    related_laws: List[dict] = Field(default=[], description="相关法规")
    suggestions: List[str] = Field(default=[], description="追问建议")


@router.post("/chat/simple", response_model=SimpleLawChatResponse)
async def simple_law_chat(request: SimpleLawChatRequest):
    """
    简单的法律问答（不使用RAG）
    
    - 直接调用LLM生成回答
    - 适用于快速响应场景
    """
    try:
        result = rag_law_chat(
            question=request.question,
            role=request.role,
            law_id=request.law_id,
            top_k=3,
            enable_rag=False  # 不使用RAG
        )
        return SimpleLawChatResponse(
            answer=result["answer"],
            related_laws=result["related_laws"],
            suggestions=result["suggestions"]
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"AI服务错误: {str(e)}")


# ============ 法条解读接口 ============

class LawInterpretRequest(BaseModel):
    """法条解读请求"""
    content: str = Field(..., description="法规名称或条款内容")
    level: str = Field(default="normal", description="解读详细程度: simple, normal, detailed")


class LawInterpretResponse(BaseModel):
    """法条解读响应"""
    interpretation: str = Field(..., description="通俗化解读")
    original_text: str = Field(..., description="原始法条")
    related_laws: List[dict] = Field(default=[], description="相关法规")


@router.post("/interpret", response_model=LawInterpretResponse)
async def interpret_law(request: LawInterpretRequest):
    """
    法条通俗化解读
    
    - 解析法规名称或条款
    - 生成通俗易懂的理解
    - 提供合规建议
    """
    try:
        # 1. 尝试检索相关法规
        laws = []
        if request.content:
            laws = law_retrieval_service.retrieve_relevant_laws(request.content, top_k=3)
        
        # 2. 构建解读提示词
        context = ""
        if laws:
            context = "【相关法规参考】\n"
            for law in laws[:3]:
                context += f"■ 《{law['title']}》\n"
                context += f"  {law.get('content_preview', '')[:300]}...\n\n"
        
        # 3. 生成解读
        level_prompts = {
            "simple": "请用最简单直白的语言解释，不要超过100字，让普通群众一听就懂。",
            "normal": "请用通俗易懂的语言解释，200字左右，适当引用法规条款。",
            "detailed": "请详细解读，包括法规背景、适用范围、具体要求、违规后果等，400字左右。"
        }
        
        prompt = f"""请解读以下法规或条款：

{request.content}

{context}

解读要求：
{level_prompts.get(request.level, level_prompts['normal'])}

请分点说明，并给出实用的合规建议。"""

        system_prompt = """你是一位专业的环保法律顾问，擅长用通俗易懂的语言解读法律法规。
请确保解读内容：
1. 准确无误，不曲解法规原意
2. 语言通俗，让非法律专业的人也能理解
3. 实用性强，给出具体的操作建议
4. 适当引用法规条款，增强说服力"""

        interpretation = llm_client.generate(prompt, system_prompt)
        
        return LawInterpretResponse(
            interpretation=interpretation,
            original_text=request.content,
            related_laws=[
                {
                    "id": law['id'],
                    "title": law['title'],
                    "level": law['level'],
                    "category": law['category']
                }
                for law in laws[:3]
            ]
        )
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"法条解读失败: {str(e)}")
