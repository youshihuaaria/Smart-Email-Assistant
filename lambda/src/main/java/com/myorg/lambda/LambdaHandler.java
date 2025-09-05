package com.myorg.lambda;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  private static final String MODEL_ID = "anthropic.claude-3-sonnet-20240229-v1:0";
  private final BedrockRuntimeClient bedRockClient;

  public LambdaHandler() {
    this.bedRockClient = BedrockRuntimeClient.builder()
        .region(Region.US_WEST_2)
        .build();
  }

  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
    String userPrompt = "Write a welcome email for a new user";

    if (input != null && input.getQueryStringParameters() != null) {
      userPrompt = input.getQueryStringParameters().getOrDefault("prompt", userPrompt);
    }

    try {
      String requestBody = "{\n" +
          "  \"anthropic_version\": \"bedrock-2023-05-31\",\n" +
          "  \"max_tokens\": 300,\n" +
          "  \"messages\": [\n" +
          "    {\"role\": \"user\", \"content\": \"" + userPrompt + "\"}\n" +
          "  ]\n" +
          "}";

      InvokeModelRequest request = InvokeModelRequest.builder()
          .modelId(MODEL_ID)
          .contentType("application/json")
          .accept("application/json")
          .body(SdkBytes.fromString(requestBody, StandardCharsets.UTF_8))
          .build();

      InvokeModelResponse response = bedRockClient.invokeModel(request);

      String responseBody = response.body().asUtf8String();

      return new APIGatewayProxyResponseEvent()
          .withStatusCode(200)
          .withHeaders(Map.of(
              "Content-Type", "application/json",
              "Access-Control-Allow-Origin", "*"))
          .withBody(responseBody);
    } catch (Exception e) {
      return new APIGatewayProxyResponseEvent().withStatusCode(500)
          .withHeaders(Map.of("Content-Type", "application/json")).withBody("{\"error\": \"" + e.getMessage() + "\"}");
    }
  }
}
