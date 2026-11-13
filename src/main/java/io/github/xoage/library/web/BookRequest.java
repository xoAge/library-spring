package io.github.xoage.library.web;

/// То, что клиент присылает, чтобы создать книгу.
public record BookRequest(String title, Integer authorId, Integer year, String isbn) {
}
