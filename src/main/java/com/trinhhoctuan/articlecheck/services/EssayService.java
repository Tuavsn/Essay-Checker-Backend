package com.trinhhoctuan.articlecheck.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.trinhhoctuan.articlecheck.dtos.EssayDto;
import com.trinhhoctuan.articlecheck.dtos.FileUploadResponse;

/**
 * Service interface for essay management functionalities.
 * Provides methods to handle essay retrieval, file uploads, processing, and AI
 * suggestions.
 * Integrates with file processing, grammar checking, and plagiarism detection
 * services.
 */
public interface EssayService {
    /**
     * Get an essay by its ID.
     * 
     * @param essayId
     * @return
     */
    public EssayDto getEssay(Long essayId);

    /**
     * Get all essays for a specific user.
     * 
     * @param userId
     * @return
     */
    public List<EssayDto> getUserEssays();

    /**
     * Upload and process a file for a specific user.
     * 
     * @param file
     * @param title
     * @return
     */
    public FileUploadResponse uploadAndProcessFile(MultipartFile file, String title);

    /**
     * Process an essay by its ID, performing grammar and plagiarism checks.
     * 
     * @param essayId
     * @return
     */
    public EssayDto processEssay(Long essayId, Long wordListId);

    /**
     * Generate AI-based suggestions for improving the essay content.
     * 
     * @param essayId
     * @param context
     * @return
     */
    public String generateAISuggestions(Long essayId, String context);

    /**
     * Update the content of an essay with a new version and log the change
     * description.
     * 
     * @param essayId
     * @param newContent
     * @param changeDescription
     * @return
     */
    public EssayDto updateEssayContent(Long essayId, String newContent, String changeDescription);
}
