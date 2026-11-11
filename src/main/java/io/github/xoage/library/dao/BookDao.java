package io.github.xoage.library.dao;

import io.github.xoage.library.model.Author;
import io.github.xoage.library.model.Book;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository //Бин (объект класса, который хранит сам Spring у себя) вместо new BookDao
public class BookDao {

    private static final String SELECT_BOOKS = """
            SELECT b.id, b.title, b.year, b.isbn,
                   a.id AS author_id, a.name AS author_name
            FROM books b
            JOIN authors a ON a.id = b.author_id
            """;

    private final JdbcTemplate jdbc;
    //Внедрение зависимостей, Spring сам передаёт Jdbc в конструктор
    public BookDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }
    //Замена старого кода с getConnection() и других..
    public List<Book> findAll() {
        return jdbc.query(SELECT_BOOKS + " ORDER BY b.id", this::mapRow);
    }
    public List<Book> findByTitle(String part) {
        return jdbc.query(
                SELECT_BOOKS + " WHERE b.title ILIKE ? ORDER BY b.id",
                this::mapRow,
                "%" + escapeLike(part) + "%"
        );
    }

    public List<Book> findByAuthor(String part) {
        return jdbc.query(
                SELECT_BOOKS + " WHERE a.name ILIKE ? ORDER BY b.id",
                this::mapRow,
                "%" + escapeLike(part) + "%"
        );
    }

    public int add(Book book) {
        //запрос, который возвращает одно значение
        Integer id = jdbc.queryForObject(
                "INSERT INTO books (title, author_id, year, isbn) VALUES (?, ?, ?, ?) RETURNING id",
                Integer.class, //возващаем одно значение, не нужен mapRow
                book.title(),
                book.author().id(), //только id автора пишем в таблицу
                book.year(),
                book.isbn()
        );
        //точно не null, т.к. возвращает id, указываем явно, т.к. в Spring 7 queryForObject может быть null
        return Objects.requireNonNull(id);
    }

    public boolean delete(int id) {
        return jdbc.update("DELETE FROM books WHERE id = ?", id) > 0;
    }
    //изменение книги
    public boolean update(Book book) {
        Integer authorId = book.author() == null ? null : book.author().id();
        //COALESCE первое значение, которое не null
        return jdbc.update("""
            UPDATE books
            SET title     = COALESCE(?, title),
                author_id = COALESCE(?, author_id),
                year      = COALESCE(?, year),
                isbn      = COALESCE(?, isbn)
            WHERE id = ?
            """,
                book.title(),
                authorId,
                book.year(),
                book.isbn(),
                book.id()
        ) > 0;
    }
    //RowMapper - превратить одну строку результата в Book
    private Book mapRow(ResultSet rs, int rowNum) throws SQLException {
        Author author = new Author(rs.getInt("author_id"), rs.getString("author_name"));
        return new Book(
                rs.getInt("id"),
                rs.getString("title"),
                author,
                rs.getObject("year", Integer.class), //null
                rs.getString("isbn")
        );
    }
    private static String escapeLike(String s) {
        return s.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
