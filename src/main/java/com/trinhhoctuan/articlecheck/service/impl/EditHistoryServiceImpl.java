package com.trinhhoctuan.articlecheck.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.trinhhoctuan.articlecheck.dto.EditHistoryDto;
import com.trinhhoctuan.articlecheck.model.EditHistory;
import com.trinhhoctuan.articlecheck.model.EditHistory.ChangeType;
import com.trinhhoctuan.articlecheck.repository.EditHistoryRepository;
import com.trinhhoctuan.articlecheck.model.Essay;
import com.trinhhoctuan.articlecheck.service.EditHistoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EditHistoryServiceImpl implements EditHistoryService {
  private final EditHistoryRepository editHistoryRepository;

  /**
   * Records an edit made to an essay.
   * @param essay The essay that was edited.
   * @return The recorded edit history.
   */
  @Override
  public void recordEdit(Essay essay, String previousContent, String newContent, String description,
      ChangeType changeType) {
    EditHistory editHistory = EditHistory.builder()
        .essay(essay)
        .previousContent(previousContent)
        .newContent(newContent)
        .changeDescription(description)
        .changeType(changeType)
        .build();
    editHistoryRepository.save(editHistory);

  }

  /**
   * Retrieves the edit history for a specific essay.
   * @param essayId The ID of the essay.
   * @return A list of edit history records for the essay.
   */
  @Override
  public List<EditHistoryDto> getEditHistory(Long essayId) {
    return editHistoryRepository.findByEssayIdOrderByCreatedAtDesc(essayId)
        .stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());

  }

  /**
   * Converts an EditHistory entity to an EditHistoryDto.
   * @param editHistory The EditHistory entity to convert.
   * @return The converted EditHistoryDto.
   */
  private EditHistoryDto convertToDto(EditHistory editHistory) {
    return EditHistoryDto.builder()
        .id(editHistory.getId())
        .previousContent(editHistory.getPreviousContent())
        .newContent(editHistory.getNewContent())
        .changeDescription(editHistory.getChangeDescription())
        .changeType(editHistory.getChangeType())
        .createdAt(editHistory.getCreatedAt())
        .build();
  }
}
