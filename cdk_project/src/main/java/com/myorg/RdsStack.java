package com.myorg;

import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.rds.*;

import java.util.Collections;
// import software.amazon.awscdk.services.sqs.Queue;
// import software.amazon.awscdk.core.Duration;

public class RdsStack extends Stack {
    public RdsStack(final Construct scope, final String id, Vpc vpc) {
        this(scope, id, null, vpc);
    }

    public RdsStack(final Construct scope, final String id, final StackProps props, Vpc vpc) {
        super(scope, id, props);

        //Cloud Formation Parameter, we're going use that to send parameters to RDS in our template.
        CfnParameter dabasePassword = CfnParameter.Builder.create(this, "databasePassword")
                .type("String")
                .description("password for our database RDS")
                .build();

        //Configuring Security Group to use port 3306
        ISecurityGroup iSecurityGroup = SecurityGroup.fromSecurityGroupId(this, id, vpc.getVpcDefaultSecurityGroup());
        iSecurityGroup.addEgressRule(Peer.anyIpv4(), Port.tcp(3306));

        DatabaseInstance databaseInstance = DatabaseInstance.Builder
                .create(this, "Rds01")
                //identifier our database to see in aws console
                .instanceIdentifier("aws-spring01-db")
                //instance of our database
                .engine(DatabaseInstanceEngine.mysql(MySqlInstanceEngineProps.builder()
                        .version(MysqlEngineVersion.VER_5_6)
                        .build()))
                //vpc
                .vpc(vpc)
                //database credentials
                .credentials(Credentials.fromUsername("admin",
                        CredentialsFromUsernameOptions.builder()
                                .password(SecretValue.plainText(dabasePassword.getValueAsString()))
                                .build()))
                //instance type, as i use aws educate i just can use the minimal configuration
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.SMALL))
                .multiAz(false)
                .allocatedStorage(5) //storage
                //securityGroups and Vpc subnet
                .securityGroups(Collections.singletonList(iSecurityGroup))
                .vpcSubnets(SubnetSelection.builder()
                        .subnets(vpc.getPrivateSubnets())
                        .build())
                .build();

        // exporting our parameters, if others stacks need

        //exporting the database endpoint
        CfnOutput.Builder.create(this, "rds-endpoint")
                .exportName("rds-endpoint")
                .value(databaseInstance.getDbInstanceEndpointAddress())
                .build();

        //export the password
        CfnOutput.Builder.create(this, "rds-password")
                .exportName("rds-password")
                .value(dabasePassword.getValueAsString())
                .build();
    }

}
