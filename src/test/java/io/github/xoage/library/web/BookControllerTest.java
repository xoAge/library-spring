package io.github.xoage.library.web;

import io.github.xoage.library.model.Author;
import io.github.xoage.library.model.Book;
import io.github.xoage.library.repository.BookRepository;
import io.github.xoage.library.service.LibraryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookRepository bookRepository;

    @MockitoBean
    private LibraryService service;

    private final Author author = new Author(1, "Михаил Булгаков");

    @Test
    void find_returnsAllBooks() throws Exception {
        when(bookRepository.findAll(any(Sort.class)))
                .thenReturn(List.of(new Book(1, "Мастер и Маргарита", author, 1967, null)));

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Мастер и Маргарита"))
                .andExpect(jsonPath("$[0].author").value("Михаил Булгаков"));
    }
}