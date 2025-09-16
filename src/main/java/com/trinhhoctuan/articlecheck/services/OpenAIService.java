package com.trinhhoctuan.articlecheck.services;

import java.util.List;

public interface OpenAIService {
    public String generateSuggestions(String text, String context);

    public String improveText(String originalText, List<String> specificIssues);
}
