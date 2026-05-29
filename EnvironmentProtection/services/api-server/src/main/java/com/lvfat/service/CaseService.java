package com.lvfat.service;

import com.lvfat.entity.CaseInfo;

import java.util.List;

public interface CaseService {
    List<CaseInfo> listCases(Integer page, Integer pageSize, String type, String keyword, String lawFirmId, Integer status);
    CaseInfo getById(String id);
    void save(CaseInfo caseInfo);
    void update(CaseInfo caseInfo);
    void updateById(CaseInfo caseInfo);
    void removeById(String id);
    void review(String id, String status);
}
