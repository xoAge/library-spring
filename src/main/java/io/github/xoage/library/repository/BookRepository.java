package io.github.xoage.library.repository;

import io.github.xoage.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findByTitleContainingIgnoreCaseOrderById(String part);

    List<Book> findByAuthorNameContainingIgnoreCaseOrderById(String part);
}
