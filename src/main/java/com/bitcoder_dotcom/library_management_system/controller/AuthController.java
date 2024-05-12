package com.bitcoder_dotcom.library_management_system.controller;

import com.bitcoder_dotcom.library_management_system.dto.ApiResponse;
import com.bitcoder_dotcom.library_management_system.dto.SignInRequest;
import com.bitcoder_dotcom.library_management_system.dto.UserRegistrationRequest;
import com.bitcoder_dotcom.library_management_system.security.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;


@Controller
@AllArgsConstructor
@RequestMapping("/lms/v1/auth")
public class AuthController {

    private AuthService authService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userRegistrationRequest", new UserRegistrationRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute UserRegistrationRequest request, RedirectAttributes redirectAttributes) {
        UserRegistrationRequest.Response response = authService.register(request);
        if (response.getMessage().equals("User registered successfully")) {
            redirectAttributes.addFlashAttribute("message", response.getMessage());
            return "redirect:/lms/v1/auth/signIn";
        } else {
            redirectAttributes.addFlashAttribute("message", response.getMessage());
            return "redirect:/registrationFailure";
        }
    }

    @GetMapping("/signIn")
    public String showSignInForm(Model model) {
        model.addAttribute("signInRequest", new SignInRequest());
        return "signIn";
    }

    @PostMapping("/signIn")
    public String signIn(@ModelAttribute SignInRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

        ResponseEntity<ApiResponse<SignInRequest.Response>> apiResponse = authService.signIn(request);
        if (apiResponse.getStatusCode() == HttpStatus.OK) {

            Cookie cookie = new Cookie("token", Objects.requireNonNull(apiResponse.getBody()).getData().getToken());
            cookie.setHttpOnly(true);

            response.addCookie(cookie);

            return "success";
        } else  {
            redirectAttributes.addFlashAttribute("error", Objects.requireNonNull(apiResponse.getBody()).getMessage());
            return "redirect:error";
        }
    }

//    @PostMapping("/signIn")
//    public String signIn(@ModelAttribute SignInRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
//        ResponseEntity<ApiResponse<SignInRequest.Response>> apiResponse = authService.signIn(request);
//        if (apiResponse.getStatusCode() == HttpStatus.OK) {
//            // Create a new cookie
//            Cookie cookie = new Cookie("token", Objects.requireNonNull(apiResponse.getBody()).getData().getToken());
//            cookie.setHttpOnly(true);
//            // Add the cookie to the response
//            response.addCookie(cookie);
//            // Redirect to the 2FA page
//            return "redirect:/lgsApp/v1/auth/2fa";
//        } else {
//            redirectAttributes.addFlashAttribute("error", Objects.requireNonNull(apiResponse.getBody()).getMessage());
//            return "redirect:/error";
//        }
//    }
}