package com.ac.springbootwebflux.services;

import com.ac.springbootwebflux.documents.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
    public Flux<Product> findAll();
    public Flux<Product> findAllWithNameUpperCase();
    public Flux<Product> findAllWithNameUpperCaseRepeat(Integer top);
    public Mono<Product> findBiId(String id);
    public Mono<Product> save(Product product);
    public Mono<Void> delete(Product product);
}
