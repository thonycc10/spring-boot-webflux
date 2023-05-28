package com.ac.springbootwebflux.controllers;

import com.ac.springbootwebflux.dao.ProductDao;
import com.ac.springbootwebflux.documents.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
public class ProrductRestController {

    private final ProductDao dao;

    public ProrductRestController(ProductDao dao) {
        this.dao = dao;
    }

    private static final Logger log = LoggerFactory.getLogger(ProrductRestController.class);

    @GetMapping()
    public Flux<Product> index() {
        return dao.findAll()
                .map(product -> {
                    product.setName(product.getName().toUpperCase());
                    return product;
                }).doOnNext(product -> log.info(product.getName()));
    }

    @GetMapping("/{id}")
    public Mono<Product> findId(@PathVariable String id) {
//        Mono<Product> productMono = dao.findById(id);
        Flux<Product> products = dao.findAll();

        return products
                .filter(product1 -> product1.getId().equals(id))
                .next() // convierte un flux a mono
                .doOnNext(product1 -> log.info(product1.getName()));
    }
}
