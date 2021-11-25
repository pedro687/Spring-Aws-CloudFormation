package com.myorg;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.events.targets.SnsTopic;
import software.amazon.awscdk.services.sns.Topic;
import software.amazon.awscdk.services.sns.subscriptions.EmailSubscription;
// import software.amazon.awscdk.services.sqs.Queue;
// import software.amazon.awscdk.core.Duration;

public class SnsStack extends Stack {
    private final SnsTopic snsTopic;

    public SnsTopic getSnsTopic() {
        return snsTopic;
    }

    public SnsStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public SnsStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        snsTopic = SnsTopic.Builder.create(Topic.Builder.create(this, "ProductsEventsTopic")
                .topicName("product-events").build()).build();

        snsTopic.getTopic().addSubscription(EmailSubscription.Builder.create("pedrospiet@gmail.com").json(true).build());
    }
}
