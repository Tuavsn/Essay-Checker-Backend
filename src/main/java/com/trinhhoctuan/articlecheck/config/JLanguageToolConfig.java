package com.trinhhoctuan.articlecheck.config;

import org.languagetool.JLanguageTool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JLanguageToolConfig {

  @Bean
  public JLanguageTool jLanguageTool() {
    System.setProperty("jdk.xml.totalEntitySizeLimit", "0");
    return new JLanguageTool(new VietnameseLanguage());
  }
}
