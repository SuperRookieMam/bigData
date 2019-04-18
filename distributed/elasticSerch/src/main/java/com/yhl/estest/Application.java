package com.yhl.estest;

import com.yhl.estest.hello.HelloEs;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
          HelloEs.test();
    }
}
