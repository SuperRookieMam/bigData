package com.yhl.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = {"com.yhl.**"})
public class Management {
    public static void main(String[] args) {
        SpringApplication.run(Management.class, args);
    }
}
