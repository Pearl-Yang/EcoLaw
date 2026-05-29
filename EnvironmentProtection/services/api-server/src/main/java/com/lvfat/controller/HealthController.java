package com.lvfat.controller;

import com.lvfat.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 */
@Tag(name = "健康检查")
@RestController
public class HealthController {
    
    @Operation(summary = "健康检查")
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("timestamp", LocalDateTime.now().toString());
        result.put("service", "lvfat-api-server");
        result.put("version", "1.0.0");
        return Result.success(result);
    }
    
    @Operation(summary = "根路径")
    @GetMapping("/")
    public Result<String> root() {
        return Result.success("Welcome to LvFat API Server");
    }
}
