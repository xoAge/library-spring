package io.github.xoage.library;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(
        title = "Домашняя библиотека API",
        version = "1.0",
        description = "REST API для учёта книг и авторов"))

@SpringBootApplication
public class LibrarySpringApplication {
    //Создание всех бинов: пул, JdbcTemplate, DAO, сервис, меню. -> проверка миграций ->
    // -> Spring ищет все бины в меню, которые реализую CommandLineRunner -> Spring закрывает пул..
    public static void main(String[] args) {
        SpringApplication.run(LibrarySpringApplication.class, args);
    }
}
