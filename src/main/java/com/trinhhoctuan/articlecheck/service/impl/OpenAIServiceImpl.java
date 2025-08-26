package com.trinhhoctuan.articlecheck.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.trinhhoctuan.articlecheck.dto.OpenAIRequest;
import com.trinhhoctuan.articlecheck.dto.OpenAIResponse;
import com.trinhhoctuan.articlecheck.service.OpenAIService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIServiceImpl implements OpenAIService {
  @Value("${openai.api.key}")
  private String apiKey;

  @Value("${openai.api.url}")
  private String apiUrl;

  private final WebClient webClient = WebClient.builder().build();

  /**
   * Generate suggestions for improving the given text based on the specified
   * context.
   * 
   * @param text the text to be improved
   * @return a string containing the suggested improvements
   */
  @Override
  public String generateSuggestions(String text, String context) {
    try {
      OpenAIRequest request = OpenAIRequest.builder()
          .model("gpt-3.5-turbo")
          .messages(List.of(
              OpenAIRequest.Message.builder()
                  .role("system")
                  .content(
                      "You are a helpful writing assistant that provides suggestions to improve academic essays. Focus on clarity, structure, and academic style.")
                  .build(),
              OpenAIRequest.Message.builder()
                  .role("user")
                  .content(
                      String.format("Context: %s\n\nPlease review and suggest improvements for this essay text:\n%s",
                          context != null ? context : "General essay improvement", text))
                  .build()))
          .maxTokens(1000)
          .temperature(0.3)
          .build();

      OpenAIResponse response = webClient.post()
          .uri(apiUrl)
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(request)
          .retrieve()
          .bodyToMono(OpenAIResponse.class)
          .block();

      if (response != null && !response.getChoices().isEmpty()) {
        return response.getChoices().get(0).getMessage().getContent();
      }

      return "No suggestions available";
    } catch (Exception e) {
      log.error("Error calling OpenAI API", e);
      return "Error generating suggestions: " + e.getMessage();
    }
  }

  /**
   * Improve the given text based on specific issues.
   * 
   * @param originalText the original text to be improved
   * @return a string containing the improved text
   */
  @Override
  public String improveText(String originalText, List<String> specificIssues) {
    try {
      String issuesText = specificIssues != null && !specificIssues.isEmpty()
          ? "Please focus on these specific issues: " + String.join(", ", specificIssues)
          : "Please improve the overall quality, grammar, and style";

      OpenAIRequest request = OpenAIRequest.builder()
          .model("gpt-3.5-turbo")
          .messages(List.of(
              OpenAIRequest.Message.builder()
                  .role("system")
                  .content(
                      "You are an expert essay editor. Improve the given text while maintaining the original meaning and author's voice.")
                  .build(),
              OpenAIRequest.Message.builder()
                  .role("user")
                  .content(String.format("%s\n\nOriginal text:\n%s", issuesText, originalText))
                  .build()))
          .maxTokens(2000)
          .temperature(0.2)
          .build();

      OpenAIResponse response = webClient.post()
          .uri(apiUrl)
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(request)
          .retrieve()
          .bodyToMono(OpenAIResponse.class)
          .block();

      if (response != null && !response.getChoices().isEmpty()) {
        return response.getChoices().get(0).getMessage().getContent();
      }

      return originalText;
    } catch (Exception e) {
      log.error("Error improving text with OpenAI", e);
      return originalText;
    }
  }
}
