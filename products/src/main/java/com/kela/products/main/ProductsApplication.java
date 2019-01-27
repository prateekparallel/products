package com.kela.products.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.kela"})
public class ProductsApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ProductsApplication.class, args);
	}

}

