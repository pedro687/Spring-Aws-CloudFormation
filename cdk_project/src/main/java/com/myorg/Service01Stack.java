package com.myorg;

import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecs.AwsLogDriverProps;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.LogDriver;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.events.targets.SnsTopic;
import software.amazon.awscdk.services.logs.LogGroup;

import java.util.HashMap;
import java.util.Map;
// import software.amazon.awscdk.services.sqs.Queue;
// import software.amazon.awscdk.core.Duration;

public class Service01Stack extends Stack {

    // Specify the cluster
    public Service01Stack(final Construct scope, final String id, Cluster cluster, SnsTopic snsTopic) {
        this(scope, id, null, cluster, snsTopic);
    }

    public Service01Stack(final Construct scope, final String id, final StackProps props, Cluster cluster, SnsTopic snsTopic) {
        super(scope, id, props);


        /*
         *Adding Database Username, Endpoint and password in our enviroments
         *
         */
        Map<String, String> envVariables = new HashMap<>();
        envVariables.put("SPRING_DATASOURCE_URL", "jdbc:mariadb://"
                + Fn.importValue("rds-endpoint")
                + ":3306/aws_spring01?createDatabaseIfNotExist=true");
        envVariables.put("SPRING_DATASOURCE_USERNAME", "admin");
        envVariables.put("SPRING_DATASOURCE_PASSWORD", Fn.importValue("rds-password"));

        envVariables.put("AWS_REGION", "us-east-1");
        envVariables.put("AWS_SNS_TOPIC_PRODUCT_EVENTS_ARN", snsTopic.getTopic().getTopicArn());


        ApplicationLoadBalancedFargateService service01 = ApplicationLoadBalancedFargateService
                .Builder
                .create(this, "ALB-01")
                .serviceName("service-01")
                .cluster(cluster)
                .cpu(512) // how much cpu we need use
                .desiredCount(2) // how much instances we want
                .listenerPort(8080)
                .memoryLimitMiB(1024)
                .taskImageOptions(
                        //Container informations
                        ApplicationLoadBalancedTaskImageOptions.builder()
                                .containerName("aws_treinamento01")
                                .image(ContainerImage.fromRegistry("pedrospiet/aws_training:1.0.4")) //Your repository on dockerHub
                                .containerPort(8080)
                                .logDriver(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                        .logGroup(LogGroup.Builder.create(this, "Service01LogGroup")
                                                .logGroupName("Service01")
                                                .removalPolicy(RemovalPolicy.DESTROY)
                                                .build()
                                        ).streamPrefix("Service01")
                                        .build()))
                                .environment(envVariables) // adding enviroments
                                .build()
                ).publicLoadBalancer(true)
                .build();



        service01.getTargetGroup().configureHealthCheck(new HealthCheck.Builder()
                .path("/actuator/health")
                .port("8080")
                .healthyHttpCodes("200")
                .build());

     /*   ScalableTaskCount scalableTaskCount = service01.getService().autoScaleTaskCount(EnableScalingProps.builder()
                .minCapacity(2)
                .maxCapacity(4)
                .build());

        scalableTaskCount.scaleOnCpuUtilization("Service01AutoScaling", CpuUtilizationScalingProps.builder()
                .targetUtilizationPercent(50)
                .scaleInCooldown(Duration.seconds(60))
                .scaleOutCooldown(Duration.seconds(60))
                .build());
      */

        snsTopic.getTopic().grantPublish(service01.getTaskDefinition().getTaskRole());
    }


}