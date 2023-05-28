package com.ac.springbootwebflux;

import com.ac.springbootwebflux.documents.Category;
import com.ac.springbootwebflux.documents.Product;
import com.ac.springbootwebflux.services.CategoryService;
import com.ac.springbootwebflux.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

import java.util.Date;

@SpringBootApplication
public class SpringBootWebfluxApplication implements CommandLineRunner {

    private final ProductService service;
    private final CategoryService categoryService;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApplication.class);

    public SpringBootWebfluxApplication(ProductService service, CategoryService categoryService, ReactiveMongoTemplate reactiveMongoTemplate) {
        this.service = service;
        this.categoryService = categoryService;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebfluxApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // ejemplo de como eliminar data solo usar en ejemplos no en PROD
        reactiveMongoTemplate.dropCollection("products").subscribe();
        reactiveMongoTemplate.dropCollection("category").subscribe();

        Category electronico = new Category("Electronico");
        Category computer = new Category("Computer");
        Category muebles = new Category("Muebles");

        Flux.just(electronico, computer, muebles)
                .flatMap(categoryService::save)
                .doOnNext(c -> {
                    log.info(String.format("Category created: %1$s", c.getName()));
                }).thenMany(
                        Flux.just(new Product("Laptop1", 100.10, electronico),
                                        new Product("Laptop2", 200.10, electronico),
                                        new Product("Laptop3", 300.10, computer),
                                        new Product("Laptop4", 400.10, computer),
                                        new Product("Laptop5", 500.10, muebles))
                                .flatMap(product -> {
                                    product.setCreateAt(new Date());
                                    return service.save(product);
                                })
                )
                .subscribe(product -> log.info(
                        String.format("Imsert Product -> id: %1$s with name: %2$s and category: %3$s",
                                product.getId(),
                                product.getName(),
                                product.getCategory().getName()
                        )
                ));
    }
}
