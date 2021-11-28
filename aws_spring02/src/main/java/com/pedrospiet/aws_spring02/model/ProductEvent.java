package com.pedrospiet.aws_spring02.model;

import lombok.Data;

@Data
public class ProductEvent {
    private long productId;
    private String code;
    private String username;
}
