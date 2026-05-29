package com.lvfat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lvfat.entity.AIMaterial;

import java.util.List;

/**
 * AI素材服务接口
 */
public interface AIMaterialService extends IService<AIMaterial> {
    
    /**
     * 分页查询素材
     */
    List<AIMaterial> listMaterials(Integer page, Integer pageSize, String type, String keyword);
    
    /**
     * 生成素材
     */
    AIMaterial generateMaterial(String type, String topic, String targetAudience, String lawId);
}
