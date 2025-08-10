package com.trinhhoctuan.articlecheck.dto;

import java.time.LocalDateTime;

import com.trinhhoctuan.articlecheck.model.EditHistory.ChangeType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditHistoryDto {
    private Long id;
    private String previousContent;
    private String newContent;
    private String changeDescription;
    private ChangeType changeType;
    private LocalDateTime createdAt;
}
