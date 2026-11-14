package io.github.xoage.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibrarySpringApplication {
    //Создание всех бинов: пул, JdbcTemplate, DAO, сервис, меню. -> проверка миграций ->
    // -> Spring ищет все бины в меню, которые реализую CommandLineRunner -> Spring закрывает пул..
    public static void main(String[] args) {
        SpringApplication.run(LibrarySpringApplication.class, args);
    }
}
