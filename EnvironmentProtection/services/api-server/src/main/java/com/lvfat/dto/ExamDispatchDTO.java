package com.lvfat.dto;

import lombok.Data;

import java.util.List;

/**
 * 考试下发请求DTO
 */
@Data
public class ExamDispatchDTO {
    
    /**
     * 试卷ID
     */
    private String paperId;
    
    /**
     * 下发用户ID列表
     */
    private List<String> userIds;
    
    /**
     * 下发组织ID列表
     */
    private List<String> organizationIds;
    
    /**
     * 下发类型: user-按用户, organization-按组织
     */
    private String dispatchType;
    
    /**
     * 关联任务ID(可选)
     */
    private String taskId;
    
    /**
     * 截止时间
     */
    private String deadline;
    
    /**
     * 备注
     */
    private String remark;
}
