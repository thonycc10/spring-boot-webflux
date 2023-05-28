package com.ac.springbootwebflux.controllers;

import com.ac.springbootwebflux.dao.ProductDao;
import com.ac.springbootwebflux.documents.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;

@Controller
public class ProductController {

    private final ProductDao dao;

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductDao dao) {
        this.dao = dao;
    }

    @GetMapping({"/listar", "/"})
    public String list(Model model) {
        Flux<Product> products = dao.findAll()
                .map(product -> {
                    product.setName(product.getName().toUpperCase());
                    return product;
                });

        products.subscribe(product -> log.info(product.getName()));

        model.addAttribute("products", products);
        model.addAttribute("title", "List products");

        return "list";
    }
}
