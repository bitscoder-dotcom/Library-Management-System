package com.bitcoder_dotcom.library_management_system.service.serviceImpl;

import com.bitcoder_dotcom.library_management_system.constant.Roles;
import com.bitcoder_dotcom.library_management_system.dto.ApiResponse;
import com.bitcoder_dotcom.library_management_system.dto.BookDto;
import com.bitcoder_dotcom.library_management_system.exception.ResourceNotFoundException;
import com.bitcoder_dotcom.library_management_system.exception.UnauthorizedException;
import com.bitcoder_dotcom.library_management_system.models.Book;
import com.bitcoder_dotcom.library_management_system.models.User;
import com.bitcoder_dotcom.library_management_system.repository.BookRepository;
import com.bitcoder_dotcom.library_management_system.repository.BorrowRepository;
import com.bitcoder_dotcom.library_management_system.repository.UserRepository;
import com.bitcoder_dotcom.library_management_system.service.BookService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.beans.FeatureDescriptor;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowRepository borrowRepository;

    @Override
//    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<BookDto.Response>> addNewBookToLibrary(BookDto bookDto, Principal principal) {
        log.info("Inserting book with title: {}", bookDto.getTitle());
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        if (user.getRoles() != Roles.LIBRARIAN) {
            throw new UnauthorizedException("Only a Librarian can insert a book");
        }
        Book book = convertDtoToEntity(bookDto, user);
        book.setPublicationYear(bookDto.getPublicationYear());
        bookRepository.save(book);
        BookDto.Response bookResponse = convertEntityToDto(book);
        ApiResponse<BookDto.Response> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Book inserted successfully to "+ bookDto.getGenre()+ " shelve by "+ user.getName(),
                bookResponse
        );
        log.info("Book inserted successfully with title: {}", bookDto.getTitle());
        return ResponseEntity.ok(apiResponse);
    }

    private BookDto.Response convertEntityToDto(Book book) {
        BookDto.Response bookResponse = new BookDto.Response();
        bookResponse.setId(book.getId());
        bookResponse.setTitle(book.getTitle());
        bookResponse.setAuthor(book.getAuthor());
        bookResponse.setIsbn(book.getIsbn());
        bookResponse.setGenre(book.getGenre());
        bookResponse.setPublicationYear(book.getPublicationYear());
        bookResponse.setQuantity(book.getQuantity());
        return bookResponse;
    }


    private Book convertDtoToEntity(BookDto bookDto, User user) {
        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setIsbn(bookDto.getIsbn());
        book.setGenre(bookDto.getGenre());
        book.setQuantity(bookDto.getQuantity());
        book.setPublicationYear(bookDto.getPublicationYear());
        book.setUser(user);
        return book;
    }

    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }
}
