package com.ac.springbootwebflux.services;

import com.ac.springbootwebflux.dao.ProductDao;
import com.ac.springbootwebflux.documents.Product;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductDao dao;

    public ProductServiceImpl(ProductDao dao) {
        this.dao = dao;
    }

    @Override
    public Flux<Product> findAll() {
        return dao.findAll();
    }

    @Override
    public Flux<Product> findAllWithNameUpperCase() {
        return dao.findAll().map(product -> {
            product.setName(product.getName().toUpperCase());
            return product;
        });
    }

    @Override
    public Flux<Product> findAllWithNameUpperCaseRepeat(Integer top) {
        return findAllWithNameUpperCase().repeat(top);
    }

    @Override
    public Mono<Product> findBiId(String id) {
        return dao.findById(id);
    }

    @Override
    public Mono<Product> save(Product product) {
        return dao.save(product);
    }

    @Override
    public Mono<Void> delete(Product product) {
        return dao.delete(product);
    }
}
