package com.myorg.lambda;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;


public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>{

  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
    String name = "world";

    if (input != null && input.getQueryStringParameters() != null) {
      name = input.getQueryStringParameters().getOrDefault("name", name);
    }

    String body = "{\"Message\": \"Hello. " + name + "!\"}";

    return new APIGatewayProxyResponseEvent().withStatusCode(200).withHeaders(Map.of( "Content-Type", "application/json",
                        "Access-Control-Allow-Origin", "*"  )).withBody(body);
  }
}
