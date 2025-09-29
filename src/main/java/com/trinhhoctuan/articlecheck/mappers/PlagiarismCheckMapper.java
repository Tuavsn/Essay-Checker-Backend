package com.trinhhoctuan.articlecheck.mappers;

import org.springframework.stereotype.Component;

import com.trinhhoctuan.articlecheck.dtos.PlagiarismCheckDto;
import com.trinhhoctuan.articlecheck.models.PlagiarismCheck;

@Component
public class PlagiarismCheckMapper {
  /**
   * Convert a PlagiarismCheck entity to a DTO.
   * 
   * @param plagiarismCheck
   * @return
   */
  public PlagiarismCheckDto convertToDto(PlagiarismCheck plagiarismCheck) {
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
