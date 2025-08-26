package com.trinhhoctuan.articlecheck.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trinhhoctuan.articlecheck.dto.PlagiarismCheckDto;
import com.trinhhoctuan.articlecheck.service.PlagiarismCheckService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/plagiarism")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PlagiarismController {
  private final PlagiarismCheckService plagiarismCheckService;

  @GetMapping("/essay/{essayId}")
  public ResponseEntity<List<PlagiarismCheckDto>> getPlagiarismChecks(@PathVariable Long essayId) {
    try {
      List<PlagiarismCheckDto> plagiarismChecks = plagiarismCheckService.getPlagiarismChecks(essayId);
      return ResponseEntity.ok(plagiarismChecks);
    } catch (Exception e) {
      log.error("Error getting plagiarism checks for essay: {}", essayId, e);
      return ResponseEntity.internalServerError().build();
    }
  }

  @GetMapping("/essay/{essayId}/high-similarity")
  public ResponseEntity<List<PlagiarismCheckDto>> getHighSimilarityChecks(
      @PathVariable Long essayId,
      @RequestParam(defaultValue = "0.8") Double threshold) {

    try {
      List<PlagiarismCheckDto> highSimilarityChecks = plagiarismCheckService.getHighSimilarityChecks(essayId,
          threshold);
      return ResponseEntity.ok(highSimilarityChecks);
    } catch (Exception e) {
      log.error("Error getting high similarity checks for essay: {}", essayId, e);
      return ResponseEntity.internalServerError().build();
    }
  }
}
