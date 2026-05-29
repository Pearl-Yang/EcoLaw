"""
RAG检索服务 - 使用MySQL全文检索替代向量检索
"""
from typing import List, Dict, Optional
import pymysql
from app.core.config import settings


class LawRetrievalService:
    """法规检索服务 - 使用MySQL FULLTEXT检索"""

    def __init__(self):
        self.db_config = {
            'host': settings.MYSQL_HOST,
            'port': settings.MYSQL_PORT,
            'user': settings.MYSQL_USER,
            'password': settings.MYSQL_PASSWORD,
            'database': settings.MYSQL_DATABASE,
            'charset': 'utf8mb4'
        }
        self.top_k = settings.TOP_K
        self.min_similarity = settings.MIN_SIMILARITY

    def get_connection(self):
        """获取数据库连接"""
        return pymysql.connect(**self.db_config)

    def retrieve_relevant_laws(self, query: str, top_k: Optional[int] = None, level: Optional[str] = None) -> List[Dict]:
        """
        检索相关法规
        
        Args:
            query: 用户查询
            top_k: 返回数量
            level: 法规层级 (national, provincial, city)
        
        Returns:
            相关法规列表
        """
        if top_k is None:
            top_k = self.top_k

        conn = self.get_connection()
        try:
            with conn.cursor(pymysql.cursors.DictCursor) as cursor:
                # 构建SQL查询 - 使用MATCH AGAINST进行全文搜索
                sql = """
                    SELECT 
                        id,
                        title,
                        level,
                        category,
                        LEFT(content, 500) as content_preview,
                        publish_date,
                        effective_date,
                        status,
                        MATCH(title, content) AGAINST(%s IN NATURAL LANGUAGE MODE) as relevance_score
                    FROM law
                    WHERE deleted = 0 
                    AND status = 'effective'
                    AND MATCH(title, content) AGAINST(%s IN NATURAL LANGUAGE MODE)
                """
                params = [query, query]

                if level:
                    sql += " AND level = %s"
                    params.append(level)

                sql += " ORDER BY relevance_score DESC LIMIT %s"
                params.append(top_k)

                cursor.execute(sql, params)
                results = cursor.fetchall()

                # 过滤低相关度结果
                filtered_results = [
                    r for r in results 
                    if r['relevance_score'] is not None and r['relevance_score'] > self.min_similarity
                ]

                # 格式化结果
                return [self._format_result(r) for r in filtered_results]

        finally:
            conn.close()

    def retrieve_relevant_news(self, query: str, top_k: Optional[int] = 3) -> List[Dict]:
        """
        检索相关新闻
        
        Args:
            query: 用户查询
            top_k: 返回数量
        
        Returns:
            相关新闻列表
        """
        if top_k is None:
            top_k = self.top_k

        conn = self.get_connection()
        try:
            with conn.cursor(pymysql.cursors.DictCursor) as cursor:
                sql = """
                    SELECT 
                        id,
                        title,
                        summary,
                        type,
                        source,
                        tags,
                        view_count,
                        like_count,
                        comment_count,
                        publish_time,
                        MATCH(title, content) AGAINST(%s IN NATURAL LANGUAGE MODE) as relevance_score
                    FROM news
                    WHERE deleted = 0 
                    AND status = 'published'
                    AND MATCH(title, content) AGAINST(%s IN NATURAL LANGUAGE MODE)
                    ORDER BY relevance_score DESC, publish_time DESC
                    LIMIT %s
                """
                cursor.execute(sql, (query, query, top_k))
                results = cursor.fetchall()

                return [
                    {
                        'id': r['id'],
                        'title': r['title'],
                        'summary': r.get('summary', ''),
                        'type': r.get('type', ''),
                        'source': r.get('source', ''),
                        'tags': self._parse_json(r.get('tags', '[]')),
                        'view_count': r.get('view_count', 0),
                        'like_count': r.get('like_count', 0),
                        'comment_count': r.get('comment_count', 0),
                        'publish_time': r.get('publish_time', ''),
                        'relevance': round(r.get('relevance_score', 0.5), 3)
                    }
                    for r in results
                ]

        except Exception as e:
            print(f"检索新闻失败: {e}")
            return []
        finally:
            conn.close()

    def retrieve_relevant_comments(self, query: str, top_k: Optional[int] = 5) -> List[Dict]:
        """
        检索相关评论（基于法规或新闻关联）
        
        Args:
            query: 用户查询
            top_k: 返回数量
        
        Returns:
            相关评论列表
        """
        if top_k is None:
            top_k = self.top_k

        conn = self.get_connection()
        try:
            with conn.cursor(pymysql.cursors.DictCursor) as cursor:
                # 检索评论内容匹配的结果
                sql = """
                    SELECT 
                        c.id,
                        c.content,
                        c.target_type,
                        c.target_id,
                        c.like_count,
                        c.reply_count,
                        c.user_nickname,
                        c.create_time,
                        MATCH(c.content) AGAINST(%s IN NATURAL LANGUAGE MODE) as relevance_score
                    FROM comment c
                    WHERE c.deleted = 0 
                    AND c.status = 'published'
                    AND MATCH(c.content) AGAINST(%s IN NATURAL LANGUAGE MODE)
                    ORDER BY relevance_score DESC, c.create_time DESC
                    LIMIT %s
                """
                cursor.execute(sql, (query, query, top_k))
                results = cursor.fetchall()

                formatted_results = []
                for r in results:
                    # 获取关联的目标信息
                    target_title = ""
                    if r['target_type'] == 'news' and r['target_id']:
                        target_title = self._get_news_title(r['target_id'], cursor)
                    elif r['target_type'] == 'law':
                        target_title = self._get_law_title(r['target_id'], cursor)

                    formatted_results.append({
                        'id': r['id'],
                        'content': r['content'][:200] + '...' if len(r['content']) > 200 else r['content'],
                        'target_type': r['target_type'],
                        'target_id': r['target_id'],
                        'target_title': target_title,
                        'like_count': r.get('like_count', 0),
                        'reply_count': r.get('reply_count', 0),
                        'user_nickname': r.get('user_nickname', '匿名用户'),
                        'create_time': r.get('create_time', '').strftime('%Y-%m-%d') if r.get('create_time') else '',
                        'relevance': round(r.get('relevance_score', 0.5), 3)
                    })

                return formatted_results

        except Exception as e:
            print(f"检索评论失败: {e}")
            return []
        finally:
            conn.close()

    def _get_news_title(self, news_id: str, cursor) -> str:
        """获取新闻标题"""
        try:
            cursor.execute("SELECT title FROM news WHERE id = %s AND deleted = 0", (news_id,))
            result = cursor.fetchone()
            return result['title'] if result else ''
        except:
            return ''

    def _get_law_title(self, law_id: str, cursor) -> str:
        """获取法规标题"""
        try:
            cursor.execute("SELECT title FROM law WHERE id = %s AND deleted = 0", (law_id,))
            result = cursor.fetchone()
            return result['title'] if result else ''
        except:
            return ''

    def retrieve_by_keyword(self, keywords: List[str], top_k: Optional[int] = None) -> List[Dict]:
        """
        基于关键词检索法规
        
        Args:
            keywords: 关键词列表
            top_k: 返回数量
        """
        if top_k is None:
            top_k = self.top_k

        # 构建LIKE查询条件
        conditions = []
        params = []
        for kw in keywords:
            conditions.append("(title LIKE %s OR content LIKE %s)")
            params.extend([f'%{kw}%', f'%{kw}%'])

        if not conditions:
            return []

        conn = self.get_connection()
        try:
            with conn.cursor(pymysql.cursors.DictCursor) as cursor:
                sql = f"""
                    SELECT 
                        id,
                        title,
                        level,
                        category,
                        LEFT(content, 500) as content_preview,
                        publish_date,
                        effective_date,
                        status
                    FROM law
                    WHERE deleted = 0 
                    AND status = 'effective'
                    AND ({' OR '.join(conditions)})
                    LIMIT %s
                """
                params.append(top_k)

                cursor.execute(sql, params)
                results = cursor.fetchall()

                return [self._format_result(r) for r in results]

        finally:
            conn.close()

    def get_law_by_id(self, law_id: str) -> Optional[Dict]:
        """
        根据ID获取法规详情
        
        Args:
            law_id: 法规ID
        
        Returns:
            法规详情
        """
        conn = self.get_connection()
        try:
            with conn.cursor(pymysql.cursors.DictCursor) as cursor:
                sql = """
                    SELECT id, title, level, category, content, 
                           publish_date, effective_date, status
                    FROM law
                    WHERE id = %s AND deleted = 0
                """
                cursor.execute(sql, (law_id,))
                result = cursor.fetchone()

                if result:
                    return {
                        'id': result['id'],
                        'title': result['title'],
                        'level': result['level'],
                        'category': result['category'],
                        'content': result['content'],
                        'publish_date': result['publish_date'],
                        'effective_date': result['effective_date'],
                        'status': result['status']
                    }
                return None

        finally:
            conn.close()

    def get_related_laws(self, law_id: str, top_k: int = 3) -> List[Dict]:
        """
        获取相关法规（基于分类）
        
        Args:
            law_id: 法规ID
            top_k: 返回数量
        """
        # 先获取目标法规的分类
        target_law = self.get_law_by_id(law_id)
        if not target_law:
            return []

        conn = self.get_connection()
        try:
            with conn.cursor(pymysql.cursors.DictCursor) as cursor:
                sql = """
                    SELECT id, title, level, category,
                           LEFT(content, 300) as content_preview
                    FROM law
                    WHERE deleted = 0 
                    AND status = 'effective'
                    AND category = %s
                    AND id != %s
                    LIMIT %s
                """
                cursor.execute(sql, (target_law['category'], law_id, top_k))
                results = cursor.fetchall()

                return [self._format_result(r) for r in results]

        finally:
            conn.close()

    def build_context_from_results(self, results: List[Dict]) -> str:
        """
        从检索结果构建上下文
        
        Args:
            results: 检索结果列表
        
        Returns:
            格式化的上下文字符串
        """
        if not results:
            return ""

        context_parts = []
        for i, result in enumerate(results, 1):
            part = f"""【法规{i}】{result['title']}
- 层级：{self._get_level_text(result['level'])}
- 分类：{result.get('category', '未分类')}
- 相关内容：{result.get('content_preview', '')}"""
            context_parts.append(part)

        return "\n\n".join(context_parts)

    def build_comprehensive_context(self, laws: List[Dict], news: List[Dict], comments: List[Dict]) -> str:
        """
        构建综合上下文（包含法规、新闻、评论）
        
        Args:
            laws: 法规列表
            news: 新闻列表
            comments: 评论列表
        
        Returns:
            格式化的上下文
        """
        parts = []

        # 法规部分
        if laws:
            parts.append("【相关法规】")
            for i, law in enumerate(laws[:5], 1):
                parts.append(f"  {i}. 《{law['title']}》")
                parts.append(f"     层级：{self._get_level_text(law['level'])} | 分类：{law.get('category', '未分类')}")

        # 新闻部分
        if news:
            parts.append("\n【相关资讯】")
            for i, item in enumerate(news[:3], 1):
                parts.append(f"  {i}. {item['title']}")
                if item.get('summary'):
                    parts.append(f"     摘要：{item['summary'][:100]}...")

        # 评论部分
        if comments:
            parts.append("\n【网友讨论】")
            for i, comment in enumerate(comments[:3], 1):
                parts.append(f"  {i}. [{comment.get('user_nickname', '网友')}]")
                parts.append(f"     {comment.get('content', '')[:100]}...")

        return "\n".join(parts) if parts else ""

    def _format_result(self, result: Dict) -> Dict:
        """格式化检索结果"""
        return {
            'id': result['id'],
            'title': result['title'],
            'level': result['level'],
            'level_text': self._get_level_text(result['level']),
            'category': result.get('category', '未分类'),
            'content_preview': result.get('content_preview', ''),
            'publish_date': result.get('publish_date', ''),
            'effective_date': result.get('effective_date', ''),
            'relevance': round(result.get('relevance_score', 0.5), 3)
        }

    def _get_level_text(self, level: str) -> str:
        """获取层级文本"""
        level_map = {
            'national': '国家级',
            'provincial': '省级',
            'city': '市级'
        }
        return level_map.get(level, level)

    def _parse_json(self, json_str: str) -> List[str]:
        """解析JSON字符串"""
        if not json_str:
            return []
        try:
            import json
            return json.loads(json_str)
        except:
            return []


# 全局实例
law_retrieval_service = LawRetrievalService()
