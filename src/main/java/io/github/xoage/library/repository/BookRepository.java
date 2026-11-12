package io.github.xoage.library.repository;

import io.github.xoage.library.model.Book;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {

    /// Книги вместе с авторами одним запросом, без N+1.
    @Override
    @EntityGraph(attributePaths = "author")
    List<Book> findAll(@NonNull Sort sort);

    /// Загрузка поле author вместе с книгой
    @EntityGraph(attributePaths = "author")
    List<Book> findByTitleContainingIgnoreCaseOrderById(String part);

    @EntityGraph(attributePaths = "author")
    List<Book> findByAuthorNameContainingIgnoreCaseOrderById(String part);
}
