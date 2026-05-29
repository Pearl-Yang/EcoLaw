package com.lvfat.service;

import com.lvfat.entity.Guardian;

import java.util.List;

/**
 * 监护人服务接口
 */
public interface GuardianService {
    
    /**
     * 获取监护人详情
     */
    Guardian getById(String id);
    
    /**
     * 获取学生的监护人列表
     */
    List<Guardian> getByStudentId(String studentId);
    
    /**
     * 获取紧急联系人
     */
    Guardian getEmergencyContact(String studentId);
    
    /**
     * 获取紧急联系人列表
     */
    List<Guardian> getEmergencyContacts(String studentId);
    
    /**
     * 创建监护人
     */
    void create(Guardian guardian);
    
    /**
     * 更新监护人
     */
    void update(Guardian guardian);
    
    /**
     * 删除监护人
     */
    void delete(String id);
    
    /**
     * 批量创建监护人
     */
    void batchCreate(List<Guardian> guardians);
    
    /**
     * 设置紧急联系人
     */
    void setEmergencyContact(String guardianId, boolean isEmergency);
    
    /**
     * 检查手机号是否可用
     */
    boolean isPhoneAvailable(String phone, String excludeId);
    
    /**
     * 根据手机号查找监护人
     */
    Guardian getByPhone(String phone);
}
