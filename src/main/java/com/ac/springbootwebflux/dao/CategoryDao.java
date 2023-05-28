package com.ac.springbootwebflux.dao;

import com.ac.springbootwebflux.documents.Category;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoryDao extends ReactiveMongoRepository<Category, String> {
}
