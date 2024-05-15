package com.bitcoder_dotcom.library_management_system.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowDto {

    private String borrowId;
    private String patronId;
    private String bookId;
    private LocalDateTime borrowedAt;
    private LocalDateTime returnedAt;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        private String borrowId;
        private String patronId;
        private String bookId;
        private LocalDateTime borrowedAt;
        private LocalDateTime returnedAt;
    }
}

