package com.pedrospiet.aws_spring02.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SnsMessage {
    @JsonProperty("message")
    private String message;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("TopicArn")
    private String topicArn;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("MesageId")
    private String messageId;
}
