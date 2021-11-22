package com.pedrospiet.aws_spring01.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "Product", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"code"}) //make code unique
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(length = 24, nullable = false)
    private String name;

    @Column(length = 8, nullable = false)
    private String code;

    private float price;
}