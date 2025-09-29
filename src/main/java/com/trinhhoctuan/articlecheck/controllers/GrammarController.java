package com.trinhhoctuan.articlecheck.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trinhhoctuan.articlecheck.dtos.GrammarCheckDto;
import com.trinhhoctuan.articlecheck.services.GrammarCheckService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/grammar")
@Slf4j
@CrossOrigin(origins = "*")
public class GrammarController {
  private final GrammarCheckService grammarCheckService;

  public GrammarController(GrammarCheckService grammarCheckService) {
    this.grammarCheckService = grammarCheckService;
  }

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
