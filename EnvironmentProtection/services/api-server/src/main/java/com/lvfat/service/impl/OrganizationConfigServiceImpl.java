package com.lvfat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lvfat.entity.OrganizationType;
import com.lvfat.entity.OrganizationCategory;
import com.lvfat.repository.OrganizationTypeMapper;
import com.lvfat.repository.OrganizationCategoryMapper;
import com.lvfat.service.OrganizationConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationConfigServiceImpl extends ServiceImpl<OrganizationTypeMapper, OrganizationType>
        implements OrganizationConfigService {

    private final OrganizationTypeMapper organizationTypeMapper;
    private final OrganizationCategoryMapper organizationCategoryMapper;

    @Override
    public List<OrganizationType> getAllTypes() {
        return organizationTypeMapper.findAll();
    }

    @Override
    public List<OrganizationType> getActiveTypes() {
        return organizationTypeMapper.findActive();
    }

    @Override
    public OrganizationType getTypeByCode(String code) {
        return organizationTypeMapper.findByCode(code);
    }

    @Override
    public List<OrganizationCategory> getCategoriesByType(String typeCode) {
        return organizationCategoryMapper.findByTypeCode(typeCode);
    }

    @Override
    public List<OrganizationCategory> getActiveCategoriesByType(String typeCode) {
        return organizationCategoryMapper.findActiveByTypeCode(typeCode);
    }

    @Override
    public OrganizationCategory getCategoryDetail(String typeCode, String code) {
        return organizationCategoryMapper.findByTypeCodeAndCode(typeCode, code);
    }

    @Override
    public void saveType(OrganizationType type) {
        organizationTypeMapper.insert(type);
    }

    @Override
    public void updateType(OrganizationType type) {
        organizationTypeMapper.updateById(type);
    }

    @Override
    public void deleteType(String id) {
        organizationTypeMapper.deleteById(id);
    }

    @Override
    public void saveCategory(OrganizationCategory category) {
        organizationCategoryMapper.insert(category);
    }

    @Override
    public void updateCategory(OrganizationCategory category) {
        organizationCategoryMapper.updateById(category);
    }

    @Override
    public void deleteCategory(String id) {
        organizationCategoryMapper.deleteById(id);
    }
}
