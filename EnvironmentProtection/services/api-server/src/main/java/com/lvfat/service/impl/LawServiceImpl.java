package com.lvfat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lvfat.entity.Law;
import com.lvfat.repository.LawMapper;
import com.lvfat.service.LawService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 法规服务实现
 */
@Service
@RequiredArgsConstructor
public class LawServiceImpl extends ServiceImpl<LawMapper, Law> implements LawService {

    @Override
    public List<Law> listLaws(String level, String category, String keyword) {
        QueryWrapper<Law> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(level)) {
            wrapper.eq("level", level);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq("category", category);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like("title", keyword);
        }
        wrapper.orderByDesc("create_time");
        return list(wrapper);
    }
}