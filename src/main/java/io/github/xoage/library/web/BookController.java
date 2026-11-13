package io.github.xoage.library.web;

import io.github.xoage.library.model.Book;
import io.github.xoage.library.repository.BookRepository;
import io.github.xoage.library.service.LibraryService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookRepository bookRepository;
    private final LibraryService service;

    public BookController(BookRepository bookRepository, LibraryService service) {
        this.bookRepository = bookRepository;
        this.service = service;
    }

    @GetMapping
    public List<BookResponse> find(@RequestParam(required = false) String title,
                                   @RequestParam(required = false) String author) {
        List<Book> books;
        if (title != null) {
            books = bookRepository.findByTitleContainingIgnoreCaseOrderById(title);
        } else if (author != null) {
            books = bookRepository.findByAuthorNameContainingIgnoreCaseOrderById(author);
        } else {
            books = bookRepository.findAll(Sort.by("id"));
        }
        return books.stream()
                .map(BookResponse::from)
                .toList();
    }

    @GetMapping("/{id}") //шаблон: на его место подходит любое значение
    public BookResponse findById(@PathVariable int id) {  //возьми то, что стоит на месте {id}
        return bookRepository.findById(id)
                .map(BookResponse::from)
                .orElseThrow(() -> new ResponseStatusException(  //Исключение, если коробка пустая, с понятным сообщением
                        HttpStatus.NOT_FOUND, "Книги с id " + id + " нет"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(@RequestBody BookRequest request) {
        Book book = service.addBook(request.title(), request.authorId(), request.year(), request.isbn());
        return BookResponse.from(book);
    }
}
