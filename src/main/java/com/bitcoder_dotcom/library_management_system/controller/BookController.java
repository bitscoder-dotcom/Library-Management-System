package com.bitcoder_dotcom.library_management_system.controller;

import com.bitcoder_dotcom.library_management_system.dto.ApiResponse;
import com.bitcoder_dotcom.library_management_system.dto.BookDto;
import com.bitcoder_dotcom.library_management_system.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@RestController
@RequestMapping("/lms/v1/book")
@AllArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<BookDto.Response>> addNewBook(@ModelAttribute BookDto bookDto, Principal principal) {
        if (principal == null) {
            // The user is not authenticated, return an error response
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // The user is authenticated, proceed with adding the book
        return bookService.addNewBookToLibrary(bookDto, principal);
    }

//    @PostMapping("/add")
//    public ResponseEntity<BookDto> addNewBook(@ModelAttribute BookDto bookDto, Principal principal) {
//        if (principal == null) {
//            // The user is not authenticated, return an error response
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        // The user is authenticated, proceed with adding the book
//        BookDto addedBook = bookService.addNewBookToLibrary(bookDto, principal);
//        return ResponseEntity.ok(addedBook);
//    }

    @GetMapping("/add")
    public String showAddBookPage(Model model) {
        model.addAttribute("bookDto", new BookDto());
        return "addBook";
    }
}
