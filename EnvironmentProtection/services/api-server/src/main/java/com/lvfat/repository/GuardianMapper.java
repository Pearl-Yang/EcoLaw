package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.Guardian;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 监护人 Mapper
 */
@Mapper
public interface GuardianMapper extends BaseMapper<Guardian> {
    
    /**
     * 根据学生ID获取监护人列表
     */
    @Select("SELECT * FROM guardian WHERE student_id = #{studentId} AND deleted = 0 ORDER BY priority, create_time")
    List<Guardian> findByStudentId(@Param("studentId") String studentId);
    
    /**
     * 根据手机号获取监护人
     */
    @Select("SELECT * FROM guardian WHERE phone = #{phone} AND deleted = 0 LIMIT 1")
    Guardian findByPhone(@Param("phone") String phone);
    
    /**
     * 获取学生的紧急联系人
     */
    @Select("SELECT * FROM guardian WHERE student_id = #{studentId} AND is_emergency_contact = 1 AND deleted = 0 ORDER BY priority LIMIT 1")
    Guardian findEmergencyContactByStudentId(@Param("studentId") String studentId);
    
    /**
     * 获取学生的紧急联系人列表
     */
    @Select("SELECT * FROM guardian WHERE student_id = #{studentId} AND is_emergency_contact = 1 AND deleted = 0 ORDER BY priority")
    List<Guardian> findEmergencyContactsByStudentId(@Param("studentId") String studentId);
    
    /**
     * 检查手机号是否存在
     */
    @Select("SELECT COUNT(*) FROM guardian WHERE phone = #{phone} AND id != #{excludeId} AND deleted = 0")
    Integer countByPhoneExcludeId(@Param("phone") String phone, @Param("excludeId") String excludeId);
    
    /**
     * 获取学生的紧急联系人数量
     */
    @Select("SELECT COUNT(*) FROM guardian WHERE student_id = #{studentId} AND is_emergency_contact = 1 AND deleted = 0")
    Integer countEmergencyContactsByStudentId(@Param("studentId") String studentId);
}
