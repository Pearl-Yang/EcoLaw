package com.lvfat.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseVO {
    private Long id;
    private String title;
    private String type;
    private String description;
    private String result;
    private Long lawFirmId;
    private String lawFirmName;
    private String lawyerName;
    private String coverUrl;
    private Integer viewCount;
    private Integer status;
    private String createdAt;
    private String updatedAt;
}
