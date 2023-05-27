package com.ac.springbootwebflux;

import com.ac.springbootwebflux.dao.ProductDao;
import com.ac.springbootwebflux.documents.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringBootWebfluxApplication implements CommandLineRunner {

    private final ProductDao dao;

    private static  final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApplication.class);

    public SpringBootWebfluxApplication(ProductDao dao) {
        this.dao = dao;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebfluxApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Flux.just(new Product("Laptop1", 100.10),
                        new Product("Laptop2", 200.10),
                        new Product("Laptop3", 300.10),
                        new Product("Laptop4", 400.10),
                        new Product("Laptop5", 500.10)
                ).flatMap(dao::save)
                .subscribe(product -> log.info("Insert -> id: " + product.getId() + ", name: " + product.getName()));
    }
}
