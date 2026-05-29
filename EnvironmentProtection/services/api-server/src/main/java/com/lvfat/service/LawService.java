package com.lvfat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lvfat.entity.Law;

import java.util.List;

/**
 * 法规服务接口
 */
public interface LawService extends IService<Law> {
    
    /**
     * 查询法规列表
     */
    List<Law> listLaws(String level, String category, String keyword);
}