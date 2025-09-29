package com.trinhhoctuan.articlecheck.services;

import java.util.List;

import com.trinhhoctuan.articlecheck.dtos.PlagiarismCheckDto;
import com.trinhhoctuan.articlecheck.models.Essay;

/**
 * Service interface for plagiarism checking functionalities.
 * Provides methods to check plagiarism and retrieve plagiarism checks.
 */
public interface PlagiarismCheckService {
    /**
     * Check the plagiarism of the given text and save the results.
     * 
     * @param essay
     * @param text
     * @return
     */
    public List<PlagiarismCheckDto> checkPlagiarism(Essay essay, String text);

    /**
     * Get all plagiarism checks for a specific essay.
     * 
     * @param essayId
     * @return
     */
    public List<PlagiarismCheckDto> getPlagiarismChecks(Long essayId);

    /**
     * Get plagiarism checks with similarity above a certain threshold for a
     * specific essay.
     * 
     * @param essayId
     * @param threshold
     * @return
     */
    public List<PlagiarismCheckDto> getHighSimilarityChecks(Long essayId, Double threshold);
}
