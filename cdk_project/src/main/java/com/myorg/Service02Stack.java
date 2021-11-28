package com.myorg;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.RemovalPolicy;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ecs.AwsLogDriverProps;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.LogDriver;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.events.targets.SnsTopic;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.sns.subscriptions.SqsSubscription;
import software.amazon.awscdk.services.sqs.DeadLetterQueue;
import software.amazon.awscdk.services.sqs.Queue;

import java.util.HashMap;
import java.util.Map;
// import software.amazon.awscdk.services.sqs.Queue;
// import software.amazon.awscdk.core.Duration;

public class Service02Stack extends Stack {
    public Service02Stack(final Construct scope, final String id, Cluster cluster, SnsTopic productEventsTopic) {
        this(scope, id, null, cluster, productEventsTopic);
    }

    public Service02Stack(final Construct scope, final String id, final StackProps props, Cluster cluster, SnsTopic productEventsTopic) {
        super(scope, id, props);

        //Criando fila da DLQ
     Queue productEventsDlq = Queue.Builder.create(this, "ProductEventsDlq")
            .queueName("product-events-dlq")
            .build();

     //Criando DLQ e adicionando nela a fila de dlq
        DeadLetterQueue deadLetterQueue = DeadLetterQueue.builder()
                .queue(productEventsDlq)
                .maxReceiveCount(2)
                .build();


        Queue productEvents = Queue.Builder.create(this, "ProductEvents")
                .queueName("product-events")
                .deadLetterQueue(deadLetterQueue) // Definindo DLQ para quando uma mensagem der erro 3 vezes, ela será redirecionada para a DLQ
                .build();

        Map<String, String> envVariables = new HashMap<>();

        //inscrevendo fila no SNS
        SqsSubscription sqsSubscription = SqsSubscription.Builder.create(productEvents).build();
        productEventsTopic.getTopic().addSubscription(sqsSubscription);

        envVariables.put("AWS_REGION", "us-east-1");
        envVariables.put("AWS_QUEUE_PRODUCT_EVENTS_NAME", productEvents.getQueueName());

        ApplicationLoadBalancedFargateService service02 = ApplicationLoadBalancedFargateService
                .Builder
                .create(this, "ALB-02")
                .serviceName("service-02")
                .cluster(cluster)
                .cpu(512) // how much cpu we need use
                .desiredCount(1) // how much instances we want
                .listenerPort(9090)
                .memoryLimitMiB(1024)
                .taskImageOptions(
                        //Container informations
                        ApplicationLoadBalancedTaskImageOptions.builder()
                                .containerName("aws_treinamento01")
                                .image(ContainerImage.fromRegistry("pedrospiet/aws_training02:1.0.1")) //Your repository on dockerHub
                                .containerPort(9090)
                                .logDriver(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                        .logGroup(LogGroup.Builder.create(this, "Service02LogGroup")
                                                .logGroupName("Service02")
                                                .removalPolicy(RemovalPolicy.DESTROY)
                                                .build()
                                        ).streamPrefix("Service02")
                                        .build())).environment(envVariables)
                                .build()
                ).publicLoadBalancer(true)
                .build();


        service02.getTargetGroup().configureHealthCheck(new HealthCheck.Builder()
                .path("/actuator/health")
                .port("8080")
                .healthyHttpCodes("200")
                .build());
    }
}
