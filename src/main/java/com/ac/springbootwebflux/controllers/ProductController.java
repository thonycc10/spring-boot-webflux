package com.ac.springbootwebflux.controllers;

import com.ac.springbootwebflux.documents.Category;
import com.ac.springbootwebflux.documents.Product;
import com.ac.springbootwebflux.services.CategoryService;
import com.ac.springbootwebflux.services.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Date;

@SessionAttributes("product")
@Controller
public class ProductController {

    private final ProductService service;
    private final CategoryService categoryService;

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductService service, CategoryService categoryService) {
        this.service = service;
        this.categoryService = categoryService;
    }

    @ModelAttribute("categories")
    public Flux<Category> categories() {
        return categoryService.findAll();
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
        model.addAttribute("category", new Category());
        model.addAttribute("title", "Form Product");
        model.addAttribute("btnSumit", "Crear");

        return Mono.just("form");
    }

    @GetMapping("/form/{idProduct}")
    public Mono<String> EditView(@PathVariable String idProduct, Model model) {
        Mono<Product> product = service.findBiId(idProduct).doOnNext(p -> {
            log.info(String.format("Product: %1$s", p.getName()));
        }).defaultIfEmpty(new Product());

        model.addAttribute("product", product);
        model.addAttribute("title", "Edit Form");
        model.addAttribute("btnSumit", "Guardar");

        return Mono.just("form");
    }

    @GetMapping("/form/v2/{idProduct}")
    public Mono<String> EditViewV2(@PathVariable String idProduct, Model model) {
        return service.findBiId(idProduct).doOnNext(p -> {
            log.info(String.format("Product: %1$s", p.getName()));
            model.addAttribute("product", p);
            model.addAttribute("title", "Edit Product");
            model.addAttribute("btnSumit", "Guardar");
        }).defaultIfEmpty(new Product()).flatMap(p -> {
            if (p.getId() == null) {
                return Mono.error(new InterruptedException("No exist the product"));
            }
            return Mono.just(p);
        }).then(Mono.just("form")).onErrorResume(ex -> Mono.just("redirect:/list?error=no+exist+the+product"));
    }

    @PostMapping("/form")
    public Mono<String> save(@Valid Product product, BindingResult result, Model model, SessionStatus sessionStatus) {
        // bindinresult debe estar siempre al costado del objeto clase

        if (result.hasErrors()) {
            model.addAttribute("product", product);
            model.addAttribute("title", "Edit Product");
            model.addAttribute("btnSumit", "Guardar");
            return Mono.just("form");
        } else {
            sessionStatus.isComplete();

            Mono<Category> category = categoryService.findBiId(product.getCategory().getId());

            return category.flatMap(c -> {
                if (product.getCreateAt() == null) {
                    product.setCreateAt(new Date());
                }
                product.setCategory(c);
                return service.save(product);
            }).doOnNext(p -> {
                log.info(String.format("Product saved: %1$s, id: %2$s and Category: %3$s",
                        p.getName(),
                        p.getId(),
                        p.getCategory().getName()));
            }).thenReturn("redirect:/list?success=product+success");
        }
    }

    @GetMapping("/delete/{idProduct}")
    private Mono<String> delete(@PathVariable String idProduct) {
        return service.findBiId(idProduct)
                .defaultIfEmpty(new Product())
                .flatMap(p -> {
                    if (p.getId() == null) {
                        return Mono.error(new InterruptedException(String.format("No exist the product: %1$s", p.getName())));
                    }
                    return Mono.just(p);
                })
                .flatMap(p -> {
                    log.info(String.format("Delete product: %1$s and id: %2$s", p.getName(), p.getId()));
                    return service.delete(p);
                })
                .then(Mono.just("redirect:/list?success=product+remove+success"))
                .onErrorResume(ex -> Mono.just("redirect:/list?error=not+exist+product"));
    }

    @GetMapping("/datadriver")
    public String listDataDriver(Model model) {
        Flux<Product> products = service.findAllWithNameUpperCase().delayElements(Duration.ofSeconds(1));

        products.subscribe(product -> log.info(product.getName()));

        model.addAttribute("products", new ReactiveDataDriverContextVariable(products, 2));
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
