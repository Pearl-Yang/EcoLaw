package com.lvfat.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvfat.entity.ExamQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 题目 Mapper
 */
@Mapper
public interface ExamQuestionMapper extends BaseMapper<ExamQuestion> {
    
    @Select("SELECT COUNT(*) FROM exam_question WHERE deleted = 0 AND status = '1'")
    int countAll();
    
    @Select("SELECT COUNT(*) FROM exam_question WHERE deleted = 0 AND status = '1' AND type = #{type}")
    int countByType(String type);
    
    @Select("SELECT COUNT(*) FROM exam_question WHERE deleted = 0 AND status = '1' AND difficulty = #{difficulty}")
    int countByDifficulty(String difficulty);
    
    @Select("SELECT * FROM exam_question WHERE deleted = 0 AND status = '1' " +
            "AND type = #{type} AND difficulty = #{difficulty} ORDER BY RAND() LIMIT #{limit}")
    List<ExamQuestion> selectRandomByTypeAndDifficulty(String type, String difficulty, int limit);
    
    @Select("SELECT * FROM exam_question WHERE deleted = 0 AND status = '1' " +
            "AND type = #{type} AND category_id = #{categoryId} ORDER BY RAND() LIMIT #{limit}")
    List<ExamQuestion> selectRandomByTypeAndCategory(String type, String categoryId, int limit);
    
    @Select("SELECT * FROM exam_question WHERE deleted = 0 AND status = '1' " +
            "AND type = #{type} AND difficulty = #{difficulty} AND category_id = #{categoryId} ORDER BY RAND() LIMIT #{limit}")
    List<ExamQuestion> selectRandomByTypeDifficultyCategory(String type, String difficulty, String categoryId, int limit);
}
