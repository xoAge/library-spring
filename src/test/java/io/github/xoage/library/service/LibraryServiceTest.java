package io.github.xoage.library.service;

import io.github.xoage.library.model.Author;
import io.github.xoage.library.model.Book;
import io.github.xoage.library.repository.AuthorRepository;
import io.github.xoage.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private LibraryService service;

    @Test
    void addBook_savesBookWithFoundAuthor() {
        // Arrange
        Author author = new Author(1, "Михаил Булгаков");
        when(authorRepository.findById(1)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenAnswer(call -> call.getArgument(0));

        // Act
        Book result = service.addBook("Мастер и Маргарита", 1, 1967, null);

        // Assert
        assertThat(result.title()).isEqualTo("Мастер и Маргарита");
        assertThat(result.author()).isSameAs(author);
        assertThat(result.year()).isEqualTo(1967);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void addBook_throwsNotFound_whenAuthorMissing() {
        when(authorRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addBook("Книга", 999, null, null))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("999");

        verify(bookRepository, never()).save(any());
    }

    @Test
    void updateBook_changesOnlyGivenFields() {
        Author author = new Author(1, "Автор");
        Book book = new Book(5, "Старое название", author, 2000, "111");
        when(bookRepository.findById(5)).thenReturn(Optional.of(book));

        boolean updated = service.updateBook(5, "Новое название", null, null, null);

        assertThat(updated).isTrue();
        assertThat(book.title()).isEqualTo("Новое название");
        assertThat(book.year()).isEqualTo(2000);
        assertThat(book.isbn()).isEqualTo("111");
        assertThat(book.author()).isSameAs(author);
        verifyNoInteractions(authorRepository);
    }

    @Test
    void updateBook_returnsFalse_whenBookMissing() {
        when(bookRepository.findById(5)).thenReturn(Optional.empty());

        boolean updated = service.updateBook(5, "Новое название", null, null, null);

        assertThat(updated).isFalse();
    }

    @Test
    void deleteBook_throwsNotFound_whenBookMissing() {
        when(bookRepository.existsById(7)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteBook(7))
                .isInstanceOf(NotFoundException.class);

        verify(bookRepository, never()).deleteById(anyInt());
    }
}