# Домашняя библиотека на Spring Boot
![CI](https://github.com/xoAge/library-spring/actions/workflows/ci.yml/badge.svg)

REST API для учёта домашней библиотеки: 
книги и авторы хранятся в PostgreSQL, доступ к данным через Spring Data JPA.

Это переработка консольного проекта [library](https://github.com/xoAge/library) (чистый JDBC) на Spring. 
Переход шёл в три этапа:

1. Spring Boot + `JdbcTemplate`: пул соединений и Flyway настраиваются автоконфигурацией, DAO получают зависимости через конструктор.
2. Spring Data JPA: сущности вместо ручного маппинга строк, репозитории вместо DAO, исправлена проблема N+1.
3. REST API вместо консольного меню: контроллеры, DTO, единый формат ошибок, валидация запросов.

## Технологии

- Java 21, Maven
- Spring Boot 4: Spring Web MVC, Spring Data JPA (Hibernate), Jakarta Validation
- PostgreSQL, HikariCP
- Flyway - миграции схемы (Hibernate только проверяет схему: `ddl-auto=validate`)

## API

### Книги

| Метод | Адрес | Что делает | Успех |
|---|---|---|---|
| `GET` | `/books` | все книги | `200` |
| `GET` | `/books?title=мастер` | поиск по части названия, без учёта регистра | `200` |
| `GET` | `/books?author=булгаков` | поиск по части имени автора | `200` |
| `GET` | `/books/{id}` | одна книга | `200` |
| `POST` | `/books` | добавить книгу | `201` |
| `PATCH` | `/books/{id}` | изменить переданные поля, остальные остаются как были | `204` |
| `DELETE` | `/books/{id}` | удалить книгу | `204` |

Пример тела для `POST /books`:

```json
{
  "title": "Мастер и Маргарита",
  "authorId": 1,
  "year": 1967,
  "isbn": "978-5-17-000000-0"
}
```

Ответ:

```json
{
  "id": 8,
  "title": "Мастер и Маргарита",
  "author": "Михаил Булгаков",
  "year": 1967,
  "isbn": "978-5-17-000000-0"
}
```

### Авторы

| Метод | Адрес | Что делает | Успех |
|---|---|---|---|
| `GET` | `/authors` | все авторы по алфавиту | `200` |
| `GET` | `/authors/stats` | авторы с количеством книг, включая авторов без книг | `200` |
| `POST` | `/authors` | добавить автора, тело `{ "name": "..." }` | `201` |

### Ошибки

Ошибки возвращаются в стандартном формате Problem Details (RFC 9457):

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "неверные данные в запросе",
  "instance": "/books",
  "errors": {
    "title": "название не должно быть пустым",
    "year": "год не раньше 1450"
  }
}
```

| Код | Когда |
|---|---|
| `400` | не прошла валидация, неверный JSON, id не числом |
| `404` | нет книги или автора с таким id |
| `409` | повтор ISBN или имени автора |

## Запуск

**1. Создать пользователя и базу** (в psql или pgAdmin под администратором PostgreSQL):

```sql
CREATE USER library_app WITH PASSWORD 'ваш_пароль';
CREATE DATABASE library_spring OWNER library_app;
```

**2. Передать пароль через переменную окружения `DB_PASSWORD`.** 
В репозитории пароля нет: в `application.properties` `${DB_PASSWORD}`.

**3. Запустить** класс `LibrarySpringApplication` или из корня проекта:

При первом запуске Flyway создаст таблицы и добавит начальные данные (7 книг 4 авторов). 
