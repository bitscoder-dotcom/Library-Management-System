package com.bitcoder_dotcom.library_management_system.service;

import com.bitcoder_dotcom.library_management_system.dto.ApiResponse;
import com.bitcoder_dotcom.library_management_system.dto.PatronDto;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface PatronService {
    ResponseEntity<ApiResponse<List<PatronDto.Response>>> getAllPatrons(Principal principal);
    ResponseEntity<ApiResponse<PatronDto.DetailedResponse>> getPatronById(String patronId, Principal principal);
    ResponseEntity<ApiResponse<PatronDto.Response>> updatePatronDetails(String patronId, PatronDto updatedDetails, Principal principal);
    ResponseEntity<ApiResponse<String>> removePatron(String patronId, Principal principal);
}
