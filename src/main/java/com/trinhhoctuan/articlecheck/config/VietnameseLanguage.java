package com.trinhhoctuan.articlecheck.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.languagetool.Language;
import org.languagetool.UserConfig;
import org.languagetool.language.Contributor;
import org.languagetool.rules.Rule;
import org.languagetool.rules.patterns.AbstractPatternRule;

/**
 * Custom Vietnamese language class for LanguageTool integration.
 */
public class VietnameseLanguage extends Language {
  private final String NAME = "Vietnamese";
  private final String SHORT_CODE = "vi";
  private final String[] COUNTRIES = { "VN" };

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String getShortCode() {
    return SHORT_CODE;
  }

  @Override
  public String[] getCountries() {
    return COUNTRIES;
  }

  @Override
  public Contributor[] getMaintainers() {
    return new Contributor[0];
  }

  @Override
  public List<Rule> getRelevantRules(ResourceBundle messages, UserConfig userConfig, Language motherTongue,
      List<Language> altLanguages) throws IOException {
    List<Rule> rules = new ArrayList<>();
    rules.add(new VietnameseHunspellRule(messages, this, userConfig));
    return rules;
  }

  @Override
  public List<AbstractPatternRule> getPatternRules() throws IOException {
    return new ArrayList<>();
  }
}
