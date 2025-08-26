package com.trinhhoctuan.articlecheck.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trinhhoctuan.articlecheck.dto.GrammarCheckDto;
import com.trinhhoctuan.articlecheck.service.GrammarCheckService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/grammar")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class GrammarController {
  private final GrammarCheckService grammarCheckService;

  @GetMapping("/essay/{essayId}")
  public ResponseEntity<List<GrammarCheckDto>> getGrammarChecks(@PathVariable Long essayId) {
    try {
      List<GrammarCheckDto> grammarChecks = grammarCheckService.getGrammarChecks(essayId);
      return ResponseEntity.ok(grammarChecks);
    } catch (Exception e) {
      log.error("Error getting grammar checks for essay: {}", essayId, e);
      return ResponseEntity.internalServerError().build();
    }
  }

  @PutMapping("/{grammarCheckId}/fix")
  public ResponseEntity<Void> markAsFixed(@PathVariable Long grammarCheckId) {
    try {
      grammarCheckService.markAsFixed(grammarCheckId);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      log.error("Error marking grammar check as fixed: {}", grammarCheckId, e);
      return ResponseEntity.internalServerError().build();
    }
  }

}
