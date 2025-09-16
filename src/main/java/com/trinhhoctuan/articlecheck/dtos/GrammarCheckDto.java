package com.trinhhoctuan.articlecheck.dtos;

import com.trinhhoctuan.articlecheck.models.GrammarCheck.ErrorSeverity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrammarCheckDto {
    private Long id;
    private Integer startPosition;
    private Integer endPosition;
    private String errorText;
    private String ruleId;
    private String message;
    private String suggestedReplacement;
    private ErrorSeverity severity;
    private Boolean isFixed;
}
