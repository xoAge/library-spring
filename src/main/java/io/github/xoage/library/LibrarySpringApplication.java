package io.github.xoage.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

///Spring переводит исключения в понятные имена
@SpringBootApplication
public class LibrarySpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibrarySpringApplication.class, args);
    }

}
