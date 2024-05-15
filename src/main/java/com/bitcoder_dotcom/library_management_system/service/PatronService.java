package com.bitcoder_dotcom.library_management_system.service;

import com.bitcoder_dotcom.library_management_system.dto.ApiResponse;
import com.bitcoder_dotcom.library_management_system.dto.PatronDto;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface PatronService {
    ResponseEntity<ApiResponse<List<PatronDto.Response>>> getAllPatrons(Principal principal);

}
