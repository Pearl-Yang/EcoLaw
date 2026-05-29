package com.lvfat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页响应DTO
 */
@Data
@Schema(description = "分页响应")
public class PageResult<T> {
    
    @Schema(description = "总记录数")
    private Long total;
    
    @Schema(description = "当前页")
    private Integer page;
    
    @Schema(description = "每页数量")
    private Integer pageSize;
    
    @Schema(description = "总页数")
    private Integer totalPages;
    
    @Schema(description = "数据列表")
    private java.util.List<T> list;
    
    public static <T> PageResult<T> of(java.util.List<T> list, long total, int page, int pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setList(list);
        result.setTotal(total);
        result.setPage(page);
        result.setPageSize(pageSize);
        result.setTotalPages((int) Math.ceil((double) total / pageSize));
        return result;
    }
}
