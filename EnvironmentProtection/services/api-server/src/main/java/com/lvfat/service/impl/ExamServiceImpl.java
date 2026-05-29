package com.lvfat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lvfat.common.utils.IdUtils;
import com.lvfat.dto.*;
import com.lvfat.entity.*;
import com.lvfat.repository.*;
import com.lvfat.service.ExamService;
import com.lvfat.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {
    
    private final ExamQuestionMapper questionMapper;
    private final ExamPaperMapper paperMapper;
    private final ExamPaperQuestionMapper paperQuestionMapper;
    private final ExamRecordMapper recordMapper;
    private final ExamAnswerMapper answerMapper;
    private final UserMapper userMapper;
    private final OrganizationMapper organizationMapper;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    
    // ==================== 题目管理 ====================
    
    @Override
    public List<Map<String, Object>> listQuestions(Integer page, Integer pageSize, String type, 
            String difficulty, String categoryId, String keyword) {
        LambdaQueryWrapper<ExamQuestion> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            wrapper.eq(ExamQuestion::getType, type);
        }
        if (difficulty != null && !difficulty.isEmpty()) {
            wrapper.eq(ExamQuestion::getDifficulty, difficulty);
        }
        if (categoryId != null && !categoryId.isEmpty()) {
            wrapper.eq(ExamQuestion::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(ExamQuestion::getContent, keyword)
                    .or().like(ExamQuestion::getTag, keyword));
        }
        wrapper.eq(ExamQuestion::getStatus, "1");
        wrapper.orderByDesc(ExamQuestion::getCreateTime);
        
        IPage<ExamQuestion> result;
        if (page != null && pageSize != null) {
            result = questionMapper.selectPage(new Page<>(page, pageSize), wrapper);
        } else {
            result = questionMapper.selectPage(new Page<>(1, 100), wrapper);
        }
        
        return result.getRecords().stream().map(this::convertQuestionToMap).collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getQuestionById(String id) {
        ExamQuestion question = questionMapper.selectById(id);
        return convertQuestionToMap(question);
    }
    
    @Override
    public void saveQuestion(Map<String, Object> question) {
        ExamQuestion entity = new ExamQuestion();
        entity.setId(IdUtils.generateId());
        entity.setType((String) question.get("type"));
        entity.setDifficulty((String) question.get("difficulty"));
        entity.setContent((String) question.get("content"));
        entity.setOptions(toJson(question.get("options")));
        entity.setAnswer(toJson(question.get("answer")));
        entity.setAnalysis((String) question.get("analysis"));
        entity.setScore(question.get("score") != null ? ((Number) question.get("score")).intValue() : 10);
        entity.setCategoryId((String) question.get("categoryId"));
        entity.setLawId((String) question.get("lawId"));
        entity.setTag(toJson(question.get("tag")));
        entity.setUsageCount(0);
        entity.setCorrectCount(0);
        entity.setStatus("1");
        questionMapper.insert(entity);
    }
    
    @Override
    public void updateQuestion(Map<String, Object> question) {
        ExamQuestion entity = questionMapper.selectById((String) question.get("id"));
        if (entity != null) {
            if (question.containsKey("type")) entity.setType((String) question.get("type"));
            if (question.containsKey("difficulty")) entity.setDifficulty((String) question.get("difficulty"));
            if (question.containsKey("content")) entity.setContent((String) question.get("content"));
            if (question.containsKey("options")) entity.setOptions(toJson(question.get("options")));
            if (question.containsKey("answer")) entity.setAnswer(toJson(question.get("answer")));
            if (question.containsKey("analysis")) entity.setAnalysis((String) question.get("analysis"));
            if (question.containsKey("score")) entity.setScore(((Number) question.get("score")).intValue());
            if (question.containsKey("categoryId")) entity.setCategoryId((String) question.get("categoryId"));
            if (question.containsKey("lawId")) entity.setLawId((String) question.get("lawId"));
            if (question.containsKey("tag")) entity.setTag(toJson(question.get("tag")));
            questionMapper.updateById(entity);
        }
    }
    
    @Override
    public void removeQuestion(String id) {
        questionMapper.deleteById(id);
    }
    
    // ==================== 试卷管理 ====================
    
    @Override
    public List<Map<String, Object>> listPapers(Integer page, Integer pageSize, String status, 
            String sourceType, String keyword) {
        LambdaQueryWrapper<ExamPaper> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(ExamPaper::getStatus, status);
        }
        if (sourceType != null && !sourceType.isEmpty()) {
            wrapper.eq(ExamPaper::getSourceType, sourceType);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(ExamPaper::getTitle, keyword)
                    .or().like(ExamPaper::getDescription, keyword));
        }
        wrapper.orderByDesc(ExamPaper::getCreateTime);
        
        IPage<ExamPaper> result;
        if (page != null && pageSize != null) {
            result = paperMapper.selectPage(new Page<>(page, pageSize), wrapper);
        } else {
            result = paperMapper.selectPage(new Page<>(1, 100), wrapper);
        }
        
        return result.getRecords().stream().map(this::convertPaperToMap).collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getPaperById(String id) {
        ExamPaper paper = paperMapper.selectById(id);
        return convertPaperToMap(paper);
    }
    
    @Override
    public ExamPaperDetailDTO getPaperDetail(String id, Boolean includeAnswer) {
        ExamPaper paper = paperMapper.selectById(id);
        if (paper == null) return null;
        
        ExamPaperDetailDTO dto = new ExamPaperDetailDTO();
        dto.setId(paper.getId());
        dto.setTitle(paper.getTitle());
        dto.setDescription(paper.getDescription());
        dto.setTotalScore(paper.getTotalScore());
        dto.setPassScore(paper.getPassScore());
        dto.setTimeLimit(paper.getTimeLimit());
        dto.setQuestionCount(paper.getQuestionCount());
        dto.setSourceType(paper.getSourceType());
        dto.setStatus(paper.getStatus());
        
        List<ExamPaperQuestion> paperQuestions = paperQuestionMapper.selectList(
                new LambdaQueryWrapper<ExamPaperQuestion>()
                        .eq(ExamPaperQuestion::getPaperId, id)
                        .orderByAsc(ExamPaperQuestion::getSortOrder));
        
        List<QuestionDTO> questions = new ArrayList<>();
        for (ExamPaperQuestion pq : paperQuestions) {
            ExamQuestion q = questionMapper.selectById(pq.getQuestionId());
            if (q != null) {
                QuestionDTO qdto = new QuestionDTO();
                qdto.setId(q.getId());
                qdto.setType(q.getType());
                qdto.setDifficulty(q.getDifficulty());
                qdto.setContent(q.getContent());
                qdto.setOptions(q.getOptions());
                qdto.setScore(q.getScore());
                qdto.setSortOrder(pq.getSortOrder());
                qdto.setShowAnswer(includeAnswer);
                if (includeAnswer) {
                    qdto.setAnswer(q.getAnswer());
                    qdto.setAnalysis(q.getAnalysis());
                }
                questions.add(qdto);
            }
        }
        dto.setQuestions(questions);
        
        return dto;
    }
    
    @Override
    @Transactional
    public Map<String, Object> createPaper(Map<String, Object> paper) {
        ExamPaper entity = new ExamPaper();
        entity.setId(IdUtils.generateId());
        entity.setTitle((String) paper.get("title"));
        entity.setDescription((String) paper.get("description"));
        entity.setTotalScore(paper.get("totalScore") != null ? ((Number) paper.get("totalScore")).intValue() : 100);
        entity.setPassScore(paper.get("passScore") != null ? ((Number) paper.get("passScore")).intValue() : 60);
        entity.setTimeLimit(paper.get("timeLimit") != null ? ((Number) paper.get("timeLimit")).intValue() : 60);
        entity.setSourceType((String) paper.get("sourceType"));
        entity.setConfig(toJson(paper.get("config")));
        entity.setStatus("draft");
        
        paperMapper.insert(entity);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", entity.getId());
        result.put("message", "试卷创建成功");
        return result;
    }
    
    @Override
    public void updatePaper(Map<String, Object> paper) {
        ExamPaper entity = paperMapper.selectById((String) paper.get("id"));
        if (entity != null) {
            if (paper.containsKey("title")) entity.setTitle((String) paper.get("title"));
            if (paper.containsKey("description")) entity.setDescription((String) paper.get("description"));
            if (paper.containsKey("totalScore")) entity.setTotalScore(((Number) paper.get("totalScore")).intValue());
            if (paper.containsKey("passScore")) entity.setPassScore(((Number) paper.get("passScore")).intValue());
            if (paper.containsKey("timeLimit")) entity.setTimeLimit(((Number) paper.get("timeLimit")).intValue());
            paperMapper.updateById(entity);
        }
    }
    
    @Override
    @Transactional
    public void removePaper(String id) {
        paperQuestionMapper.delete(new LambdaQueryWrapper<ExamPaperQuestion>()
                .eq(ExamPaperQuestion::getPaperId, id));
        paperMapper.deleteById(id);
    }
    
    @Override
    @Transactional
    public void publishPaper(String id) {
        ExamPaper paper = paperMapper.selectById(id);
        if (paper != null) {
            paper.setStatus("published");
            paper.setPublishTime(LocalDateTime.now());
            paperMapper.updateById(paper);
        }
    }
    
    // ==================== 智能组卷 ====================
    
    @Override
    @Transactional
    public Map<String, Object> autoGeneratePaper(AutoGeneratePaperDTO config) {
        List<ExamQuestion> selectedQuestions = new ArrayList<>();
        int totalScore = 0;
        int questionCount = 0;
        
        // 根据题型配置选题
        if (config.getQuestionTypes() != null) {
            for (AutoGeneratePaperDTO.QuestionTypeConfig typeConfig : config.getQuestionTypes()) {
                int count = typeConfig.getCount();
                int score = typeConfig.getScore();
                String type = typeConfig.getType();
                
                List<ExamQuestion> questions = selectQuestions(type, config, count);
                for (ExamQuestion q : questions) {
                    q.setScore(score);
                    selectedQuestions.add(q);
                    totalScore += score;
                    questionCount++;
                }
            }
        }
        
        // 创建试卷
        ExamPaper paper = new ExamPaper();
        paper.setId(IdUtils.generateId());
        paper.setTitle(config.getTitle());
        paper.setDescription(config.getDescription());
        paper.setTotalScore(totalScore);
        paper.setPassScore(config.getPassScore() != null ? config.getPassScore() : 60);
        paper.setTimeLimit(config.getTimeLimit() != null ? config.getTimeLimit() : 60);
        paper.setQuestionCount(questionCount);
        paper.setSourceType("auto");
        paper.setConfig(toJson(config));
        paper.setStatus("draft");
        paperMapper.insert(paper);
        
        // 关联题目
        for (int i = 0; i < selectedQuestions.size(); i++) {
            ExamQuestion q = selectedQuestions.get(i);
            ExamPaperQuestion pq = new ExamPaperQuestion();
            pq.setId(IdUtils.generateId());
            pq.setPaperId(paper.getId());
            pq.setQuestionId(q.getId());
            pq.setSortOrder(i + 1);
            paperQuestionMapper.insert(pq);
            
            q.setUsageCount(q.getUsageCount() + 1);
            questionMapper.updateById(q);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", paper.getId());
        result.put("title", paper.getTitle());
        result.put("totalScore", totalScore);
        result.put("questionCount", questionCount);
        result.put("message", "智能组卷成功");
        
        return result;
    }
    
    private List<ExamQuestion> selectQuestions(String type, AutoGeneratePaperDTO config, int count) {
        List<ExamQuestion> result = new ArrayList<>();
        
        // 优先按分类权重分配
        if (config.getCategories() != null && !config.getCategories().isEmpty()) {
            Map<String, Integer> categoryWeights = new HashMap<>();
            for (AutoGeneratePaperDTO.CategoryConfig cat : config.getCategories()) {
                categoryWeights.put(cat.getCategoryId(), cat.getWeight());
            }
            
            // 计算每个分类应该抽取的题目数
            Map<String, Integer> categoryCounts = new HashMap<>();
            for (AutoGeneratePaperDTO.CategoryConfig cat : config.getCategories()) {
                int catCount = (int) Math.ceil(count * cat.getWeight() / 100.0);
                categoryCounts.put(cat.getCategoryId(), Math.min(catCount, count - result.size()));
            }
            
            // 按分类抽取
            for (AutoGeneratePaperDTO.CategoryConfig cat : config.getCategories()) {
                int needCount = categoryCounts.get(cat.getCategoryId());
                if (needCount <= 0) continue;
                
                List<ExamQuestion> questions = selectByCategoryAndDifficulty(
                        type, cat.getCategoryId(), config.getDifficulty(), needCount);
                result.addAll(questions);
            }
        }
        
        // 如果数量不够，随机补充
        if (result.size() < count) {
            int remaining = count - result.size();
            Set<String> existingIds = result.stream().map(ExamQuestion::getId).collect(Collectors.toSet());
            List<ExamQuestion> additional = selectRandomQuestions(type, config.getDifficulty(), remaining, existingIds);
            result.addAll(additional);
        }
        
        return result.subList(0, Math.min(count, result.size()));
    }
    
    private List<ExamQuestion> selectByCategoryAndDifficulty(String type, String categoryId, 
            AutoGeneratePaperDTO.DifficultyConfig difficulty, int count) {
        List<ExamQuestion> result = new ArrayList<>();
        
        if (difficulty != null) {
            int easyCount = (int) Math.ceil(count * difficulty.getEasy() / 100.0);
            int mediumCount = (int) Math.ceil(count * difficulty.getMedium() / 100.0);
            int hardCount = count - easyCount - mediumCount;
            
            result.addAll(questionMapper.selectRandomByTypeDifficultyCategory(type, "easy", categoryId, easyCount));
            result.addAll(questionMapper.selectRandomByTypeDifficultyCategory(type, "medium", categoryId, mediumCount));
            result.addAll(questionMapper.selectRandomByTypeDifficultyCategory(type, "hard", categoryId, Math.max(0, hardCount)));
        } else {
            result.addAll(questionMapper.selectRandomByTypeAndCategory(type, categoryId, count));
        }
        
        return result;
    }
    
    private List<ExamQuestion> selectRandomQuestions(String type, 
            AutoGeneratePaperDTO.DifficultyConfig difficulty, int count, Set<String> excludeIds) {
        LambdaQueryWrapper<ExamQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestion::getType, type);
        wrapper.eq(ExamQuestion::getStatus, "1");
        if (excludeIds != null && !excludeIds.isEmpty()) {
            wrapper.notIn(ExamQuestion::getId, excludeIds);
        }
        wrapper.last("LIMIT " + count);
        
        return questionMapper.selectList(wrapper);
    }
    
    // ==================== 考试下发 ====================
    
    @Override
    @Transactional
    public Map<String, Object> dispatchExam(ExamDispatchDTO dispatchDTO) {
        ExamPaper paper = paperMapper.selectById(dispatchDTO.getPaperId());
        if (paper == null) {
            throw new RuntimeException("试卷不存在");
        }
        
        List<String> userIds = new ArrayList<>();
        
        // 根据下发类型获取用户
        if ("user".equals(dispatchDTO.getDispatchType())) {
            userIds = dispatchDTO.getUserIds();
        } else if ("organization".equals(dispatchDTO.getDispatchType())) {
            // 查询组织下的所有用户
            if (dispatchDTO.getOrganizationIds() != null) {
                for (String orgId : dispatchDTO.getOrganizationIds()) {
                    List<User> users = userMapper.selectList(
                            new LambdaQueryWrapper<User>().eq(User::getOrganizationId, orgId));
                    userIds.addAll(users.stream().map(User::getId).collect(Collectors.toList()));
                }
            }
        }
        
        // 创建考试记录
        List<String> recordIds = new ArrayList<>();
        for (String userId : userIds) {
            User user = userMapper.selectById(userId);
            if (user == null) continue;
            
            // 检查是否已有考试记录
            LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ExamRecord::getPaperId, dispatchDTO.getPaperId());
            wrapper.eq(ExamRecord::getUserId, userId);
            wrapper.apply("deleted = 0");
            ExamRecord existing = recordMapper.selectOne(wrapper);
            
            if (existing == null) {
                ExamRecord record = new ExamRecord();
                record.setId(IdUtils.generateId());
                record.setPaperId(dispatchDTO.getPaperId());
                record.setPaperTitle(paper.getTitle());
                record.setUserId(userId);
                record.setUserName(user.getNickname());
                record.setOrganizationId(user.getOrganizationId());
                record.setOrganizationName("");
                record.setStatus("not_started");
                record.setTotalCount(paper.getQuestionCount());
                record.setTaskId(dispatchDTO.getTaskId());
                recordMapper.insert(record);
                recordIds.add(record.getId());
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("dispatchedCount", recordIds.size());
        result.put("recordIds", recordIds);
        result.put("message", "考试下发成功");
        
        return result;
    }
    
    // ==================== 考试记录 ====================
    
    @Override
    public List<Map<String, Object>> listExamRecords(Integer page, Integer pageSize, String paperId, 
            String userId, String status, String organizationId) {
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        if (paperId != null && !paperId.isEmpty()) {
            wrapper.eq(ExamRecord::getPaperId, paperId);
        }
        if (userId != null && !userId.isEmpty()) {
            wrapper.eq(ExamRecord::getUserId, userId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(ExamRecord::getStatus, status);
        }
        if (organizationId != null && !organizationId.isEmpty()) {
            wrapper.eq(ExamRecord::getOrganizationId, organizationId);
        }
        wrapper.orderByDesc(ExamRecord::getCreateTime);
        
        IPage<ExamRecord> result;
        if (page != null && pageSize != null) {
            result = recordMapper.selectPage(new Page<>(page, pageSize), wrapper);
        } else {
            result = recordMapper.selectPage(new Page<>(1, 100), wrapper);
        }
        
        return result.getRecords().stream().map(this::convertRecordToMap).collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getExamRecordById(String id) {
        ExamRecord record = recordMapper.selectById(id);
        return convertRecordToMap(record);
    }
    
    @Override
    @Transactional
    public Map<String, Object> startExam(String paperId, String userId) {
        ExamPaper paper = paperMapper.selectById(paperId);
        if (paper == null) {
            throw new RuntimeException("试卷不存在");
        }
        
        // 检查是否有进行中的考试
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getPaperId, paperId);
        wrapper.eq(ExamRecord::getUserId, userId);
        wrapper.in(ExamRecord::getStatus, "not_started", "in_progress");
        wrapper.apply("deleted = 0");
        ExamRecord existing = recordMapper.selectOne(wrapper);
        
        if (existing != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("recordId", existing.getId());
            result.put("message", "继续考试");
            return result;
        }
        
        User user = userMapper.selectById(userId);
        ExamRecord record = new ExamRecord();
        record.setId(IdUtils.generateId());
        record.setPaperId(paperId);
        record.setPaperTitle(paper.getTitle());
        record.setUserId(userId);
        record.setUserName(user != null ? user.getNickname() : "");
        record.setOrganizationId(user != null ? user.getOrganizationId() : null);
        record.setStatus("in_progress");
        record.setStartTime(LocalDateTime.now());
        record.setTotalCount(paper.getQuestionCount());
        recordMapper.insert(record);
        
        Map<String, Object> result = new HashMap<>();
        result.put("recordId", record.getId());
        result.put("message", "考试开始");
        return result;
    }
    
    @Override
    @Transactional
    public Map<String, Object> submitExam(ExamSubmitDTO submitDTO, String userId) {
        ExamRecord record = recordMapper.selectById(submitDTO.getExamRecordId());
        if (record == null) {
            throw new RuntimeException("考试记录不存在");
        }
        
        // 计算成绩
        int totalScore = 0;
        int correctCount = 0;
        
        try {
            List<Map<String, Object>> answers = objectMapper.readValue(
                    submitDTO.getAnswers(), new TypeReference<List<Map<String, Object>>>() {});
            
            for (Map<String, Object> answer : answers) {
                String questionId = (String) answer.get("questionId");
                Object userAnswerObj = answer.get("userAnswer");
                Integer answerTime = answer.get("answerTime") != null ? ((Number) answer.get("answerTime")).intValue() : 0;
                
                ExamQuestion question = questionMapper.selectById(questionId);
                if (question == null) continue;
                
                String userAnswer = userAnswerObj != null ? userAnswerObj.toString() : "";
                String correctAnswer = question.getAnswer();
                
                boolean isCorrect = checkAnswer(question.getType(), userAnswer, correctAnswer);
                
                ExamAnswer examAnswer = new ExamAnswer();
                examAnswer.setId(IdUtils.generateId());
                examAnswer.setExamRecordId(record.getId());
                examAnswer.setQuestionId(questionId);
                examAnswer.setQuestionType(question.getType());
                examAnswer.setUserAnswer(userAnswer);
                examAnswer.setIsCorrect(isCorrect ? 1 : 0);
                examAnswer.setScore(isCorrect ? question.getScore() : 0);
                examAnswer.setSortOrder(answer.get("sortOrder") != null ? ((Number) answer.get("sortOrder")).intValue() : 0);
                examAnswer.setAnswerTime(answerTime);
                answerMapper.insert(examAnswer);
                
                if (isCorrect) {
                    correctCount++;
                    totalScore += question.getScore();
                    
                    question.setCorrectCount(question.getCorrectCount() + 1);
                    questionMapper.updateById(question);
                }
            }
        } catch (Exception e) {
            log.error("解析答题数据失败", e);
        }
        
        // 更新考试记录
        int duration = 0;
        if (record.getStartTime() != null) {
            duration = (int) java.time.Duration.between(record.getStartTime(), LocalDateTime.now()).getSeconds();
        }
        
        record.setStatus("graded");
        record.setScore(totalScore);
        record.setCorrectCount(correctCount);
        record.setCorrectRate(BigDecimal.valueOf(correctCount * 100.0 / record.getTotalCount())
                .setScale(2, RoundingMode.HALF_UP));
        record.setIsPassed((totalScore >= (recordMapper.selectById(record.getId()).getPaperId() != null ? 1 : 0)) ? 1 : 0);
        record.setSubmitTime(LocalDateTime.now());
        record.setDuration(duration);
        recordMapper.updateById(record);
        
        Map<String, Object> result = new HashMap<>();
        result.put("recordId", record.getId());
        result.put("score", totalScore);
        result.put("correctCount", correctCount);
        result.put("totalCount", record.getTotalCount());
        result.put("isPassed", totalScore >= (record.getPaperId() != null ? 1 : 0));
        result.put("message", "提交成功");
        
        return result;
    }
    
    private boolean checkAnswer(String type, String userAnswer, String correctAnswer) {
        if (userAnswer == null || correctAnswer == null) return false;
        
        try {
            List<String> userList = objectMapper.readValue(userAnswer, new TypeReference<List<String>>() {});
            List<String> correctList = objectMapper.readValue(correctAnswer, new TypeReference<List<String>>() {});
            
            if (type.equals("multiple_choice")) {
                Collections.sort(userList);
                Collections.sort(correctList);
            }
            
            return userList.equals(correctList);
        } catch (Exception e) {
            return userAnswer.equalsIgnoreCase(correctAnswer.replace("[\"", "").replace("\"]", ""));
        }
    }
    
    @Override
    public List<AnswerDetailDTO> getAnswerDetails(String recordId) {
        ExamRecord record = recordMapper.selectById(recordId);
        if (record == null) return new ArrayList<>();
        
        List<ExamAnswer> answers = answerMapper.selectByExamRecordId(recordId);
        
        return answers.stream().map(a -> {
            ExamQuestion question = questionMapper.selectById(a.getQuestionId());
            AnswerDetailDTO dto = new AnswerDetailDTO();
            dto.setQuestionId(a.getQuestionId());
            dto.setQuestionType(a.getQuestionType());
            if (question != null) {
                dto.setContent(question.getContent());
                dto.setOptions(question.getOptions());
                dto.setCorrectAnswer(question.getAnswer());
                dto.setQuestionScore(question.getScore());
                dto.setAnalysis(question.getAnalysis());
            }
            dto.setUserAnswer(a.getUserAnswer());
            dto.setIsCorrect(a.getIsCorrect());
            dto.setScore(a.getScore());
            dto.setSortOrder(a.getSortOrder());
            dto.setAnswerTime(a.getAnswerTime());
            return dto;
        }).collect(Collectors.toList());
    }
    
    // ==================== 统计分析 ====================
    
    @Override
    public ExamStatisticsDTO getExamStatistics(String paperId) {
        ExamPaper paper = paperMapper.selectById(paperId);
        if (paper == null) return null;
        
        ExamStatisticsDTO stats = new ExamStatisticsDTO();
        stats.setPaperId(paperId);
        stats.setPaperTitle(paper.getTitle());
        
        stats.setTotalCount(recordMapper.countByPaperId(paperId));
        stats.setCompletedCount(recordMapper.countByPaperIdAndStatus(paperId, "graded"));
        
        BigDecimal completionRate = BigDecimal.ZERO;
        if (stats.getTotalCount() > 0) {
            completionRate = BigDecimal.valueOf(stats.getCompletedCount() * 100.0 / stats.getTotalCount())
                    .setScale(2, RoundingMode.HALF_UP);
        }
        stats.setCompletionRate(completionRate);
        
        stats.setAvgScore(recordMapper.getAvgScoreByPaperId(paperId));
        stats.setMaxScore(recordMapper.getMaxScoreByPaperId(paperId));
        stats.setMinScore(recordMapper.getMinScoreByPaperId(paperId));
        
        stats.setPassCount(recordMapper.countPassedByPaperId(paperId));
        BigDecimal passRate = BigDecimal.ZERO;
        if (stats.getCompletedCount() > 0) {
            passRate = BigDecimal.valueOf(stats.getPassCount() * 100.0 / stats.getCompletedCount())
                    .setScale(2, RoundingMode.HALF_UP);
        }
        stats.setPassRate(passRate);
        
        stats.setAvgCorrectRate(recordMapper.getAvgCorrectRateByPaperId(paperId));
        
        return stats;
    }
    
    @Override
    public List<Map<String, Object>> getExamRecordsByUser(String userId) {
        List<ExamRecord> records = recordMapper.selectByUserId(userId);
        return records.stream().map(this::convertRecordToMap).collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getExamRank(String paperId, String userId) {
        List<ExamRecord> records = recordMapper.selectList(
                new LambdaQueryWrapper<ExamRecord>()
                        .eq(ExamRecord::getPaperId, paperId)
                        .eq(ExamRecord::getStatus, "graded")
                        .orderByDesc(ExamRecord::getScore));
        
        int rank = 1;
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getUserId().equals(userId)) {
                rank = i + 1;
                break;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("rank", rank);
        result.put("total", records.size());
        return result;
    }
    
    // ==================== 工具方法 ====================
    
    private Map<String, Object> convertQuestionToMap(ExamQuestion q) {
        Map<String, Object> map = new HashMap<>();
        if (q == null) return map;
        map.put("id", q.getId());
        map.put("type", q.getType());
        map.put("difficulty", q.getDifficulty());
        map.put("content", q.getContent());
        map.put("options", fromJson(q.getOptions()));
        map.put("answer", fromJson(q.getAnswer()));
        map.put("analysis", q.getAnalysis());
        map.put("score", q.getScore());
        map.put("categoryId", q.getCategoryId());
        map.put("lawId", q.getLawId());
        map.put("tag", fromJson(q.getTag()));
        map.put("usageCount", q.getUsageCount());
        map.put("correctRate", q.getUsageCount() > 0 ? 
                BigDecimal.valueOf(q.getCorrectCount() * 100.0 / q.getUsageCount()).setScale(2, RoundingMode.HALF_UP) : 0);
        map.put("status", q.getStatus());
        map.put("createTime", q.getCreateTime());
        return map;
    }
    
    private Map<String, Object> convertPaperToMap(ExamPaper p) {
        Map<String, Object> map = new HashMap<>();
        if (p == null) return map;
        map.put("id", p.getId());
        map.put("title", p.getTitle());
        map.put("description", p.getDescription());
        map.put("totalScore", p.getTotalScore());
        map.put("passScore", p.getPassScore());
        map.put("timeLimit", p.getTimeLimit());
        map.put("questionCount", p.getQuestionCount());
        map.put("sourceType", p.getSourceType());
        map.put("status", p.getStatus());
        map.put("publishTime", p.getPublishTime());
        map.put("createTime", p.getCreateTime());
        
        // 统计考试情况
        map.put("totalRecords", recordMapper.countByPaperId(p.getId()));
        map.put("completedRecords", recordMapper.countByPaperIdAndStatus(p.getId(), "graded"));
        return map;
    }
    
    private Map<String, Object> convertRecordToMap(ExamRecord r) {
        Map<String, Object> map = new HashMap<>();
        if (r == null) return map;
        map.put("id", r.getId());
        map.put("paperId", r.getPaperId());
        map.put("paperTitle", r.getPaperTitle());
        map.put("userId", r.getUserId());
        map.put("userName", r.getUserName());
        map.put("organizationId", r.getOrganizationId());
        map.put("organizationName", r.getOrganizationName());
        map.put("status", r.getStatus());
        map.put("score", r.getScore());
        map.put("isPassed", r.getIsPassed());
        map.put("correctCount", r.getCorrectCount());
        map.put("totalCount", r.getTotalCount());
        map.put("correctRate", r.getCorrectRate());
        map.put("startTime", r.getStartTime());
        map.put("submitTime", r.getSubmitTime());
        map.put("duration", r.getDuration());
        map.put("createTime", r.getCreateTime());
        return map;
    }
    
    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return obj.toString();
        }
    }
    
    private Object fromJson(String json) {
        if (json == null || json.isEmpty()) return null;
        try {
            return objectMapper.readValue(json, Object.class);
        } catch (Exception e) {
            return json;
        }
    }
}
