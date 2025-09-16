package com.trinhhoctuan.articlecheck.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.trinhhoctuan.articlecheck.dtos.EssayDto;
import com.trinhhoctuan.articlecheck.dtos.FileUploadResponse;
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

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

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
  private String uploadDir;

  public EssayServiceImpl(
      EssayRepository essayRepository,
      UserRepository userRepository,
      FileProcessingService fileProcessingService,
      GrammarCheckService grammarCheckService,
      PlagiarismCheckService plagiarismCheckService,
      EditHistoryService editHistoryService,
      OpenAIService openAIService,
      @Value("${file.upload.dir}") String uploadDir) {
    this.essayRepository = essayRepository;
    this.userRepository = userRepository;
    this.fileProcessingService = fileProcessingService;
    this.grammarCheckService = grammarCheckService;
    this.plagiarismCheckService = plagiarismCheckService;
    this.editHistoryService = editHistoryService;
    this.openAIService = openAIService;
    this.uploadDir = uploadDir;
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
    return convertToDto(essay);
  }

  /**
   * Get all essays for a user.
   *
   * @param userId the ID of the user
   * @return the list of essay DTOs
   */
  @Override
  public List<EssayDto> getUserEssays(Long userId) {
    return essayRepository.findByUserIdOrderByCreatedAtDesc(userId)
        .stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  /**
   * Upload and process a file.
   *
   * @param file   the file to upload
   * @param userId the ID of the user
   * @param title  the title of the essay
   * @return the file upload response
   */
  @Override
  @Transactional
  public FileUploadResponse uploadAndProcessFile(MultipartFile file, Long userId, String title) {

    try {
      // Validate File
      if (file.isEmpty() || !fileProcessingService.isSupportedFileType(file.getOriginalFilename())) {
        throw new IllegalArgumentException("Invalid file type or empty file");
      }

      // Find User
      User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

      // Extract File
      String extractedText = fileProcessingService.extractTextFromFile(file);

      // Save File
      String fileName = saveFileToSystem(file);

      // Save Essay
      Essay essay = Essay.builder()
          .title(title != null ? title : file.getOriginalFilename())
          .originalContent(extractedText)
          .processedContent(extractedText)
          .fileName(fileName)
          .fileType(getFileExtension(file.getOriginalFilename()))
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
  public EssayDto processEssay(Long essayId) {
    Essay essay = essayRepository.findById(essayId)
        .orElseThrow(() -> new RuntimeException("Essay not found"));

    try {
      // Update status to processing
      essay.setStatus(EssayStatus.PROCESSING);
      essayRepository.save(essay);

      // Perform grammar check
      grammarCheckService.checkGrammar(essay, essay.getOriginalContent());
      essay.setStatus(EssayStatus.GRAMMAR_CHECKED);
      essayRepository.save(essay);

      // Perform plagiarism check
      plagiarismCheckService.checkPlagiarism(essay, essay.getOriginalContent());
      essay.setStatus(EssayStatus.PLAGIARISM_CHECKED);
      essayRepository.save(essay);

      // Mark as completed
      essay.setStatus(EssayStatus.COMPLETED);
      essayRepository.save(essay);

      return convertToDto(essay);

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

    return convertToDto(updatedEssay);
  }

  // ============= Utils Operations ============
  /**
   * Save a file to the system.
   *
   * @param file the file to save
   * @return the filename of the saved file
   * @throws IOException if an error occurs while saving the file
   */
  private String saveFileToSystem(MultipartFile file) throws IOException {
    // Create upload directory if it doesn't exist
    Path uploadPath = Paths.get(uploadDir);
    if (!Files.exists(uploadPath)) {
      Files.createDirectories(uploadPath);
    }

    // Generate unique filename
    String originalFileName = file.getOriginalFilename();
    String fileName = System.currentTimeMillis() + "_" + originalFileName;
    Path filePath = uploadPath.resolve(fileName);

    // Save file
    Files.copy(file.getInputStream(), filePath);

    return fileName;
  }

  /**
   * Get the file extension of a given file.
   *
   * @param fileName the name of the file
   * @return the file extension
   */
  private String getFileExtension(String fileName) {
    if (fileName == null)
      return "";
    int lastDotIndex = fileName.lastIndexOf('.');
    return lastDotIndex == -1 ? "" : fileName.substring(lastDotIndex + 1);
  }

  /**
   * Convert an Essay entity to a DTO.
   *
   * @param essay the Essay entity
   * @return the corresponding Essay DTO
   */
  private EssayDto convertToDto(Essay essay) {
    return EssayDto.builder()
        .id(essay.getId())
        .title(essay.getTitle())
        .originalContent(essay.getOriginalContent())
        .processedContent(essay.getProcessedContent())
        .fileName(essay.getFileName())
        .fileType(essay.getFileType())
        .status(essay.getStatus())
        .userId(essay.getUser().getId())
        .createdAt(essay.getCreatedAt())
        .updatedAt(essay.getUpdatedAt())
        .build();
  }
}
