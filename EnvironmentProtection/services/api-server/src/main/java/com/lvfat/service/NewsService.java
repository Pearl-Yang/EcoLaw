package com.lvfat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lvfat.entity.News;
import java.util.List;

/**
 * 新闻服务接口
 */
public interface NewsService extends IService<News> {
    /**
     * 获取新闻列表（仅返回已发布）
     */
    List<News> listNews(String type, String keyword);

    /**
     * 获取新闻列表（支持分页，返回所有状态，用于管理端）
     */
    IPage<News> listNewsPage(Integer page, Integer pageSize, String type, String keyword, String status);

    /**
     * 获取热门新闻
     */
    List<News> listHotNews(Integer limit);

    /**
     * 增加浏览次数
     */
    void incrementViewCount(String id);

    /**
     * 增加点赞次数
     */
    void incrementLikeCount(String id);

    /**
     * 减少点赞次数
     */
    void decrementLikeCount(String id);

    /**
     * 增加评论次数
     */
    void incrementCommentCount(String id);
}
