package io.github.xoage.library.web;

import io.github.xoage.library.model.Book;
import io.github.xoage.library.repository.BookRepository;
import io.github.xoage.library.service.LibraryService;
import io.github.xoage.library.service.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Книги", description = "Поиск, добавление, изменение и удаление книг")
@RestController
@RequestMapping("/books")
public class BookController {

    private final BookRepository bookRepository;
    private final LibraryService service;

    public BookController(BookRepository bookRepository, LibraryService service) {
        this.bookRepository = bookRepository;
        this.service = service;
    }

    @Operation(summary = "Список книг с поиском по названию или автору, постранично")
    @GetMapping
    public PagedModel<BookResponse> find(@RequestParam(required = false) String title,
                                         @RequestParam(required = false) String author,
                                         @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Book> books;
        if (title != null) {
            books = bookRepository.findByTitleContainingIgnoreCase(title, pageable);
        } else if (author != null) {
            books = bookRepository.findByAuthorNameContainingIgnoreCase(author, pageable);
        } else {
            books = bookRepository.findAll(pageable);
        }
        return new PagedModel<>(books.map(BookResponse::from));
    }

    @Operation(summary = "Книга по id")
    @GetMapping("/{id}") //шаблон: на его место подходит любое значение
    public BookResponse findById(@PathVariable int id) {  //возьми то, что стоит на месте {id}
        return bookRepository.findById(id)
                .map(BookResponse::from)
                .orElseThrow(() -> new NotFoundException("Книги с id " + id + " нет")); //Исключение, если коробка пустая, с понятным сообщением
    }

    @Operation(summary = "Добавить книгу")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(@Valid @RequestBody BookRequest request) {
        Book book = service.addBook(request.title(), request.authorId(), request.year(), request.isbn());
        return BookResponse.from(book);
    }

    @Operation(summary = "Изменить переданные поля книги")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @PathVariable int id, @RequestBody BookUpdateRequest request) {
        if (!service.updateBook(id, request.title(), request.authorId(), request.year(), request.isbn())) {
            throw new NotFoundException("Книги с id " + id + " нет");
        }
    }

    @Operation(summary = "Удалить книгу")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        service.deleteBook(id);
    }
}
