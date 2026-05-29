package com.lvfat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lvfat.entity.SchoolClass;

import java.util.List;
import java.util.Map;

/**
 * 班级服务接口
 */
public interface SchoolClassService {
    
    /**
     * 分页查询班级
     */
    IPage<SchoolClass> pageClasses(Page<SchoolClass> page, String schoolId, Integer grade, String academicYear);
    
    /**
     * 获取班级详情
     */
    SchoolClass getById(String id);
    
    /**
     * 根据学校ID获取班级列表
     */
    List<SchoolClass> getBySchoolId(String schoolId);
    
    /**
     * 根据年级获取班级列表
     */
    List<SchoolClass> getBySchoolIdAndGrade(String schoolId, Integer grade);
    
    /**
     * 根据班主任获取班级
     */
    List<SchoolClass> getByHeadTeacherId(String teacherId);
    
    /**
     * 获取班级学生数
     */
    Integer getStudentCount(String classId);
    
    /**
     * 创建班级
     */
    void create(SchoolClass schoolClass);
    
    /**
     * 更新班级
     */
    void update(SchoolClass schoolClass);
    
    /**
     * 删除班级
     */
    void delete(String id);
    
    /**
     * 设置班主任
     */
    void setHeadTeacher(String classId, String teacherId, String teacherName);
    
    /**
     * 调整班级学生数
     */
    void adjustStudentCount(String classId, int delta);
    
    /**
     * 获取班级统计信息
     */
    Map<String, Object> getStatistics(String schoolId);
    
    /**
     * 批量创建班级
     */
    void batchCreate(List<SchoolClass> classes);
    
    /**
     * 获取年级列表
     */
    List<Integer> getGradesBySchool(String schoolId);
}
