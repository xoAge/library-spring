package io.github.xoage.library.web;

import io.github.xoage.library.model.Author;
import io.github.xoage.library.model.Book;
import io.github.xoage.library.repository.BookRepository;
import io.github.xoage.library.service.LibraryService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    void find_returnsPageOfBooks() throws Exception {
        Book book = new Book(1, "Мастер и Маргарита", author, 1967, null);
        when(bookRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(book), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Мастер и Маргарита"))
                .andExpect(jsonPath("$.content[0].author").value("Михаил Булгаков"))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    void find_passesPageAndSortToRepository() throws Exception {
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/books?page=1&size=5&sort=title"))
                .andExpect(status().isOk());

        verify(bookRepository).findAll(PageRequest.of(1, 5, Sort.by("title")));
    }

    @Test
    void findById_returnsBook() throws Exception {
        when(bookRepository.findById(1))
                .thenReturn(Optional.of(new Book(1, "Мастер и Маргарита", author, 1967, null)));

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Мастер и Маргарита"))
                .andExpect(jsonPath("$.year").value(1967));
    }

    @Test
    void findById_returns404_whenMissing() throws Exception {
        when(bookRepository.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Книги с id 999 нет"));
    }

    @Test
    void create_returns201() throws Exception {
        when(service.addBook("Новая книга", 1, 2024, null))
                .thenReturn(new Book(10, "Новая книга", author, 2024, null));

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            { "title": "Новая книга", "authorId": 1, "year": 2024 }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.author").value("Михаил Булгаков"));
    }

    @Test
    void create_returns400_whenInvalid() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            { "title": "", "year": 1000 }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("название не должно быть пустым"))
                .andExpect(jsonPath("$.errors.authorId").value("нужно указать id автора"))
                .andExpect(jsonPath("$.errors.year").value("год не раньше 1450"));

        verifyNoInteractions(service);
    }
}