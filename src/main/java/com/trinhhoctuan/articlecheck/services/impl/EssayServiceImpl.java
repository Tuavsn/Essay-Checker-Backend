package com.trinhhoctuan.articlecheck.services.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.trinhhoctuan.articlecheck.dtos.EssayDto;
import com.trinhhoctuan.articlecheck.dtos.FileUploadResponse;
import com.trinhhoctuan.articlecheck.mappers.EssayMapper;
import com.trinhhoctuan.articlecheck.models.Essay;
import com.trinhhoctuan.articlecheck.models.User;
import com.trinhhoctuan.articlecheck.models.EditHistory.ChangeType;
import com.trinhhoctuan.articlecheck.models.Essay.EssayStatus;
import com.trinhhoctuan.articlecheck.repositories.EssayRepository;
import com.trinhhoctuan.articlecheck.repositories.UserRepository;
import com.trinhhoctuan.articlecheck.services.EditHistoryService;
import com.trinhhoctuan.articlecheck.services.EssayService;
import com.trinhhoctuan.articlecheck.services.FileProcessingService;
import com.trinhhoctuan.articlecheck.services.GrammarCheckService;
import com.trinhhoctuan.articlecheck.services.OpenAIService;
import com.trinhhoctuan.articlecheck.services.PlagiarismCheckService;
import com.trinhhoctuan.articlecheck.services.IgnoreWordsService;
import com.trinhhoctuan.articlecheck.utils.SecurityUtil;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the EssayService interface.
 * This class provides methods to manage essays, including uploading, processing,
 * and updating essay content.
 */
@Service
@Slf4j
public class EssayServiceImpl implements EssayService {
  private final EssayRepository essayRepository;
  private final UserRepository userRepository;
  private final FileProcessingService fileProcessingService;
  private final GrammarCheckService grammarCheckService;
  private final PlagiarismCheckService plagiarismCheckService;
  private final EditHistoryService editHistoryService;
  private final OpenAIService openAIService;
  private final IgnoreWordsService wordListService;
  private final SecurityUtil securityUtil;
  private final EssayMapper essayMapper;

  public EssayServiceImpl(
      EssayRepository essayRepository,
      UserRepository userRepository,
      FileProcessingService fileProcessingService,
      GrammarCheckService grammarCheckService,
      PlagiarismCheckService plagiarismCheckService,
      EditHistoryService editHistoryService,
      OpenAIService openAIService,
      IgnoreWordsService wordListService,
      SecurityUtil securityUtil,
      EssayMapper essayMapper) {
    this.essayRepository = essayRepository;
    this.userRepository = userRepository;
    this.fileProcessingService = fileProcessingService;
    this.grammarCheckService = grammarCheckService;
    this.plagiarismCheckService = plagiarismCheckService;
    this.editHistoryService = editHistoryService;
    this.openAIService = openAIService;
    this.wordListService = wordListService;
    this.securityUtil = securityUtil;
    this.essayMapper = essayMapper;
  }

  /**
   * Get an essay by its ID.
   *
   * @param essayId the ID of the essay
   * @return the essay DTO
   */
  @Override
  public EssayDto getEssay(Long essayId) {
    Essay essay = essayRepository.findById(essayId)
        .orElseThrow(() -> new RuntimeException("Essay not found"));
    return essayMapper.convertToDto(essay);
  }

  /**
   * Get all essays for a user.
   *
   * @return the list of essay DTOs
   */
  @Override
  public List<EssayDto> getUserEssays() {
    Long currentUserId = securityUtil.getCurrentUserId();
    return essayRepository.findByUserIdOrderByCreatedAtDesc(currentUserId)
        .stream()
        .map(essayMapper::convertToDto)
        .collect(Collectors.toList());
  }

