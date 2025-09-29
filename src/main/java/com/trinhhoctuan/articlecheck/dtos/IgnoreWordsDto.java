package com.trinhhoctuan.articlecheck.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IgnoreWordsDto {
  private long id;
  private long userId;
  private String words;
  private boolean isPublic;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
