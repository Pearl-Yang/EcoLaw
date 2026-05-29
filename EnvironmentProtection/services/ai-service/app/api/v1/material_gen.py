"""
素材生成 API
"""
from fastapi import APIRouter, HTTPException
from pydantic import BaseModel, Field
from typing import Optional, List

from app.core.doubao_client import llm_client  # 使用豆包客户端
from app.core.prompt import MATERIAL_GENERATION_SYSTEM, MATERIAL_TYPES, USER_ROLES

router = APIRouter()


class MaterialGenRequest(BaseModel):
    """素材生成请求"""
    topic: str = Field(..., description="普法主题")
    material_type: str = Field(..., description="素材类型")
    audience: str = Field(default="common", description="目标受众")
    industry: Optional[str] = Field(None, description="行业")
    custom_requirements: Optional[str] = Field(None, description="自定义需求")
    law_ids: Optional[List[str]] = Field(None, description="关联法规ID")


class MaterialGenResponse(BaseModel):
    """素材生成响应"""
    title: str = Field(..., description="素材标题")
    content: str = Field(..., description="生成的内容")
    material_type: str = Field(..., description="素材类型")
    law_references: List[str] = Field(default=[], description="法规依据")
    suggestions: List[str] = Field(default=[], description="使用建议")


@router.post("/generate", response_model=MaterialGenResponse)
async def generate_material(request: MaterialGenRequest):
    """
    AI生成普法宣传素材
    
    支持多种素材类型：
    - 短视频脚本
    - 海报文案
    - 公众号推文
    - 村广播稿
    - 宣传单页
    - 闯关答题
    """
    try:
        material_type_text = MATERIAL_TYPES.get(request.material_type, "宣传素材")
        audience_text = USER_ROLES.get(request.audience, "普通群众")
        
        system_prompt = MATERIAL_GENERATION_SYSTEM
        
        user_prompt = f"""请为以下主题生成{material_type_text}：

主题：{request.topic}
目标受众：{audience_text}
"""
        
        if request.industry:
            user_prompt += f"适用行业：{request.industry}\n"
        
        if request.custom_requirements:
            user_prompt += f"自定义要求：{request.custom_requirements}\n"
        
        if request.law_ids:
            user_prompt += f"\n相关法规ID：{', '.join(request.law_ids)}\n"
        
        user_prompt += """
请生成完整的内容，包括：
1. 标题
2. 具体内容（可直接使用）
3. 版权水印建议
4. 使用场景建议"""

        result = llm_client.generate(user_prompt, system_prompt)
        
        # 简单解析结果
        lines = result.split('\n')
        title = request.topic
        content = result
        
        if len(lines) > 0 and lines[0].strip():
            title = lines[0].strip()
            content = '\n'.join(lines[1:])
        
        return MaterialGenResponse(
            title=title,
            content=content,
            material_type=request.material_type,
            law_references=request.law_ids or [],
            suggestions=["发布前请进行合规审核", "可根据实际情况调整"]
        )
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"素材生成失败: {str(e)}")


@router.post("/custom")
async def custom_material(
    description: str,
    material_type: str = "article"
):
    """
    定制化素材生成
    
    根据用户自定义描述生成素材
    """
    try:
        material_type_text = MATERIAL_TYPES.get(material_type, "文章")
        
        system_prompt = MATERIAL_GENERATION_SYSTEM + "\n\n用户有明确的素材需求，请严格按照需求生成。"
        
        user_prompt = f"""用户定制需求：{description}

请生成{material_type_text}，确保内容：
1. 完全符合用户需求
2. 通俗易懂
3. 包含法律依据
4. 可直接使用"""

        result = llm_client.generate(user_prompt, system_prompt)
        
        return {
            "content": result,
            "material_type": material_type
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"定制素材生成失败: {str(e)}")