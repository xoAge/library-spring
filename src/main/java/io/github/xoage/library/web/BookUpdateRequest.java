package io.github.xoage.library.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/// Изменение книги: null в поле - оставить как есть.
public record BookUpdateRequest(
        @Pattern(regexp = ".*\\S.*", message = "название не должно быть пустым")
        @Size(max = 100, message = "название до 100 символов")
        String title,

        Integer authorId,

        @Min(value = 1450, message = "год не раньше 1450")
        @Max(value = 2100, message = "год не позже 2100")
        Integer year,

        @Size(max = 17, message = "ISBN до 17 символов")
        String isbn
) {
}
