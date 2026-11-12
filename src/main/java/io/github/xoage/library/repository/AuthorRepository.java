package io.github.xoage.library.repository;

import io.github.xoage.library.model.Author;
import io.github.xoage.library.model.AuthorStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Integer> {

    /** Все авторы с количеством книг, включая авторов без книг. */
    @Query("""
            SELECT new io.github.xoage.library.model.AuthorStats(a.name, COUNT(b.id))
            FROM Author a
            LEFT JOIN Book b ON b.author = a
            GROUP BY a.id, a.name
            ORDER BY COUNT(b.id) DESC, a.name
            """)
    List<AuthorStats> findAllWithBookCount();
}