  /**
   * Upload and process a file.
   *
   * @param file  the file to upload
   * @param title the title of the essay
   * @return the file upload response
   */
  @Override
  @Transactional
  public FileUploadResponse uploadAndProcessFile(MultipartFile file, String title) {
    try {
      // Validate File
      if (file.isEmpty() || !fileProcessingService.isSupportedFileType(file.getOriginalFilename())) {
        throw new IllegalArgumentException("Invalid file type or empty file");
      }

      // Find User
      Long userId = securityUtil.getCurrentUserId();

      User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

      // Extract File
      String extractedText = fileProcessingService.extractTextFromFile(file);

      // Save File
      String fileName = fileProcessingService.saveFileToSystem(file);

      // Save Essay
      Essay essay = Essay.builder()
          .title(title != null ? title : file.getOriginalFilename())
          .originalContent(extractedText)
          .processedContent(extractedText)
          .fileName(fileName)
          .fileType(fileProcessingService.getFileExtension(file.getOriginalFilename()))
          .status(EssayStatus.UPLOADED)
          .user(user)
          .build();

      Essay savedEssay = essayRepository.save(essay);

      // Record Edit History
      editHistoryService.recordEdit(savedEssay, "", extractedText,
          "Initial file upload", ChangeType.MANUAL_EDIT);

      return FileUploadResponse.builder()
          .essayId(savedEssay.getId())
          .fileName(file.getOriginalFilename())
          .message("File uploaded successfully")
          .success(true)
          .build();
    } catch (IOException e) {
      log.error("Error processing file: {}", e.getMessage());
      return FileUploadResponse.builder()
          .success(false)
          .message("Error processing file: " + e.getMessage())
          .build();
    }
  }

  /**
   * Process an essay.
   *
   * @param essayId the ID of the essay
   * @return the processed essay DTO
   */
  @Override
  public EssayDto processEssay(Long essayId, Long wordListId) {
    Essay essay = essayRepository.findById(essayId)
        .orElseThrow(() -> new RuntimeException("Essay not found"));

    List<String> customWords = wordListService.getIgnoreWordssById(wordListId);

    try {
      // Update status to processing
      essay.setStatus(EssayStatus.PROCESSING);
      essayRepository.save(essay);

      // Perform grammar check
      grammarCheckService.checkGrammar(essay, customWords);
      essay.setStatus(EssayStatus.GRAMMAR_CHECKED);
      essayRepository.save(essay);

      // Perform plagiarism check
      // plagiarismCheckService.checkPlagiarism(essay, essay.getOriginalContent());
      // essay.setStatus(EssayStatus.PLAGIARISM_CHECKED);
      // essayRepository.save(essay);

      // Mark as completed
      essay.setStatus(EssayStatus.COMPLETED);
      essayRepository.save(essay);

      return essayMapper.convertToDto(essay);
    } catch (Exception e) {
      log.error("Error processing essay: {}", essayId, e);
      essay.setStatus(EssayStatus.ERROR);
      essayRepository.save(essay);
      throw new RuntimeException("Essay processing failed", e);
    }
  }

  /**
   * Generate AI suggestions for an essay.
   *
   * @param essayId the ID of the essay
   * @param context the context for generating suggestions
   * @return the AI-generated suggestions
   */
  @Override
  public String generateAISuggestions(Long essayId, String context) {
    Essay essay = essayRepository.findById(essayId)
        .orElseThrow(() -> new RuntimeException("Essay not found"));

    return openAIService.generateSuggestions(essay.getOriginalContent(), context);
  }

  /**
   * Update the content of an essay.
   *
   * @param essayId           the ID of the essay
   * @param newContent        the new content for the essay
   * @param changeDescription a description of the change
   * @return the updated essay DTO
   */
  @Override
  @Transactional
  public EssayDto updateEssayContent(Long essayId, String newContent, String changeDescription) {
    Essay essay = essayRepository.findById(essayId)
        .orElseThrow(() -> new RuntimeException("Essay not found"));

    String previousContent = essay.getProcessedContent();

    // Record the edit in history
    editHistoryService.recordEdit(essay, previousContent, newContent,
        changeDescription, ChangeType.MANUAL_EDIT);

    // Update essay content
    essay.setProcessedContent(newContent);
    Essay updatedEssay = essayRepository.save(essay);

    return essayMapper.convertToDto(updatedEssay);
  }
}
