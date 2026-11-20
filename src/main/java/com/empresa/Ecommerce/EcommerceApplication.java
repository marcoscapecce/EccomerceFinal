package com.empresa.Ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = {"com.empresa", "com.techlab"})
@EnableJpaRepositories(basePackages = {"com.empresa.repositorios", "com.techlab.repositorios"})
@EntityScan(basePackages = {"com.empresa", "com.techlab"})
public class EcommerceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }
}