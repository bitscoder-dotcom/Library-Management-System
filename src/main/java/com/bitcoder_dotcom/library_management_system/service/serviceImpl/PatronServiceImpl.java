package com.bitcoder_dotcom.library_management_system.service.serviceImpl;

import com.bitcoder_dotcom.library_management_system.constant.Roles;
import com.bitcoder_dotcom.library_management_system.dto.ApiResponse;
import com.bitcoder_dotcom.library_management_system.dto.BookDto;
import com.bitcoder_dotcom.library_management_system.dto.PatronDto;
import com.bitcoder_dotcom.library_management_system.exception.ResourceNotFoundException;
import com.bitcoder_dotcom.library_management_system.exception.UnauthorizedException;
import com.bitcoder_dotcom.library_management_system.models.Book;
import com.bitcoder_dotcom.library_management_system.models.Borrow;
import com.bitcoder_dotcom.library_management_system.models.Patron;
import com.bitcoder_dotcom.library_management_system.models.User;
import com.bitcoder_dotcom.library_management_system.repository.PatronRepository;
import com.bitcoder_dotcom.library_management_system.repository.UserRepository;
import com.bitcoder_dotcom.library_management_system.service.PatronService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class PatronServiceImpl implements PatronService {

    private final UserRepository userRepository;
    private final PatronRepository patronRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<ApiResponse<List<PatronDto.Response>>> getAllPatrons(Principal principal) {
        log.info("Fetching all patrons for user: {}", principal.getName());
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        if (user.getRoles() != Roles.LIBRARIAN) {
            throw new UnauthorizedException("Only a Librarian can fetch all patrons");
        }
        List<Patron> patrons = patronRepository.findAll();
        List<PatronDto.Response> patronResponses = patrons.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
        ApiResponse<List<PatronDto.Response>> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Fetched all patrons for user: " + user.getName(),
                patronResponses
        );
        log.info("Fetched all patrons for user: {}", principal.getName());
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<PatronDto.DetailedResponse>> getPatronById(String patronId, Principal principal) {
        log.info("Fetching patron details for id: {}", patronId);
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        if (user.getRoles() != Roles.LIBRARIAN) {
            throw new UnauthorizedException("Only a Librarian can fetch patron details");
        }
        Patron patron = patronRepository.findById(patronId)
                .orElseThrow(() -> new ResourceNotFoundException("Patron", "id", patronId));
        PatronDto.DetailedResponse patronResponse = convertEntityToDto2(patron);
        ApiResponse<PatronDto.DetailedResponse> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Fetched patron details for id: " + patronId,
                patronResponse
        );
        log.info("Fetched patron details for id: {}", patronId);
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<PatronDto.Response>> updatePatronDetails(String patronId, PatronDto updatedDetails, Principal principal) {
        log.info("Updating patron details for id: {}", patronId);

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));

        if (!user.getId().equals(patronId) && user.getRoles() != Roles.LIBRARIAN) {
            throw new UnauthorizedException("Only the owner of the account or a Librarian can update patron details");
        }

        Patron patron = patronRepository.findById(patronId)
                .orElseThrow(() -> new ResourceNotFoundException("Patron", "id", patronId));

        patron.setName(updatedDetails.getName());
        patron.setEmail(updatedDetails.getEmail());
        patron.setPassword(passwordEncoder.encode(updatedDetails.getPassword()));

        patronRepository.save(patron);

        PatronDto.Response patronResponse = convertEntityToDto(patron);
        ApiResponse<PatronDto.Response> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Updated patron details for id: " + patronId,
                patronResponse
        );

        log.info("Updated patron details for id: {}", patronId);
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<String>> removePatron(String patronId, Principal principal) {
        log.info("Removing patron with id: {}", patronId);

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));

        if (user.getRoles() != Roles.LIBRARIAN) {
            throw new UnauthorizedException("Only a Librarian can remove a patron");
        }

        Patron patron = patronRepository.findById(patronId)
                .orElseThrow(() -> new ResourceNotFoundException("Patron", "id", patronId));

        if (!patron.getBorrows().isEmpty()) {
            throw new IllegalStateException("Patron has books that need to be returned");
        }

        patronRepository.delete(patron);

        ApiResponse<String> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Removed patron with id: " + patronId,
                "Patron removed successfully"
        );

        log.info("Removed patron with id: {}", patronId);
        return ResponseEntity.ok(apiResponse);
    }

    private PatronDto.Response convertEntityToDto(Patron patron) {
        PatronDto.Response dto = new PatronDto.Response();
        List<String> bookNames = patron.getBorrows().stream()
                .map(Borrow::getBook)
                .map(Book::getTitle)
                .collect(Collectors.toList());
        dto.setBookIds(bookNames);

        PatronDto.Response patronResponse = new PatronDto.Response();
        patronResponse.setId(patron.getId());
        patronResponse.setName(patron.getName());
        patronResponse.setEmail(patron.getEmail());
        patronResponse.setRoles(patron.getRoles());
        patronResponse.setWithBorrowedBook(patron.isWithBorrowedBook());
        patronResponse.setBookIds(bookNames);

        return patronResponse;
    }

    private PatronDto.DetailedResponse convertEntityToDto2(Patron patron) {
        PatronDto.DetailedResponse dto = new PatronDto.DetailedResponse();
        List<BookDto.Response> borrowedBooks = patron.getBorrows().stream()
                .map(borrow -> {
                    BookDto.Response bookDto = convertBookEntityToDto(borrow.getBook());
                    bookDto.setBorrowedAt(borrow.getBorrowedAt());
                    return bookDto;
                })
                .collect(Collectors.toList());
        dto.setBorrowedBooks(borrowedBooks);

        dto.setId(patron.getId());
        dto.setName(patron.getName());
        dto.setEmail(patron.getEmail());
        dto.setRoles(patron.getRoles());
        dto.setWithBorrowedBook(patron.isWithBorrowedBook());

        return dto;
    }


    private BookDto.Response convertBookEntityToDto(Book book) {
        BookDto.Response dto = new BookDto.Response();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setGenre(book.getGenre());
        dto.setPublicationYear(book.getPublicationYear());
        dto.setQuantity(book.getQuantity());
        return dto;
    }
}
