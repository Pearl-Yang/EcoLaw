package com.lvfat.controller;

import com.lvfat.common.result.Result;
import com.lvfat.entity.Law;
import com.lvfat.service.LawService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 法规管理
 */
@Tag(name = "法规管理")
@RestController
@RequestMapping("/laws")
@RequiredArgsConstructor
public class LawController {

    private final LawService lawService;

    @Operation(summary = "获取法规列表")
    @GetMapping
    public Result<List<Law>> listLaws(
            @Parameter(description = "法规层级") @RequestParam(required = false) String level,
            @Parameter(description = "法规分类") @RequestParam(required = false) String category,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword
    ) {
        return Result.success(lawService.listLaws(level, category, keyword));
    }

    @Operation(summary = "获取法规详情")
    @GetMapping("/{id}")
    public Result<Law> getLaw(@PathVariable String id) {
        return Result.success(lawService.getById(id));
    }

    @Operation(summary = "创建法规")
    @PostMapping
    public Result<Void> createLaw(@RequestBody Law law) {
        lawService.save(law);
        return Result.success("法规创建成功");
    }

    @Operation(summary = "更新法规")
    @PutMapping("/{id}")
    public Result<Void> updateLaw(@PathVariable String id, @RequestBody Law law) {
        law.setId(id);
        lawService.updateById(law);
        return Result.success("法规更新成功");
    }

    @Operation(summary = "删除法规")
    @DeleteMapping("/{id}")
    public Result<Void> deleteLaw(@PathVariable String id) {
        lawService.removeById(id);
        return Result.success("法规删除成功");
    }
}