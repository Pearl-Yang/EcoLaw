package com.lvfat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lvfat.entity.OrganizationType;
import com.lvfat.entity.OrganizationCategory;

import java.util.List;

/**
 * 组织类型配置服务接口
 */
public interface OrganizationConfigService extends IService<OrganizationType> {
    
    /**
     * 获取所有组织类型
     */
    List<OrganizationType> getAllTypes();
    
    /**
     * 获取启用的组织类型
     */
    List<OrganizationType> getActiveTypes();
    
    /**
     * 根据编码获取组织类型
     */
    OrganizationType getTypeByCode(String code);
    
    /**
     * 获取指定类型的所有分类
     */
    List<OrganizationCategory> getCategoriesByType(String typeCode);
    
    /**
     * 获取指定类型的启用分类
     */
    List<OrganizationCategory> getActiveCategoriesByType(String typeCode);
    
    /**
     * 获取分类详情
     */
    OrganizationCategory getCategoryDetail(String typeCode, String code);
    
    /**
     * 保存组织类型
     */
    void saveType(OrganizationType type);
    
    /**
     * 更新组织类型
     */
    void updateType(OrganizationType type);
    
    /**
     * 删除组织类型
     */
    void deleteType(String id);
    
    /**
     * 保存组织分类
     */
    void saveCategory(OrganizationCategory category);
    
    /**
     * 更新组织分类
     */
    void updateCategory(OrganizationCategory category);
    
    /**
     * 删除组织分类
     */
    void deleteCategory(String id);
}
