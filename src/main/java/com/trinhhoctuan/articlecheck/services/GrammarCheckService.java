package com.trinhhoctuan.articlecheck.services;

import java.util.List;

import com.trinhhoctuan.articlecheck.dtos.GrammarCheckDto;
import com.trinhhoctuan.articlecheck.models.Essay;

/**
 * Service interface for grammar checking functionalities.
 * Provides methods to check grammar, retrieve grammar checks, and mark issues
 * as fixed.
 * Uses LanguageTool for grammar analysis.
 */
public interface GrammarCheckService {
    /**
     * Check the grammar of the given text and save the results.
     * 
     * @param essay
     * @param text
     * @return
     */
    public List<GrammarCheckDto> checkGrammar(Essay essay, List<String> customWords);

    /**
     * Get all grammar checks for a specific essay.
     * 
     * @param essayId
     * @return
     */
    public List<GrammarCheckDto> getGrammarChecks(Long essayId);

    /**
     * Mark a grammar check issue as fixed.
     * 
     * @param grammarCheckId
     */
    public void markAsFixed(Long grammarCheckId);
}
