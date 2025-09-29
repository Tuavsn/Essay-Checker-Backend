package com.trinhhoctuan.articlecheck.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trinhhoctuan.articlecheck.dtos.IgnoreWordsDto;
import com.trinhhoctuan.articlecheck.services.IgnoreWordsService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/grammar-ignore-words")
@Slf4j
@CrossOrigin(origins = "*")
public class GrammarIgnoreWordsController {
  private final IgnoreWordsService ignoreWordsService;
  // DTOs
  private record IgnoreWordsRequest(String words) {};

  public GrammarIgnoreWordsController(IgnoreWordsService ignoreWordsService) {
    this.ignoreWordsService = ignoreWordsService;
  }

  @PostMapping()
  public ResponseEntity<IgnoreWordsDto> postMethodName(@RequestBody IgnoreWordsRequest request) {
    log.info("Creating word list: {}", request.words());
    IgnoreWordsDto createdWords = ignoreWordsService.createUserIgnoreWordss(request.words());
    return ResponseEntity.ok(createdWords);
  }

  @PutMapping("/{wordsId}")
  public ResponseEntity<IgnoreWordsDto> updateIgnoreWordss(@PathVariable Long wordsId, @RequestBody IgnoreWordsRequest request) {
    log.info("Updating word list {}: {}", wordsId, request.words());
    IgnoreWordsDto updatedWords = ignoreWordsService.updateUserIgnoreWordss(wordsId, request.words());
    return ResponseEntity.ok(updatedWords);
  }

  @DeleteMapping("/{wordsId}")
  public ResponseEntity<Void> deleteIgnoreWordss(@PathVariable Long wordsId) {
    log.info("Deleting word list: {}", wordsId);
    ignoreWordsService.deleteUserIgnoreWordss(wordsId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/user")
  public ResponseEntity<List<IgnoreWordsDto>> getIgnoreWordss() {
    try {
      List<IgnoreWordsDto> words = ignoreWordsService.getUserIgnoreWordss();
      return ResponseEntity.ok(words);
    } catch (Exception e) {
      log.error("Error fetching word lists", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  @GetMapping("/{wordsId}")
  public ResponseEntity<List<IgnoreWordsDto>> getIgnoreWordssById(@PathVariable Long wordsId) {
    try {
      List<IgnoreWordsDto> words = ignoreWordsService.getPublicIgnoreWordss(wordsId);
      return ResponseEntity.ok(words);
    } catch (Exception e) {
      log.error("Error fetching word list by ID", e);
      return ResponseEntity.internalServerError().build();
    }
  }
}
