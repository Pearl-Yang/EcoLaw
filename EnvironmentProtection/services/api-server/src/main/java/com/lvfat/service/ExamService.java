package com.lvfat.service;

import com.lvfat.dto.*;

import java.util.List;
import java.util.Map;

public interface ExamService {
    
    // 题目管理
    List<Map<String, Object>> listQuestions(Integer page, Integer pageSize, String type, String difficulty, String categoryId, String keyword);
    Map<String, Object> getQuestionById(String id);
    void saveQuestion(Map<String, Object> question);
    void updateQuestion(Map<String, Object> question);
    void removeQuestion(String id);
    
    // 试卷管理
    List<Map<String, Object>> listPapers(Integer page, Integer pageSize, String status, String sourceType, String keyword);
    Map<String, Object> getPaperById(String id);
    ExamPaperDetailDTO getPaperDetail(String id, Boolean includeAnswer);
    Map<String, Object> createPaper(Map<String, Object> paper);
    void updatePaper(Map<String, Object> paper);
    void removePaper(String id);
    void publishPaper(String id);
    
    // 智能组卷
    Map<String, Object> autoGeneratePaper(AutoGeneratePaperDTO config);
    
    // 考试下发
    Map<String, Object> dispatchExam(ExamDispatchDTO dispatchDTO);
    
    // 考试记录
    List<Map<String, Object>> listExamRecords(Integer page, Integer pageSize, String paperId, String userId, String status, String organizationId);
    Map<String, Object> getExamRecordById(String id);
    Map<String, Object> startExam(String paperId, String userId);
    Map<String, Object> submitExam(ExamSubmitDTO submitDTO, String userId);
    List<AnswerDetailDTO> getAnswerDetails(String recordId);
    
    // 统计分析
    ExamStatisticsDTO getExamStatistics(String paperId);
    List<Map<String, Object>> getExamRecordsByUser(String userId);
    Map<String, Object> getExamRank(String paperId, String userId);
}
