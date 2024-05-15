package com.bitcoder_dotcom.library_management_system.controller;

import com.bitcoder_dotcom.library_management_system.dto.ApiResponse;
import com.bitcoder_dotcom.library_management_system.dto.BookDto;
import com.bitcoder_dotcom.library_management_system.dto.BorrowDto;
import com.bitcoder_dotcom.library_management_system.service.BorrowService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/lms/v1/book")
@AllArgsConstructor
@Slf4j
public class BorrowController {

    private final BorrowService borrowService;


    @PostMapping("/borrowBook")
    public String borrowBook(@ModelAttribute BorrowDto borrowDto, RedirectAttributes redirectAttributes, Principal principal) {
        log.info("Borrowing book with id: {}", borrowDto.getBookId());
        ResponseEntity<ApiResponse<BorrowDto.Response>> response = borrowService.borrowBook(borrowDto, principal);
        if (response.getStatusCode() == HttpStatus.OK) {
            redirectAttributes.addFlashAttribute("alertMessage", "Please save this Book ID: "
                    + borrowDto.getBookId() + " and Borrow ID: " + response.getBody().getData().getBorrowId());
            redirectAttributes.addFlashAttribute("successMessage", "Book borrowed successfully");
            return "redirect:/lms/v1/book/bookTransaction";
        } else {
            // handle error
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred: " + response.getBody().getMessage());
            return "redirect:/lms/v1/book/bookTransaction";
        }
    }

    @PostMapping("/returnBook")
    public String returnBook(@RequestParam String bookId, @RequestParam String borrowId, RedirectAttributes redirectAttributes, Principal principal) {
        log.info("Returning book with book id: {}", borrowId);
        ResponseEntity<ApiResponse<BorrowDto.Response>> response = borrowService.returnBook(bookId, borrowId, principal);
        if (response.getStatusCode() == HttpStatus.OK) {
            redirectAttributes.addFlashAttribute("successMessage", "Book returned successfully");
            return "redirect:/lms/v1/book/bookTransaction";
        } else {
            // handle error
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred: " + response.getBody().getMessage());
            return "redirect:/lms/v1/book/bookTransaction";
        }
    }

    @GetMapping("/bookTransaction")
    public String showBookTransactionPage() {
        log.info("Received request to show book transaction page");
        return "bookTransaction";
    }
}
