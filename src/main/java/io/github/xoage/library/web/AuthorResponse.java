package io.github.xoage.library.web;

import io.github.xoage.library.model.Author;

/// То, что клиент видит об авторе в JSON.
public record AuthorResponse(Integer id, String name) {

    public static AuthorResponse from(Author author) {
        return new AuthorResponse(author.id(), author.name());
    }
}