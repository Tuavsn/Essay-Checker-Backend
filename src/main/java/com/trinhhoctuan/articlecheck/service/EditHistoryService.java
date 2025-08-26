package com.trinhhoctuan.articlecheck.service;

import java.util.List;

import com.trinhhoctuan.articlecheck.dto.EditHistoryDto;
import com.trinhhoctuan.articlecheck.model.EditHistory.ChangeType;
import com.trinhhoctuan.articlecheck.model.Essay;

public interface EditHistoryService {
    public void recordEdit(Essay essay, String previousContent, String newContent, String description,
            ChangeType changeType);

    public List<EditHistoryDto> getEditHistory(Long essayId);
}
