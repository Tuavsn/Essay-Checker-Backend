package com.trinhhoctuan.articlecheck.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileType {
  TXT("txt"),
  PDF("pdf"),
  DOC("doc"),
  DOCX("docx");

  private final String name;

  public static FileType fromString(String text) {
    for (FileType b : FileType.values()) {
      if (b.name.equalsIgnoreCase(text)) {
        return b;
      }
    }
    throw new IllegalArgumentException("No constant with text " + text + " found");
  }
}
