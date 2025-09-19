package com.myorg.lambda;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class RequestUtil {
  public static final Map<String, String> CORS_HEADERS = Map.of("Content-Type", "application/json",
      "Access-Control-Allow-Origin", "*", "Access-Control-Allow-Methods", "OPTIONS, GET, POST");

  private final APIGatewayProxyRequestEvent event;
  private final String instructions;
  private final String mail;
  private final String prompt;
  private final String mode;

  private RequestUtil(APIGatewayProxyRequestEvent proxyEvent) {
    event = proxyEvent;

    String bodyStr = proxyEvent.getBody();
    if (bodyStr == null || bodyStr.isBlank()) {
      throw new RuntimeException("Empty Body");
    }

    JsonObject body = new Gson().fromJson(bodyStr, JsonObject.class);

    if (body.has("prompt") && body.get("prompt").isJsonPrimitive()) {
      prompt = body.get("prompt").getAsString();
      mail = null;
      instructions = null;
      mode = "prompt";
    } else {
      prompt = null;

      if (!body.has("mail") || !body.has("instructions")) {
        throw new RuntimeException("Malformed body: expected 'mail' and 'instructions' or 'prompt'");
      }

      if (!body.get("mail").isJsonPrimitive() || !body.get("instructions").isJsonPrimitive()) {
        throw new RuntimeException("Malformed body: mail/instructions must be string");
      }

      mail = body.get("mail").getAsString();
      instructions = body.get("instruction").getAsString();
      mode = "reply";
    }
  }

  public static RequestUtil fromEvent(APIGatewayProxyRequestEvent proxyEvent) {
    return new RequestUtil(proxyEvent);
  }

  public String getInstructions() {
    return instructions;
  }

  public String getMail() {
    return mail;
  }

  public String getPrompt() {
    return prompt;
  }

  public String getMode() {
    return mode;
  }
}
