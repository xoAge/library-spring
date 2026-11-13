package io.github.xoage.library.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/// То, что клиент присылает, чтобы создать автора.
public record AuthorRequest(
        @NotBlank(message = "имя не должно быть пустым")
        @Size(max = 50, message = "имя до 50 символов")
        String name
) {
}
