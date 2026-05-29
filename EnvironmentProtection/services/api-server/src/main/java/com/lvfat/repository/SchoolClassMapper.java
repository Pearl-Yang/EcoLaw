package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lvfat.entity.SchoolClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 班级 Mapper
 */
@Mapper
public interface SchoolClassMapper extends BaseMapper<SchoolClass> {
    
    /**
     * 根据学校ID获取班级列表
     */
    @Select("SELECT * FROM school_class WHERE school_id = #{schoolId} AND deleted = 0 ORDER BY grade, class_number")
    List<SchoolClass> findBySchoolId(@Param("schoolId") String schoolId);
    
    /**
     * 根据年级获取班级列表
     */
    @Select("SELECT * FROM school_class WHERE school_id = #{schoolId} AND grade = #{grade} AND deleted = 0 ORDER BY class_number")
    List<SchoolClass> findBySchoolIdAndGrade(@Param("schoolId") String schoolId, @Param("grade") Integer grade);
    
    /**
     * 根据班主任ID获取班级列表
     */
    @Select("SELECT * FROM school_class WHERE head_teacher_id = #{teacherId} AND deleted = 0 ORDER BY academic_year DESC")
    List<SchoolClass> findByHeadTeacherId(@Param("teacherId") String teacherId);
    
    /**
     * 根据学年获取班级列表
     */
    @Select("SELECT * FROM school_class WHERE academic_year = #{academicYear} AND deleted = 0 ORDER BY school_id, grade, class_number")
    List<SchoolClass> findByAcademicYear(@Param("academicYear") String academicYear);
    
    /**
     * 分页查询班级
     */
    IPage<SchoolClass> selectClassPage(Page<SchoolClass> page, @Param("schoolId") String schoolId,
                                        @Param("grade") Integer grade, 
                                        @Param("academicYear") String academicYear);
    
    /**
     * 获取班级学生数量
     */
    @Select("SELECT COUNT(*) FROM student WHERE class_id = #{classId} AND deleted = 0")
    Integer countStudentsByClassId(@Param("classId") String classId);
    
    /**
     * 根据班级编号获取班级
     */
    @Select("SELECT * FROM school_class WHERE class_code = #{classCode} AND deleted = 0")
    SchoolClass findByClassCode(@Param("classCode") String classCode);
}
