package com.lvfat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lvfat.common.result.Result;
import com.lvfat.entity.Organization;
import com.lvfat.entity.School;
import com.lvfat.service.SchoolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 学校管理 Controller
 */
@Tag(name = "学校管理")
@RestController
@RequestMapping("/schools")
@RequiredArgsConstructor
public class SchoolController {
    
    private final SchoolService schoolService;
    
    @Operation(summary = "分页查询学校")
    @GetMapping
    public Result<IPage<School>> pageSchools(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "学校名称") @RequestParam(required = false) String schoolName,
            @Parameter(description = "学校类型") @RequestParam(required = false) String schoolType,
            @Parameter(description = "教育局ID") @RequestParam(required = false) String educationBureauId
    ) {
        Page<School> page = new Page<>(pageNum, pageSize);
        IPage<School> result = schoolService.pageSchools(page, schoolName, schoolType, educationBureauId);
        return Result.success(result);
    }
    
    @Operation(summary = "获取学校详情")
    @GetMapping("/{id}")
    public Result<School> getSchool(@PathVariable String id) {
        return Result.success(schoolService.getById(id));
    }
    
    @Operation(summary = "根据组织ID获取学校")
    @GetMapping("/organization/{organizationId}")
    public Result<School> getByOrganizationId(@PathVariable String organizationId) {
        return Result.success(schoolService.getByOrganizationId(organizationId));
    }
    
    @Operation(summary = "根据学校代码获取学校")
    @GetMapping("/code/{schoolCode}")
    public Result<School> getBySchoolCode(@PathVariable String schoolCode) {
        return Result.success(schoolService.getBySchoolCode(schoolCode));
    }
    
    @Operation(summary = "获取教育局下的学校列表")
    @GetMapping("/bureau/{bureauId}")
    public Result<List<School>> getByEducationBureau(@PathVariable String bureauId) {
        return Result.success(schoolService.getByEducationBureauId(bureauId));
    }
    
    @Operation(summary = "获取学校列表")
    @GetMapping("/list")
    public Result<List<School>> listSchools(
            @Parameter(description = "学校类型") @RequestParam(required = false) String schoolType
    ) {
        return Result.success(schoolService.listSchools(schoolType));
    }
    
    @Operation(summary = "创建学校")
    @PostMapping
    public Result<Void> createSchool(@RequestBody School school) {
        schoolService.create(school);
        return Result.success("学校创建成功");
    }
    
    @Operation(summary = "更新学校")
    @PutMapping("/{id}")
    public Result<Void> updateSchool(@PathVariable String id, @RequestBody School school) {
        school.setId(id);
        schoolService.update(school);
        return Result.success("学校更新成功");
    }
    
    @Operation(summary = "删除学校")
    @DeleteMapping("/{id}")
    public Result<Void> deleteSchool(@PathVariable String id) {
        schoolService.delete(id);
        return Result.success("学校删除成功");
    }
    
    @Operation(summary = "获取学校管辖链")
    @GetMapping("/{id}/jurisdiction")
    public Result<List<Organization>> getJurisdictionChain(@PathVariable String id) {
        School school = schoolService.getById(id);
        if (school == null || school.getOrganizationId() == null) {
            return Result.success(List.of());
        }
        return Result.success(schoolService.getJurisdictionChain(school.getOrganizationId()));
    }
    
    @Operation(summary = "获取学校统计信息")
    @GetMapping("/{id}/statistics")
    public Result<Map<String, Object>> getStatistics(@PathVariable String id) {
        return Result.success(schoolService.getStatistics(id));
    }
}
