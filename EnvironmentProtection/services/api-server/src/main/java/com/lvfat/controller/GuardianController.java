package com.lvfat.controller;

import com.lvfat.common.result.Result;
import com.lvfat.entity.Guardian;
import com.lvfat.service.GuardianService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 监护人管理 Controller
 */
@Tag(name = "监护人管理")
@RestController
@RequestMapping("/guardians")
@RequiredArgsConstructor
public class GuardianController {
    
    private final GuardianService guardianService;
    
    @Operation(summary = "获取监护人详情")
    @GetMapping("/{id}")
    public Result<Guardian> getGuardian(@PathVariable String id) {
        return Result.success(guardianService.getById(id));
    }
    
    @Operation(summary = "获取学生的监护人列表")
    @GetMapping("/student/{studentId}")
    public Result<List<Guardian>> getByStudent(@PathVariable String studentId) {
        return Result.success(guardianService.getByStudentId(studentId));
    }
    
    @Operation(summary = "获取紧急联系人")
    @GetMapping("/student/{studentId}/emergency")
    public Result<Guardian> getEmergencyContact(@PathVariable String studentId) {
        return Result.success(guardianService.getEmergencyContact(studentId));
    }
    
    @Operation(summary = "获取紧急联系人列表")
    @GetMapping("/student/{studentId}/emergency-list")
    public Result<List<Guardian>> getEmergencyContacts(@PathVariable String studentId) {
        return Result.success(guardianService.getEmergencyContacts(studentId));
    }
    
    @Operation(summary = "根据手机号查找监护人")
    @GetMapping("/phone/{phone}")
    public Result<Guardian> getByPhone(@PathVariable String phone) {
        return Result.success(guardianService.getByPhone(phone));
    }
    
    @Operation(summary = "创建监护人")
    @PostMapping
    public Result<Void> createGuardian(@RequestBody Guardian guardian) {
        guardianService.create(guardian);
        return Result.success("监护人创建成功");
    }
    
    @Operation(summary = "批量创建监护人")
    @PostMapping("/batch")
    public Result<Void> batchCreate(@RequestBody List<Guardian> guardians) {
        guardianService.batchCreate(guardians);
        return Result.success("监护人批量创建成功");
    }
    
    @Operation(summary = "更新监护人")
    @PutMapping("/{id}")
    public Result<Void> updateGuardian(@PathVariable String id, @RequestBody Guardian guardian) {
        guardian.setId(id);
        guardianService.update(guardian);
        return Result.success("监护人更新成功");
    }
    
    @Operation(summary = "删除监护人")
    @DeleteMapping("/{id}")
    public Result<Void> deleteGuardian(@PathVariable String id) {
        guardianService.delete(id);
        return Result.success("监护人删除成功");
    }
    
    @Operation(summary = "设置紧急联系人")
    @PutMapping("/{id}/emergency")
    public Result<Void> setEmergency(
            @PathVariable String id,
            @Parameter(description = "是否紧急联系人") @RequestParam boolean isEmergency
    ) {
        guardianService.setEmergencyContact(id, isEmergency);
        return Result.success("紧急联系人设置成功");
    }
    
    @Operation(summary = "检查手机号是否可用")
    @GetMapping("/check-phone")
    public Result<Boolean> checkPhone(
            @Parameter(description = "手机号") @RequestParam String phone,
            @Parameter(description = "排除的监护人ID") @RequestParam(required = false) String excludeId
    ) {
        return Result.success(guardianService.isPhoneAvailable(phone, excludeId));
    }
}
