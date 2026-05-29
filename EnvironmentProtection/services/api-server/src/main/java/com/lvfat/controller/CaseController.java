package com.lvfat.controller;

import com.lvfat.common.result.Result;
import com.lvfat.entity.CaseInfo;
import com.lvfat.entity.User;
import com.lvfat.repository.UserMapper;
import com.lvfat.service.CaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 案例管理控制器
 */
@Tag(name = "案例管理")
@RestController
@RequestMapping("/cases")
@RequiredArgsConstructor
public class CaseController {
    
    private final CaseService caseService;
    private final UserMapper userMapper;
    
    @Operation(summary = "获取案例列表")
    @GetMapping
    public Result<List<CaseInfo>> listCases(
            @Parameter(description = "页码") @RequestParam(required = false) Integer page,
            @Parameter(description = "每页数量") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "类型") @RequestParam(required = false) String type,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "律所ID") @RequestParam(required = false) String lawFirmId,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status
    ) {
        return Result.success(caseService.listCases(page, pageSize, type, keyword, lawFirmId, status));
    }
    
    @Operation(summary = "获取案例详情")
    @GetMapping("/{id}")
    public Result<CaseInfo> getCase(@PathVariable String id) {
        return Result.success(caseService.getById(id));
    }
    
    @Operation(summary = "创建案例")
    @PostMapping
    public Result<Void> createCase(@RequestBody CaseInfo caseInfo) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.findByUsername(username);
        if (user != null) {
            caseInfo.setLawyerId(user.getId());
            caseInfo.setLawyerName(user.getNickname());
        }
        caseInfo.setStatus(0); // 待审核
        caseService.save(caseInfo);
        return Result.success("案例创建成功");
    }
    
    @Operation(summary = "更新案例")
    @PutMapping("/{id}")
    public Result<Void> updateCase(@PathVariable String id, @RequestBody CaseInfo caseInfo) {
        caseInfo.setId(id);
        caseService.updateById(caseInfo);
        return Result.success("案例更新成功");
    }
    
    @Operation(summary = "删除案例")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCase(@PathVariable String id) {
        caseService.removeById(id);
        return Result.success("案例删除成功");
    }
    
    @Operation(summary = "审核案例")
    @PostMapping("/{id}/review")
    public Result<Void> reviewCase(
            @PathVariable String id,
            @RequestBody CaseInfo reviewData
    ) {
        CaseInfo caseInfo = caseService.getById(id);
        if (caseInfo != null) {
            caseInfo.setStatus(reviewData.getStatus());
            caseService.updateById(caseInfo);
        }
        return Result.success("审核完成");
    }
    
    @Operation(summary = "获取案例分类")
    @GetMapping("/categories")
    public Result<?> getCategories() {
        // 返回静态分类
        return Result.success(List.of(
            new java.util.HashMap<String, Object>() {{ put("value", "civil"); put("label", "民事案例"); put("count", 0); }},
            new java.util.HashMap<String, Object>() {{ put("value", "criminal"); put("label", "刑事案例"); put("count", 0); }},
            new java.util.HashMap<String, Object>() {{ put("value", "administrative"); put("label", "行政案例"); put("count", 0); }}
        ));
    }
}
