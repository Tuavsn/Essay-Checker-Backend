package com.trinhhoctuan.articlecheck.mappers;

import org.springframework.stereotype.Component;

import com.trinhhoctuan.articlecheck.dtos.IgnoreWordsDto;
import com.trinhhoctuan.articlecheck.models.IgnoreWords;

@Component
public class IgnoreWordsMapper {
  /**
   * Convert Words entity to IgnoreWordssDto
   * 
   * @param words
   * @return
   */
  public IgnoreWordsDto convertToDto(IgnoreWords words) {
    return IgnoreWordsDto.builder()
        .id(words.getId())
        .userId(words.getUser().getId())
        .words(words.getWords())
        .isPublic(words.isPublic())
        .createdAt(words.getCreatedAt())
        .updatedAt(words.getUpdatedAt())
        .build();
  }
}
