package com.bitcoder_dotcom.library_management_system.dto;

import com.bitcoder_dotcom.library_management_system.constant.Roles;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PatronDto {

    private String patronId;
    private String name;
    private String email;
    private String password;
    private Roles roles;
    private List<String> bookIds;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        private String id;
        private String name;
        private String email;
        private Roles roles;
        private boolean withBorrowedBook;
        private List<String> bookIds;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailedResponse {

        private String id;
        private String name;
        private String email;
        private Roles roles;
        private boolean withBorrowedBook;
        private List<BookDto.Response> borrowedBooks;
    }
}
