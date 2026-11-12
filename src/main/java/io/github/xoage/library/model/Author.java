package io.github.xoage.library.model;

import jakarta.persistence.*;

@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    /// Для Hibernate: он создаёт пустой объект и сам заполняет поля.
    protected Author() {
    }

    public Author(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer id() {
        return id;
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return "Author[id=" + id + ", name=" + name + "]";
    }
}
