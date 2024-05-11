package com.bitcoder_dotcom.library_management_system.dto;

import com.bitcoder_dotcom.library_management_system.constant.Roles;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LibrarianDto {

    private String name;
    private String email;
    private String password;
    private Roles roles;
    private List<Long> bookIds;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        private String id;
        private String name;
        private String email;
        private Roles roles;
        private List<String> bookIds;
    }
}

