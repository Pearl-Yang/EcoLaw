package com.lvfat.service;

import com.lvfat.entity.Organization;

import java.util.List;

public interface OrganizationService {
    List<Organization> getTree();
    List<Organization> listOrganizations(String parentId, Integer level);
    List<Organization> getByParentId(String parentId);
    Organization getById(String id);
    Organization getWithUsers(String id);
    void save(Organization organization);
    void update(Organization organization);
    void updateById(Organization organization);
    void removeById(String id);
}
