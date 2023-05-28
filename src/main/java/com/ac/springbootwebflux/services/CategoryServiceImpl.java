package com.ac.springbootwebflux.services;

import com.ac.springbootwebflux.dao.CategoryDao;
import com.ac.springbootwebflux.documents.Category;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryDao dao;

    public CategoryServiceImpl(CategoryDao dao) {
        this.dao = dao;
    }

    @Override
    public Flux<Category> findAll() {
        return dao.findAll();
    }

    @Override
    public Mono<Category> findBiId(String id) {
        return dao.findById(id);
    }

    @Override
    public Mono<Category> save(Category category) {
        return dao.save(category);
    }

    @Override
    public Mono<Void> delete(Category category) {
        return dao.delete(category);
    }
}
