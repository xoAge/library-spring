package io.github.xoage.library.web;

/// Изменение книги: null в поле - оставить как есть.
public record BookUpdateRequest(String title, Integer authorId, Integer year, String isbn) {
}
