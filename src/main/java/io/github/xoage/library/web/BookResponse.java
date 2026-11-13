package io.github.xoage.library.web;

import io.github.xoage.library.model.Book;

/// То, что клиент видит о книге в JSON.
public record BookResponse(Integer id, String title, String author, Integer year, String isbn) {

    //сделай DTO из сущности
    public static BookResponse from(Book book) {
        return new BookResponse(
                book.id(),
                book.title(),
                book.author().name(),
                book.year(),
                book.isbn()
        );
    }
}