package com.trinhhoctuan.articlecheck.services;

import java.util.List;

import com.trinhhoctuan.articlecheck.dtos.GrammarCheckDto;
import com.trinhhoctuan.articlecheck.models.Essay;

public interface GrammarCheckService {
    public List<GrammarCheckDto> checkGrammar(Essay essay, String text);

    public List<GrammarCheckDto> getGrammarChecks(Long essayId);

    public void markAsFixed(Long grammarCheckId);
}
