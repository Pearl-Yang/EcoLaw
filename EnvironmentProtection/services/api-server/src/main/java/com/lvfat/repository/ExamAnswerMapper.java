package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.ExamAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 答题记录 Mapper
 */
@Mapper
public interface ExamAnswerMapper extends BaseMapper<ExamAnswer> {
    
    @Select("SELECT * FROM exam_answer WHERE exam_record_id = #{examRecordId} ORDER BY sort_order")
    List<ExamAnswer> selectByExamRecordId(String examRecordId);
    
    @Select("SELECT COUNT(*) FROM exam_answer WHERE exam_record_id = #{examRecordId}")
    int countByExamRecordId(String examRecordId);
    
    @Select("SELECT COUNT(*) FROM exam_answer WHERE exam_record_id = #{examRecordId} AND is_correct = 1")
    int countCorrectByExamRecordId(String examRecordId);
}
