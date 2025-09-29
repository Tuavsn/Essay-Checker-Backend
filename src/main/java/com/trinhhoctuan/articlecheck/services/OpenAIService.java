package com.trinhhoctuan.articlecheck.services;

import java.util.List;

/**
 * Service interface for OpenAI-related functionalities.
 * Provides methods to generate suggestions and improve text using OpenAI's
 * capabilities.
 * Integrates with OpenAI API for text generation and enhancement.
 */
public interface OpenAIService {
    /**
     * Generate suggestions based on the provided text and context.
     * 
     * @param text
     * @param context
     * @return
     */
    public String generateSuggestions(String text, String context);

    /**
     * Improve the given text by addressing specific issues.
     * 
     * @param originalText
     * @param specificIssues
     * @return
     */
    public String improveText(String originalText, List<String> specificIssues);
}
