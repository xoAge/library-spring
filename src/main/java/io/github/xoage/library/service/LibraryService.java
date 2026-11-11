package io.github.xoage.library.service;

import io.github.xoage.library.dao.AuthorDao;
import io.github.xoage.library.dao.BookDao;
import io.github.xoage.library.model.Author;
import io.github.xoage.library.model.Book;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service //бизнес-логика, тоже бин
public class LibraryService {

    private final BookDao bookDao;
    private final AuthorDao authorDao;

    public LibraryService(BookDao bookDao, AuthorDao authorDao) {
        this.bookDao = bookDao;
        this.authorDao = authorDao;
    }

    /** Автор и книга сохраняются вместе: либо оба, либо ничего. */
    @Transactional
    public int addBookWithNewAuthor(String authorName, String title, Integer year, String isbn) {
        int authorId = authorDao.add(authorName);
        return bookDao.add(new Book(null, title, new Author(authorId, authorName), year, isbn));
    }
}
