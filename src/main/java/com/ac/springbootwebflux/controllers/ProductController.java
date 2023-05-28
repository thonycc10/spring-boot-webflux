package com.ac.springbootwebflux.controllers;

import com.ac.springbootwebflux.documents.Product;
import com.ac.springbootwebflux.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Controller
public class ProductController {

    private final ProductService service;

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping({"/list", "/"})
    public String list(Model model) {
        Flux<Product> products = service.findAllWithNameUpperCase();

        products.subscribe(product -> log.info(product.getName()));

        model.addAttribute("products", products);
        model.addAttribute("title", "List products");

        return "list";
    }

    @GetMapping("/form")
    public Mono<String> createView(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("title", "Form Product");

        return Mono.just("form");
    }

    @PostMapping("/form")
    public Mono<String> save(Product product) {
        return service.save(product)
                .doOnNext(prod -> {
                    log.info(String.format("Product saved: %1$s, id: %2$s", prod.getName(), prod.getId()));
                })
                .thenReturn("redirect:/list");
    }

    @GetMapping("/datadriver")
    public String listDataDriver(Model model) {
        Flux<Product> products = service.findAllWithNameUpperCase().delayElements(Duration.ofSeconds(1));

        products.subscribe(product -> log.info(product.getName()));

        model.addAttribute("products", new ReactiveDataDriverContextVariable(products, 2 ));
        model.addAttribute("title", "List products");

        return "list";
    }

    @GetMapping("/list-full")
    public String listFull(Model model) {
        Flux<Product> products = service.findAllWithNameUpperCaseRepeat(5000);

        model.addAttribute("products", products);
        model.addAttribute("title", "List products");

        return "list";
    }

    @GetMapping("/list-chunked")
    public String listChunked(Model model) {
        Flux<Product> products = service.findAllWithNameUpperCaseRepeat(5000);

        model.addAttribute("products", products);
        model.addAttribute("title", "List products");

        return "list-chunked";
    }
}
