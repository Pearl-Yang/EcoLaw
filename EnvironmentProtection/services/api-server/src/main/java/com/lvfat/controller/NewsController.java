package com.lvfat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lvfat.common.result.Result;
import com.lvfat.dto.PageResult;
import com.lvfat.entity.News;
import com.lvfat.service.NewsService;
import com.lvfat.service.UserLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 新闻/热点管理
 */
@Tag(name = "新闻管理")
@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;
    private final UserLikeService userLikeService;

    @Operation(summary = "获取新闻列表（小程序端用，返回已发布）")
    @GetMapping
    public Result<List<News>> listNews(
            @Parameter(description = "类型") @RequestParam(required = false) String type,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword
    ) {
        return Result.success(newsService.listNews(type, keyword));
    }

    @Operation(summary = "获取新闻列表（管理端用，支持分页和状态筛选）")
    @GetMapping("/page")
    public Result<PageResult<News>> listNewsPage(
            @Parameter(description = "页码") @RequestParam(required = false, defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @Parameter(description = "类型") @RequestParam(required = false) String type,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) String status
    ) {
        IPage<News> result = newsService.listNewsPage(page, pageSize, type, keyword, status);
        return Result.success(PageResult.of(result.getRecords(), result.getTotal(), (int) result.getCurrent(), (int) result.getSize()));
    }

    @Operation(summary = "获取热门新闻")
    @GetMapping("/hot")
    public Result<List<News>> listHotNews(
            @Parameter(description = "数量限制") @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        return Result.success(newsService.listHotNews(limit));
    }

    @Operation(summary = "获取新闻详情")
    @GetMapping("/{id}")
    public Result<News> getNews(@PathVariable String id) {
        newsService.incrementViewCount(id);
        return Result.success(newsService.getById(id));
    }

    @Operation(summary = "点赞新闻")
    @PostMapping("/{id}/like")
    public Result<Void> likeNews(
            @PathVariable String id,
            @Parameter(description = "用户ID") @RequestParam String userId
    ) {
        if (userLikeService.isLiked(userId, "news", id)) {
            userLikeService.unlike(userId, "news", id);
            newsService.decrementLikeCount(id);
            return Result.success("取消点赞成功");
        } else {
            userLikeService.like(userId, "news", id);
            newsService.incrementLikeCount(id);
            return Result.success("点赞成功");
        }
    }

    @Operation(summary = "检查是否已点赞")
    @GetMapping("/{id}/like/status")
    public Result<Boolean> isLiked(
            @PathVariable String id,
            @Parameter(description = "用户ID") @RequestParam String userId
    ) {
        return Result.success(userLikeService.isLiked(userId, "news", id));
    }

    @Operation(summary = "创建新闻")
    @PostMapping
    public Result<Void> createNews(@RequestBody News news) {
        newsService.save(news);
        return Result.success("新闻创建成功");
    }

    @Operation(summary = "更新新闻")
    @PutMapping("/{id}")
    public Result<Void> updateNews(@PathVariable String id, @RequestBody News news) {
        news.setId(id);
        newsService.updateById(news);
        return Result.success("新闻更新成功");
    }

    @Operation(summary = "删除新闻")
    @DeleteMapping("/{id}")
    public Result<Void> deleteNews(@PathVariable String id) {
        newsService.removeById(id);
        return Result.success("新闻删除成功");
    }
}
