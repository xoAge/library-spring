package io.github.xoage.library.service;

/// Запрошенной записи нет. До клиента доходит как 404.
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}