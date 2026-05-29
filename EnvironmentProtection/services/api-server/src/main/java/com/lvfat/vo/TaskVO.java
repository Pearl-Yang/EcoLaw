package com.lvfat.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskVO {
    private Long id;
    private String title;
    private String content;
    private String type;
    private String status;
    private Long creatorId;
    private String creatorName;
    private Long organizationId;
    private String organizationName;
    private String startTime;
    private String endTime;
    private Integer targetCount;
    private Integer completedCount;
    private Double progress;
    private String createdAt;
    private String updatedAt;
}
