package com.lvfat.controller;

import com.lvfat.common.result.Result;
import com.lvfat.entity.OrganizationType;
import com.lvfat.entity.OrganizationCategory;
import com.lvfat.service.OrganizationConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "组织类型配置")
@RestController
@RequestMapping("/organization-config")
@RequiredArgsConstructor
public class OrganizationConfigController {
    
    private final OrganizationConfigService organizationConfigService;
    
    @Operation(summary = "获取所有组织类型")
    @GetMapping("/types")
    public Result<List<OrganizationType>> getAllTypes() {
        return Result.success(organizationConfigService.getAllTypes());
    }
    
    @Operation(summary = "获取启用的组织类型")
    @GetMapping("/types/active")
    public Result<List<OrganizationType>> getActiveTypes() {
        return Result.success(organizationConfigService.getActiveTypes());
    }
    
    @Operation(summary = "根据编码获取组织类型")
    @GetMapping("/types/code/{code}")
    public Result<OrganizationType> getTypeByCode(@PathVariable String code) {
        return Result.success(organizationConfigService.getTypeByCode(code));
    }
    
    @Operation(summary = "创建组织类型")
    @PostMapping("/types")
    public Result<Void> createType(@RequestBody OrganizationType type) {
        organizationConfigService.saveType(type);
        return Result.success("组织类型创建成功");
    }
    
    @Operation(summary = "更新组织类型")
    @PutMapping("/types/{id}")
    public Result<Void> updateType(@PathVariable String id, @RequestBody OrganizationType type) {
        type.setId(id);
        organizationConfigService.updateType(type);
        return Result.success("组织类型更新成功");
    }
    
    @Operation(summary = "删除组织类型")
    @DeleteMapping("/types/{id}")
    public Result<Void> deleteType(@PathVariable String id) {
        organizationConfigService.deleteType(id);
        return Result.success("组织类型删除成功");
    }
    
    @Operation(summary = "获取指定类型的所有分类")
    @GetMapping("/categories/{typeCode}")
    public Result<List<OrganizationCategory>> getCategoriesByType(@PathVariable String typeCode) {
        return Result.success(organizationConfigService.getCategoriesByType(typeCode));
    }
    
    @Operation(summary = "获取指定类型的启用分类")
    @GetMapping("/categories/{typeCode}/active")
    public Result<List<OrganizationCategory>> getActiveCategoriesByType(@PathVariable String typeCode) {
        return Result.success(organizationConfigService.getActiveCategoriesByType(typeCode));
    }
    
    @Operation(summary = "获取分类详情")
    @GetMapping("/categories/{typeCode}/{code}")
    public Result<OrganizationCategory> getCategoryDetail(
            @PathVariable String typeCode,
            @PathVariable String code
    ) {
        return Result.success(organizationConfigService.getCategoryDetail(typeCode, code));
    }
    
    @Operation(summary = "创建组织分类")
    @PostMapping("/categories")
    public Result<Void> createCategory(@RequestBody OrganizationCategory category) {
        organizationConfigService.saveCategory(category);
        return Result.success("组织分类创建成功");
    }
    
    @Operation(summary = "更新组织分类")
    @PutMapping("/categories/{id}")
    public Result<Void> updateCategory(@PathVariable String id, @RequestBody OrganizationCategory category) {
        category.setId(id);
        organizationConfigService.updateCategory(category);
        return Result.success("组织分类更新成功");
    }
    
    @Operation(summary = "删除组织分类")
    @DeleteMapping("/categories/{id}")
    public Result<Void> deleteCategory(@PathVariable String id) {
        organizationConfigService.deleteCategory(id);
        return Result.success("组织分类删除成功");
    }
}
