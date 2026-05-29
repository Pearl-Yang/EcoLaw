package com.lvfat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lvfat.common.exception.BusinessException;
import com.lvfat.entity.Organization;
import com.lvfat.entity.School;
import com.lvfat.entity.SchoolClass;
import com.lvfat.repository.OrganizationMapper;
import com.lvfat.repository.SchoolClassMapper;
import com.lvfat.repository.SchoolMapper;
import com.lvfat.repository.StudentMapper;
import com.lvfat.service.SchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学校服务实现
 */
@Service
@RequiredArgsConstructor
public class SchoolServiceImpl implements SchoolService {
    
    private final SchoolMapper schoolMapper;
    private final SchoolClassMapper classMapper;
    private final StudentMapper studentMapper;
    private final OrganizationMapper organizationMapper;
    
    @Override
    public IPage<School> pageSchools(Page<School> page, String schoolName, String schoolType, String educationBureauId) {
        return schoolMapper.selectSchoolPage(page, schoolName, schoolType, educationBureauId);
    }
    
    @Override
    public School getById(String id) {
        School school = schoolMapper.selectById(id);
        if (school != null) {
            enrichSchoolInfo(school);
        }
        return school;
    }
    
    @Override
    public School getByOrganizationId(String organizationId) {
        School school = schoolMapper.findByOrganizationId(organizationId);
        if (school != null) {
            enrichSchoolInfo(school);
        }
        return school;
    }
    
    @Override
    public School getBySchoolCode(String schoolCode) {
        return schoolMapper.findBySchoolCode(schoolCode);
    }
    
    @Override
    public List<School> getByEducationBureauId(String bureauId) {
        List<School> schools = schoolMapper.findByEducationBureauId(bureauId);
        schools.forEach(this::enrichSchoolInfo);
        return schools;
    }
    
    @Override
    public List<School> listSchools(String schoolType) {
        LambdaQueryWrapper<School> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(School::getStatus, School.STATUS_ACTIVE);
        if (schoolType != null) {
            wrapper.eq(School::getSchoolType, schoolType);
        }
        wrapper.orderByDesc(School::getCreateTime);
        return schoolMapper.selectList(wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(School school) {
        // 检查学校代码是否已存在
        if (school.getSchoolCode() != null && schoolMapper.findBySchoolCode(school.getSchoolCode()) != null) {
            throw new BusinessException("学校代码已存在");
        }
        // 检查组织ID是否已关联
        if (school.getOrganizationId() != null) {
            School existing = schoolMapper.findByOrganizationId(school.getOrganizationId());
            if (existing != null) {
                throw new BusinessException("该组织已关联学校");
            }
        }
        school.setStatus(School.STATUS_ACTIVE);
        schoolMapper.insert(school);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(School school) {
        School existing = schoolMapper.selectById(school.getId());
        if (existing == null) {
            throw new BusinessException("学校不存在");
        }
        // 检查学校代码是否冲突
        if (school.getSchoolCode() != null && !school.getSchoolCode().equals(existing.getSchoolCode())) {
            School conflict = schoolMapper.findBySchoolCode(school.getSchoolCode());
            if (conflict != null) {
                throw new BusinessException("学校代码已存在");
            }
        }
        schoolMapper.updateById(school);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        School school = schoolMapper.selectById(id);
        if (school == null) {
            throw new BusinessException("学校不存在");
        }
        // 检查是否有班级
        List<SchoolClass> classes = classMapper.findBySchoolId(id);
        if (!classes.isEmpty()) {
            throw new BusinessException("该学校下存在班级，无法删除");
        }
        schoolMapper.deleteById(id);
    }
    
    @Override
    public List<Organization> getJurisdictionChain(String schoolOrgId) {
        return schoolMapper.getJurisdictionChain(schoolOrgId);
    }
    
    @Override
    public Map<String, Object> getStatistics(String id) {
        School school = schoolMapper.selectById(id);
        if (school == null) {
            throw new BusinessException("学校不存在");
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("schoolId", id);
        stats.put("schoolName", school.getSchoolName());
        
        // 班级数
        List<SchoolClass> classes = classMapper.findBySchoolId(id);
        stats.put("totalClasses", classes.size());
        
        // 学生数
        Integer studentCount = studentMapper.countBySchoolId(id);
        stats.put("totalStudents", studentCount != null ? studentCount : 0);
        
        // 教师数
        stats.put("totalTeachers", school.getTotalTeachers() != null ? school.getTotalTeachers() : 0);
        
        // 各年级学生分布
        Map<Integer, Integer> gradeDistribution = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            gradeDistribution.put(i, 0);
        }
        for (SchoolClass cls : classes) {
            Integer count = studentMapper.countBySchoolIdAndGrade(id, cls.getGrade());
            if (count != null && gradeDistribution.containsKey(cls.getGrade())) {
                gradeDistribution.put(cls.getGrade(), gradeDistribution.get(cls.getGrade()) + count);
            }
        }
        stats.put("gradeDistribution", gradeDistribution);
        
        return stats;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncStudentCountToOrganization(String schoolId) {
        School school = schoolMapper.selectById(schoolId);
        if (school != null && school.getOrganizationId() != null) {
            Integer count = studentMapper.countBySchoolId(schoolId);
            Organization org = organizationMapper.selectById(school.getOrganizationId());
            if (org != null) {
                org.setStudentCount(count != null ? count : 0);
                organizationMapper.updateById(org);
            }
        }
    }
    
    /**
     * 补充学校扩展信息
     */
    private void enrichSchoolInfo(School school) {
        if (school.getOrganizationId() != null) {
            Organization org = organizationMapper.selectById(school.getOrganizationId());
            school.setOrganization(org);
        }
        if (school.getEducationBureauId() != null) {
            Organization bureau = organizationMapper.selectById(school.getEducationBureauId());
            school.setEducationBureau(bureau);
            if (bureau != null) {
                school.setEducationBureauName(bureau.getName());
            }
        }
        school.setSchoolTypeName(getSchoolTypeName(school.getSchoolType()));
        school.setSchoolLevelName(getSchoolLevelName(school.getSchoolLevel()));
        school.setSchoolNatureName(getSchoolNatureName(school.getSchoolNature()));
    }
    
    private String getSchoolTypeName(String type) {
        if (type == null) return "";
        return switch (type) {
            case School.SCHOOL_TYPE_PRIMARY -> "小学";
            case School.SCHOOL_TYPE_JUNIOR_MIDDLE -> "初中";
            case School.SCHOOL_TYPE_SENIOR_MIDDLE -> "高中";
            case School.SCHOOL_TYPE_VOCATIONAL -> "职高";
            case School.SCHOOL_TYPE_NINE_YEAR -> "九年一贯制";
            case School.SCHOOL_TYPE_TWELVE_YEAR -> "十二年一贯制";
            case School.SCHOOL_TYPE_COMPLETE -> "完全中学";
            default -> type;
        };
    }
    
    private String getSchoolLevelName(String level) {
        if (level == null) return "";
        return switch (level) {
            case School.LEVEL_PROVINCIAL_KEY -> "省级示范";
            case School.LEVEL_CITY_KEY -> "市级示范";
            case School.LEVEL_COUNTY_KEY -> "县级示范";
            case School.LEVEL_ORDINARY -> "普通";
            default -> level;
        };
    }
    
    private String getSchoolNatureName(String nature) {
        if (nature == null) return "";
        return switch (nature) {
            case School.NATURE_PUBLIC -> "公办";
            case School.NATURE_PRIVATE -> "民办";
            case School.NATURE_SHARED -> "共建";
            default -> nature;
        };
    }
}
