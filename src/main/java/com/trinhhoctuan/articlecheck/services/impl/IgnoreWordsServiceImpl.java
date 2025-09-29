package com.trinhhoctuan.articlecheck.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.trinhhoctuan.articlecheck.dtos.IgnoreWordsDto;
import com.trinhhoctuan.articlecheck.mappers.IgnoreWordsMapper;
import com.trinhhoctuan.articlecheck.models.User;
import com.trinhhoctuan.articlecheck.models.IgnoreWords;
import com.trinhhoctuan.articlecheck.repositories.UserRepository;
import com.trinhhoctuan.articlecheck.repositories.IgnoreWordsRepository;
import com.trinhhoctuan.articlecheck.services.IgnoreWordsService;
import com.trinhhoctuan.articlecheck.utils.SecurityUtil;

/**
 * Implementation of the IgnoreWordsService interface.
 * This class provides methods to manage ignore word lists for users.
 */
@Service
public class IgnoreWordsServiceImpl implements IgnoreWordsService {
  private final IgnoreWordsRepository wordListRepository;
  private final UserRepository userRepository;
  private final SecurityUtil securityUtil;
  private final IgnoreWordsMapper wordListMapper;

  public IgnoreWordsServiceImpl(
      IgnoreWordsRepository wordListRepository,
      UserRepository userRepository,
      SecurityUtil securityUtil,
      IgnoreWordsMapper wordListMapper) {
    this.wordListRepository = wordListRepository;
    this.userRepository = userRepository;
    this.securityUtil = securityUtil;
    this.wordListMapper = wordListMapper;
  }

  /**
   * Get all word lists of the current user.
   * 
   * @return A list of IgnoreWordsDto.
   */
  @Override
  public List<IgnoreWordsDto> getUserIgnoreWordss() {
    Long currentUserId = securityUtil.getCurrentUserId();
    return wordListRepository.findByUserIdOrderByCreatedAtDesc(currentUserId)
        .stream()
        .map(wordListMapper::convertToDto)
        .toList();
  }

  /**
   * Get all public word lists of the current user.
   * 
   * @param wordListId The ID of the word list (not used in this implementation).
   * @return A list of public IgnoreWordsDto.
   */
  @Override
  public List<IgnoreWordsDto> getPublicIgnoreWordss(Long wordListId) {
    Long currentUserId = securityUtil.getCurrentUserId();
    return wordListRepository.findByUserIdAndIsPublicOrderByCreatedAtDesc(currentUserId, true)
        .stream()
        .map(wordListMapper::convertToDto)
        .toList();
  }

  /**
   * Get the list of words from a word list by its ID.
   * 
   * @param wordListId The ID of the word list.
   * @return A list of words in the word list.
   */
  @Override
  public List<String> getIgnoreWordssById(Long wordListId) {
    IgnoreWords words = wordListRepository.findById(wordListId)
        .orElseThrow(() -> new IllegalArgumentException("Word list not found"));

    return List.of(words.getWords().split(","));
  }

  /**
   * Create a new word list for the current user.
   * 
   * @param words The words to include in the new word list.
   * @return The created IgnoreWordsDto.
   */
  @Override
  public IgnoreWordsDto createUserIgnoreWordss(String words) {
    Long currentUserId = securityUtil.getCurrentUserId();

    User user = userRepository.findById(currentUserId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    IgnoreWords savedWords = wordListRepository.save(IgnoreWords.builder().user(user).words(words).build());

    return wordListMapper.convertToDto(savedWords);
  }

  /**
   * Update a word list by its ID.
   * 
   * @param wordListId The ID of the word list to update.
   * @param words      The new words for the word list.
   * @return The updated IgnoreWordsDto.
   */
  @Override
  public IgnoreWordsDto updateUserIgnoreWordss(Long wordListId, String words) {
    Long currentUserId = securityUtil.getCurrentUserId();

    IgnoreWords existingWords = wordListRepository.findByIdAndUserId(wordListId, currentUserId)
        .orElseThrow(() -> new IllegalArgumentException("Word list not found"));

    existingWords.setWords(words);

    IgnoreWords savedWords = wordListRepository.save(existingWords);

    return wordListMapper.convertToDto(savedWords);
  }

  /**
   * Delete a word list by its ID.
   * 
   * @param wordListId The ID of the word list to delete.
   */
  @Override
  public void deleteUserIgnoreWordss(Long wordListId) {
    Long currentUserId = securityUtil.getCurrentUserId();

    IgnoreWords existingWords = wordListRepository.findByIdAndUserId(wordListId, currentUserId)
        .orElseThrow(() -> new IllegalArgumentException("Word list not found"));

    wordListRepository.delete(existingWords);
  }

  /**
   * Set the public status of a word list.
   * 
   * @param wordListId The ID of the word list.
   * @param isPublic   The new public status.
   */
  @Override
  public void setIsPublic(Long wordListId, boolean isPublic) {
    Long currentUserId = securityUtil.getCurrentUserId();

    IgnoreWords existingWords = wordListRepository.findById(wordListId)
        .orElseThrow(() -> new IllegalArgumentException("Word list not found"));

    if (!existingWords.getUser().getId().equals(currentUserId)) {
      throw new SecurityException("You do not have permission to update this word list");
    }

    existingWords.setPublic(isPublic);

    wordListRepository.save(existingWords);
  }
}
