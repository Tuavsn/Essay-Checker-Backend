package com.trinhhoctuan.articlecheck.services;

import java.util.List;

import com.trinhhoctuan.articlecheck.dtos.IgnoreWordsDto;

public interface IgnoreWordsService {
  public List<IgnoreWordsDto> getUserIgnoreWordss();

  public List<IgnoreWordsDto> getPublicIgnoreWordss(Long wordListId);
  
  public List<String> getIgnoreWordssById(Long wordListId);

  public IgnoreWordsDto createUserIgnoreWordss(String words);

  public IgnoreWordsDto updateUserIgnoreWordss(Long wordListId, String words);

  public void deleteUserIgnoreWordss(Long wordListId);

  public void setIsPublic(Long wordListId, boolean isPublic);
}
