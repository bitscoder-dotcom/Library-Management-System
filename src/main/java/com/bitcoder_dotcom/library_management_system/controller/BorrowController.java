package com.bitcoder_dotcom.library_management_system.controller;

import com.bitcoderdotcom.librarymanagementsystem.dto.ApiResponse;
import com.bitcoderdotcom.librarymanagementsystem.dto.BorrowDto;
import com.bitcoderdotcom.librarymanagementsystem.dto.BorrowRequestDto;
import com.bitcoderdotcom.librarymanagementsystem.dto.ReturnRequestDto;
import com.bitcoderdotcom.librarymanagementsystem.service.BorrowService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

@Controller
@AllArgsConstructor
public class BorrowController {

    private BorrowService borrowService;

    @PostMapping("/borrow")
    public ResponseEntity<ApiResponse<BorrowDto.Response>> borrowBook(@RequestBody BorrowRequestDto borrowRequestDto, Principal principal) {
        return borrowService.borrowBook(borrowRequestDto, principal);
    }

    @PostMapping("/return")
    public ResponseEntity<ApiResponse<BorrowDto.Response>> returnBook(@RequestBody ReturnRequestDto returnRequestDto, Principal principal) {
        return borrowService.returnBook(returnRequestDto, principal);
    }
}
