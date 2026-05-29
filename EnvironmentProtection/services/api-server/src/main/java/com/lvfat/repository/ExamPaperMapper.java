package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.ExamPaper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 试卷 Mapper
 */
@Mapper
public interface ExamPaperMapper extends BaseMapper<ExamPaper> {
    
    @Select("SELECT COUNT(*) FROM exam_paper WHERE deleted = 0")
    int countAll();
    
    @Select("SELECT COUNT(*) FROM exam_paper WHERE deleted = 0 AND status = #{status}")
    int countByStatus(String status);
}
