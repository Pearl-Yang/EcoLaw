package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.News;
import org.apache.ibatis.annotations.Mapper;

/**
 * 新闻 Mapper
 */
@Mapper
public interface NewsMapper extends BaseMapper<News> {
}
