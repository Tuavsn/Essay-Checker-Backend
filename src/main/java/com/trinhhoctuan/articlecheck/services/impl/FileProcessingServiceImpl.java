package com.trinhhoctuan.articlecheck.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.trinhhoctuan.articlecheck.constants.CommonConstants;
import com.trinhhoctuan.articlecheck.enums.FileType;
import com.trinhhoctuan.articlecheck.services.FileProcessingService;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the FileProcessingService interface.
 * This class provides methods to process and extract text from various file
 * types.
 */
@Service
@Slf4j
public class FileProcessingServiceImpl implements FileProcessingService {
  private String uploadDir;

  public FileProcessingServiceImpl(
      @Value("${file.upload.dir}") String uploadDir) {
    this.uploadDir = uploadDir;
  }

  // ========================== Core operations ========================

  /**
   * Extract text content from the given file.
   * 
   * @param file The file to extract text from.
   * @return The extracted text content.
   */
  @Override
  public String extractTextFromFile(MultipartFile file) throws IOException {
    String fileName = file.getOriginalFilename();
    String contentType = file.getContentType();
    InputStream inputStream = file.getInputStream();

    log.info("Processing file: {} with content type: {}", fileName, contentType);

    if (fileName == null) {
      throw new IllegalArgumentException("File name can't be null");
    }

    FileType extension = getFileExtension(fileName);

    return switch (extension) {
      case TXT -> extractTextFromTxt(inputStream);
      case PDF -> extractTextFromPdf(inputStream);
      case DOC -> extractTextFromDoc(inputStream);
      case DOCX -> extractTextFromDocx(inputStream);
      default -> throw new UnsupportedOperationException("Unsupported file type: " + extension);
    };
  }

  /**
   * Check if the given file type is supported.
   * 
   * @param fileName The name of the file to check.
   * @return true if the file type is supported, false otherwise.
   */
  @Override
  public boolean isSupportedFileType(String fileName) {
    if (fileName == null)
      return false;
    FileType extension = getFileExtension(fileName);
    return extension == FileType.TXT ||
        extension == FileType.PDF ||
        extension == FileType.DOC ||
        extension == FileType.DOCX;
  }

  // ========================== Utils operations ========================

  /**
   * Save a file to the system.
   *
   * @param file the file to save
   * @return the filename of the saved file
   * @throws IOException if an error occurs while saving the file
   */
  public String saveFileToSystem(MultipartFile file) throws IOException {
    // Create upload directory if it doesn't exist
    Path uploadPath = Paths.get(uploadDir);

    if (!Files.exists(uploadPath)) {
      Files.createDirectories(uploadPath);
    }

    // Generate unique filename
    String originalFileName = file.getOriginalFilename();
    String fileName = System.currentTimeMillis() + "_" + originalFileName;
    Path filePath = uploadPath.resolve(fileName);

    // Save file
    Files.copy(file.getInputStream(), filePath);

    log.info("Saved file: {}", filePath.toAbsolutePath());

    return fileName;
  }

  /**
   * Get the file extension from the file name.
   * 
   * @param fileName The name of the file.
   * @return The file extension, or an empty string if not found.
   */
  public FileType getFileExtension(String fileName) {
    int lastDotIndex = fileName.lastIndexOf(CommonConstants.DOT);
    if (lastDotIndex == -1)
      return null;
    String extension = fileName.substring(lastDotIndex + 1).toLowerCase();
    return FileType.fromString(extension);
  }

  /**
   * Extract text content from a TXT file.
   * 
   * @param inputStream The input stream of the TXT file.
   * @return The extracted text content.
   * @throws IOException If an I/O error occurs.
   */
  private String extractTextFromTxt(InputStream inputStream) throws IOException {
    return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
  }

  /**
   * Extract text content from a DOC file.
   * 
   * @param inputStream The input stream of the DOC file.
   * @return The extracted text content.
   * @throws IOException If an I/O error occurs.
   */
  private String extractTextFromPdf(InputStream inputStream) throws IOException {
    try (PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {
      PDFTextStripper pdfStripper = new PDFTextStripper();
      return pdfStripper.getText(document);
    }
  }

  /**
   * Extract text content from a PDF file.
   * 
   * @param inputStream The input stream of the PDF file.
   * @return The extracted text content.
   * @throws IOException If an I/O error occurs.
   */
  private String extractTextFromDoc(InputStream inputStream) throws IOException {
    try (HWPFDocument document = new HWPFDocument(inputStream);
        WordExtractor extractor = new WordExtractor(document)) {
      return extractor.getText();
    }
  }

  /**
   * Extract text content from a DOCX file.
   * 
   * @param inputStream The input stream of the DOCX file.
   * @return The extracted text content.
   * @throws IOException If an I/O error occurs.
   */
  private String extractTextFromDocx(InputStream inputStream) throws IOException {
    try (XWPFDocument document = new XWPFDocument(inputStream);
        XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
      return extractor.getText();
    }
  }
}
