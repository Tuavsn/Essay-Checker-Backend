package com.trinhhoctuan.articlecheck.service;

import java.util.List;

import com.trinhhoctuan.articlecheck.dto.GrammarCheckDto;
import com.trinhhoctuan.articlecheck.model.Essay;

public interface GrammarCheckService {
    public List<GrammarCheckDto> checkGrammar(Essay essay, String text);

    public List<GrammarCheckDto> getGrammarChecks(Long essayId);

    public void markAsFixed(Long grammarCheckId);
}
