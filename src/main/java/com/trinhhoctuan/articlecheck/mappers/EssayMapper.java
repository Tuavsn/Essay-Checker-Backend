package com.trinhhoctuan.articlecheck.mappers;

import org.springframework.stereotype.Component;

import com.trinhhoctuan.articlecheck.dtos.EssayDto;
import com.trinhhoctuan.articlecheck.models.Essay;

@Component
public class EssayMapper {
  /**
   * Convert an Essay entity to a DTO.
   *
   * @param essay the Essay entity
   * @return the corresponding Essay DTO
   */
  public EssayDto convertToDto(Essay essay) {
    return EssayDto.builder()
        .id(essay.getId())
        .title(essay.getTitle())
        .originalContent(essay.getOriginalContent())
        .processedContent(essay.getProcessedContent())
        .fileName(essay.getFileName())
        .fileType(essay.getFileType())
        .status(essay.getStatus())
        .userId(essay.getUser().getId())
        .createdAt(essay.getCreatedAt())
        .updatedAt(essay.getUpdatedAt())
        .build();
  }
}
