package com.lvfat.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LawVO {
    private Long id;
    private String title;
    private String level;
    private String category;
    private String content;
    private String publishDate;
    private String effectiveDate;
    private String publisher;
    private Integer viewCount;
    private String createdAt;
}
