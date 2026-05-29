package com.lvfat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lvfat.entity.Guardian;
import com.lvfat.entity.Student;

import java.util.List;
import java.util.Map;

/**
 * 学生服务接口
 */
public interface StudentService {
    
    /**
     * 分页查询学生
     */
    IPage<Student> pageStudents(Page<Student> page, String schoolId, String classId, Integer grade, 
                                 String studentName, String studentStatus);
    
    /**
     * 获取学生详情
     */
    Student getById(String id);
    
    /**
     * 根据学号获取学生
     */
    Student getByStudentNo(String studentNo);
    
    /**
     * 根据学校获取学生列表
     */
    List<Student> getBySchoolId(String schoolId);
    
    /**
     * 根据班级获取学生列表
     */
    List<Student> getByClassId(String classId);
    
    /**
     * 根据年级获取学生列表
     */
    List<Student> getBySchoolIdAndGrade(String schoolId, Integer grade);
    
    /**
     * 获取学生监护人列表
     */
    List<Guardian> getGuardians(String studentId);
    
    /**
     * 创建学生
     */
    void create(Student student);
    
    /**
     * 更新学生
     */
    void update(Student student);
    
    /**
     * 删除学生
     */
    void delete(String id);
    
    /**
     * 调整班级
     */
    void transferClass(String studentId, String newClassId, Integer newGrade);
    
    /**
     * 变更学籍状态
     */
    void changeStatus(String studentId, String status);
    
    /**
     * 获取学生统计信息
     */
    Map<String, Object> getStatistics(String schoolId, String classId);
    
    /**
     * 批量导入学生
     */
    void batchImport(List<Student> students);
    
    /**
     * 检查学号是否可用
     */
    boolean isStudentNoAvailable(String studentNo, String excludeId);
    
    /**
     * 获取学校学生数量
     */
    Integer getSchoolStudentCount(String schoolId);
    
    /**
     * 获取班级学生数量
     */
    Integer getClassStudentCount(String classId);
    
    /**
     * 同步学生数到班级和学校
     */
    void syncStudentCount(String classId);
}
