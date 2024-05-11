package com.bitcoder_dotcom.library_management_system.dto;

import com.bitcoder_dotcom.library_management_system.constant.Genre;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private String title;
    private String author;
    private String isbn;
    private Genre genre;
    private long quantity;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        private String id;
        private String title;
        private String author;
        private String isbn;
        private Genre genre;
        private String publicationYear;
        private long quantity;
    }
}

