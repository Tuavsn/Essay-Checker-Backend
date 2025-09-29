package com.trinhhoctuan.articlecheck.mappers;

import org.springframework.stereotype.Component;

import com.trinhhoctuan.articlecheck.dtos.GrammarCheckDto;
import com.trinhhoctuan.articlecheck.models.GrammarCheck;

@Component
public class GrammarCheckMapper {
  /**
   * Convert a GrammarCheck entity to a DTO.
   * 
   * @param grammarCheck
   * @return
   */
  public GrammarCheckDto convertToDto(GrammarCheck grammarCheck) {
    return GrammarCheckDto.builder()
        .id(grammarCheck.getId())
        .startPosition(grammarCheck.getStartPosition())
        .endPosition(grammarCheck.getEndPosition())
        .errorText(grammarCheck.getErrorText())
        .ruleId(grammarCheck.getRuleId())
        .message(grammarCheck.getMessage())
        .suggestedReplacement(grammarCheck.getSuggestedReplacement())
        .severity(grammarCheck.getSeverity())
        .isFixed(grammarCheck.getIsFixed())
        .build();
  }
}
