package com.trinhhoctuan.articlecheck.services;

import java.util.List;

import com.trinhhoctuan.articlecheck.dtos.EditHistoryDto;
import com.trinhhoctuan.articlecheck.models.Essay;
import com.trinhhoctuan.articlecheck.models.EditHistory.ChangeType;

public interface EditHistoryService {
    public void recordEdit(Essay essay, String previousContent, String newContent, String description,
            ChangeType changeType);

    public List<EditHistoryDto> getEditHistory(Long essayId);
}
