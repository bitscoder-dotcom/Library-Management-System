package com.bitcoder_dotcom.library_management_system.controller;

import com.bitcoder_dotcom.library_management_system.dto.ApiResponse;
import com.bitcoder_dotcom.library_management_system.dto.BookDto;
import com.bitcoder_dotcom.library_management_system.service.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.Objects;


@Controller
@RequestMapping("/lms/v1/book")
@AllArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    @PostMapping("/add")
    public String addNewBookToLibrary(@ModelAttribute BookDto bookDto, RedirectAttributes redirectAttributes, Principal principal) {
        ResponseEntity<ApiResponse<BookDto.Response>> response = bookService.addNewBookToLibrary(bookDto, principal);
        if (response.getStatusCode() == HttpStatus.OK) {
            // Modify the success message
            String successMessage = "Book with Title " + bookDto.getTitle() + "inserted successfully to " + bookDto.getGenre()
                    + " shelve by Librarian " + principal.getName();

            redirectAttributes.addFlashAttribute("book", response.getBody().getData());
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            return "redirect:/lms/v1/book/bookDetails";
        } else {
            // handle error
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred: " + response.getBody().getMessage());
            return "redirect:/lms/v1/book/addBook";
        }
    }

    @GetMapping("/bookDetails")
    public String showBookDetails(@ModelAttribute("book") BookDto.Response book, Model model) {
        log.info("Received request to show book details page");
        model.addAttribute("book", book);
        return "bookDetails";
    }

    @GetMapping("/addBook")
    public String showAddBookPage(Model model) {
        log.info("Received request to show add book page");
        model.addAttribute("bookDto", new BookDto());
        return "addBook";
    }

    @GetMapping("/userPage")
    public String showUserPage() {
        log.info("Received request to show user page");
        return "userPage";
    }
}
