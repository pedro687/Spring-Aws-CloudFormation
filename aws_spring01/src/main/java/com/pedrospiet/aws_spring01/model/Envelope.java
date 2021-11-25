package com.pedrospiet.aws_spring01.model;

import com.pedrospiet.aws_spring01.enums.EventType;
import lombok.Data;

@Data
public class Envelope {
    private EventType eventType;
    private String data;


}
