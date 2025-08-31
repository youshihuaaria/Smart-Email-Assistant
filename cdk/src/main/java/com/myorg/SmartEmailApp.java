package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class SmartEmailApp {

    static String ACCOUNT_ID = "787798618349";
    static String Region = "us-west-2";
    public static void main(final String[] args) {
        App app = new App();
        
        Environment env = Environment.builder().account(ACCOUNT_ID).region(Region).build();

        new SmartEmailStack(app, "SmartEmailStack", StackProps.builder().env(env).build());
        
        app.synth();
    }
}

