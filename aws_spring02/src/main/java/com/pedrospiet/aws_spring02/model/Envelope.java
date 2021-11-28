package com.pedrospiet.aws_spring02.model;

import com.pedrospiet.aws_spring02.enums.EventType;
import lombok.Data;

@Data
public class Envelope {
    private EventType eventType;
    private String data;
}
