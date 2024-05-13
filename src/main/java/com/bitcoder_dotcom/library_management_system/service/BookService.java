package com.bitcoder_dotcom.library_management_system.service;

import com.bitcoder_dotcom.library_management_system.dto.ApiResponse;
import com.bitcoder_dotcom.library_management_system.dto.BookDto;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface BookService {

    ResponseEntity<ApiResponse<BookDto.Response>> addNewBookToLibrary(BookDto bookDto, Principal principal);
}
