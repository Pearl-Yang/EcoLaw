package com.lvfat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lvfat.common.result.Result;
import com.lvfat.entity.SchoolClass;
import com.lvfat.service.SchoolClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 班级管理 Controller
 */
@Tag(name = "班级管理")
@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
public class SchoolClassController {
    
    private final SchoolClassService classService;
    
    @Operation(summary = "分页查询班级")
    @GetMapping
    public Result<IPage<SchoolClass>> pageClasses(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "学校ID") @RequestParam(required = false) String schoolId,
            @Parameter(description = "年级") @RequestParam(required = false) Integer grade,
            @Parameter(description = "学年") @RequestParam(required = false) String academicYear
    ) {
        Page<SchoolClass> page = new Page<>(pageNum, pageSize);
        IPage<SchoolClass> result = classService.pageClasses(page, schoolId, grade, academicYear);
        return Result.success(result);
    }
    
    @Operation(summary = "获取班级详情")
    @GetMapping("/{id}")
    public Result<SchoolClass> getClass(@PathVariable String id) {
        return Result.success(classService.getById(id));
    }
    
    @Operation(summary = "根据学校ID获取班级列表")
    @GetMapping("/school/{schoolId}")
    public Result<List<SchoolClass>> getBySchool(@PathVariable String schoolId) {
        return Result.success(classService.getBySchoolId(schoolId));
    }
    
    @Operation(summary = "根据年级获取班级列表")
    @GetMapping("/school/{schoolId}/grade/{grade}")
    public Result<List<SchoolClass>> getBySchoolAndGrade(
            @PathVariable String schoolId, 
            @PathVariable Integer grade
    ) {
        return Result.success(classService.getBySchoolIdAndGrade(schoolId, grade));
    }
    
    @Operation(summary = "根据班主任获取班级")
    @GetMapping("/teacher/{teacherId}")
    public Result<List<SchoolClass>> getByTeacher(@PathVariable String teacherId) {
        return Result.success(classService.getByHeadTeacherId(teacherId));
    }
    
    @Operation(summary = "获取班级学生数")
    @GetMapping("/{id}/student-count")
    public Result<Integer> getStudentCount(@PathVariable String id) {
        return Result.success(classService.getStudentCount(id));
    }
    
    @Operation(summary = "创建班级")
    @PostMapping
    public Result<Void> createClass(@RequestBody SchoolClass schoolClass) {
        classService.create(schoolClass);
        return Result.success("班级创建成功");
    }
    
    @Operation(summary = "批量创建班级")
    @PostMapping("/batch")
    public Result<Void> batchCreateClasses(@RequestBody List<SchoolClass> classes) {
        classService.batchCreate(classes);
        return Result.success("班级批量创建成功");
    }
    
    @Operation(summary = "更新班级")
    @PutMapping("/{id}")
    public Result<Void> updateClass(@PathVariable String id, @RequestBody SchoolClass schoolClass) {
        schoolClass.setId(id);
        classService.update(schoolClass);
        return Result.success("班级更新成功");
    }
    
    @Operation(summary = "删除班级")
    @DeleteMapping("/{id}")
    public Result<Void> deleteClass(@PathVariable String id) {
        classService.delete(id);
        return Result.success("班级删除成功");
    }
    
    @Operation(summary = "设置班主任")
    @PutMapping("/{id}/head-teacher")
    public Result<Void> setHeadTeacher(
            @PathVariable String id,
            @Parameter(description = "教师ID") @RequestParam(required = false) String teacherId,
            @Parameter(description = "教师名称") @RequestParam(required = false) String teacherName
    ) {
        classService.setHeadTeacher(id, teacherId, teacherName);
        return Result.success("班主任设置成功");
    }
    
    @Operation(summary = "获取学校班级统计")
    @GetMapping("/school/{schoolId}/statistics")
    public Result<Map<String, Object>> getStatistics(@PathVariable String schoolId) {
        return Result.success(classService.getStatistics(schoolId));
    }
    
    @Operation(summary = "获取学校年级列表")
    @GetMapping("/school/{schoolId}/grades")
    public Result<List<Integer>> getGrades(@PathVariable String schoolId) {
        return Result.success(classService.getGradesBySchool(schoolId));
    }
}
