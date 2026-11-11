package io.github.xoage.library.ui;

import io.github.xoage.library.dao.AuthorDao;
import io.github.xoage.library.dao.BookDao;
import io.github.xoage.library.model.Author;
import io.github.xoage.library.model.AuthorStats;
import io.github.xoage.library.model.Book;
import io.github.xoage.library.service.LibraryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

@Component //Spring сам создаёт меню и запускает его после старта
public class ConsoleMenu implements CommandLineRunner {

    private final BookDao bookDao;
    private final AuthorDao authorDao;
    private final LibraryService service;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleMenu(BookDao bookDao, AuthorDao authorDao, LibraryService service) {
        this.bookDao = bookDao;
        this.authorDao = authorDao;
        this.service = service;
    }

    @Override
    public void run(String... args) {
        while (true) {
            System.out.println();
            System.out.println("1 - показать все книги");
            System.out.println("2 - найти по названию");
            System.out.println("3 - добавить книгу");
            System.out.println("4 - удалить книгу");
            System.out.println("5 - изменить книгу");
            System.out.println("6 - добавить книгу нового автора");
            System.out.println("7 - найти по автору");
            System.out.println("8 - авторы и число книг");
            System.out.println("0 - выход");
            System.out.print("Выбор: ");
            String choice = scanner.nextLine().trim();

            try {//try внутри while, потому что работает от try к catch, если будет снаружи: ошибка ловится снаружи, а catch уже нет.
                switch (choice) {
                    case "1" -> printBooks(bookDao.findAll());
                    case "2" -> search();
                    case "3" -> addBook();
                    case "4" -> deleteBook();
                    case "5" -> updateBook();
                    case "6" -> addBookWithNewAuthor();
                    case "7" -> searchByAuthor();
                    case "8" -> printAuthorStats();
                    case "0" -> {
                        System.out.println("Пока!");
                        return;
                    }
                    default -> System.out.println("Нет такого пункта");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: нужно ввести целое число");
            } catch (DataAccessException e) {
                System.out.println("Ошибка: " + explain(e));
            }
        }
    }

    /// Spring переводит SQLException в свои исключения, но код ошибки PostgreSQL сохраняется внутри.
    private static String explain(DataAccessException e) {
        if (!(e.getMostSpecificCause() instanceof SQLException sql) || sql.getSQLState() == null) {
            return e.getMessage();
        }
        return switch (sql.getSQLState()) {
            case "23503" -> "нет автора с таким номером";
            case "23505" -> "такая запись уже есть: автор с таким именем или книга с таким ISBN";
            case "23514" -> "пустое имя или название, либо год не от 1450 до 2100";
            case "22001" -> "слишком длинное значение: имя до 50 символов, название до 100, ISBN до 17";
            default -> "база данных ответила: " + sql.getMessage();
        };
    }

    private void search() {
        System.out.print("Часть названия: ");
        String part = scanner.nextLine().trim(); //nextInt() читает число, а Enter после него оставляет во вводе.
        // Следующий nextLine() подхватывает этот пустой Enter, поэтому везде nextLine()
        printBooks(bookDao.findByTitle(part));
    }

    private void searchByAuthor() {
        System.out.print("Часть имени автора: ");
        String part = scanner.nextLine().trim();
        printBooks(bookDao.findByAuthor(part));
    }

    private void printAuthorStats() {
        for (AuthorStats stats : authorDao.findAllWithBookCount()) {
            System.out.printf("%-25s книг: %d%n", stats.name(), stats.bookCount());
        }
    }

    private void addBook() {
        System.out.print("Название: ");
        String title = scanner.nextLine().trim();

        System.out.println("Авторы:");
        for (Author author : authorDao.findAll()) {
            System.out.println("  " + author.id() + ". " + author.name());
        }
        System.out.print("Номер автора: ");
        int authorId = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Год (Enter - неизвестен): ");
        String yearText = scanner.nextLine().trim();
        Integer year = yearText.isEmpty() ? null : Integer.parseInt(yearText);

        System.out.print("ISBN (Enter - нет): ");
        String isbnText = scanner.nextLine().trim();
        String isbn = isbnText.isEmpty() ? null : isbnText;

        int id = bookDao.add(new Book(null, title, new Author(authorId, null), year, isbn));
        System.out.println("Книга добавлена, id = " + id);
    }

    private void deleteBook() {
        System.out.print("id книги для удаления: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        if (bookDao.delete(id)) {
            System.out.println("Книга удалена");
        } else {
            System.out.println("Книги с id " + id + " нет");
        }
    }

    private void updateBook() {
        System.out.print("id книги: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Новое название (Enter - оставить как есть): ");
        String titleText = scanner.nextLine().trim();
        String newTitle = titleText.isEmpty() ? null : titleText;

        System.out.print("Новый год (Enter - оставить как есть): ");
        String yearText = scanner.nextLine().trim();
        Integer newYear = yearText.isEmpty() ? null : Integer.parseInt(yearText);

        if (newTitle == null && newYear == null) {
            System.out.println("Ничего не изменено");
            return;
        }

        if (bookDao.update(new Book(id, newTitle, null, newYear, null))) {
            System.out.println("Книга изменена");
        } else {
            System.out.println("Книги с id " + id + " нет");
        }
    }

    private void addBookWithNewAuthor() {
        System.out.print("Имя нового автора: ");
        String authorName = scanner.nextLine().trim();

        System.out.print("Название книги: ");
        String title = scanner.nextLine().trim();

        System.out.print("Год (Enter - неизвестен): ");
        String yearText = scanner.nextLine().trim();
        Integer year = yearText.isEmpty() ? null : Integer.parseInt(yearText);

        System.out.print("ISBN (Enter - нет): ");
        String isbnText = scanner.nextLine().trim();
        String isbn = isbnText.isEmpty() ? null : isbnText;

        int bookId = service.addBookWithNewAuthor(authorName, title, year, isbn);
        System.out.println("Автор и книга добавлены, id книги = " + bookId);
    }

    private void printBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("Ничего не найдено");
            return;
        }
        for (Book book : books) {
            System.out.printf("  %3d  %-26s %-18s %s%n",
                    book.id(), book.title(), book.author().name(),
                    book.year() == null ? "-" : book.year());
        }
    }
}
