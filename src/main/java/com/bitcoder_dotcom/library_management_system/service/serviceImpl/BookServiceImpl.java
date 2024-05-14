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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowRepository borrowRepository;

    @Override
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

    @Override
    public ResponseEntity<ApiResponse<List<BookDto.Response>>> getAllBooks(Principal principal) {
        log.info("Fetching all books for user: {}", principal.getName());
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        List<Book> books = bookRepository.findAll();
        List<BookDto.Response> bookResponses = books.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
        ApiResponse<List<BookDto.Response>> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Fetched all books for user: " + user.getName(),
                bookResponses
        );
        log.info("Fetched all books for user: {}", principal.getName());
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<BookDto.Response>> getBookById(String id, Principal principal) {
        log.info("Fetching book with id: {}", id);
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        BookDto.Response bookResponse = convertEntityToDto(book);
        ApiResponse<BookDto.Response> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Fetched book with id: " + id + " for user: " + user.getName(),
                bookResponse
        );
        log.info("Fetched book with id: {}", id);
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<BookDto.Response>> updateBook(String id, BookDto bookRequest, Principal principal) {
        log.info("Updating book with id: {}", id);
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        if (user.getRoles() != Roles.LIBRARIAN) {
            throw new UnauthorizedException("Only a Librarian can update book details");
        }
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        book.setTitle(bookRequest.getTitle());
        book.setAuthor(bookRequest.getAuthor());
        book.setIsbn(bookRequest.getIsbn());
        book.setGenre(bookRequest.getGenre());
        book.setQuantity(bookRequest.getQuantity());
        book.setPublicationYear(bookRequest.getPublicationYear());
        book.setUser(user);
        bookRepository.save(book);
        BookDto.Response bookResponse = convertEntityToDto(book);
        ApiResponse<BookDto.Response> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Updated book with id: " + id + " for user: " + user.getName(),
                bookResponse
        );
        log.info("Updated book with id: {}", id);
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<String>> removeBook(String id, Principal principal) {
        log.info("Removing book with id: {}", id);
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        if (user.getRoles() != Roles.LIBRARIAN) {
            throw new UnauthorizedException("Only a Librarian can remove a book");
        }
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        bookRepository.delete(book);
        ApiResponse<String> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Removed book with id: " + id + " by user: " + user.getName(),
                "Book removed successfully"
        );
        log.info("Removed book with id: {}", id);
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
