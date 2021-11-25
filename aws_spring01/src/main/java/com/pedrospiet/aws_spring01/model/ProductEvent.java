package com.pedrospiet.aws_spring01.model;

import lombok.Data;

@Data
public class ProductEvent {
    private Long productId;
    private String code;
    private String username;


}
