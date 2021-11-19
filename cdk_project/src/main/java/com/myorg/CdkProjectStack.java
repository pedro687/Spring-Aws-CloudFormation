package com.myorg;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
// import software.amazon.awscdk.services.sqs.Queue;
// import software.amazon.awscdk.core.Duration;

public class CdkProjectStack extends Stack {
    public CdkProjectStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public CdkProjectStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // The code that defines your stack goes here

        // example resource
        // final Queue queue = Queue.Builder.create(this, "CdkProjectQueue")
        //         .visibilityTimeout(Duration.seconds(300))
        //         .build();
    }
}
