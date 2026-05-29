package com.lvfat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lvfat.entity.News;
import com.lvfat.repository.NewsMapper;
import com.lvfat.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 新闻服务实现
 */
@Service
@RequiredArgsConstructor
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {

    @Override
    public List<News> listNews(String type, String keyword) {
        QueryWrapper<News> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "published");
        if (StringUtils.hasText(type)) {
            wrapper.eq("type", type);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like("title", keyword).or().like("summary", keyword));
        }
        wrapper.orderByDesc("is_top", "publish_time");
        return list(wrapper);
    }

    @Override
    public IPage<News> listNewsPage(Integer page, Integer pageSize, String type, String keyword, String status) {
        QueryWrapper<News> wrapper = new QueryWrapper<>();
        // 如果没有指定状态，默认返回所有状态（包括草稿），管理端需要看到所有新闻
        if (StringUtils.hasText(status)) {
            wrapper.eq("status", status);
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq("type", type);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like("title", keyword).or().like("summary", keyword));
        }
        wrapper.orderByDesc("is_top", "publish_time", "create_time");
        return page(new Page<>(page != null ? page : 1, pageSize != null ? pageSize : 10), wrapper);
    }

    @Override
    public List<News> listHotNews(Integer limit) {
        QueryWrapper<News> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "published")
                .eq("is_hot", 1)
                .orderByDesc("view_count", "publish_time");
        if (limit != null && limit > 0) {
            wrapper.last("LIMIT " + limit);
        }
        return list(wrapper);
    }

    @Override
    public void incrementViewCount(String id) {
        News news = getById(id);
        if (news != null) {
            news.setViewCount(news.getViewCount() + 1);
            updateById(news);
        }
    }

    @Override
    public void incrementLikeCount(String id) {
        News news = getById(id);
        if (news != null) {
            news.setLikeCount(news.getLikeCount() + 1);
            updateById(news);
        }
    }

    @Override
    public void decrementLikeCount(String id) {
        News news = getById(id);
        if (news != null && news.getLikeCount() > 0) {
            news.setLikeCount(news.getLikeCount() - 1);
            updateById(news);
        }
    }

    @Override
    public void incrementCommentCount(String id) {
        News news = getById(id);
        if (news != null) {
            news.setCommentCount(news.getCommentCount() + 1);
            updateById(news);
        }
    }
}
