package com.trinhhoctuan.articlecheck.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.trinhhoctuan.articlecheck.enums.FileType;

/**
 * Service interface for file processing functionalities.
 * Provides methods to extract text from files and check supported file types.
 * Utilizes libraries for handling various file formats (e.g., PDF, DOCX, TXT).
 */
public interface FileProcessingService {
  /**
   * Extract text content from the given file.
   * 
   * @param file
   * @return
   * @throws IOException
   */
  public String extractTextFromFile(MultipartFile file) throws IOException;

  /**
   * Check if the file type is supported based on its extension.
   * 
   * @param fileName
   * @return
   */
  public boolean isSupportedFileType(String fileName);

  /**
   * Save the uploaded file to the system and return the saved file name.
   * 
   * @param file
   * @return
   * @throws IOException
   */
  public String saveFileToSystem(MultipartFile file) throws IOException;

  /**
   * Get the file extension as a FileType enum from the given file name.
   * 
   * @param fileName
   * @return
   */
  public FileType getFileExtension(String fileName);
}
