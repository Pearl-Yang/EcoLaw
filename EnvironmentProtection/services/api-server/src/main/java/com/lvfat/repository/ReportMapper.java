package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.Report;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 举报 Mapper
 */
@Mapper
public interface ReportMapper extends BaseMapper<Report> {
    
    @Select("SELECT type, COUNT(*) as count FROM report WHERE deleted = 0 GROUP BY type")
    List<Map<String, Object>> countByType();
}
