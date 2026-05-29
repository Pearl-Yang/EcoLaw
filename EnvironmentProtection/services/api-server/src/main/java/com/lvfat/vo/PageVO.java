package com.lvfat.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageVO<T> {
    private List<T> list;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}
