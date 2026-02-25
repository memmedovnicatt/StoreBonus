package com.nicat.storebonus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StoreBonusApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreBonusApplication.class, args);
    }

}
