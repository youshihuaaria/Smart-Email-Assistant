package com.myorg.lambda;

import org.json.JSONArray;
import org.json.JSONObject;

import dev.langchain4j.model.input.PromptTemplate;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

public class BedrockClaude3Sonnet extends Bedrock {
  private static final int DEFAULT_MAX_TOKENS = 2048;
  private static final double DEFAULT_TEMPERATURE = 0.2;
  private static final int DEFAULT_TOP_K = 50;
  private static final String SYSTEM_PROMPT = """
      You are a chat bot that assists business users in replying to emails.
      Your task is to be friendly, concise and factually correct.
      When drafting the response make sure to adhere to the instructions given in the <instructions> tag.
      Write the mail using proper html syntax.
      Answer in the same language as the email in the <email> tag.
      If the email is on a first name basis answer on a first name basis and in the same tone.
      Put my name to the kind regards at the end of my email.
      Answer immediately and skip the preamble.
      """;

  public BedrockClaude3Sonnet() {
    super();
    promptTemplate = PromptTemplate.from(Claude3Prompts.SONNET);
  }

  public String generateEmailReply(String mail, String instructions, String promptOverride) {
    String prompt = (promptOverride != null && !promptOverride.isBlank()) ? promptOverride
        : generatePrompt(mail, instructions);

    String anthropicVersion = getAnthropicVersion();
    int maxTokens = Integer.parseInt(System.getenv().getOrDefault("MAX_TOKENS", String.valueOf(DEFAULT_MAX_TOKENS)));
    double temperature = Double
        .parseDouble(System.getenv().getOrDefault("TEMPERATURE", String.valueOf(DEFAULT_TEMPERATURE)));
    int topK = Integer.parseInt(System.getenv().getOrDefault("TOP_K", String.valueOf(DEFAULT_TOP_K)));
    String modelID = getModelId();

    var payload = new JSONObject().put("anthropic_version", anthropicVersion)
        .put("max_tokens", maxTokens)
        .put("top_k", topK)
        .put("temperature", temperature)
        .put("system", SYSTEM_PROMPT)
        .put("messages", new JSONArray()
            .put(new JSONObject()
                .put("role", "user")
                .put("content", new JSONArray()
                    .put(new JSONObject()
                        .put("type", "text")
                        .put("text", prompt)))));

    InvokeModelRequest request = InvokeModelRequest.builder()
          .modelId(modelID)
          .contentType("application/json")
          .accept("application/json")
          .body(SdkBytes.fromUtf8String(payload.toString()))
          .build();
    
    InvokeModelResponse response = client.invokeModel(request);
    String responseString = response.body().asUtf8String();

    JSONObject responseBody = new JSONObject(responseString);
    
    if (responseBody.has("content")) {
      try {
          return responseBody.getJSONArray("content")
          .getJSONObject(0)
          .getString("text");
      } catch (Exception e) {
      }
    }

    if (responseBody.has("generated_text")) {
      return responseBody.getString("generated_text");
    }

    return responseString;
  }

}
