package com.bitcoder_dotcom.library_management_system.controller;

import com.bitcoder_dotcom.library_management_system.dto.ApiResponse;
import com.bitcoder_dotcom.library_management_system.dto.BookDto;
import com.bitcoder_dotcom.library_management_system.dto.PatronDto;
import com.bitcoder_dotcom.library_management_system.service.PatronService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("/lms/v1/patron")
@AllArgsConstructor
@Slf4j
public class PatronController {

    private final PatronService patronService;

    @GetMapping("/patrons")
    public String showPatronsPage(Model model, Principal principal) {
        log.info("Received request to show lists of patrons page");
        ResponseEntity<ApiResponse<List<PatronDto.Response>>> response = patronService.getAllPatrons(principal);
        if (response.getStatusCode() == HttpStatus.OK) {
            model.addAttribute("patrons", response.getBody().getData());
            return "patrons";
        } else {
            // handle error
            model.addAttribute("errorMessage", "An error occurred: " + response.getBody().getMessage());
            return "errorPage";
        }
    }

    @GetMapping("/{id}")
    public String showPatronDetails(@PathVariable String id, Model model, Principal principal) {
        ResponseEntity<ApiResponse<PatronDto.DetailedResponse>> response = patronService.getPatronById(id, principal);
        if (response.getStatusCode() == HttpStatus.OK) {
            model.addAttribute("patron", response.getBody().getData());
            return "patronDetails";
        } else {
            // handle error
            model.addAttribute("errorMessage", "An error occurred: " + response.getBody().getMessage());
            return "errorPage";
        }
    }

    @GetMapping("/updatePatron/{id}")
    public String showUpdatePatronPage(@PathVariable String id, Model model, Principal principal) {
        log.info("Received request to show update patron page");
        ResponseEntity<ApiResponse<PatronDto.DetailedResponse>> response = patronService.getPatronById(id, principal);
        if (response.getStatusCode() == HttpStatus.OK) {
            model.addAttribute("patronDto", new PatronDto());
            model.addAttribute("patron", response.getBody().getData());
            return "updatePatron";
        } else {
            // handle error
            model.addAttribute("errorMessage", "An error occurred: " + response.getBody().getMessage());
            return "errorPage";
        }
    }

    @PostMapping("/updatePatron/{id}")
    public String updatePatron(@PathVariable String id, @ModelAttribute PatronDto patronRequest, RedirectAttributes redirectAttributes, Principal principal) {
        log.info("Updating patron with id: {}", id);
        ResponseEntity<ApiResponse<PatronDto.Response>> response = patronService.updatePatronDetails(id, patronRequest, principal);
        if (response.getStatusCode() == HttpStatus.OK) {
            redirectAttributes.addFlashAttribute("patron", response.getBody().getData());
            redirectAttributes.addFlashAttribute("successMessage", "Patron details updated successfully");
            return "redirect:/lms/v1/patron/patronDetails";
        } else {
            // handle error
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred: " + response.getBody().getMessage());
            return "redirect:/lms/v1/patron/updatePatron/" + id;
        }
    }

    @GetMapping("/deletePatron")
    public String showDeletePatronPage(Model model) {
        model.addAttribute("patronDto", new PatronDto());
        return "deletePatron";
    }

    @PostMapping("/deletePatron")
    public String deletePatron(@ModelAttribute PatronDto patron, RedirectAttributes redirectAttributes, Principal principal) {
        log.info("Deleting patron with id: {}", patron.getPatronId());
        ResponseEntity<ApiResponse<String>> response = patronService.removePatron(patron.getPatronId(), principal);
        if (response.getStatusCode() == HttpStatus.OK) {
            redirectAttributes.addFlashAttribute("successMessage", "Patron deleted successfully");
            return "redirect:/lms/v1/patron/deletePatron";
        } else {
            // handle error
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred: " + response.getBody().getMessage());
            return "errorPage";
        }
    }
}