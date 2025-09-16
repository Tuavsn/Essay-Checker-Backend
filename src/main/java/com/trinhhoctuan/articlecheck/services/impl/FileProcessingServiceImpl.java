package com.trinhhoctuan.articlecheck.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.trinhhoctuan.articlecheck.services.FileProcessingService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileProcessingServiceImpl implements FileProcessingService {
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

    String extension = getFileExtension(fileName);

    switch (extension) {
      case "txt":
        return extractTextFromTxt(inputStream);
      case "pdf":
        return extractTextFromPdf(inputStream);
      case "doc":
        return extractTextFromDoc(inputStream);
      case "docx":
        return extractTextFromDocx(inputStream);
      default:
        throw new UnsupportedOperationException("Unsupported file type: " + extension);
    }
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
    String extension = getFileExtension(fileName).toLowerCase();
    return extension.equals("txt") ||
        extension.equals("pdf") ||
        extension.equals("doc") ||
        extension.equals("docx");
  }

  // ========================== Utils operations ========================
  /**
   * Get the file extension from the file name.
   * 
   * @param fileName The name of the file.
   * @return The file extension, or an empty string if not found.
   */
  private String getFileExtension(String fileName) {
    int lastDotIndex = fileName.lastIndexOf('.');
    if (lastDotIndex == -1)
      return "";
    return fileName.substring(lastDotIndex + 1);
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
