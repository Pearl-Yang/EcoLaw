package com.lvfat.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportVO {
    private Long id;
    private String title;
    private String content;
    private String type;
    private String location;
    private Double longitude;
    private Double latitude;
    private String images;
    private String status;
    private Long reporterId;
    private String reporterName;
    private String reporterPhone;
    private Long handlerId;
    private String handlerName;
    private String handleResult;
    private String createdAt;
    private String handleAt;
}
