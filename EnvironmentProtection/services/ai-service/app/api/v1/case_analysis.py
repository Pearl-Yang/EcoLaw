"""
案例拆解 API
"""
from fastapi import APIRouter, HTTPException
from pydantic import BaseModel, Field
from typing import Optional, List

from app.core.doubao_client import llm_client  # 使用豆包客户端
from app.core.prompt import CASE_ANALYSIS_SYSTEM

router = APIRouter()


class CaseAnalysisRequest(BaseModel):
    """案例分析请求"""
    case_content: str = Field(..., description="案例内容")
    case_type: str = Field(default="administrative", description="案例类型: administrative, criminal, civil")
    target_audience: str = Field(default="common", description="目标受众")
    law_firm_name: Optional[str] = Field(None, description="律所名称")
    lawyer_name: Optional[str] = Field(None, description="律师名称")


class CaseAnalysisResponse(BaseModel):
    """案例分析响应"""
    case_summary: str = Field(..., description="案件概述")
    core_facts: str = Field(..., description="核心事实")
    legal_basis: str = Field(..., description="法律依据")
    penalty_result: str = Field(..., description="处罚结果")
    compliance_tips: List[str] = Field(..., description="合规启示")
    story_script: Optional[str] = Field(None, description="警示故事/短视频脚本")
    materials: List[dict] = Field(default=[], description="生成的普法素材")
    law_firm: Optional[str] = Field(None, description="律所信息")
    lawyer: Optional[str] = Field(None, description="律师信息")


@router.post("/analyze", response_model=CaseAnalysisResponse)
async def analyze_case(request: CaseAnalysisRequest):
    """
    以案释法智能拆解
    
    - 提取案件核心事实
    - 明确法律依据
    - 给出合规启示
    - 生成警示故事/普法素材
    """
    try:
        system_prompt = CASE_ANALYSIS_SYSTEM
        
        user_prompt = f"""请分析以下白色污染相关案例：

案例类型：{request.case_type}
案例内容：
{request.case_content}

请从以下方面进行拆解：
1. 案件概述（100字以内）
2. 核心事实（列出关键要素）
3. 法律依据（引用具体法条）
4. 处罚结果
5. 合规启示（3-5条）
6. 警示故事/短视频脚本（可选）

如果需要生成普法素材，请一并提供。"""

        result = llm_client.generate(user_prompt, system_prompt)
        
        # 简单解析
        # TODO: 更好的解析逻辑
        parts = result.split('\n\n')
        
        return CaseAnalysisResponse(
            case_summary=parts[0] if len(parts) > 0 else "",
            core_facts=parts[1] if len(parts) > 1 else "",
            legal_basis=parts[2] if len(parts) > 2 else "",
            penalty_result=parts[3] if len(parts) > 3 else "",
            compliance_tips=["遵守禁塑规定", "保留进货凭证", "及时整改"],
            story_script=result if len(parts) > 4 else None,
            materials=[],
            law_firm=request.law_firm_name,
            lawyer=request.lawyer_name
        )
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"案例分析失败: {str(e)}")


@router.post("/generate-materials")
async def generate_case_materials(
    case_id: str,
    material_types: List[str] = ["article", "video_script"]
):
    """
    基于案例生成多种普法素材
    """
    try:
        # TODO: 从数据库获取案例内容
        case_content = "【示例案例】某餐饮店因使用不可降解塑料袋被市场监管部门处罚..."
        
        materials = []
        
        if "article" in material_types:
            materials.append({
                "type": "article",
                "title": "以案说法：餐饮商家注意了！",
                "content": "..."
            })
        
        if "video_script" in material_types:
            materials.append({
                "type": "video_script",
                "title": "餐饮禁塑警示短视频脚本",
                "content": "..."
            })
        
        return {"materials": materials}
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"素材生成失败: {str(e)}")