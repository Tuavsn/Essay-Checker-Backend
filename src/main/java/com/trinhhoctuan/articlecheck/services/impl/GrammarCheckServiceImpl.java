package com.trinhhoctuan.articlecheck.services.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.languagetool.JLanguageTool;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.spelling.hunspell.HunspellRule;
import org.springframework.stereotype.Service;

import com.trinhhoctuan.articlecheck.dtos.GrammarCheckDto;
import com.trinhhoctuan.articlecheck.mappers.GrammarCheckMapper;
import com.trinhhoctuan.articlecheck.models.Essay;
import com.trinhhoctuan.articlecheck.models.GrammarCheck;
import com.trinhhoctuan.articlecheck.models.GrammarCheck.ErrorSeverity;
import com.trinhhoctuan.articlecheck.repositories.GrammarCheckRepository;
import com.trinhhoctuan.articlecheck.services.GrammarCheckService;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the GrammarCheckService interface.
 * This class provides methods to check and manage grammar checks for essays.
 */
@Service
@Slf4j
public class GrammarCheckServiceImpl implements GrammarCheckService {
  private final GrammarCheckRepository grammarCheckRepository;
  private final GrammarCheckMapper grammarCheckMapper;
  private final JLanguageTool languageTool;

  public GrammarCheckServiceImpl(
      GrammarCheckRepository grammarCheckRepository,
      GrammarCheckMapper grammarCheckMapper,
      JLanguageTool languageTool) {
    this.grammarCheckRepository = grammarCheckRepository;
    this.grammarCheckMapper = grammarCheckMapper;
    this.languageTool = languageTool;
  }

  /**
   * Check the grammar of the given text.
   * 
   * @param essay The essay entity.
   * @param text  The text to check.
   * @return A list of grammar check results.
   */
  @Override
  public List<GrammarCheckDto> checkGrammar(Essay essay, List<String> customWords) {
    try {
      // Add custom words to the Hunspell dictionary
      addCustomWordsToDictionary(customWords);

      // Check the text for grammar issues
      List<RuleMatch> matches = languageTool.check(essay.getOriginalContent());

      // Convert RuleMatches to GrammarCheck entities
      List<GrammarCheck> grammarChecks = matches.stream()
          .map(match -> convertToGrammarCheck(match, essay))
          .collect(Collectors.toList());

      // Save grammar checks to the database
      grammarCheckRepository.saveAll(grammarChecks);

      return grammarChecks.stream()
          .map(grammarCheckMapper::convertToDto)
          .collect(Collectors.toList());
    } catch (IOException e) {
      log.error("Error checking grammar for essay {}", essay.getId(), e);
      throw new RuntimeException("Grammar check failed", e);

    }
  }

  /**
   * Get all grammar checks for a specific essay.
   * 
   * @param essayId The ID of the essay.
   * @return A list of grammar check results.
   */
  @Override
  public List<GrammarCheckDto> getGrammarChecks(Long essayId) {
    return grammarCheckRepository.findByEssayId(essayId)
        .stream()
        .map(grammarCheckMapper::convertToDto)
        .collect(Collectors.toList());
  }

  /**
   * Mark a specific grammar check as fixed.
   */
  @Override
  public void markAsFixed(Long grammarCheckId) {
    GrammarCheck grammarCheck = grammarCheckRepository.findById(grammarCheckId)
        .orElseThrow(() -> new RuntimeException("Grammar check not found"));
    grammarCheck.setIsFixed(true);
    grammarCheckRepository.save(grammarCheck);

  }

  /**
   * Convert a RuleMatch to a GrammarCheck entity.
   * 
   * @param match
   * @param essay
   * @return
   */
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

  /**
   * Map a category name to an error severity level.
   * 
   * @param categoryName
   * @return
   */
  private ErrorSeverity mapSeverity(String categoryName) {
    if (categoryName.contains("Grammar") || categoryName.contains("Punctuation")) {
      return ErrorSeverity.HIGH;
    } else if (categoryName.contains("Style")) {
      return ErrorSeverity.MEDIUM;
    } else {
      return ErrorSeverity.LOW;
    }
  }

  private void addCustomWordsToDictionary(List<String> customWords) {
    // Add custom words to the Hunspell dictionary
    languageTool.getAllActiveRules().stream()
        .filter(rule -> rule instanceof HunspellRule)
        .map(rule -> (HunspellRule) rule)
        .forEach(hunspellRule -> {
          hunspellRule.addIgnoreTokens(customWords);
        });
  }
}
