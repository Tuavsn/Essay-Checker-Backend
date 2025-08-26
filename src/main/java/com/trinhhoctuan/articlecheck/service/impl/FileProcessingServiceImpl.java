package com.trinhhoctuan.articlecheck.service.impl;

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

import com.trinhhoctuan.articlecheck.service.FileProcessingService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileProcessingServiceImpl implements FileProcessingService {
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
  private String getFileExtension(String fileName) {
    int lastDotIndex = fileName.lastIndexOf('.');
    if (lastDotIndex == -1)
      return "";
    return fileName.substring(lastDotIndex + 1);
  }

  private String extractTextFromTxt(InputStream inputStream) throws IOException {
    return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
  }

  private String extractTextFromPdf(InputStream inputStream) throws IOException {
    try (PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {
      PDFTextStripper pdfStripper = new PDFTextStripper();
      return pdfStripper.getText(document);
    }
  }

  private String extractTextFromDoc(InputStream inputStream) throws IOException {
    try (HWPFDocument document = new HWPFDocument(inputStream);
        WordExtractor extractor = new WordExtractor(document)) {
      return extractor.getText();
    }
  }

  private String extractTextFromDocx(InputStream inputStream) throws IOException {
    try (XWPFDocument document = new XWPFDocument(inputStream);
        XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
      return extractor.getText();
    }
  }
}
