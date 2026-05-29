-- =============================================
-- 智能搜索 FULLTEXT 索引修复脚本
-- 用于确保 law、news、comment 表有全文检索索引
-- =============================================

USE lvfat;

-- 1. 检查并添加 law 表 FULLTEXT 索引
ALTER TABLE law DROP INDEX IF EXISTS idx_title_content;
ALTER TABLE law ADD FULLTEXT INDEX idx_title_content (title, content);

-- 2. 检查并添加 news 表 FULLTEXT 索引
ALTER TABLE news DROP INDEX IF EXISTS idx_title_content;
ALTER TABLE news ADD FULLTEXT INDEX idx_title_content (title, content);

-- 3. 检查并添加 comment 表 FULLTEXT 索引（原来缺失）
ALTER TABLE comment DROP INDEX IF EXISTS idx_content_fulltext;
ALTER TABLE comment ADD FULLTEXT INDEX idx_content_fulltext (content);

-- 4. 验证索引是否创建成功
SHOW INDEX FROM law;
SHOW INDEX FROM news;
SHOW INDEX FROM comment;

SELECT '✅ FULLTEXT 索引创建完成！' AS message;