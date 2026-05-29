package com.lvfat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lvfat.entity.CaseInfo;
import com.lvfat.repository.CaseInfoMapper;
import com.lvfat.service.CaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CaseServiceImpl implements CaseService {

    private final CaseInfoMapper caseInfoMapper;

    @Override
    public List<CaseInfo> listCases(Integer page, Integer pageSize, String type, String keyword, String lawFirmId, Integer status) {
        LambdaQueryWrapper<CaseInfo> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            wrapper.eq(CaseInfo::getType, type);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(CaseInfo::getTitle, keyword).or().like(CaseInfo::getDescription, keyword));
        }
        if (lawFirmId != null && !lawFirmId.isEmpty()) {
            wrapper.eq(CaseInfo::getLawFirmId, lawFirmId);
        }
        if (status != null) {
            wrapper.eq(CaseInfo::getStatus, status);
        }
        wrapper.orderByDesc(CaseInfo::getCreateTime);
        
        if (page != null && pageSize != null) {
            IPage<CaseInfo> result = caseInfoMapper.selectPage(new Page<>(page, pageSize), wrapper);
            return result.getRecords();
        }
        return caseInfoMapper.selectList(wrapper);
    }

    @Override
    public CaseInfo getById(String id) {
        return caseInfoMapper.selectById(id);
    }

    @Override
    public void save(CaseInfo caseInfo) {
        if (caseInfo.getStatus() == null) {
            caseInfo.setStatus(0);
        }
        caseInfoMapper.insert(caseInfo);
    }

    @Override
    public void update(CaseInfo caseInfo) {
        caseInfoMapper.updateById(caseInfo);
    }

    @Override
    public void updateById(CaseInfo caseInfo) {
        caseInfoMapper.updateById(caseInfo);
    }

    @Override
    public void removeById(String id) {
        caseInfoMapper.deleteById(id);
    }

    @Override
    public void review(String id, String status) {
        CaseInfo caseInfo = caseInfoMapper.selectById(id);
        if (caseInfo != null) {
            caseInfo.setStatus(Integer.parseInt(status));
            caseInfoMapper.updateById(caseInfo);
        }
    }
}
