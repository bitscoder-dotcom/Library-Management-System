package com.bitcoder_dotcom.library_management_system.service;

import com.bitcoder_dotcom.library_management_system.dto.ApiResponse;
import com.bitcoder_dotcom.library_management_system.dto.BorrowDto;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface BorrowService {

    ResponseEntity<ApiResponse<BorrowDto.Response>> borrowBook(BorrowDto borrowDto, Principal principal);
    ResponseEntity<ApiResponse<BorrowDto.Response>> returnBook(String bookId, String borrowId, Principal principal);
}
