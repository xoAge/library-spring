package io.github.xoage.library.dao;

import io.github.xoage.library.model.Author;
import io.github.xoage.library.model.AuthorStats;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/// один класс отвечает за одну таблицу.
@Repository //Бин для работы с базой
public class AuthorDao {

    private final JdbcTemplate jdbc;

    public AuthorDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Author> findAll() {
        return jdbc.query(
                "SELECT id, name FROM authors ORDER BY name",
                (rs, rowNum) -> new Author(rs.getInt("id"), rs.getString("name"))
        );
    }

    public int add(String name) {
        Integer id = jdbc.queryForObject(
                "INSERT INTO authors (name) VALUES (?) RETURNING id",
                Integer.class,
                name
        );
        return Objects.requireNonNull(id);
    }

    /** Все авторы с количеством книг, включая авторов без книг. */
    public List<AuthorStats> findAllWithBookCount() {
        //COUNT(b.id) честный null при 0 книг
        return jdbc.query("""
                SELECT a.name, COUNT(b.id) AS book_count
                FROM authors a
                LEFT JOIN books b ON b.author_id = a.id
                GROUP BY a.id, a.name
                ORDER BY book_count DESC, a.name
                """,
                (rs, rowNum) -> new AuthorStats(rs.getString("name"), rs.getInt("book_count"))
        );
    }
}
