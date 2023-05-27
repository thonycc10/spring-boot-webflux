package com.ac.springbootwebflux.dao;

import com.ac.springbootwebflux.documents.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductDao extends ReactiveMongoRepository<Product, String> {

}
