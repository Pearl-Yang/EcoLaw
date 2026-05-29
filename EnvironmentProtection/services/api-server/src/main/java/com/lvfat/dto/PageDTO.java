package com.lvfat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页查询DTO
 */
@Data
@Schema(description = "分页查询参数")
public class PageDTO {
    
    @Schema(description = "页码", example = "1")
    private Integer page = 1;
    
    @Schema(description = "每页数量", example = "10")
    private Integer pageSize = 10;
    
    @Schema(description = "关键词")
    private String keyword;
}
