package com.myorg.lambda;

import java.util.Map;

import dev.langchain4j.model.input.PromptTemplate;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

public abstract class Bedrock {
  protected final BedrockRuntimeClient client;
  protected PromptTemplate promptTemplate;

  public Bedrock() {
    client = BedrockRuntimeClient.builder()
        .region(Region.of(System.getenv().getOrDefault("AWS_REGION", "us-west-2")))
        .build();
  }

  public abstract String generateEmailReply(String mail, String instructions, String promptOverride);

  protected String generatePrompt(String mail, String instructions) {
    Map<String, Object> variables = Map.of("mail", mail, "instructions", instructions);
    return promptTemplate.apply(variables).text();

  }

  protected String getModelId() {
    return System.getenv().getOrDefault("MODEL_ID", "anthropic.claude-3-sonnet-20240229-v1:0");
  }

  protected String getAnthropicVersion() {
    return System.getenv().getOrDefault("ANTHROPIC_VERSION", "bedrock-2023-05-31");
  }
}
