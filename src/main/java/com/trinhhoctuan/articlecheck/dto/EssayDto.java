package com.trinhhoctuan.articlecheck.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.trinhhoctuan.articlecheck.model.Essay.EssayStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EssayDto {
    private Long id;
    private String title;
    private String originalContent;
    private String processedContent;
    private String fileName;
    private String fileType;
    private EssayStatus status;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<GrammarCheckDto> grammarChecks;
    private List<PlagiarismCheckDto> plagiarismChecks;

}
