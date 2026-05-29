package com.lvfat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lvfat.common.result.Result;
import com.lvfat.entity.Guardian;
import com.lvfat.entity.Student;
import com.lvfat.service.GuardianService;
import com.lvfat.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 学生管理 Controller
 */
@Tag(name = "学生管理")
@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {
    
    private final StudentService studentService;
    private final GuardianService guardianService;
    
    @Operation(summary = "分页查询学生")
    @GetMapping
    public Result<IPage<Student>> pageStudents(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "学校ID") @RequestParam(required = false) String schoolId,
            @Parameter(description = "班级ID") @RequestParam(required = false) String classId,
            @Parameter(description = "年级") @RequestParam(required = false) Integer grade,
            @Parameter(description = "学生姓名") @RequestParam(required = false) String studentName,
            @Parameter(description = "学籍状态") @RequestParam(required = false) String studentStatus
    ) {
        Page<Student> page = new Page<>(pageNum, pageSize);
        IPage<Student> result = studentService.pageStudents(page, schoolId, classId, grade, studentName, studentStatus);
        return Result.success(result);
    }
    
    @Operation(summary = "获取学生详情")
    @GetMapping("/{id}")
    public Result<Student> getStudent(@PathVariable String id) {
        return Result.success(studentService.getById(id));
    }
    
    @Operation(summary = "根据学号获取学生")
    @GetMapping("/no/{studentNo}")
    public Result<Student> getByStudentNo(@PathVariable String studentNo) {
        return Result.success(studentService.getByStudentNo(studentNo));
    }
    
    @Operation(summary = "根据学校获取学生列表")
    @GetMapping("/school/{schoolId}")
    public Result<List<Student>> getBySchool(@PathVariable String schoolId) {
        return Result.success(studentService.getBySchoolId(schoolId));
    }
    
    @Operation(summary = "根据班级获取学生列表")
    @GetMapping("/class/{classId}")
    public Result<List<Student>> getByClass(@PathVariable String classId) {
        return Result.success(studentService.getByClassId(classId));
    }
    
    @Operation(summary = "根据年级获取学生列表")
    @GetMapping("/school/{schoolId}/grade/{grade}")
    public Result<List<Student>> getBySchoolAndGrade(
            @PathVariable String schoolId, 
            @PathVariable Integer grade
    ) {
        return Result.success(studentService.getBySchoolIdAndGrade(schoolId, grade));
    }
    
    @Operation(summary = "获取学生监护人列表")
    @GetMapping("/{id}/guardians")
    public Result<List<Guardian>> getGuardians(@PathVariable String id) {
        return Result.success(studentService.getGuardians(id));
    }
    
    @Operation(summary = "创建学生")
    @PostMapping
    public Result<Void> createStudent(@RequestBody Student student) {
        studentService.create(student);
        return Result.success("学生创建成功");
    }
    
    @Operation(summary = "批量导入学生")
    @PostMapping("/batch")
    public Result<Void> batchImport(@RequestBody List<Student> students) {
        studentService.batchImport(students);
        return Result.success("学生批量导入成功");
    }
    
    @Operation(summary = "更新学生")
    @PutMapping("/{id}")
    public Result<Void> updateStudent(@PathVariable String id, @RequestBody Student student) {
        student.setId(id);
        studentService.update(student);
        return Result.success("学生更新成功");
    }
    
    @Operation(summary = "删除学生")
    @DeleteMapping("/{id}")
    public Result<Void> deleteStudent(@PathVariable String id) {
        studentService.delete(id);
        return Result.success("学生删除成功");
    }
    
    @Operation(summary = "调整班级")
    @PutMapping("/{id}/transfer")
    public Result<Void> transferClass(
            @PathVariable String id,
            @Parameter(description = "新班级ID") @RequestParam String newClassId,
            @Parameter(description = "新年级") @RequestParam(required = false) Integer newGrade
    ) {
        studentService.transferClass(id, newClassId, newGrade);
        return Result.success("班级调整成功");
    }
    
    @Operation(summary = "变更学籍状态")
    @PutMapping("/{id}/status")
    public Result<Void> changeStatus(
            @PathVariable String id,
            @Parameter(description = "状态") @RequestParam String status
    ) {
        studentService.changeStatus(id, status);
        return Result.success("学籍状态变更成功");
    }
    
    @Operation(summary = "获取学生统计信息")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(
            @Parameter(description = "学校ID") @RequestParam(required = false) String schoolId,
            @Parameter(description = "班级ID") @RequestParam(required = false) String classId
    ) {
        return Result.success(studentService.getStatistics(schoolId, classId));
    }
    
    @Operation(summary = "检查学号是否可用")
    @GetMapping("/check-student-no")
    public Result<Boolean> checkStudentNo(
            @Parameter(description = "学号") @RequestParam String studentNo,
            @Parameter(description = "排除的学生ID") @RequestParam(required = false) String excludeId
    ) {
        return Result.success(studentService.isStudentNoAvailable(studentNo, excludeId));
    }
}
