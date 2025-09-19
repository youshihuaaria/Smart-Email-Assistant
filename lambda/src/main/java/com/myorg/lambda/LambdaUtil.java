package com.myorg.lambda;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LambdaUtil {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static String makeErrorMessageJson(String errorMessage) {
    try {
      return MAPPER.writeValueAsString(Map.of("error", errorMessage));
    } catch (Exception e) {
      return "{\"error\":\"Unknown error\"}";
    }
  }

  public static String makeBodyForSuccessfulRequest(String llmResponse) {
    try {
      return MAPPER.writeValueAsString(
          Map.of("response", llmResponse, "model", System.getenv().getOrDefault("MODEL_ID", "unknown")));
    } catch (Exception e) {
      return "{\"response\":\"" + llmResponse + "\"}";
    }
  }

  public static APIGatewayProxyResponseEvent makeApiGatewayProxyResponse(int statusCode, String body) {
    return new APIGatewayProxyResponseEvent()
        .withStatusCode(statusCode)
        .withHeaders(RequestUtil.CORS_HEADERS)
        .withBody(body);
  }
}
