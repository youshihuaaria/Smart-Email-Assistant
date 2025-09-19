package com.myorg.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
  private final Bedrock bedRock;

  public LambdaHandler() {
    this.bedRock = new BedrockClaude3Sonnet();
  }

  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
    try {
      var requestUtil = RequestUtil.fromEvent(event);
      String llmResponse;

      if ("prompt".equals(requestUtil.getMode())) {
          String prompt = requestUtil.getPrompt();
          llmResponse = bedRock.generateEmailReply(null, null, prompt);
      } else {
          var mail = requestUtil.getMail();
          var instructions = requestUtil.getInstructions();
          llmResponse = bedRock.generateEmailReply(mail, instructions, null);
      }

      String body = LambdaUtil.makeBodyForSuccessfulRequest(llmResponse);
      return LambdaUtil.makeApiGatewayProxyResponse(200, body);

    } catch (Exception e) {
      return LambdaUtil.makeApiGatewayProxyResponse(500, LambdaUtil.makeErrorMessageJson(e.getMessage()));
    }
  }
}
