package io.github.xoage.library.service;

import io.github.xoage.library.dao.AuthorDao;
import io.github.xoage.library.dao.BookDao;
import io.github.xoage.library.model.Author;
import io.github.xoage.library.model.Book;
import io.github.xoage.library.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service //бизнес-логика, тоже бин
public class LibraryService {

    private final BookDao bookDao;
    private final AuthorDao authorDao;
    private final BookRepository bookRepository;

    public LibraryService(BookDao bookDao, AuthorDao authorDao, BookRepository bookRepository) {
        this.bookDao = bookDao;
        this.authorDao = authorDao;
        this.bookRepository = bookRepository;
    }

    /** Автор и книга сохраняются вместе: либо оба, либо ничего. */
    @Transactional
    public int addBookWithNewAuthor(String authorName, String title, Integer year, String isbn) {
        int authorId = authorDao.add(authorName);
        return bookDao.add(new Book(null, title, new Author(authorId, authorName), year, isbn));
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
}
