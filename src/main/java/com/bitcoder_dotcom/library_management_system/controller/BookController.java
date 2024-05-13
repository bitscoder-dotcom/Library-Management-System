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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

;import java.security.Principal;
import java.util.Objects;

@Controller
@RequestMapping("/lms/v1/book")
@AllArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    @PostMapping("/add")
    public ModelAndView addNewBookToLibrary(@ModelAttribute BookDto bookDto, RedirectAttributes redirectAttributes, Principal principal) throws JsonProcessingException {
        ApiResponse<BookDto.Response> response = bookService.addNewBookToLibrary(bookDto, principal).getBody();
        ModelAndView modelAndView = new ModelAndView();
        if (response.getMessage().equals("Book added successfully")) {
            // Modify the success message
            String successMessage = "Book with BookId " + response.getData().getId() + ", with Title " + response.getData().getTitle() +
                    " inserted successfully to " + bookDto.getGenre() + " shelve by Librarian " + principal.getName();

            // Convert the book to a JSON string
            ObjectMapper objectMapper = new ObjectMapper();
            String bookJson = objectMapper.writeValueAsString(response.getData());

            redirectAttributes.addFlashAttribute("book", bookJson);
            modelAndView.addObject("successMessage", successMessage);
            modelAndView.setViewName("redirect:/lms/v1/book/bookDetails");
        } else {
            // handle error
            modelAndView.addObject("errorMessage", "An error occurred: " + response.getMessage());
            modelAndView.setViewName("addBook");
        }
        return modelAndView;
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

    @GetMapping("/bookDetails")
    public String showBookDetails(@ModelAttribute("book") String bookJson, Model model) throws JsonProcessingException {
        // Convert the JSON string to a BookDto.Response object
        ObjectMapper objectMapper = new ObjectMapper();
        BookDto.Response book = objectMapper.readValue(bookJson, BookDto.Response.class);

        model.addAttribute("book", book);
        return "bookDetails";
    }

}
