package com.trinhhoctuan.articlecheck.service;

import java.util.List;

import com.trinhhoctuan.articlecheck.dto.PlagiarismCheckDto;
import com.trinhhoctuan.articlecheck.model.Essay;

public interface PlagiarismCheckService {
    public List<PlagiarismCheckDto> checkPlagiarism(Essay essay, String text);

    public List<PlagiarismCheckDto> getPlagiarismChecks(Long essayId);

    public List<PlagiarismCheckDto> getHighSimilarityChecks(Long essayId, Double threshold);
}
