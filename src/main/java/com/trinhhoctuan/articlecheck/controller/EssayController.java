package com.trinhhoctuan.articlecheck.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.trinhhoctuan.articlecheck.dto.EssayDto;
import com.trinhhoctuan.articlecheck.dto.FileUploadResponse;
import com.trinhhoctuan.articlecheck.service.EssayService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/essays")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class EssayController {
  private final EssayService essayService;

  @PostMapping("/upload")
  public ResponseEntity<FileUploadResponse> uploadFile(
      @RequestParam("file") MultipartFile file,
      @RequestParam("userId") Long userId,
      @RequestParam(value = "title", required = false) String title) {

    log.info("Uploading file: {} for user: {}", file.getOriginalFilename(), userId);

    FileUploadResponse response = essayService.uploadAndProcessFile(file, userId, title);

    if (response.getSuccess()) {
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.badRequest().body(response);
    }
  }

  @PostMapping("/{essayId}/process")
  public ResponseEntity<EssayDto> processEssay(@PathVariable Long essayId) {
    log.info("Processing essay: {}", essayId);

    try {
      EssayDto processedEssay = essayService.processEssay(essayId);
      return ResponseEntity.ok(processedEssay);
    } catch (Exception e) {
      log.error("Error processing essay: {}", essayId, e);
      return ResponseEntity.internalServerError().build();
    }
  }

  @GetMapping("/{essayId}")
  public ResponseEntity<EssayDto> getEssay(@PathVariable Long essayId) {
    try {
      EssayDto essay = essayService.getEssay(essayId);
      return ResponseEntity.ok(essay);
    } catch (Exception e) {
      log.error("Error getting essay: {}", essayId, e);
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<EssayDto>> getUserEssays(@PathVariable Long userId) {
    try {
      List<EssayDto> essays = essayService.getUserEssays(userId);
      return ResponseEntity.ok(essays);
    } catch (Exception e) {
      log.error("Error getting user essays: {}", userId, e);
      return ResponseEntity.internalServerError().build();
    }
  }

  @PutMapping("/{essayId}/content")
  public ResponseEntity<EssayDto> updateEssayContent(
      @PathVariable Long essayId,
      @RequestParam String content,
      @RequestParam(defaultValue = "Manual content update") String description) {

    try {
      EssayDto updatedEssay = essayService.updateEssayContent(essayId, content, description);
      return ResponseEntity.ok(updatedEssay);
    } catch (Exception e) {
      log.error("Error updating essay content: {}", essayId, e);
      return ResponseEntity.internalServerError().build();
    }
  }

  @GetMapping("/{essayId}/ai-suggestions")
  public ResponseEntity<String> getAISuggestions(
      @PathVariable Long essayId,
      @RequestParam(defaultValue = "General improvement") String context) {

    try {
      String suggestions = essayService.generateAISuggestions(essayId, context);
      return ResponseEntity.ok(suggestions);
    } catch (Exception e) {
      log.error("Error generating AI suggestions for essay: {}", essayId, e);
      return ResponseEntity.internalServerError().body("Error generating suggestions");
    }
  }

}
