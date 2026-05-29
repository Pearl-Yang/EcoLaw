package com.lvfat.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialVO {
    private Long id;
    private String title;
    private String type;
    private String content;
    private String coverUrl;
    private String sourceUrl;
    private Long creatorId;
    private String creatorName;
    private Integer status;
    private String createdAt;
}
