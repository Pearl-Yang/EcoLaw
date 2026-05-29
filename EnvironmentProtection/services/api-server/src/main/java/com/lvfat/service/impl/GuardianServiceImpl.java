package com.lvfat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lvfat.common.exception.BusinessException;
import com.lvfat.entity.Guardian;
import com.lvfat.entity.Student;
import com.lvfat.repository.GuardianMapper;
import com.lvfat.repository.StudentMapper;
import com.lvfat.service.GuardianService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 监护人服务实现
 */
@Service
@RequiredArgsConstructor
public class GuardianServiceImpl implements GuardianService {
    
    private final GuardianMapper guardianMapper;
    private final StudentMapper studentMapper;
    
    @Override
    public Guardian getById(String id) {
        Guardian guardian = guardianMapper.selectById(id);
        if (guardian != null) {
            enrichGuardianInfo(guardian);
        }
        return guardian;
    }
    
    @Override
    public List<Guardian> getByStudentId(String studentId) {
        List<Guardian> guardians = guardianMapper.findByStudentId(studentId);
        guardians.forEach(this::enrichGuardianInfo);
        return guardians;
    }
    
    @Override
    public Guardian getEmergencyContact(String studentId) {
        return guardianMapper.findEmergencyContactByStudentId(studentId);
    }
    
    @Override
    public List<Guardian> getEmergencyContacts(String studentId) {
        return guardianMapper.findEmergencyContactsByStudentId(studentId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Guardian guardian) {
        // 验证学生存在
        Student student = studentMapper.selectById(guardian.getStudentId());
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        // 如果设置为紧急联系人，检查是否已有紧急联系人
        if (guardian.getIsEmergencyContact() != null && guardian.getIsEmergencyContact() == 1) {
            Integer existingCount = guardianMapper.countEmergencyContactsByStudentId(guardian.getStudentId());
            if (existingCount != null && existingCount >= 2) {
                throw new BusinessException("紧急联系人最多设置2个");
            }
        }
        
        // 设置优先级
        if (guardian.getPriority() == null) {
            guardian.setPriority(1);
        }
        
        // 默认状态
        if (guardian.getStatus() == null) {
            guardian.setStatus(Guardian.STATUS_ACTIVE);
        }
        
        guardianMapper.insert(guardian);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Guardian guardian) {
        Guardian existing = guardianMapper.selectById(guardian.getId());
        if (existing == null) {
            throw new BusinessException("监护人不存在");
        }
        
        // 如果修改紧急联系人状态
        if (guardian.getIsEmergencyContact() != null && guardian.getIsEmergencyContact() == 1 
                && existing.getIsEmergencyContact() != 1) {
            Integer existingCount = guardianMapper.countEmergencyContactsByStudentId(guardian.getStudentId());
            if (existingCount != null && existingCount >= 2) {
                throw new BusinessException("紧急联系人最多设置2个");
            }
        }
        
        guardianMapper.updateById(guardian);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        Guardian guardian = guardianMapper.selectById(id);
        if (guardian == null) {
            throw new BusinessException("监护人不存在");
        }
        guardianMapper.deleteById(id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCreate(List<Guardian> guardians) {
        for (Guardian guardian : guardians) {
            create(guardian);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setEmergencyContact(String guardianId, boolean isEmergency) {
        Guardian guardian = guardianMapper.selectById(guardianId);
        if (guardian == null) {
            throw new BusinessException("监护人不存在");
        }
        
        if (isEmergency) {
            Integer existingCount = guardianMapper.countEmergencyContactsByStudentId(guardian.getStudentId());
            if (existingCount != null && existingCount >= 2) {
                throw new BusinessException("紧急联系人最多设置2个");
            }
        }
        
        guardian.setIsEmergencyContact(isEmergency ? 1 : 0);
        guardianMapper.updateById(guardian);
    }
    
    @Override
    public boolean isPhoneAvailable(String phone, String excludeId) {
        Integer count = guardianMapper.countByPhoneExcludeId(phone, excludeId != null ? excludeId : "");
        return count == null || count == 0;
    }
    
    @Override
    public Guardian getByPhone(String phone) {
        return guardianMapper.findByPhone(phone);
    }
    
    /**
     * 补充监护人扩展信息
     */
    private void enrichGuardianInfo(Guardian guardian) {
        guardian.setRelationName(getRelationName(guardian.getRelation()));
        guardian.setStatusName(getStatusName(guardian.getStatus()));
    }
    
    private String getRelationName(String relation) {
        if (relation == null) return "";
        return switch (relation) {
            case Guardian.RELATION_FATHER -> "父亲";
            case Guardian.RELATION_MOTHER -> "母亲";
            case Guardian.RELATION_GRANDFATHER -> "祖父";
            case Guardian.RELATION_GRANDMOTHER -> "祖母";
            case Guardian.RELATION_MATERNAL_GRANDFATHER -> "外祖父";
            case Guardian.RELATION_MATERNAL_GRANDMOTHER -> "外祖母";
            case Guardian.RELATION_OTHER -> "其他";
            default -> relation;
        };
    }
    
    private String getStatusName(String status) {
        if (status == null) return "";
        return switch (status) {
            case Guardian.STATUS_ACTIVE -> "有效";
            case Guardian.STATUS_INACTIVE -> "无效";
            default -> status;
        };
    }
}
