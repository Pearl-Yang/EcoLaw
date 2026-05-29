package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lvfat.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 学生 Mapper
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {
    
    /**
     * 根据学校ID获取学生列表
     */
    @Select("SELECT * FROM student WHERE school_id = #{schoolId} AND deleted = 0 ORDER BY grade, student_no")
    List<Student> findBySchoolId(@Param("schoolId") String schoolId);
    
    /**
     * 根据班级ID获取学生列表
     */
    @Select("SELECT * FROM student WHERE class_id = #{classId} AND deleted = 0 ORDER BY student_no")
    List<Student> findByClassId(@Param("classId") String classId);
    
    /**
     * 根据学号获取学生
     */
    @Select("SELECT * FROM student WHERE student_no = #{studentNo} AND deleted = 0")
    Student findByStudentNo(@Param("studentNo") String studentNo);
    
    /**
     * 根据年级获取学生列表
     */
    @Select("SELECT * FROM student WHERE school_id = #{schoolId} AND grade = #{grade} AND deleted = 0 ORDER BY student_no")
    List<Student> findBySchoolIdAndGrade(@Param("schoolId") String schoolId, @Param("grade") Integer grade);
    
    /**
     * 根据学籍状态获取学生列表
     */
    @Select("SELECT * FROM student WHERE student_status = #{status} AND deleted = 0 ORDER BY create_time DESC")
    List<Student> findByStudentStatus(@Param("status") String status);
    
    /**
     * 分页查询学生
     */
    IPage<Student> selectStudentPage(Page<Student> page, @Param("schoolId") String schoolId,
                                     @Param("classId") String classId,
                                     @Param("grade") Integer grade,
                                     @Param("studentName") String studentName,
                                     @Param("studentStatus") String studentStatus);
    
    /**
     * 获取学校学生数量
     */
    @Select("SELECT COUNT(*) FROM student WHERE school_id = #{schoolId} AND deleted = 0")
    Integer countBySchoolId(@Param("schoolId") String schoolId);
    
    /**
     * 获取班级学生数量
     */
    @Select("SELECT COUNT(*) FROM student WHERE class_id = #{classId} AND deleted = 0")
    Integer countByClassId(@Param("classId") String classId);
    
    /**
     * 根据年级获取学生数量
     */
    @Select("SELECT COUNT(*) FROM student WHERE school_id = #{schoolId} AND grade = #{grade} AND deleted = 0")
    Integer countBySchoolIdAndGrade(@Param("schoolId") String schoolId, @Param("grade") Integer grade);
    
    /**
     * 检查学号是否存在
     */
    @Select("SELECT COUNT(*) FROM student WHERE student_no = #{studentNo} AND id != #{excludeId} AND deleted = 0")
    Integer countByStudentNoExcludeId(@Param("studentNo") String studentNo, @Param("excludeId") String excludeId);
}
