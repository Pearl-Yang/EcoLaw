package com.lvfat.controller;

import com.lvfat.common.result.Result;
import com.lvfat.entity.Organization;
import com.lvfat.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 组织管理
 */
@Tag(name = "组织管理")
@RestController
@RequestMapping("/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @Operation(summary = "获取组织树")
    @GetMapping("/tree")
    public Result<List<Organization>> getTree() {
        return Result.success(organizationService.getTree());
    }

    @Operation(summary = "获取组织列表")
    @GetMapping
    public Result<List<Organization>> listOrganizations(
            @Parameter(description = "父级ID") @RequestParam(required = false) String parentId,
            @Parameter(description = "层级") @RequestParam(required = false) Integer level
    ) {
        return Result.success(organizationService.listOrganizations(parentId, level));
    }

    @Operation(summary = "获取组织详情")
    @GetMapping("/{id}")
    public Result<Organization> getOrganization(@PathVariable String id) {
        return Result.success(organizationService.getById(id));
    }

    @Operation(summary = "创建组织")
    @PostMapping
    public Result<Void> createOrganization(@RequestBody Organization organization) {
        organizationService.save(organization);
        return Result.success("组织创建成功");
    }

    @Operation(summary = "更新组织")
    @PutMapping("/{id}")
    public Result<Void> updateOrganization(@PathVariable String id, @RequestBody Organization organization) {
        organization.setId(id);
        organizationService.update(organization);
        return Result.success("组织更新成功");
    }

    @Operation(summary = "删除组织")
    @DeleteMapping("/{id}")
    public Result<Void> deleteOrganization(@PathVariable String id) {
        organizationService.removeById(id);
        return Result.success("删除成功");
    }
}
