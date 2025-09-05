package com.myorg;

import java.util.List;

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

    Function helloFunction = Function.Builder.create(this, "HelloFunction")
        .runtime(Runtime.JAVA_17)
        .handler("com.myorg.lambda.LambdaHandler")
        .memorySize(512)
        .timeout(Duration.seconds(30))
        .code(Code.fromAsset("../lambda/target/lambda-1.0-SNAPSHOT.jar"))
        .build();

    helloFunction.getRole().addToPrincipalPolicy(PolicyStatement.Builder.create()
        .effect(Effect.ALLOW)
        .actions(List.of("bedrock:InvokeModel"))
        .resources(List.of("*"))
        .build());

    CorsOptions corsOptions = CorsOptions.builder().allowOrigins(Cors.ALL_ORIGINS)
        .allowMethods(Cors.ALL_METHODS)
        .build();

    RestApi api = RestApi.Builder.create(this, "HelloApi")
        .restApiName("HelloApi")
        .description("Test: minimal API backed by AWS Lambda")
        .defaultCorsPreflightOptions(corsOptions)
        .build();

    LambdaIntegration hellIntegration = new LambdaIntegration(helloFunction);

    api.getRoot().addMethod("GET", hellIntegration);

    Resource hello = api.getRoot().addResource("hello");
    hello.addMethod("GET", hellIntegration);

    CfnOutput.Builder.create(this, "ApiURL").description("Base URL for the REST API").value(api.getUrl()).build();
  }
}
