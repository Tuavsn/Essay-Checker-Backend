package com.trinhhoctuan.articlecheck.services;

import java.util.List;

import com.trinhhoctuan.articlecheck.dtos.PlagiarismCheckDto;
import com.trinhhoctuan.articlecheck.models.Essay;

public interface PlagiarismCheckService {
    public List<PlagiarismCheckDto> checkPlagiarism(Essay essay, String text);

    public List<PlagiarismCheckDto> getPlagiarismChecks(Long essayId);

    public List<PlagiarismCheckDto> getHighSimilarityChecks(Long essayId, Double threshold);
}
