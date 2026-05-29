"""
RAG增强的法律检索 - 集成高级搜索
支持全文检索 + 向量检索 + 关键词匹配的混合搜索
"""
from pydantic import BaseModel, Field
from typing import Optional, List, Dict
from app.services.law_retrieval import law_retrieval_service
from app.services.advanced_search import advanced_search_service, SearchStrategy
from app.core.doubao_client import llm_client  # 使用豆包客户端
from app.core.prompt import LAW_INTERPRET_SYSTEM, USER_ROLES


class RAGChatRequest(BaseModel):
    """RAG问答请求"""
    question: str = Field(..., description="用户问题")
    role: str = Field(default="common", description="用户角色")
    law_id: Optional[str] = Field(None, description="关联的法规ID")
    top_k: int = Field(default=5, description="检索结果数量")
    enable_rag: bool = Field(default=True, description="是否启用RAG")


class RAGChatResponse(BaseModel):
    """RAG问答响应"""
    answer: str = Field(..., description="AI回答")
    related_laws: List[dict] = Field(default=[], description="相关法规")
    related_news: List[dict] = Field(default=[], description="相关新闻")
    related_comments: List[dict] = Field(default=[], description="相关评论")
    suggestions: List[str] = Field(default=[], description="追问建议")
    used_rag: bool = Field(default=False, description="是否使用了RAG")
    summary: Optional[str] = Field(default=None, description="综合总结")


