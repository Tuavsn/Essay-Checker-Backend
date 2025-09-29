package com.trinhhoctuan.articlecheck.services;

import java.util.List;

import com.trinhhoctuan.articlecheck.dtos.EditHistoryDto;
import com.trinhhoctuan.articlecheck.models.Essay;
import com.trinhhoctuan.articlecheck.models.EditHistory.ChangeType;

/**
 * Service interface for managing edit history of essays.
 * Provides methods to record edits and retrieve edit history.
 */
public interface EditHistoryService {
    /**
     * Record an edit made to an essay.
     * 
     * @param essay
     * @param previousContent
     * @param newContent
     * @param description
     * @param changeType
     */
    public void recordEdit(Essay essay, String previousContent, String newContent, String description,
            ChangeType changeType);

    /**
     * Get all edit history records for a specific essay.
     * 
     * @param essayId
     * @return
     */
    public List<EditHistoryDto> getEditHistory(Long essayId);
}
