package com.lvfat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lvfat.common.exception.BusinessException;
import com.lvfat.entity.Guardian;
import com.lvfat.entity.School;
import com.lvfat.entity.SchoolClass;
import com.lvfat.entity.Student;
import com.lvfat.repository.*;
import com.lvfat.service.GuardianService;
import com.lvfat.service.SchoolClassService;
import com.lvfat.service.SchoolService;
import com.lvfat.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生服务实现
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    
    private final StudentMapper studentMapper;
    private final GuardianMapper guardianMapper;
    private final SchoolMapper schoolMapper;
    private final SchoolClassMapper classMapper;
    private final GuardianService guardianService;
    private final SchoolClassService classService;
    private final SchoolService schoolService;
    
    @Override
    public IPage<Student> pageStudents(Page<Student> page, String schoolId, String classId, Integer grade,
                                       String studentName, String studentStatus) {
        return studentMapper.selectStudentPage(page, schoolId, classId, grade, studentName, studentStatus);
    }
    
    @Override
    public Student getById(String id) {
        Student student = studentMapper.selectById(id);
        if (student != null) {
            enrichStudentInfo(student);
        }
        return student;
    }
    
    @Override
    public Student getByStudentNo(String studentNo) {
        Student student = studentMapper.findByStudentNo(studentNo);
        if (student != null) {
            enrichStudentInfo(student);
        }
        return student;
    }
    
    @Override
    public List<Student> getBySchoolId(String schoolId) {
        List<Student> students = studentMapper.findBySchoolId(schoolId);
        students.forEach(this::enrichStudentInfo);
        return students;
    }
    
    @Override
    public List<Student> getByClassId(String classId) {
        List<Student> students = studentMapper.findByClassId(classId);
        students.forEach(this::enrichStudentInfo);
        return students;
    }
    
    @Override
    public List<Student> getBySchoolIdAndGrade(String schoolId, Integer grade) {
        List<Student> students = studentMapper.findBySchoolIdAndGrade(schoolId, grade);
        students.forEach(this::enrichStudentInfo);
        return students;
    }
    
    @Override
    public List<Guardian> getGuardians(String studentId) {
        return guardianMapper.findByStudentId(studentId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Student student) {
        // 检查学号是否已存在
        if (student.getStudentNo() != null && studentMapper.findByStudentNo(student.getStudentNo()) != null) {
            throw new BusinessException("学号已存在");
        }
        // 检查班级是否存在
        if (student.getClassId() != null) {
            SchoolClass schoolClass = classMapper.selectById(student.getClassId());
            if (schoolClass == null) {
                throw new BusinessException("班级不存在");
            }
            // 检查班级人数是否已满
            Integer currentCount = studentMapper.countByClassId(student.getClassId());
            if (currentCount != null && schoolClass.getStudentLimit() != null 
                    && currentCount >= schoolClass.getStudentLimit()) {
                throw new BusinessException("班级人数已满");
            }
        }
        // 默认学籍状态
        if (student.getStudentStatus() == null) {
            student.setStudentStatus(Student.STATUS_STUDYING);
        }
        studentMapper.insert(student);
        
        // 更新班级学生数
        if (student.getClassId() != null) {
            classService.adjustStudentCount(student.getClassId(), 1);
            // 同步到学校
            SchoolClass schoolClass = classMapper.selectById(student.getClassId());
            if (schoolClass != null) {
                schoolService.syncStudentCountToOrganization(schoolClass.getSchoolId());
            }
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Student student) {
        Student existing = studentMapper.selectById(student.getId());
        if (existing == null) {
            throw new BusinessException("学生不存在");
        }
        // 检查学号是否冲突
        if (student.getStudentNo() != null && !student.getStudentNo().equals(existing.getStudentNo())) {
            Student conflict = studentMapper.findByStudentNo(student.getStudentNo());
            if (conflict != null) {
                throw new BusinessException("学号已存在");
            }
        }
        // 检查班级人数限制
        if (student.getClassId() != null && !student.getClassId().equals(existing.getClassId())) {
            SchoolClass schoolClass = classMapper.selectById(student.getClassId());
            if (schoolClass == null) {
                throw new BusinessException("班级不存在");
            }
            Integer currentCount = studentMapper.countByClassId(student.getClassId());
            if (currentCount != null && schoolClass.getStudentLimit() != null 
                    && currentCount >= schoolClass.getStudentLimit()) {
                throw new BusinessException("班级人数已满");
            }
        }
        studentMapper.updateById(student);
        
        // 如果班级变更，同步更新学生数
        if (!org.springframework.util.StringUtils.hasText(existing.getClassId()) 
                && org.springframework.util.StringUtils.hasText(student.getClassId())) {
            // 新增班级
            classService.adjustStudentCount(student.getClassId(), 1);
        } else if (org.springframework.util.StringUtils.hasText(existing.getClassId()) 
                && !existing.getClassId().equals(student.getClassId())) {
            // 班级变更
            classService.adjustStudentCount(existing.getClassId(), -1);
            if (student.getClassId() != null) {
                classService.adjustStudentCount(student.getClassId(), 1);
            }
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        studentMapper.deleteById(id);
        
        // 更新班级学生数
        if (student.getClassId() != null) {
            classService.adjustStudentCount(student.getClassId(), -1);
            // 同步到学校
            SchoolClass schoolClass = classMapper.selectById(student.getClassId());
            if (schoolClass != null) {
                schoolService.syncStudentCountToOrganization(schoolClass.getSchoolId());
            }
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transferClass(String studentId, String newClassId, Integer newGrade) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        String oldClassId = student.getClassId();
        
        // 检查新班级
        if (newClassId != null) {
            SchoolClass newClass = classMapper.selectById(newClassId);
            if (newClass == null) {
                throw new BusinessException("目标班级不存在");
            }
            // 检查班级人数限制
            Integer currentCount = studentMapper.countByClassId(newClassId);
            if (currentCount != null && newClass.getStudentLimit() != null 
                    && currentCount >= newClass.getStudentLimit()) {
                throw new BusinessException("目标班级人数已满");
            }
        }
        
        // 更新学生班级信息
        student.setClassId(newClassId);
        if (newGrade != null) {
            student.setGrade(newGrade);
        }
        studentMapper.updateById(student);
        
        // 同步班级学生数
        if (oldClassId != null && !oldClassId.equals(newClassId)) {
            classService.adjustStudentCount(oldClassId, -1);
        }
        if (newClassId != null && !newClassId.equals(oldClassId)) {
            classService.adjustStudentCount(newClassId, 1);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(String studentId, String status) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        student.setStudentStatus(status);
        studentMapper.updateById(student);
    }
    
    @Override
    public Map<String, Object> getStatistics(String schoolId, String classId) {
        Map<String, Object> stats = new HashMap<>();
        
        if (schoolId != null) {
            Integer schoolCount = studentMapper.countBySchoolId(schoolId);
            stats.put("totalStudents", schoolCount != null ? schoolCount : 0);
        }
        
        if (classId != null) {
            Integer classCount = studentMapper.countByClassId(classId);
            stats.put("classStudents", classCount != null ? classCount : 0);
        }
        
        // 学籍状态分布
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        if (schoolId != null) {
            wrapper.eq(Student::getSchoolId, schoolId);
        }
        if (classId != null) {
            wrapper.eq(Student::getClassId, classId);
        }
        List<Student> students = studentMapper.selectList(wrapper);
        
        Map<String, Long> statusDistribution = students.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        s -> s.getStudentStatus() != null ? s.getStudentStatus() : "unknown",
                        java.util.stream.Collectors.counting()
                ));
        stats.put("statusDistribution", statusDistribution);
        
        return stats;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchImport(List<Student> students) {
        for (Student student : students) {
            create(student);
        }
    }
    
    @Override
    public boolean isStudentNoAvailable(String studentNo, String excludeId) {
        Integer count = studentMapper.countByStudentNoExcludeId(studentNo, excludeId != null ? excludeId : "");
        return count == null || count == 0;
    }
    
    @Override
    public Integer getSchoolStudentCount(String schoolId) {
        return studentMapper.countBySchoolId(schoolId);
    }
    
    @Override
    public Integer getClassStudentCount(String classId) {
        return studentMapper.countByClassId(classId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncStudentCount(String classId) {
        SchoolClass schoolClass = classMapper.selectById(classId);
        if (schoolClass != null) {
            Integer count = studentMapper.countByClassId(classId);
            schoolClass.setCurrentStudents(count != null ? count : 0);
            classMapper.updateById(schoolClass);
            schoolService.syncStudentCountToOrganization(schoolClass.getSchoolId());
        }
    }
    
    /**
     * 补充学生扩展信息
     */
    private void enrichStudentInfo(Student student) {
        // 获取学校信息
        if (student.getSchoolId() != null) {
            School school = schoolMapper.selectById(student.getSchoolId());
            if (school != null) {
                student.setSchool(school);
                student.setSchoolName(school.getSchoolName());
            }
        }
        // 获取班级信息
        if (student.getClassId() != null) {
            SchoolClass schoolClass = classMapper.selectById(student.getClassId());
            if (schoolClass != null) {
                student.setSchoolClass(schoolClass);
                student.setClassName(schoolClass.getClassName());
            }
        }
        // 获取监护人信息
        List<Guardian> guardians = guardianMapper.findByStudentId(student.getId());
        student.setGuardians(guardians);
        
        // 状态名称
        student.setStudentStatusName(getStudentStatusName(student.getStudentStatus()));
        student.setGenderName(getGenderName(student.getGender()));
    }
    
    private String getStudentStatusName(String status) {
        if (status == null) return "";
        return switch (status) {
            case Student.STATUS_STUDYING -> "在读";
            case Student.STATUS_SUSPENDED -> "休学";
            case Student.STATUS_TRANSFERRED -> "转出";
            case Student.STATUS_DROPPED -> "辍学";
            case Student.STATUS_GRADUATED -> "毕业";
            case Student.STATUS_EXPELLED -> "开除";
            default -> status;
        };
    }
    
    private String getGenderName(String gender) {
        if (gender == null) return "";
        return switch (gender) {
            case Student.GENDER_MALE -> "男";
            case Student.GENDER_FEMALE -> "女";
            default -> gender;
        };
    }
}
