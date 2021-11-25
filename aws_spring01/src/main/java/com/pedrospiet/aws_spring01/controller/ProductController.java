package com.pedrospiet.aws_spring01.controller;

import com.pedrospiet.aws_spring01.enums.EventType;
import com.pedrospiet.aws_spring01.model.Product;
import com.pedrospiet.aws_spring01.repository.ProductRepository;
import com.pedrospiet.aws_spring01.service.ProductPublisherEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = "/v1/products")
public class ProductController {

    private ProductPublisherEvent productPublisherEvent;

    @Autowired
    public ProductController(ProductRepository productRepository, ProductPublisherEvent productPublisherEvent) {
        this.productRepository = productRepository;
        this.productPublisherEvent = productPublisherEvent;
    }

    @Autowired
    private ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<Product> saveProduct(
            @RequestBody Product product) {
        product.setId(null);
        Product productCreated = productRepository.save(product);

        productPublisherEvent.publishProductEvent(product, EventType.PRODUCT_CREATED, "Pedro");
        return new ResponseEntity<Product>(productCreated,
                HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable("id") long id) {
        Optional<Product> optProduct = productRepository.findById(id);
        if (optProduct.isPresent()) {
            Product product = optProduct.get();

            productRepository.delete(product);
            productPublisherEvent.publishProductEvent(product, EventType.PRODUCT_DELETED, "Pedro");

            return new ResponseEntity<Product>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping
    public Iterable<Product> findAll() {
        return productRepository.findAll();
    }


    @PutMapping(path = "/{id}")
    public ResponseEntity<Product> updateProduct(
            @RequestBody Product product, @PathVariable("id") long id) {
        if (productRepository.existsById(id)) {
            product.setId(id);

            Product productUpdated = productRepository.save(product);
            productPublisherEvent.publishProductEvent(product, EventType.PRODUCT_UPDATED, "Pedro");

            return new ResponseEntity<Product>(productUpdated,
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/bycode")
    public ResponseEntity<Product> findByCode(@RequestParam String code) {
        Optional<Product> optProduct = productRepository.findByCode(code);
        if (optProduct.isPresent()) {
            return new ResponseEntity<Product>(optProduct.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable long id) {
        Optional<Product> optProduct = productRepository.findById(id);
        if (optProduct.isPresent()) {
            return new ResponseEntity<Product>(optProduct.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
