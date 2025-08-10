package com.trinhhoctuan.articlecheck.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlagiarismCheckDto {
    private Long id;
    private String matchedText;
    private String sourceUrl;
    private String sourceName;
    private Double similarityScore;
    private Integer startPosition;
    private Integer endPosition;
}
