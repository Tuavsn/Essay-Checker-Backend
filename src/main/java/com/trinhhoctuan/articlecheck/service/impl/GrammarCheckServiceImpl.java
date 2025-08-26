package com.trinhhoctuan.articlecheck.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.languagetool.JLanguageTool;
import org.languagetool.rules.RuleMatch;
import org.springframework.stereotype.Service;

import com.trinhhoctuan.articlecheck.dto.GrammarCheckDto;
import com.trinhhoctuan.articlecheck.model.Essay;
import com.trinhhoctuan.articlecheck.model.GrammarCheck;
import com.trinhhoctuan.articlecheck.model.GrammarCheck.ErrorSeverity;
import com.trinhhoctuan.articlecheck.repository.GrammarCheckRepository;
import com.trinhhoctuan.articlecheck.service.GrammarCheckService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GrammarCheckServiceImpl implements GrammarCheckService {
  private final GrammarCheckRepository grammarCheckRepository;
  private final JLanguageTool languageTool;

  @Override
  public List<GrammarCheckDto> checkGrammar(Essay essay, String text) {
    try {
      List<RuleMatch> matches = languageTool.check(text);

      List<GrammarCheck> grammarChecks = matches.stream()
          .map(match -> convertToGrammarCheck(match, essay))
          .collect(Collectors.toList());

      grammarCheckRepository.saveAll(grammarChecks);

      return grammarChecks.stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    } catch (IOException e) {
      log.error("Error checking grammar for essay {}", essay.getId(), e);
      throw new RuntimeException("Grammar check failed", e);

    }
  }

  @Override
  public List<GrammarCheckDto> getGrammarChecks(Long essayId) {
    return grammarCheckRepository.findByEssayId(essayId)
        .stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  @Override
  public void markAsFixed(Long grammarCheckId) {
    GrammarCheck grammarCheck = grammarCheckRepository.findById(grammarCheckId)
        .orElseThrow(() -> new RuntimeException("Grammar check not found"));
    grammarCheck.setIsFixed(true);
    grammarCheckRepository.save(grammarCheck);

  }

  private GrammarCheck convertToGrammarCheck(RuleMatch match, Essay essay) {
    return GrammarCheck.builder()
        .essay(essay)
        .startPosition(match.getFromPos())
        .endPosition(match.getToPos())
        .errorText(essay.getOriginalContent().substring(match.getFromPos(), match.getToPos()))
        .ruleId(match.getRule().getId())
        .message(match.getMessage())
        .suggestedReplacement(
            match.getSuggestedReplacements().isEmpty() ? null : String.join(", ", match.getSuggestedReplacements()))
        .severity(mapSeverity(match.getRule().getCategory().getName()))
        .isFixed(false)
        .build();
  }

  private ErrorSeverity mapSeverity(String categoryName) {
    if (categoryName.contains("Grammar") || categoryName.contains("Punctuation")) {
      return ErrorSeverity.HIGH;
    } else if (categoryName.contains("Style")) {
      return ErrorSeverity.MEDIUM;
    } else {
      return ErrorSeverity.LOW;
    }
  }

  private GrammarCheckDto convertToDto(GrammarCheck grammarCheck) {
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
