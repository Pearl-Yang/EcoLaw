package com.lvfat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lvfat.entity.AIMaterial;
import com.lvfat.entity.User;
import com.lvfat.repository.AIMaterialMapper;
import com.lvfat.repository.UserMapper;
import com.lvfat.service.AIMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

/**
 * AI素材服务实现
 */
@Service
@RequiredArgsConstructor
public class AIMaterialServiceImpl extends ServiceImpl<AIMaterialMapper, AIMaterial> implements AIMaterialService {
    
    private final UserMapper userMapper;
    
    @Override
    public List<AIMaterial> listMaterials(Integer page, Integer pageSize, String type, String keyword) {
        QueryWrapper<AIMaterial> wrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(type)) {
            wrapper.eq("type", type);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like("title", keyword).or().like("content", keyword));
        }
        
        wrapper.orderByDesc("create_time");
        
        if (page != null && pageSize != null) {
            wrapper.last("LIMIT " + ((page - 1) * pageSize) + ", " + pageSize);
        }
        
        return list(wrapper);
    }
    
    @Override
    public AIMaterial generateMaterial(String type, String topic, String targetAudience, String lawId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.findByUsername(username);
        
        AIMaterial material = new AIMaterial();
        material.setId(UUID.randomUUID().toString().replace("-", ""));
        material.setType(type);
        material.setTopic(topic);
        material.setTargetAudience(targetAudience);
        material.setLawId(lawId);
        material.setCreatorId(user != null ? user.getId() : null);
        material.setStatus("generating");
        
        // TODO: 调用AI服务生成素材内容
        // 这里暂时模拟生成
        material.setTitle("【" + type.toUpperCase() + "】" + topic + " - AI生成素材");
        material.setContent("这是一份关于" + topic + "的普法宣传素材。\n\n" +
                "适用对象: " + (targetAudience != null ? targetAudience : "普通群众") + "\n\n" +
                "内容正在由AI智能生成中...");
        
        save(material);
        
        // 更新状态为完成
        material.setStatus("completed");
        updateById(material);
        
        return material;
    }
}
