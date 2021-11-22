package com.pedrospiet.aws_spring01.repository;

import com.pedrospiet.aws_spring01.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByCode(String code);

}