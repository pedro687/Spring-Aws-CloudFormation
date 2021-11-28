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
// import software.amazon.awscdk.services.sqs.Queue;
// import software.amazon.awscdk.core.Duration;

public class Service02Stack extends Stack {
    public Service02Stack(final Construct scope, final String id, Cluster cluster) {
        this(scope, id, null, cluster);
    }

    public Service02Stack(final Construct scope, final String id, final StackProps props, Cluster cluster) {
        super(scope, id, props);

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
                                        .build()))
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
