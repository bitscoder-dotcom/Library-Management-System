package com.bitcoder_dotcom.library_management_system.service.serviceImpl;

import com.bitcoder_dotcom.library_management_system.constant.Roles;
import com.bitcoder_dotcom.library_management_system.dto.ApiResponse;
import com.bitcoder_dotcom.library_management_system.dto.BorrowDto;
import com.bitcoder_dotcom.library_management_system.dto.PatronDto;
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
import com.bitcoder_dotcom.library_management_system.service.PatronService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;

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


    private PatronDto.Response convertEntityToDto(Patron patron) {
        List<String> bookIds = patron.getBooks().stream()
                .map(book -> book.getId())
                .collect(Collectors.toList());

        PatronDto.Response patronResponse = new PatronDto.Response();
        patronResponse.setId(patron.getId());
        patronResponse.setName(patron.getName());
        patronResponse.setEmail(patron.getEmail());
        patronResponse.setRoles(patron.getRoles());
        patronResponse.setWithBorrowedBook(patron.isWithBorrowedBook());
        patronResponse.setBookIds(bookIds);

        return patronResponse;
    }
}
