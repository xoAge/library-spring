package io.github.xoage.library.web;

import com.jayway.jsonpath.JsonPath;
import io.github.xoage.library.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/// Всё приложение + настоящий PostgreSQL: HTTP -> контроллер -> сервис -> JPA -> база.
@SpringBootTest(properties = "DB_PASSWORD=unused")
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class BookApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void initialDataFromFlywayIsAvailable() throws Exception {
        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createdBookCanBeReadBack() throws Exception {
        String body = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "title": "Интеграционная книга", "authorId": 1, "year": 2024, "isbn": "it-isbn-1" }
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer id = JsonPath.read(body, "$.id");

        mockMvc.perform(get("/books/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Интеграционная книга"))
                .andExpect(jsonPath("$.isbn").value("it-isbn-1"));
    }

    @Test
    void duplicateIsbn_returns409() throws Exception {
        String json = """
                { "title": "Дубликат", "authorId": 1, "isbn": "it-isbn-dup" }
                """;

        mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }
}
