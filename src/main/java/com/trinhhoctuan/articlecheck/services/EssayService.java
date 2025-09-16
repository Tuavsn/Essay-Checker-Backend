package com.trinhhoctuan.articlecheck.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.trinhhoctuan.articlecheck.dtos.EssayDto;
import com.trinhhoctuan.articlecheck.dtos.FileUploadResponse;

public interface EssayService {
    public EssayDto getEssay(Long essayId);

    public List<EssayDto> getUserEssays(Long userId);

    public FileUploadResponse uploadAndProcessFile(MultipartFile file, Long userId, String title);

    public EssayDto processEssay(Long essayId);

    public String generateAISuggestions(Long essayId, String context);

    public EssayDto updateEssayContent(Long essayId, String newContent, String changeDescription);
}
