package com.ac.springbootwebflux.services;

import com.ac.springbootwebflux.documents.Category;
import com.ac.springbootwebflux.documents.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryService {
    public Flux<Category> findAll();
    public Mono<Category> findBiId(String id);
    public Mono<Category> save(Category category);
    public Mono<Void> delete(Category category);
}
