package io.github.xoage.library.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    /// Много книг - один автор. Внешний ключ author_id лежит в таблице books.
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    private Integer year;

    private String isbn;

    /// Для Hibernate.
    protected Book() {
    }

    public Book(Integer id, String title, Author author, Integer year, String isbn) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.isbn = isbn;
    }

    public Integer id() {
        return id;
    }

    public String title() {
        return title;
    }

    public Author author() {
        return author;
    }

    public Integer year() {
        return year;
    }

    public String isbn() {
        return isbn;
    }

    @Override
    public String toString() {
        return "Book[id=" + id + ", title=" + title + ", author=" + author
                + ", year=" + year + ", isbn=" + isbn + "]";
    }
}
