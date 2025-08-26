package com.trinhhoctuan.articlecheck.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trinhhoctuan.articlecheck.dto.EditHistoryDto;
import com.trinhhoctuan.articlecheck.service.EditHistoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/edit-history")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class EditHistoryController {
  private final EditHistoryService editHistoryService;

  @GetMapping("/essay/{essayId}")
  public ResponseEntity<List<EditHistoryDto>> getEditHistory(@PathVariable Long essayId) {
    try {
      List<EditHistoryDto> editHistory = editHistoryService.getEditHistory(essayId);
      return ResponseEntity.ok(editHistory);
    } catch (Exception e) {
      log.error("Error getting edit history for essay: {}", essayId, e);
      return ResponseEntity.internalServerError().build();
    }
  }
}
