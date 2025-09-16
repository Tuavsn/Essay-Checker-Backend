package com.trinhhoctuan.articlecheck.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileProcessingService {
  public String extractTextFromFile(MultipartFile file) throws IOException;

  public boolean isSupportedFileType(String fileName);
}
