package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.ExamRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 考试记录 Mapper
 */
@Mapper
public interface ExamRecordMapper extends BaseMapper<ExamRecord> {
    
    @Select("SELECT COUNT(*) FROM exam_record WHERE deleted = 0")
    int countAll();
    
    @Select("SELECT COUNT(*) FROM exam_record WHERE deleted = 0 AND paper_id = #{paperId}")
    int countByPaperId(String paperId);
    
    @Select("SELECT COUNT(*) FROM exam_record WHERE deleted = 0 AND paper_id = #{paperId} AND status = #{status}")
    int countByPaperIdAndStatus(@Param("paperId") String paperId, @Param("status") String status);
    
    @Select("SELECT AVG(score) FROM exam_record WHERE deleted = 0 AND paper_id = #{paperId} AND score IS NOT NULL")
    BigDecimal getAvgScoreByPaperId(String paperId);
    
    @Select("SELECT MAX(score) FROM exam_record WHERE deleted = 0 AND paper_id = #{paperId}")
    Integer getMaxScoreByPaperId(String paperId);
    
    @Select("SELECT MIN(score) FROM exam_record WHERE deleted = 0 AND paper_id = #{paperId} AND score IS NOT NULL")
    Integer getMinScoreByPaperId(String paperId);
    
    @Select("SELECT COUNT(*) FROM exam_record WHERE deleted = 0 AND paper_id = #{paperId} AND is_passed = 1")
    int countPassedByPaperId(String paperId);
    
    @Select("SELECT AVG(correct_rate) FROM exam_record WHERE deleted = 0 AND paper_id = #{paperId}")
    BigDecimal getAvgCorrectRateByPaperId(String paperId);
    
    @Select("SELECT * FROM exam_record WHERE deleted = 0 AND user_id = #{userId} ORDER BY create_time DESC")
    List<ExamRecord> selectByUserId(String userId);
}
