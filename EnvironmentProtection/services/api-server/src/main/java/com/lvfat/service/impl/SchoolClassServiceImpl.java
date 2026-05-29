package com.lvfat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lvfat.common.exception.BusinessException;
import com.lvfat.entity.Employee;
import com.lvfat.entity.SchoolClass;
import com.lvfat.entity.Student;
import com.lvfat.repository.EmployeeMapper;
import com.lvfat.repository.SchoolClassMapper;
import com.lvfat.repository.StudentMapper;
import com.lvfat.service.SchoolClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 班级服务实现
 */
@Service
@RequiredArgsConstructor
public class SchoolClassServiceImpl implements SchoolClassService {
    
    private final SchoolClassMapper classMapper;
    private final StudentMapper studentMapper;
    private final EmployeeMapper employeeMapper;
    
    @Override
    public IPage<SchoolClass> pageClasses(Page<SchoolClass> page, String schoolId, Integer grade, String academicYear) {
        return classMapper.selectClassPage(page, schoolId, grade, academicYear);
    }
    
    @Override
    public SchoolClass getById(String id) {
        SchoolClass schoolClass = classMapper.selectById(id);
        if (schoolClass != null) {
            enrichClassInfo(schoolClass);
        }
        return schoolClass;
    }
    
    @Override
    public List<SchoolClass> getBySchoolId(String schoolId) {
        List<SchoolClass> classes = classMapper.findBySchoolId(schoolId);
        classes.forEach(this::enrichClassInfo);
        return classes;
    }
    
    @Override
    public List<SchoolClass> getBySchoolIdAndGrade(String schoolId, Integer grade) {
        List<SchoolClass> classes = classMapper.findBySchoolIdAndGrade(schoolId, grade);
        classes.forEach(this::enrichClassInfo);
        return classes;
    }
    
    @Override
    public List<SchoolClass> getByHeadTeacherId(String teacherId) {
        List<SchoolClass> classes = classMapper.findByHeadTeacherId(teacherId);
        classes.forEach(this::enrichClassInfo);
        return classes;
    }
    
