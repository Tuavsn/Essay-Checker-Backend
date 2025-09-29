package com.trinhhoctuan.articlecheck.config;

import java.io.IOException;
import java.util.ResourceBundle;

import org.languagetool.Language;
import org.languagetool.UserConfig;
import org.languagetool.rules.spelling.hunspell.HunspellRule;

public class VietnameseHunspellRule extends HunspellRule {
  private final String RULE_ID = "VIETNAMESE_HUNSPELL_RULE";
  private final String RULE_DESCRIPTION = "Vietnamese spell checking using Hunspell";
  private final String DICT_RESOURCE_PATH = "src/main/resources/dictionaries/";

  public VietnameseHunspellRule(ResourceBundle messages, Language language, UserConfig userConfig) throws IOException {
    super(messages, language, userConfig, null);
  }

  @Override
  public String getId() {
    return RULE_ID;
  }

  @Override
  public String getDescription() {
    return RULE_DESCRIPTION;
  }

  @Override
  public String getDictFilenameInResources(String langCountry) {
    return DICT_RESOURCE_PATH + langCountry;
  }
}
