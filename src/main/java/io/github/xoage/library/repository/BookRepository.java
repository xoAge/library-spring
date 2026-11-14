package io.github.xoage.library.repository;

import io.github.xoage.library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Integer> {

    /// Книги вместе с авторами одним запросом, без N+1.
    @Override
    @EntityGraph(attributePaths = "author")
    Page<Book> findAll(Pageable pageable);

    /// Загрузка поле author вместе с книгой
    @EntityGraph(attributePaths = "author")
    Page<Book> findByTitleContainingIgnoreCase(String part, Pageable pageable);

    @EntityGraph(attributePaths = "author")
    Page<Book> findByAuthorNameContainingIgnoreCase(String part, Pageable pageable);
}
