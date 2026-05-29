package com.lvfat.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingVO {
    private Long id;
    private String title;
    private String content;
    private String type;
    private Integer duration;
    private String coverUrl;
    private String videoUrl;
    private String attachmentUrl;
    private Integer viewCount;
    private Integer completedCount;
    private Long creatorId;
    private String creatorName;
    private Integer status;
    private String createdAt;
    private String updatedAt;
}
