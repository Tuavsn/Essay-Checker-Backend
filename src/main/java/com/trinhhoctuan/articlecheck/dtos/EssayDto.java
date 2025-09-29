package com.trinhhoctuan.articlecheck.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.trinhhoctuan.articlecheck.enums.FileType;
import com.trinhhoctuan.articlecheck.models.Essay.EssayStatus;

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
    private Long userId;
    private String title;
    private String originalContent;
    private String processedContent;
    private String fileName;
    private FileType fileType;
    private EssayStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<GrammarCheckDto> grammarChecks;
    private List<PlagiarismCheckDto> plagiarismChecks;

}
