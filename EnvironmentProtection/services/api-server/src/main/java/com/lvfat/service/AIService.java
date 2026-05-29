package com.lvfat.service;

import com.lvfat.entity.AIMaterial;

import java.util.List;
import java.util.Map;

public interface AIService {
    Map<String, Object> generateMaterial(Map<String, Object> data);
    Map<String, Object> explainLaw(Map<String, Object> data);
    Map<String, Object> analyzeCase(Map<String, Object> data);
    Map<String, Object> generateExam(Map<String, Object> data);
    Map<String, Object> generateReport(Map<String, Object> data);
    Map<String, Object> chat(Map<String, Object> data);
    List<AIMaterial> listMaterials(String type, String keyword);
    AIMaterial getMaterial(String id);
    void deleteMaterial(String id);
}
