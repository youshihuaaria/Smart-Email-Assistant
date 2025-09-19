package com.myorg;

import java.util.List;
import java.util.Map;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.Cors;
import software.amazon.awscdk.services.apigateway.CorsOptions;
import software.amazon.awscdk.services.apigateway.LambdaIntegration;
import software.amazon.awscdk.services.apigateway.Resource;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

public class SmartEmailStack extends Stack {
  public SmartEmailStack(final Construct scope, final String id, StackProps props) {
    super(scope, id, props);

    Function generateEmailFunction = Function.Builder.create(this, "GenerateEmailFunction")
        .runtime(Runtime.JAVA_17)
        .handler("com.myorg.lambda.LambdaHandler")
        .memorySize(1024)
        .timeout(Duration.seconds(30)) 
        .code(Code.fromAsset("../lambda/target/lambda-1.0-SNAPSHOT.jar"))
        .environment(Map.of(
            "MODEL_ID", "anthropic.claude-3-sonnet-20240229-v1:0",
            "ANTHROPIC_VERSION", "bedrock-2023-05-31",
            "MAX_TOKENS", "1024",
            "TEMPERATURE", "0.2",
            "TOP_K", "50"))
        .build();

    generateEmailFunction.getRole().addToPrincipalPolicy(PolicyStatement.Builder.create()
        .effect(Effect.ALLOW)
        .actions(List.of("bedrock:InvokeModel"))
        .resources(List.of("*"))
        .build());

    CorsOptions corsOptions = CorsOptions.builder().allowOrigins(Cors.ALL_ORIGINS)
        .allowMethods(Cors.ALL_METHODS)
        .build();

    RestApi api = RestApi.Builder.create(this, "EmailApi")
        .restApiName("EmailApi")
        .description("Generate Email API")
        .defaultCorsPreflightOptions(corsOptions)
        .build();

    LambdaIntegration generateEmailIntegration = new LambdaIntegration(generateEmailFunction);

    Resource email = api.getRoot().addResource("email");
    email.addMethod("POST", generateEmailIntegration);

    CfnOutput.Builder.create(this, "ApiURL").description("Base URL for the REST API").value(api.getUrl()).build();
  }
}
