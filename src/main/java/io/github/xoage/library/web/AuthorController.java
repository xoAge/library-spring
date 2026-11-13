package io.github.xoage.library.web;

import io.github.xoage.library.model.Author;
import io.github.xoage.library.model.AuthorStats;
import io.github.xoage.library.repository.AuthorRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorRepository authorRepository;

    public AuthorController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @GetMapping
    public List<AuthorResponse> findAll() {
        return authorRepository.findAll(Sort.by("name")).stream()
                .map(AuthorResponse::from)
                .toList();
    }

    /** Все авторы с количеством книг, включая авторов без книг. */
    @GetMapping("/stats")
    public List<AuthorStats> stats() {
        return authorRepository.findAllWithBookCount();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponse create(@Valid @RequestBody AuthorRequest request) {
        Author saved = authorRepository.save(new Author(null, request.name()));
        return AuthorResponse.from(saved);
    }
}