    @Override
    public Integer getStudentCount(String classId) {
        return studentMapper.countByClassId(classId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(SchoolClass schoolClass) {
        // 检查班级编号是否冲突
        if (schoolClass.getClassCode() != null) {
            SchoolClass existing = classMapper.findByClassCode(schoolClass.getClassCode());
            if (existing != null) {
                throw new BusinessException("班级编号已存在");
            }
        }
        // 检查班级人数限制
        if (schoolClass.getStudentLimit() == null) {
            schoolClass.setStudentLimit(50); // 默认50人
        }
        schoolClass.setCurrentStudents(0);
        schoolClass.setStatus(SchoolClass.STATUS_ACTIVE);
        classMapper.insert(schoolClass);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SchoolClass schoolClass) {
        SchoolClass existing = classMapper.selectById(schoolClass.getId());
        if (existing == null) {
            throw new BusinessException("班级不存在");
        }
        // 检查班级编号是否冲突
        if (schoolClass.getClassCode() != null && !schoolClass.getClassCode().equals(existing.getClassCode())) {
            SchoolClass conflict = classMapper.findByClassCode(schoolClass.getClassCode());
            if (conflict != null) {
                throw new BusinessException("班级编号已存在");
            }
        }
        classMapper.updateById(schoolClass);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        SchoolClass schoolClass = classMapper.selectById(id);
        if (schoolClass == null) {
            throw new BusinessException("班级不存在");
        }
        // 检查是否有学生
        Integer studentCount = studentMapper.countByClassId(id);
        if (studentCount != null && studentCount > 0) {
            throw new BusinessException("该班级存在学生，无法删除");
        }
        classMapper.deleteById(id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setHeadTeacher(String classId, String teacherId, String teacherName) {
        SchoolClass schoolClass = classMapper.selectById(classId);
        if (schoolClass == null) {
            throw new BusinessException("班级不存在");
        }
        // 如果指定了教师ID，验证教师存在
        if (teacherId != null) {
            Employee teacher = employeeMapper.selectById(teacherId);
            if (teacher == null) {
                throw new BusinessException("教师不存在");
            }
            schoolClass.setHeadTeacherId(teacherId);
            schoolClass.setHeadTeacherName(teacher.getName());
        } else {
            schoolClass.setHeadTeacherId(null);
            schoolClass.setHeadTeacherName(null);
        }
        classMapper.updateById(schoolClass);
    }
    
    @Override
    public void adjustStudentCount(String classId, int delta) {
        SchoolClass schoolClass = classMapper.selectById(classId);
        if (schoolClass != null) {
            int newCount = (schoolClass.getCurrentStudents() != null ? schoolClass.getCurrentStudents() : 0) + delta;
            newCount = Math.max(0, newCount);
            schoolClass.setCurrentStudents(newCount);
            classMapper.updateById(schoolClass);
        }
    }
    
    @Override
    public Map<String, Object> getStatistics(String schoolId) {
        List<SchoolClass> classes = classMapper.findBySchoolId(schoolId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalClasses", classes.size());
        stats.put("totalStudents", classes.stream()
                .mapToInt(c -> c.getCurrentStudents() != null ? c.getCurrentStudents() : 0)
                .sum());
        
        // 年级分布
        Map<Integer, Integer> gradeDistribution = classes.stream()
                .collect(Collectors.groupingBy(
                        SchoolClass::getGrade,
                        Collectors.summingInt(c -> c.getCurrentStudents() != null ? c.getCurrentStudents() : 0)
                ));
        stats.put("gradeDistribution", gradeDistribution);
        
        // 班级详情
        List<Map<String, Object>> classDetails = classes.stream()
                .map(c -> {
                    Map<String, Object> detail = new HashMap<>();
                    detail.put("id", c.getId());
                    detail.put("className", c.getClassName());
                    detail.put("grade", c.getGrade());
                    detail.put("currentStudents", c.getCurrentStudents());
                    detail.put("studentLimit", c.getStudentLimit());
                    detail.put("headTeacherName", c.getHeadTeacherName());
                    return detail;
                })
                .collect(Collectors.toList());
        stats.put("classDetails", classDetails);
        
        return stats;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCreate(List<SchoolClass> classes) {
        for (SchoolClass schoolClass : classes) {
            if (schoolClass.getStudentLimit() == null) {
                schoolClass.setStudentLimit(50);
            }
            schoolClass.setCurrentStudents(0);
            schoolClass.setStatus(SchoolClass.STATUS_ACTIVE);
            classMapper.insert(schoolClass);
        }
    }
    
    @Override
    public List<Integer> getGradesBySchool(String schoolId) {
        List<SchoolClass> classes = classMapper.findBySchoolId(schoolId);
        return classes.stream()
                .map(SchoolClass::getGrade)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * 补充班级扩展信息
     */
    private void enrichClassInfo(SchoolClass schoolClass) {
        // 获取当前学生数
        Integer studentCount = studentMapper.countByClassId(schoolClass.getId());
        schoolClass.setCurrentStudents(studentCount != null ? studentCount : 0);
        
        // 获取班主任信息
        if (schoolClass.getHeadTeacherId() != null) {
            Employee teacher = employeeMapper.selectById(schoolClass.getHeadTeacherId());
            if (teacher != null) {
                schoolClass.setHeadTeacher(teacher);
            }
        }
        
        schoolClass.setClassTypeName(getClassTypeName(schoolClass.getClassType()));
        schoolClass.setStatusName(getStatusName(schoolClass.getStatus()));
    }
    
    private String getClassTypeName(String type) {
        if (type == null) return "";
        return switch (type) {
            case SchoolClass.CLASS_TYPE_ORDINARY -> "普通班";
            case SchoolClass.CLASS_TYPE_KEY -> "重点班";
            case SchoolClass.CLASS_TYPE_ART -> "美术班";
            case SchoolClass.CLASS_TYPE_MUSIC -> "音乐班";
            case SchoolClass.CLASS_TYPE_SPORTS -> "体育班";
            case SchoolClass.CLASS_TYPE_INTERNATIONAL -> "国际班";
            case SchoolClass.CLASS_TYPE_SCIENCE -> "理科班";
            case SchoolClass.CLASS_TYPE_LIBERAL -> "文科班";
            default -> type;
        };
    }
    
    private String getStatusName(String status) {
        if (status == null) return "";
        return switch (status) {
            case SchoolClass.STATUS_ACTIVE -> "在读";
            case SchoolClass.STATUS_GRADUATED -> "已毕业";
            case SchoolClass.STATUS_CANCELLED -> "已取消";
            default -> status;
        };
    }
}
