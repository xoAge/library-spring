# Домашняя библиотека на Spring Boot
![CI](https://github.com/xoAge/library-spring/actions/workflows/ci.yml/badge.svg)

REST API для учёта домашней библиотеки: 
книги и авторы хранятся в PostgreSQL, доступ к данным через Spring Data JPA.
Приложение и база запускаются одной командой через Docker Compose, 
каждый pull request проверяется тестами в GitHub Actions.

Это переработка консольного проекта [library](https://github.com/xoAge/library) (чистый JDBC) на Spring. 
Переход шёл в три этапа:

1. Spring Boot + `JdbcTemplate`: пул соединений и Flyway настраиваются автоконфигурацией, 
DAO получают зависимости через конструктор.
2. Spring Data JPA: сущности вместо ручного маппинга строк, репозитории вместо DAO, 
исправлена проблема N+1.
3. REST API вместо консольного меню: контроллеры, DTO, единый формат ошибок, валидация запросов.
4. Docker и Docker Compose, тесты на трёх уровнях, CI на GitHub Actions, пагинация, документация Swagger.

## Технологии

- Java 21, Maven
- Spring Boot 4: Spring Web MVC, Spring Data JPA (Hibernate), Jakarta Validation
- PostgreSQL, HikariCP, Flyway - миграции схемы (Hibernate только проверяет схему: `ddl-auto=validate`)
- springdoc-openapi - документация API и Swagger UI
- JUnit 5, Mockito, MockMvc, Testcontainers
- Docker, Docker Compose, GitHub Actions

## Быстрый старт через Docker

Нужен только [Docker Desktop](https://www.docker.com/products/docker-desktop/).

**1.** Создать файл `.env` с паролем базы из образца:

```
cp .env.example .env
```

(в Windows: `copy .env.example .env`), затем вписать в `.env` свой пароль.

**2.** Запустить приложение и базу:

```
docker compose up --build
```

При первом запуске Flyway создаст таблицы и добавит начальные данные (7 книг 4 авторов). 
Данные хранятся в томе Docker и сохраняются между перезапусками.

**3.** Открыть:

- Swagger UI: http://localhost:8080/swagger-ui.html
- список книг: http://localhost:8080/books

Остановить: `Ctrl+C` или `docker compose down`. 
Команда `docker compose down -v` дополнительно удаляет том, то есть все данные базы.

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

## Тесты

```
./mvnw verify
```

(в Windows: `.\mvnw.cmd verify`). 
Для интеграционных тестов должен быть запущен Docker: 
Testcontainers сам поднимает PostgreSQL в контейнере, локальная база не нужна.

| Уровень | Инструменты | Что проверяется |
|---|---|---|
| Unit | JUnit 5, Mockito | логика `LibraryService` с моками репозиториев |
| Веб-слой | `@WebMvcTest`, MockMvc | HTTP-коды, JSON, пагинация, валидация, ошибки |
| Интеграционные | `@SpringBootTest`, Testcontainers | весь путь HTTP → контроллер → сервис → JPA → PostgreSQL, миграции Flyway, ограничения базы |

GitHub Actions запускает сборку и все тесты на каждый push в `main` 
и на каждый pull request; мерж в `main` возможен только при зелёной проверке.

## Запуск из IDE

1. Поднять только базу: `docker compose up -d db` (она будет доступна на порту `15432`).
2. В конфигурации запуска `LibrarySpringApplication` задать переменные окружения:
```
DB_PASSWORD=пароль_из_.env;SPRING_DATASOURCE_URL=jdbc:postgresql://127.0.0.1:15432/library_spring
```

3. Запустить `LibrarySpringApplication`.
   Пароль в репозитории не хранится: в `application.properties` стоит `${DB_PASSWORD}`, 
   файл `.env` добавлен в `.gitignore`.
