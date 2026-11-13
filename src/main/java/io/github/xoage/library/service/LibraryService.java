package io.github.xoage.library.service;

import io.github.xoage.library.model.Author;
import io.github.xoage.library.model.Book;
import io.github.xoage.library.repository.AuthorRepository;
import io.github.xoage.library.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service //бизнес-логика, тоже бин
public class LibraryService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public LibraryService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    /** Автор и книга сохраняются вместе: либо оба, либо ничего. */
    @Transactional
    public int addBookWithNewAuthor(String authorName, String title, Integer year, String isbn) {
        Author author = authorRepository.save(new Author(null, authorName));
        Book book = bookRepository.save(new Book(null, title, author, year, isbn));
        return book.id();
    }

    /** null в параметре - оставить поле как есть. false, если книги нет. */
    @Transactional
    public boolean updateBook(int id, String newTitle, Integer newYear) {
        Optional<Book> found = bookRepository.findById(id);
        if (found.isEmpty()) {
            return false;
        }
        Book book = found.get();
        if (newTitle != null) {
            book.setTitle(newTitle);
        }
        if (newYear != null) {
            book.setYear(newYear);
        }
        return true;
    }

    @Transactional
    public Book addBook(String title, Integer authorId, Integer year, String isbn) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Автора с id " + authorId + " нет"));
        return bookRepository.save(new Book(null, title, author, year, isbn));
    }
}
