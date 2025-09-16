package com.trinhhoctuan.articlecheck.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.trinhhoctuan.articlecheck.dtos.PlagiarismCheckDto;
import com.trinhhoctuan.articlecheck.models.Essay;
import com.trinhhoctuan.articlecheck.models.PlagiarismCheck;
import com.trinhhoctuan.articlecheck.repositories.PlagiarismCheckRepository;
import com.trinhhoctuan.articlecheck.services.PlagiarismCheckService;

import info.debatty.java.stringsimilarity.Cosine;
import info.debatty.java.stringsimilarity.Jaccard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlagiarismCheckServiceImpl implements PlagiarismCheckService {
  private final PlagiarismCheckRepository plagiarismCheckRepository;
  private final Cosine cosine = new Cosine();
  private final Jaccard jaccard = new Jaccard();

  List<String> referenceTexts = new ArrayList<>();

  /**
   * Check for plagiarism in the given essay text.
   * 
   * @param essay The essay entity.
   * @return A list of plagiarism check results.
   */
  @Override
  public List<PlagiarismCheckDto> checkPlagiarism(Essay essay, String text) {
    log.info("Checking plagiarism for essay: {}", essay.getId());

    List<PlagiarismCheck> plagiarismChecks = new ArrayList<>();

    // Split text into sentences for more granular checking
    String[] sentences = text.split("\\. ");

    for (int i = 0; i < sentences.length; i++) {
      String sentence = sentences[i].trim();
      if (sentence.length() < 50)
        continue; // Skip very short sentences

      for (int j = 0; j < referenceTexts.size(); j++) {
        String referenceText = referenceTexts.get(j);

        // Calculate similarity using different algorithms
        double cosineSimilarity = 1 - cosine.distance(sentence, referenceText);
        double jaccardSimilarity = 1 - jaccard.distance(sentence, referenceText);

        // Use average similarity
        double averageSimilarity = (cosineSimilarity + jaccardSimilarity) / 2;

        // Threshold for potential plagiarism
        if (averageSimilarity > 0.7) {
          int startPos = text.indexOf(sentence);
          int endPos = startPos + sentence.length();

          PlagiarismCheck check = PlagiarismCheck.builder()
              .essay(essay)
              .matchedText(sentence)
              .sourceUrl("https://example-source-" + (j + 1) + ".com")
              .sourceName("Reference Source " + (j + 1))
              .similarityScore(averageSimilarity)
              .startPosition(startPos)
              .endPosition(endPos)
              .build();

          plagiarismChecks.add(check);
        }
      }

    }

    plagiarismCheckRepository.saveAll(plagiarismChecks);

    return plagiarismChecks.stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  /**
   * Get all plagiarism checks for a specific essay.
   * 
   * @param essayId The ID of the essay.
   * @return A list of plagiarism check results.
   */
  @Override
  public List<PlagiarismCheckDto> getPlagiarismChecks(Long essayId) {
    return plagiarismCheckRepository.findByEssayId(essayId)
        .stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());

  }

  /**
   * Get all high similarity plagiarism checks for a specific essay.
   * 
   * @param essayId   The ID of the essay.
   * @param threshold The similarity score threshold.
   * @return A list of high similarity plagiarism check results.
   */
  @Override
  public List<PlagiarismCheckDto> getHighSimilarityChecks(Long essayId, Double threshold) {
    return plagiarismCheckRepository.findByEssayIdAndSimilarityScoreGreaterThan(essayId, threshold)
        .stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());

  }

  /**
   * Convert a PlagiarismCheck entity to a DTO.
   * 
   * @param plagiarismCheck
   * @return
   */
  private PlagiarismCheckDto convertToDto(PlagiarismCheck plagiarismCheck) {
    return PlagiarismCheckDto.builder()
        .id(plagiarismCheck.getId())
        .matchedText(plagiarismCheck.getMatchedText())
        .sourceUrl(plagiarismCheck.getSourceUrl())
        .sourceName(plagiarismCheck.getSourceName())
        .similarityScore(plagiarismCheck.getSimilarityScore())
        .startPosition(plagiarismCheck.getStartPosition())
        .endPosition(plagiarismCheck.getEndPosition())
        .build();
  }
}
