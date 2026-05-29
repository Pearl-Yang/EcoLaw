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
public class OrganizationVO {
    private Long id;
    private String name;
    private Integer level;
    private Long parentId;
    private String parentName;
    private String code;
    private String description;
    private Integer status;
    private List<OrganizationVO> children;
}
