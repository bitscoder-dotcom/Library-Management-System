package com.bitcoder_dotcom.library_management_system.service.serviceImpl;

import com.bitcoder_dotcom.library_management_system.constant.Roles;
import com.bitcoder_dotcom.library_management_system.dto.ApiResponse;
import com.bitcoder_dotcom.library_management_system.dto.BorrowDto;
import com.bitcoder_dotcom.library_management_system.exception.ResourceNotFoundException;
import com.bitcoder_dotcom.library_management_system.exception.UnauthorizedException;
import com.bitcoder_dotcom.library_management_system.models.Book;
import com.bitcoder_dotcom.library_management_system.models.Borrow;
import com.bitcoder_dotcom.library_management_system.models.Patron;
import com.bitcoder_dotcom.library_management_system.models.User;
import com.bitcoder_dotcom.library_management_system.repository.BookRepository;
import com.bitcoder_dotcom.library_management_system.repository.BorrowRepository;
import com.bitcoder_dotcom.library_management_system.repository.PatronRepository;
import com.bitcoder_dotcom.library_management_system.repository.UserRepository;
import com.bitcoder_dotcom.library_management_system.service.BorrowService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@AllArgsConstructor
public class BorrowServiceImpl implements BorrowService {
    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final PatronRepository patronRepository;


    @Override
    @Transactional
    public ResponseEntity<ApiResponse<BorrowDto.Response>> borrowBook(BorrowDto borrowDto, Principal principal) {
        log.info("Borrow book method called");

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        if (user.getRoles() != Roles.PATRON) {
            throw new UnauthorizedException("Only a Patron can borrow a book");
        }

        Patron patron = (Patron) user;
        Book book = bookRepository.findById(borrowDto.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", borrowDto.getBookId()));

        Borrow borrow = new Borrow(patron, book);
        borrow.setBorrowedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        borrowRepository.save(borrow);

        patron.setWithBorrowedBook(true);
        patronRepository.save(patron);

        long currentQuantity = book.getQuantity();
        book.setQuantity(currentQuantity - 1);
        bookRepository.save(book);

        BorrowDto.Response borrowResponse = convertEntityToDto(borrow);
        ApiResponse<BorrowDto.Response> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Book borrowed successfully by " + user.getName(),
                borrowResponse
        );

        log.info("Book borrowed successfully with id: {}", borrow.getId());
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<BorrowDto.Response>> returnBook(String bookId, String borrowId, Principal principal) {
        log.info("Return book method called");

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        if (user.getRoles() != Roles.PATRON) {
            throw new UnauthorizedException("Only a Patron can return a book");
        }

        Patron patron = (Patron) user;
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));

        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow", "id", borrowId));

        if (!borrow.getPatron().equals(patron) || !borrow.getBook().equals(book)) {
            throw new UnauthorizedException("The provided borrowId does not match the provided bookId and user");
        }

        borrow.setReturnedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        borrowRepository.save(borrow);

        List<Borrow> borrowedBooks = borrowRepository.findByPatron(patron);
        if (borrowedBooks.size() > 1) {
            patron.setWithBorrowedBook(true);
        } else {
            patron.setWithBorrowedBook(false);
        }
        patronRepository.save(patron);

        long currentQuantity = book.getQuantity();
        book.setQuantity(currentQuantity + 1);
        bookRepository.save(book);

        BorrowDto.Response borrowResponse = convertEntityToDto(borrow);
        ApiResponse<BorrowDto.Response> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Book returned successfully by " + user.getName(),
                borrowResponse
        );

        log.info("Book returned successfully with id: {}", borrow.getId());

        // Schedule to delete borrow record after 1 day
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {
            borrowRepository.delete(borrow);
            log.info("Borrow record deleted successfully with id: {}", borrow.getId());
        }, 1, TimeUnit.DAYS);

        return ResponseEntity.ok(apiResponse);
    }


    private BorrowDto.Response convertEntityToDto(Borrow borrow) {
        BorrowDto.Response borrowResponse = new BorrowDto.Response();
        borrowResponse.setBorrowId(borrow.getId());
        borrowResponse.setPatronId(borrow.getPatron().getId());
        borrowResponse.setBookId(borrow.getBook().getId());
        borrowResponse.setBorrowedAt(borrow.getBorrowedAt());
        borrowResponse.setReturnedAt(borrow.getReturnedAt());
        return borrowResponse;
    }
}
