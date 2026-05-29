package com.lvfat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lvfat.entity.Organization;
import com.lvfat.entity.User;
import com.lvfat.repository.OrganizationMapper;
import com.lvfat.repository.UserMapper;
import com.lvfat.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationMapper organizationMapper;
    private final UserMapper userMapper;

    @Override
    public List<Organization> getTree() {
        List<Organization> all = organizationMapper.selectList(null);
        return buildTree(all, null);
    }

    private List<Organization> buildTree(List<Organization> all, String parentId) {
        return all.stream()
                .filter(org -> (parentId == null && org.getParentId() == null) 
                        || (parentId != null && parentId.equals(org.getParentId())))
                .peek(org -> {
                    List<Organization> children = buildTree(all, org.getId());
                    if (!children.isEmpty()) {
                        org.setChildren(children);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Organization> listOrganizations(String parentId, Integer level) {
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
        if (parentId != null && !parentId.isEmpty()) {
            wrapper.eq(Organization::getParentId, parentId);
        }
        if (level != null) {
            wrapper.eq(Organization::getLevel, level);
        }
        wrapper.orderByAsc(Organization::getLevel);
        return organizationMapper.selectList(wrapper);
    }

    @Override
    public List<Organization> getByParentId(String parentId) {
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
        if (parentId != null && !parentId.isEmpty()) {
            wrapper.eq(Organization::getParentId, parentId);
        } else {
            wrapper.isNull(Organization::getParentId);
        }
        return organizationMapper.selectList(wrapper);
    }

    @Override
    public Organization getById(String id) {
        return organizationMapper.selectById(id);
    }

    @Override
    public Organization getWithUsers(String id) {
        Organization org = organizationMapper.selectById(id);
        if (org != null) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getOrganizationId, id);
            List<User> users = userMapper.selectList(wrapper);
            org.setUsers(users);
        }
        return org;
    }

    @Override
    public void save(Organization organization) {
        if (organization.getLevel() == null) {
            organization.setLevel(1);
        }
        organizationMapper.insert(organization);
    }

    @Override
    public void update(Organization organization) {
        organizationMapper.updateById(organization);
    }

    @Override
    public void updateById(Organization organization) {
        organizationMapper.updateById(organization);
    }

    @Override
    public void removeById(String id) {
        organizationMapper.deleteById(id);
    }
}
