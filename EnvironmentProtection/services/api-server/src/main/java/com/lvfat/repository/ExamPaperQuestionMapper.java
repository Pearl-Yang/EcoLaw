package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.ExamPaperQuestion;
import org.apache.ibatis.annotations.Mapper;

/**
 * 试卷题目关联 Mapper
 */
@Mapper
public interface ExamPaperQuestionMapper extends BaseMapper<ExamPaperQuestion> {
}
