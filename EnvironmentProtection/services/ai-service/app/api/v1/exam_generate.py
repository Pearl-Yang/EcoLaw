"""
智能组卷 API
"""
from fastapi import APIRouter, HTTPException
from pydantic import BaseModel, Field
from typing import List, Optional

from app.core.doubao_client import llm_client  # 使用豆包客户端
from app.core.prompt import EXAM_GENERATION_SYSTEM, INDUSTRIES

router = APIRouter()


class ExamGenerateRequest(BaseModel):
    """组卷请求"""
    topic: str = Field(..., description="考核主题")
    industry: Optional[str] = Field(None, description="适用行业")
    question_count: int = Field(default=10, description="题目数量")
    question_types: List[str] = Field(default=["single_choice"], description="题型")
    difficulty: str = Field(default="medium", description="难度: easy, medium, hard")


class ExamQuestion(BaseModel):
    """考试题目"""
    type: str = Field(..., description="题型")
    question: str = Field(..., description="题目内容")
    options: Optional[List[str]] = Field(None, description="选项")
    answer: str = Field(..., description="正确答案")
    analysis: str = Field(..., description="答案解析")


class ExamGenerateResponse(BaseModel):
    """组卷响应"""
    exam_id: str = Field(..., description="试卷ID")
    title: str = Field(..., description="试卷标题")
    total_score: int = Field(..., description="总分")
    questions: List[ExamQuestion] = Field(..., description="题目列表")
    time_limit: int = Field(..., description="时长(分钟)")


@router.post("/generate", response_model=ExamGenerateResponse)
async def generate_exam(request: ExamGenerateRequest):
    """
    AI智能组卷
    
    - 根据主题和行业生成考试题目
    - 支持多种题型
    - 提供答案和解析
    """
    try:
        industry_text = INDUSTRIES.get(request.industry, "") if request.industry else ""
        
        system_prompt = EXAM_GENERATION_SYSTEM
        
        type_names = {
            "single_choice": "单选题",
            "multi_choice": "多选题",
            "true_false": "判断题",
            "short_answer": "简答题",
            "case_analysis": "案例分析题"
        }
        types_text = "、".join([type_names.get(t, t) for t in request.question_types])
        
        user_prompt = f"""请为以下主题生成{request.question_count}道考试题目：

考核主题：{request.topic}
适用行业：{industry_text if industry_text else "通用"}
题型要求：{types_text}
难度要求：{request.difficulty}

请按以下JSON格式输出：
{{
  "title": "试卷标题",
  "questions": [
    {{
      "type": "single_choice",
      "question": "题目内容",
      "options": ["A选项", "B选项", "C选项", "D选项"],
      "answer": "A",
      "analysis": "答案解析"
    }}
  ]
}}

注意：
1. 单选题和多选题必须提供4个选项
2. 每道题标注分值（单选题2分，多选题3分，判断题1分，简答题5分，案例分析题10分）
3. 答案解析要清晰明了"""

        result = llm_client.generate(user_prompt, system_prompt)
        
        # 解析结果（简化处理）
        # TODO: 使用JSON解析
        
        return ExamGenerateResponse(
            exam_id=f"exam_{hash(result) % 100000}",
            title=request.topic + "考核试卷",
            total_score=request.question_count * 2,
            questions=[],
            time_limit=request.question_count * 2
        )
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"组卷失败: {str(e)}")


@router.post("/random")
async def random_exam(
    count: int = 10,
    category: Optional[str] = None
):
    """
    随机组卷
    
    从题库中随机抽取题目组成试卷
    """
    try:
        # TODO: 从题库随机抽取
        return {
            "exam_id": f"random_{count}",
            "title": "随机抽题考核",
            "total_score": count * 2,
            "questions": [],
            "time_limit": count * 2
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"组卷失败: {str(e)}")