def rag_law_chat(question: str, role: str = "common", law_id: Optional[str] = None,
                 top_k: int = 5, enable_rag: bool = True,
                 use_hybrid_search: bool = True, search_strategy: str = "hybrid") -> Dict:
    """
    RAG增强的法律智能问答 - 支持高级搜索
    
    Args:
        question: 用户问题
        role: 用户角色
        law_id: 指定关联的法规ID
        top_k: 检索结果数量
        enable_rag: 是否启用RAG
        use_hybrid_search: 是否使用混合搜索
        search_strategy: 搜索策略 (fulltext/vector/keyword/hybrid)
    
    Returns:
        包含回答、相关法规、相关新闻、相关评论、追问建议的字典
    """
    # 1. 检索相关法规 (RAG)
    relevant_laws = []
    related_news = []
    related_comments = []
    used_rag = False
    search_info = {}  # 记录搜索信息
    
    if enable_rag:
        try:
            # 如果指定了法规ID，获取该法规
            if law_id:
                specified_law = law_retrieval_service.get_law_by_id(law_id)
                if specified_law:
                    relevant_laws.append(specified_law)
            
            # 根据策略选择检索方式
            if use_hybrid_search and search_strategy == "hybrid":
                # 使用混合搜索
                strategy_map = {
                    "fulltext": SearchStrategy.FULLTEXT,
                    "vector": SearchStrategy.VECTOR,
                    "keyword": SearchStrategy.KEYWORD,
                    "hybrid": SearchStrategy.HYBRID
                }
                strategy = strategy_map.get(search_strategy, SearchStrategy.HYBRID)
                
                hybrid_results = advanced_search_service.search(
                    query=question,
                    sources=["law", "news"],
                    strategy=strategy,
                    top_k=top_k * 2  # 混合搜索返回更多结果
                )
                
                # 解析混合搜索结果
                for result in hybrid_results:
                    if result.source == "law":
                        relevant_laws.append({
                            "id": result.id,
                            "title": result.title,
                            "content_preview": result.content,
                            "level": result.metadata.get("level", ""),
                            "category": result.metadata.get("category", ""),
                            "relevance": result.relevance_score,
                            "level_text": result.metadata.get("level", ""),
                            "search_strategy": search_strategy
                        })
                    elif result.source == "news":
                        related_news.append({
                            "id": result.id,
                            "title": result.title,
                            "summary": result.content,
                            "relevance": result.relevance_score
                        })
                
                search_info = {
                    "strategy": search_strategy,
                    "total_results": len(hybrid_results),
                    "hybrid_used": True
                }
            else:
                # 使用原有的全文检索
                retrieved_laws = law_retrieval_service.retrieve_relevant_laws(question, top_k=top_k)
                
                # 合并结果，去重
                existing_ids = {law['id'] for law in relevant_laws}
                for law in retrieved_laws:
                    if law['id'] not in existing_ids:
                        relevant_laws.append(law)
                        existing_ids.add(law['id'])
                
                # 检索相关新闻
                related_news = law_retrieval_service.retrieve_relevant_news(question, top_k=3)
                
                search_info = {
                    "strategy": "fulltext",
                    "hybrid_used": False
                }
            
            # 检索相关评论
            related_comments = law_retrieval_service.retrieve_relevant_comments(question, top_k=5)
            
            used_rag = len(relevant_laws) > 0
            
        except Exception as e:
            print(f"RAG检索失败: {e}")
            relevant_laws = []
            related_news = []
            related_comments = []
            search_info = {"error": str(e)}

    # 2. 构建增强上下文
    context = law_retrieval_service.build_comprehensive_context(relevant_laws, related_news, related_comments)

    # 3. 构建提示词
    role_text = USER_ROLES.get(role, "普通群众")
    
    system_prompt = LAW_INTERPRET_SYSTEM + f"""

当前用户角色：{role_text}
"""

    if relevant_laws:
        system_prompt += f"""

【重要】根据用户问题检索到了以下内容：

"""
        for law in relevant_laws[:5]:
            system_prompt += f"""■ 《{law['title']}》
  层级：{law['level_text']} | 分类：{law['category']}
  内容预览：{law['content_preview'][:200]}...
  
"""
        
        if related_news:
            system_prompt += f"\n【相关新闻】{len(related_news)}篇：\n"
            for news in related_news[:3]:
                system_prompt += f"  - {news['title']}\n"
        
        if related_comments:
            system_prompt += f"\n【网友讨论】{len(related_comments)}条：\n"
            for comment in related_comments[:3]:
                system_prompt += f"  - [{comment.get('user_nickname', '网友')}]: {comment.get('content', '')[:80]}...\n"
        
        system_prompt += """
请基于以上内容回答用户问题。
如内容与问题相关度不高，请说明情况并提供一般性建议。
在回答中应适当引用相关法规的具体条款。
"""
    else:
        system_prompt += """

未检索到与问题直接相关的法规。
请基于您的法律知识，用通俗易懂的语言回答用户问题。
如问题涉及专业法律领域，建议用户咨询专业律师。
"""

    # 4. 生成回答
    user_prompt = f"""用户问题：{question}
    
请根据提供的上下文，用通俗易懂的语言回答用户问题。

回答要求：
1. 明确告诉用户"什么不能做"、"违规有什么后果"
2. 告知用户"遇到问题怎么办"
3. 如涉及具体法规，请在回答中注明法规名称
4. 如果没有相关法规或检索到的内容无法回答用户问题，请诚实说明，并提供基于一般法律知识的建议
5. 回答应该简洁、有条理，适合非法律专业普通用户理解
"""

    try:
        answer = llm_client.generate(user_prompt, system_prompt)
    except Exception as e:
        answer = f"抱歉，AI服务暂时不可用。请稍后再试或联系管理员。\n\n错误信息：{str(e)}"

    # 5. 生成追问建议
    suggestions = []
    try:
        suggestions_raw = llm_client.generate(
            f"基于用户问题「{question}」，生成3个最可能的追问建议，每条建议不超过20字，应是用户可能想进一步了解的方面。",
            "你是一个法律顾问助手，请根据用户问题生成3个追问建议，帮助用户进一步了解相关法律知识。"
        )
        suggestions = [s.strip() for s in suggestions_raw.split('\n') if s.strip()][:3]
    except Exception as e:
        print(f"生成追问建议失败: {e}")
        suggestions = [
            "相关法规具体有哪些？",
            "违规行为会有什么处罚？",
            "遇到这种情况应该怎么处理？"
        ]

    # 6. 生成综合总结
    summary = None
    if relevant_laws or related_news:
        try:
            summary_prompt = f"""请对以下关于「{question}」的搜索结果进行简要总结（100字以内）：

法规：{len(relevant_laws)}部
新闻：{len(related_news)}篇
评论：{len(related_comments)}条

请用一句话概括这些内容的核心要点。"""
            
            summary = llm_client.generate(summary_prompt, "你是一个信息整理助手，请简洁概括搜索结果的核心内容。")
            summary = summary[:150] + '...' if len(summary) > 150 else summary
        except:
            summary = f"搜索到{len(relevant_laws)}部相关法规、{len(related_news)}篇资讯、{len(related_comments)}条网友讨论"

    return {
        "answer": answer,
        "related_laws": [
            {
                "id": law['id'],
                "title": law['title'],
                "level": law['level'],
                "level_text": law['level_text'],
                "category": law['category'],
                "relevance": law['relevance']
            }
            for law in relevant_laws[:5]
        ],
        "related_news": [
            {
                "id": news['id'],
                "title": news['title'],
                "summary": news.get('summary', ''),
                "type": news.get('type', ''),
                "source": news.get('source', ''),
                "view_count": news.get('view_count', 0)
            }
            for news in related_news[:3]
        ],
        "related_comments": [
            {
                "id": comment['id'],
                "content": comment['content'],
                "user_nickname": comment.get('user_nickname', '匿名'),
                "like_count": comment.get('like_count', 0),
                "target_title": comment.get('target_title', '')
            }
            for comment in related_comments[:5]
        ],
        "suggestions": suggestions,
        "used_rag": used_rag,
        "summary": summary,
        "search_info": search_info  # 搜索策略信息
    }
